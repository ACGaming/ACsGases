package net.trentv.gases.common.configuration;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import net.trentv.gases.Gases;

@Config(modid = Gases.MODID, name = "ACsGases")
public class GasesMainConfigurations
{
    @Config.Comment("World generation settings")
    @Config.Name("World Generation")
    public static final WorldGeneration WORLD_GENERATION = new WorldGeneration();

    public static class WorldGeneration
    {
        @Config.Comment("Overworld generation settings")
        @Config.Name("Overworld")
        public final Overworld OVERWORLD = new Overworld();

        public static class Overworld
        {
            @Config.Comment("Gas pocket generation frequencies (chance per 16×16×16 chunk section)")
            @Config.Name("Gases")
            public final Gases GASES = new Gases();

            public static class Gases
            {
                @Config.Comment("The frequency of natural gas pockets per 16x16x16 blocks")
                @Config.Name("Natural Gas")
                @Config.RangeDouble(min = 0.0, max = 10.0)
                public float naturalGas = 1.0f;

                @Config.Comment("The frequency of red gas pockets per 16x16x16 blocks")
                @Config.Name("Red Gas")
                @Config.RangeDouble(min = 0.0, max = 10.0)
                public float redGas = 0.5f;

                @Config.Comment("The frequency of nitrous gas pockets per 16x16x16 blocks")
                @Config.Name("Nitrous Gas")
                @Config.RangeDouble(min = 0.0, max = 10.0)
                public float nitrousGas = 0.5f;

                @Config.Comment("The frequency of chlorine gas pockets per 16x16x16 blocks")
                @Config.Name("Chlorine Gas")
                @Config.RangeDouble(min = 0.0, max = 10.0)
                public float chlorineGas = 0.25f;

                @Config.Comment("The frequency of iocalfaeus gas pockets per 16x16x16 blocks")
                @Config.Name("Iocalfaeus Gas")
                @Config.RangeDouble(min = 0.0, max = 10.0)
                public float iocalfaeusGas = 0.125f;

                @Config.Comment("The frequency of helium pockets per 16x16x16 blocks")
                @Config.Name("Helium")
                @Config.RangeDouble(min = 0.0, max = 10.0)
                public float helium = 0.5f;

                @Config.Comment("The frequency of black damp clouds per 16x16x16 blocks")
                @Config.Name("Black Damp")
                @Config.RangeDouble(min = 0.0, max = 10.0)
                public float blackDamp = 0.25f;
            }

            @Config.Comment("Underground gas pipe generation")
            @Config.Name("Gas Pipe")
            public final GasPipe GAS_PIPE = new GasPipe();

            public static class GasPipe
            {
                @Config.Comment("The amount of times pipes will be attempted generated underground")
                @Config.Name("Generation Checks")
                @Config.RangeInt(min = 0, max = 256)
                public int generationChecks = 32;
            }

            @Config.Comment("Cracked bedrock generation in the bedrock layer")
            @Config.Name("Cracked Bedrock")
            public final CrackedBedrock CRACKED_BEDROCK = new CrackedBedrock();

            public static class CrackedBedrock
            {
                @Config.Comment("The amount of times Cracked Bedrock will be attempted to generate in bedrock")
                @Config.Name("Generation Attempts")
                @Config.RangeInt(min = 0, max = 64)
                public int generationChecks = 8;
            }
        }

        @Config.Comment("Nether generation settings")
        @Config.Name("The Nether")
        public final Nether NETHER = new Nether();

        public static class Nether
        {
            @Config.Comment("Gas generation in the Nether")
            @Config.Name("Gases")
            public final Gases GASES = new Gases();

            public static class Gases
            {
                @Config.Comment("The frequency of electric gas clouds in the nether per 16x16x16 blocks")
                @Config.Name("Electric Gas")
                @Config.RangeDouble(min = 0.0, max = 10.0)
                public float electricGas = 0.125f;

                @Config.Comment("The frequency of corrosive gas clouds in the nether per 16x16x16 blocks")
                @Config.Name("Corrosive Gas")
                @Config.RangeDouble(min = 0.0, max = 10.0)
                public float corrosiveGas = 0.125f;
            }
        }

        @Config.Comment("The End generation settings")
        @Config.Name("The End")
        public final End END = new End();

        public static class End
        {
            @Config.Comment("Gas generation in The End")
            @Config.Name("Gases")
            public final Gases GASES = new Gases();

            public static class Gases
            {
                @Config.Comment("The frequency of finine gas clouds in the end per 16x16x16 blocks")
                @Config.Name("Finine")
                @Config.RangeDouble(min = 0.0, max = 10.0)
                public float finineGas = 0.03125f;
            }
        }

        @Config.Comment("Diabaline ore generation in gas pocket walls")
        @Config.Name("Diabaline Ore")
        public final DiabalineOre DIABALINE_ORE = new DiabalineOre();

        public static class DiabalineOre
        {
            @Config.Comment("The commonness of diabaline in the walls of gas pockets (probability)")
            @Config.Name("Commonness")
            @Config.RangeDouble(min = 0.0, max = 1.0)
            public float commonness = 0.0625f;
        }
    }

    @Config.Comment("General gas behavior and mechanics")
    @Config.Name("Gases")
    public static final Gases GASES = new Gases();

    public static class Gases
    {
        @Config.Comment("Smoke-related settings")
        @Config.Name("Smoke")
        public final Smoke SMOKE = new Smoke();

        public static class Smoke
        {
            @Config.Comment("The amount of smoke that will be generated by fire (0–16)")
            @Config.Name("Fire Smoke Amount")
            @Config.RangeInt(min = 0, max = 16)
            public int fireSmokeAmount = 8;
        }

        @Config.Comment("Void gas behavior")
        @Config.Name("Void Gas")
        public final VoidGas VOID_GAS = new VoidGas();

        public static class VoidGas
        {
            @Config.Comment("The maximal height at which void gas can appear")
            @Config.Name("Max Height")
            @Config.RangeInt(min = 0, max = 255)
            public int maxHeight = 64;
        }

        @Config.Comment("Corrosive gas settings")
        @Config.Name("Corrosive Gas")
        public final CorrosiveGas CORROSIVE_GAS = new CorrosiveGas();

        public static class CorrosiveGas
        {
            @Config.Comment("The block hardness threshold for block corrosion")
            @Config.Name("Corrosive Power")
            @Config.RangeDouble(min = 0.0, max = 50.0)
            public float corrosivePower = 2.0f;
        }

        @Config.Comment("Coal dust generation")
        @Config.Name("Coal Dust")
        public final CoalDust COAL_DUST = new CoalDust();

        public static class CoalDust
        {
            @Config.Comment("The amount of coal dust generated by breaking a coal-holding block (0–16)")
            @Config.Name("Amount on Mine")
            @Config.RangeInt(min = 0, max = 16)
            public int amountOnMine = 16;

            @Config.Comment("Blocks that can emit coal dust when mined")
            @Config.Name("Blocks")
            public String[] blocks = new String[] {"minecraft:coal_ore"};
        }

        @Config.Comment("Steam generation")
        @Config.Name("Steam")
        public final Steam STEAM = new Steam();

        public static class Steam
        {
            @Config.Comment("The amount of steam generated by reactions (0–16)")
            @Config.Name("Amount on Reaction")
            @Config.RangeInt(min = 0, max = 16)
            public int amountOnReaction = 16;
        }

        @Config.Comment("General dust generation")
        @Config.Name("Dust")
        public final Dust DUST = new Dust();

        public static class Dust
        {
            @Config.Comment("The amount of dust generated by breaking stone (0–16)")
            @Config.Name("Amount on Mine")
            @Config.RangeInt(min = 0, max = 16)
            public int amountOnMine = 3;
        }

        @Config.Comment("Chlorine gas effects")
        @Config.Name("Chlorine Gas")
        public final ChlorineGas CHLORINE_GAS = new ChlorineGas();

        public static class ChlorineGas
        {
            @Config.Comment("Tool/armor materials that can be destroyed by chlorine gas (one per line)")
            @Config.Name("Destroyable Materials")
            public String[] destroyableMaterials = new String[] {"IRON", "CHAIN"};
        }

        @Config.Comment("Finine gas behavior")
        @Config.Name("Finine")
        public final Finine FININE = new Finine();

        public static class Finine
        {
            @Config.Comment("Maximum searches Finine will conduct to find a suitable teleportation location")
            @Config.Name("Max Teleportation Searches")
            @Config.RangeInt(min = 1, max = 100)
            public int maxTeleportSearches = 20;
        }
    }

    @Config.Comment("Gas furnace recipe tweaks")
    @Config.Name("Gas Furnace Recipes")
    public static final GasFurnaceRecipes GAS_FURNACE_RECIPES = new GasFurnaceRecipes();

    public static class GasFurnaceRecipes
    {
        @Config.Comment("If true, a full stack of coal can be smelted into a diamond in a gas furnace")
        @Config.Name("Coal to Diamond")
        public boolean coalToDiamond = true;
    }

    @Mod.EventBusSubscriber(modid = "gases")
    public static class ConfigEventHandler
    {
        @SubscribeEvent
        public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event)
        {
            if (event.getModID().equals("gases"))
            {
                ConfigManager.sync("gases", Config.Type.INSTANCE);
            }
        }
    }
}
