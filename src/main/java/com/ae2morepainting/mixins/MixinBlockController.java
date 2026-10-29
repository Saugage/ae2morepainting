package com.ae2morepainting.mixins;

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

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.ae2morepainting.ColorProperty;

import appeng.api.implementations.tiles.IColorableTile;
import appeng.api.util.AEColor;
import appeng.block.networking.BlockController;
import appeng.block.networking.BlockController.ControllerRenderType;
import appeng.tile.networking.TileController;

@Mixin(BlockController.class)
public abstract class MixinBlockController {

    @Inject(method = "createBlockState", at = @At("HEAD"), cancellable = true)
    private void ae2morepainting$addColorProperty(final CallbackInfoReturnable<BlockStateContainer> cir) {
        cir.setReturnValue(new ExtendedBlockState((Block) (Object) this,
                new IProperty[] { BlockController.CONTROLLER_STATE, BlockController.CONTROLLER_TYPE },
                new IUnlistedProperty[] { ColorProperty.INSTANCE }));
    }

    @Inject(method = "getExtendedState", at = @At("HEAD"), cancellable = true, remap = false)
    private void ae2morepainting$addColor(final IBlockState state, final IBlockAccess world, final BlockPos pos,
            final CallbackInfoReturnable<IBlockState> cir) {
        if (state instanceof IExtendedBlockState) {
            final AEColor color = ae2morepainting$getControllerColor(world, pos);
            if (color != null) {
                cir.setReturnValue(((IExtendedBlockState) state).withProperty(ColorProperty.INSTANCE, color));
            }
        }
    }

    @Inject(method = "getActualState", at = @At("HEAD"), cancellable = true)
    private void ae2morepainting$colorAwareActualState(final IBlockState state, final IBlockAccess world,
            final BlockPos pos, final CallbackInfoReturnable<IBlockState> cir) {
        final AEColor color = ae2morepainting$getControllerColor(world, pos);
        if (color == null) {
            return;
        }

        final boolean xx = ae2morepainting$isConnected(world, pos.west(), color)
                && ae2morepainting$isConnected(world, pos.east(), color);
        final boolean yy = ae2morepainting$isConnected(world, pos.down(), color)
                && ae2morepainting$isConnected(world, pos.up(), color);
        final boolean zz = ae2morepainting$isConnected(world, pos.north(), color)
                && ae2morepainting$isConnected(world, pos.south(), color);

        ControllerRenderType type = ControllerRenderType.block;
        if (xx && !yy && !zz) {
            type = ControllerRenderType.column_x;
        } else if (!xx && yy && !zz) {
            type = ControllerRenderType.column_y;
        } else if (!xx && !yy && zz) {
            type = ControllerRenderType.column_z;
        } else if ((xx ? 1 : 0) + (yy ? 1 : 0) + (zz ? 1 : 0) >= 2) {
            final int v = (Math.abs(pos.getX()) + Math.abs(pos.getY()) + Math.abs(pos.getZ())) % 2;
            type = v == 0 ? ControllerRenderType.inside_a : ControllerRenderType.inside_b;
        }

        cir.setReturnValue(state.withProperty(BlockController.CONTROLLER_TYPE, type));
    }

    @Unique
    private static AEColor ae2morepainting$getControllerColor(final IBlockAccess world, final BlockPos pos) {
        final TileEntity te = world.getTileEntity(pos);
        return te instanceof TileController && te instanceof IColorableTile ? ((IColorableTile) te).getColor() : null;
    }

    @Unique
    private static boolean ae2morepainting$isConnected(final IBlockAccess world, final BlockPos pos,
            final AEColor color) {
        if (pos.getY() < 0) {
            return false;
        }
        final AEColor other = ae2morepainting$getControllerColor(world, pos);
        return other != null && other.matches(color);
    }
}
