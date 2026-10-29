package com.ae2morepainting;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;

import appeng.api.implementations.tiles.IColorableTile;
import appeng.block.AEBaseTileBlock;
import appeng.block.misc.BlockInterface;
import appeng.block.storage.BlockDrive;
import appeng.block.storage.BlockIOPort;
import appeng.fluids.block.BlockFluidInterface;

/** Block-state helpers for the AE2 machines that get a paintable color. */
public final class ColorableBlocks {

    private ColorableBlocks() {}

    public static boolean isColorable(final Block block) {
        return block instanceof BlockDrive || block instanceof BlockInterface || block instanceof BlockFluidInterface
                || block instanceof BlockIOPort || block instanceof IColorableBlock;
    }

    /**
     * @param extra additional unlisted properties to declare, for blocks whose container lacks ones that
     *              {@link AEBaseTileBlock#getExtendedState} sets once the state becomes extended
     */
    public static BlockStateContainer withColor(final Block block, final BlockStateContainer container,
            final IUnlistedProperty<?>... extra) {
        final List<IUnlistedProperty<?>> unlisted = new ArrayList<>();
        if (container instanceof ExtendedBlockState) {
            unlisted.addAll(((ExtendedBlockState) container).getUnlistedProperties());
        }
        if (unlisted.contains(ColorProperty.INSTANCE)) {
            return container;
        }
        for (final IUnlistedProperty<?> property : extra) {
            if (!unlisted.contains(property)) {
                unlisted.add(property);
            }
        }
        unlisted.add(ColorProperty.INSTANCE);
        return new ExtendedBlockState(block, container.getProperties().toArray(new IProperty<?>[0]),
                unlisted.toArray(new IUnlistedProperty<?>[0]));
    }

    public static IBlockState addColor(final IBlockState state, final IBlockAccess world, final BlockPos pos) {
        if (!(state instanceof IExtendedBlockState)) {
            return state;
        }
        final IExtendedBlockState extendedState = (IExtendedBlockState) state;
        if (!extendedState.getUnlistedProperties().containsKey(ColorProperty.INSTANCE)) {
            return state;
        }
        final TileEntity te = world.getTileEntity(pos);
        if (!(te instanceof IColorableTile)) {
            return state;
        }
        return extendedState.withProperty(ColorProperty.INSTANCE, ((IColorableTile) te).getColor());
    }
}
