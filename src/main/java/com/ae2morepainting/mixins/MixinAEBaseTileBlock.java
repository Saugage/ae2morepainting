package com.ae2morepainting.mixins;

import net.minecraft.block.Block;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.ae2morepainting.ColorableBlocks;

import appeng.block.AEBaseTileBlock;

@Mixin(AEBaseTileBlock.class)
public abstract class MixinAEBaseTileBlock {

    @Inject(method = "createBlockState", at = @At("RETURN"), cancellable = true)
    private void ae2morepainting$addColorProperty(final CallbackInfoReturnable<BlockStateContainer> cir) {
        final Block block = (Block) (Object) this;
        if (ColorableBlocks.isColorable(block)) {
            cir.setReturnValue(ColorableBlocks.withColor(block, cir.getReturnValue()));
        }
    }

    @Inject(method = "getExtendedState", at = @At("RETURN"), cancellable = true, remap = false)
    private void ae2morepainting$addColor(final IBlockState state, final IBlockAccess world, final BlockPos pos,
            final CallbackInfoReturnable<IBlockState> cir) {
        cir.setReturnValue(ColorableBlocks.addColor(cir.getReturnValue(), world, pos));
    }
}
