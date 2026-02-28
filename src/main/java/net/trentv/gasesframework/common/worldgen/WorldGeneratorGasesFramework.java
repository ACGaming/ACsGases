package net.trentv.gasesframework.common.worldgen;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.common.IWorldGenerator;

import java.util.*;
import net.trentv.gasesframework.GasesFramework;
import net.trentv.gasesframework.api.GFManipulationAPI;
import net.trentv.gasesframework.api.gasworldgentype.GasWorldGenType;

public class WorldGeneratorGasesFramework implements IWorldGenerator
{
    private static GasWorldGenType currentType;
    private static World currentWorld;

    private final HashMap<String, HashMap<String, TypeHandle>> typeHandlesByDimensionName = new HashMap<>();
    private final IdentityHashMap<World, HashMap<ChunkPos, ChunkBlobs>> chunkBlobsMapsByDimension = new IdentityHashMap<>();

    public WorldGeneratorGasesFramework()
    {

    }

    public boolean isGasWorldGenTypeRegistered(GasWorldGenType type, String dimension)
    {
        String dimensionName = dimension.toLowerCase();
        HashMap<String, TypeHandle> typeHandles = typeHandlesByDimensionName.get(dimensionName);
        return typeHandles != null && typeHandles.containsKey(type.name);
    }

    public void registerGasWorldGenType(GasWorldGenType type, String dimension)
    {
        if (isGasWorldGenTypeRegistered(type, dimension))
        {
            GasesFramework.logger.fatal("A gas world gen type was attempted registered to a dimension it was already registered to");
            return;
        }

        if (type.generationFrequency > 0.0F)
        {
            String dimensionName = dimension.toLowerCase();
            HashMap<String, TypeHandle> typeHandles = typeHandlesByDimensionName.get(dimensionName);
            if (typeHandles == null)
            {
                typeHandles = new HashMap<>();
                typeHandlesByDimensionName.put(dimension, typeHandles);
            }
            typeHandles.put(type.name, new TypeHandle(type));
        }
    }

    @Override
    public synchronized void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider)
    {
        if (world.getWorldType() == WorldType.FLAT) return;
        HashMap<String, TypeHandle> typeHandles = typeHandlesByDimensionName.get(world.provider.getDimensionType().getName().toLowerCase());
        if (typeHandles != null)
        {
            generate(chunkX, chunkZ, world, typeHandles.values());
        }
    }

    private void generate(int chunkX, int chunkZ, World world, Collection<TypeHandle> typeHandles)
    {
        currentWorld = world;

        if (typeHandles != null && !typeHandles.isEmpty())
        {
            int chunkMinX = chunkX << 4;
            int chunkMinZ = chunkZ << 4;
            int chunkMaxX = chunkMinX + 16;
            int chunkMaxZ = chunkMinZ + 16;

            HashMap<ChunkPos, ChunkBlobs> chunkBlobsMap = chunkBlobsMapsByDimension.computeIfAbsent(world, w -> new HashMap<>());
            ChunkPos positionKey = new ChunkPos(chunkX, chunkZ);
            ChunkBlobs chunkBlobs = chunkBlobsMap.get(positionKey);
            if (chunkBlobs == null)
            {
                chunkBlobs = new ChunkBlobs(chunkX, chunkZ, typeHandles);
                chunkBlobsMap.put(positionKey, chunkBlobs);
            }

            chunkBlobs.generate(chunkMinX, chunkMinZ, chunkMaxX, chunkMaxZ, typeHandles);

            if (areChunksAroundChunkLoaded(world.getChunkProvider(), chunkX, chunkZ))
            {
                chunkBlobsMap.remove(positionKey);
            }
        }
    }

    private boolean areChunksAroundChunkLoaded(IChunkProvider chunkGenerator, int x, int z)
    {
        for (int x1 = x - 1; x1 < x + 1; x1++)
        {
            for (int z1 = z - 1; z1 < z + 1; z1++)
            {
                if (x1 != x && z1 != z && !chunkGenerator.isChunkGeneratedAt(x1, z1))
                {
                    return false;
                }
            }
        }

        return true;
    }

    private static class ChunkBlobs
    {
        private static int randomRound(float f, Random random)
        {
            float r = (float) Math.floor(f);
            if (random.nextFloat() < f - r) r += 1.0f;
            return (int) r;
        }

        public final Map<String, Pocket[]> pocketsByType = new HashMap<>();

        public ChunkBlobs(int chunkX, int chunkZ, Collection<TypeHandle> types)
        {
            Random random = new Random(currentWorld.getSeed() + (long) new ChunkPos(chunkX, chunkZ).hashCode() * currentWorld.provider.getDimensionType().getName().hashCode());

            for (TypeHandle type : types)
            {
                int numPockets = randomRound((random.nextFloat() + 0.5f) * type.type.generationFrequency * (type.type.maxY - type.type.minY) / 16.0f, random);
                Pocket[] pockets = new Pocket[numPockets];
                for (int pocket = 0; pocket < numPockets; pocket++)
                {
                    pockets[pocket] = new Pocket(random, chunkX << 4, chunkZ << 4, type);
                }
                pocketsByType.put(type.type.name, pockets);
            }
        }

        public void generate(int chunkMinX, int chunkMinZ, int chunkMaxX, int chunkMaxZ, Collection<TypeHandle> types)
        {
            for (TypeHandle type : types)
            {
                currentType = type.type;
                Pocket[] pockets = pocketsByType.get(currentType.name);

                for (Pocket pocket : pockets)
                {
                    pocket.generate(chunkMinX, chunkMinZ, chunkMaxX, chunkMaxZ);
                }
            }
        }

        private static class Pocket
        {
            private static final float[][][] preCalculatedDistances = new float[15][15][15];

            static
            {
                for (int x = 0; x < 15; x++)
                {
                    for (int y = 0; y < 15; y++)
                    {
                        for (int z = 0; z < 15; z++)
                        {
                            preCalculatedDistances[x][y][z] = (float) Math.sqrt(x * x + y * y + z * z);
                        }
                    }
                }
            }

            private static float getApproximateDistance(float x, float y, float z)
            {
                return preCalculatedDistances[Math.abs(Math.round(x))][Math.abs(Math.round(y))][Math.abs(Math.round(z))];
            }

            public final Blob[] blobs;
            public int minX, minY, minZ, maxX, maxY, maxZ;

            public Pocket(Random random, int absoluteChunkX, int absoluteChunkZ, TypeHandle type)
            {
                float offsetX = 8.0f + (random.nextFloat() - 0.5f) * 8.0f;
                float offsetZ = 8.0f + (random.nextFloat() - 0.5f) * 8.0f;

                float pocketX = absoluteChunkX + offsetX;
                float pocketY = random.nextFloat() * (type.type.maxY - type.type.minY) + type.type.minY;
                float pocketZ = absoluteChunkZ + offsetZ;

                int numBlobs = randomRound(type.averageBlobFrequency, random);

                blobs = new Blob[numBlobs];

                for (int blob = 0; blob < numBlobs; blob++)
                {
                    float blobX;
                    float blobY;
                    float blobZ;
                    do
                    {
                        blobX = random.nextFloat() * 2.0f - 1.0f;
                        blobY = random.nextFloat() * 2.0f - 1.0f;
                        blobZ = random.nextFloat() * 2.0f - 1.0f;
                    } while (blobX * blobX + blobY * blobY + blobZ * blobZ > 1.0f);

                    float blobRadius = (random.nextFloat() + 0.5f) * type.averageBlobRadius;

                    Blob b = new Blob(blobX * type.blobSpread + pocketX, blobY * type.blobSpread + pocketY, blobZ * type.blobSpread + pocketZ, blobRadius);
                    blobs[blob] = b;

                    if (blob != 0)
                    {
                        minX = Math.min(minX, (int) Math.floor(b.x - b.w));
                        maxX = Math.min(maxX, (int) Math.ceil(b.x + b.w));
                        minY = Math.min(minY, (int) Math.floor(b.y - b.w));
                        maxY = Math.min(maxY, (int) Math.ceil(b.y + b.w));
                        minZ = Math.min(minZ, (int) Math.floor(b.z - b.w));
                        maxZ = Math.min(maxZ, (int) Math.ceil(b.z + b.w));
                    }
                    else
                    {
                        minX = (int) Math.floor(b.x - b.w);
                        maxX = (int) Math.ceil(b.x + b.w);
                        minY = (int) Math.floor(b.y - b.w);
                        maxY = (int) Math.ceil(b.y + b.w);
                        minZ = (int) Math.floor(b.z - b.w);
                        maxZ = (int) Math.ceil(b.z + b.w);
                    }
                }
            }

            public void generate(int chunkMinX, int chunkMinZ, int chunkMaxX, int chunkMaxZ)
            {
                int minX = Math.max(this.minX, chunkMinX);
                int maxX = Math.min(this.maxX, chunkMaxX);
                int minZ = Math.max(this.minZ, chunkMinZ);
                int maxZ = Math.min(this.maxZ, chunkMaxZ);

                for (int x = minX; x < maxX; x++)
                {
                    float xf = x;
                    for (int y = minY; y < maxY; y++)
                    {
                        float yf = y;
                        for (int z = minZ; z < maxZ; z++)
                        {
                            float zf = z;
                            float score = 0.0f;

                            for (Blob b : blobs)
                            {
                                float r = (b.w - getApproximateDistance(b.x - xf, b.y - yf, b.z - zf)) / b.w;

                                if (r > 0.0f)
                                {
                                    score += r;
                                }
                            }

                            if (score >= 0.25f)
                            {
                                int volume = currentType.getPlacementVolume(currentWorld, x, y, z, score - 0.25f);
                                if (volume > 0)
                                {
                                    GFManipulationAPI.addGasLevel(new BlockPos(x, y, z), currentWorld, currentType.gasType, volume);
                                }
                            }
                        }
                    }
                }
            }

            private static class Blob
            {
                public final float x, y, z, w;

                public Blob(float x, float y, float z, float w)
                {
                    this.x = x;
                    this.y = y;
                    this.z = z;
                    this.w = w;
                }
            }
        }
    }

    private static class TypeHandle
    {
        public final GasWorldGenType type;
        public final float averageBlobFrequency;
        public final float averagePocketRadius;
        public final float averageBlobRadius;
        public final float blobSpread;

        public TypeHandle(GasWorldGenType type)
        {
            float r3 = (float) (type.averageVolume * 3.0D / (4.0D * Math.PI));

            this.type = type;
            this.averageBlobFrequency = 1.0f + (1.0f - type.evenness) * 9.0f;
            this.averagePocketRadius = (float) Math.cbrt(r3);
            this.averageBlobRadius = 1.5f * (float) Math.cbrt(type.averageVolume / averageBlobFrequency);
            this.blobSpread = 2.0f + averagePocketRadius * (1.0f - type.evenness);
        }
    }
}
