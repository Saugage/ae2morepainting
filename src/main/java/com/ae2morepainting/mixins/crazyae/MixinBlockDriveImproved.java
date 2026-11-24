package com.ae2morepainting.mixins.crazyae;

import net.minecraft.block.Block;
import net.minecraft.block.state.BlockStateContainer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.ae2morepainting.ColorableBlocks;
import com.ae2morepainting.IColorableBlock;

import dev.beecube31.crazyae2.common.blocks.storage.BlockDriveImproved;

@Mixin(BlockDriveImproved.class)
public abstract class MixinBlockDriveImproved implements IColorableBlock {

    @Inject(method = "createBlockState", at = @At("RETURN"), cancellable = true)
    private void ae2morepainting$addColorProperty(final CallbackInfoReturnable<BlockStateContainer> cir) {
        cir.setReturnValue(ColorableBlocks.withColor((Block) (Object) this, cir.getReturnValue()));
    }
}
