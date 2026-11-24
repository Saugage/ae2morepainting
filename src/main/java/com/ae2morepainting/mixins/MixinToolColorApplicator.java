package com.ae2morepainting.mixins;

import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.ae2morepainting.ModConfig;

import appeng.items.tools.powered.ToolColorApplicator;

@Mixin(value = ToolColorApplicator.class, remap = false)
public abstract class MixinToolColorApplicator {

    @Inject(method = "getBytes", at = @At("HEAD"), cancellable = true)
    private void ae2morepainting$getBytes(ItemStack cellItem, CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(ModConfig.getColorApplicatorBytes());
    }
}
