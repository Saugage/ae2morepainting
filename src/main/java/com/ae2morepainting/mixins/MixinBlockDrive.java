package com.ae2morepainting.mixins;

import net.minecraft.block.Block;
import net.minecraft.block.state.BlockStateContainer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.ae2morepainting.ColorableBlocks;

import appeng.block.storage.BlockDrive;

@Mixin(BlockDrive.class)
public abstract class MixinBlockDrive {

    @Inject(method = "createBlockState", at = @At("RETURN"), cancellable = true)
    private void ae2morepainting$addColorProperty(final CallbackInfoReturnable<BlockStateContainer> cir) {
        cir.setReturnValue(ColorableBlocks.withColor((Block) (Object) this, cir.getReturnValue()));
    }
}
