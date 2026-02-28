package net.trentv.gases.common.block;

import net.minecraft.block.BlockOre;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import net.trentv.gases.common.GasesObjects;
import net.trentv.gasesframework.api.GFManipulationAPI;
import net.trentv.gasesframework.api.GasType;
import net.trentv.gasesframework.api.GasesFrameworkAPI;

/**
 * Diabaline ore block. Must be refined into refined diabaline in a gas furnace.
 */
public class BlockDiabalineOre extends BlockOre
{
    /**
     * Current glowing status. Determines texture.
     */
    protected boolean glowing;

    /**
     * Creates a new Diabaline Ore block.
     *
     * @param glowing Sets the block to glowing if true or non-glowing if false.
     */
    public BlockDiabalineOre(boolean glowing, ResourceLocation registry)
    {
        super();
        this.glowing = glowing;
        if (glowing) setLightLevel(4.0F / 16.0F);
        setHardness(1.5F);
        setResistance(10.0F);
        setRegistryName(registry);
        setTranslationKey(registry.getPath());
    }

    @Override
    public void onNeighborChange(IBlockAccess world, BlockPos pos, BlockPos neighbor)
    {
        if (world instanceof World)
        {
            onBlockAdded((World) world, pos, world.getBlockState(pos));
        }
    }

    @Override
    public void onBlockAdded(World world, BlockPos pos, IBlockState state)
    {
        boolean isSurroundedByGas = false;

        for (int side = 0; side < 6; side++)
        {
            int xDirection = pos.getX() + (side == 4 ? 1 : (side == 5 ? -1 : 0));
            int yDirection = pos.getY() + (side == 0 ? 1 : (side == 1 ? -1 : 0));
            int zDirection = pos.getZ() + (side == 2 ? 1 : (side == 3 ? -1 : 0));

            GasType type = GFManipulationAPI.getGasType(new BlockPos(xDirection, yDirection, zDirection), world);
            if (type != null && type != GasesFrameworkAPI.AIR)
            {
                isSurroundedByGas = true;
                break;
            }
        }

        if (isSurroundedByGas & !glowing)
        {
            world.setBlockState(pos, GasesObjects.DIABALINE_ORE_GLOWING.getDefaultState());
        }
        else if (!isSurroundedByGas & glowing)
        {
            world.setBlockState(pos, GasesObjects.DIABALINE_ORE.getDefaultState());
        }
    }
}
