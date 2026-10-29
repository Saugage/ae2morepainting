package com.ae2morepainting.mixins.ae2fc;

import net.minecraft.block.Block;
import net.minecraft.block.state.BlockStateContainer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.ae2morepainting.ColorableBlocks;
import com.ae2morepainting.IColorableBlock;
import com.glodblock.github.common.block.BlockDualInterface;

import appeng.block.AEBaseTileBlock;

@Mixin(BlockDualInterface.class)
public abstract class MixinBlockDualInterface implements IColorableBlock {

    @Inject(method = "createBlockState", at = @At("RETURN"), cancellable = true)
    private void ae2morepainting$addColorProperty(final CallbackInfoReturnable<BlockStateContainer> cir) {
        cir.setReturnValue(ColorableBlocks.withColor((Block) (Object) this, cir.getReturnValue(),
                AEBaseTileBlock.FORWARD, AEBaseTileBlock.UP));
    }
}
