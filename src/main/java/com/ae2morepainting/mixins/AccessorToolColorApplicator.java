package com.ae2morepainting.mixins;

import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import appeng.api.util.AEColor;
import appeng.items.tools.powered.ToolColorApplicator;

@Mixin(value = ToolColorApplicator.class, remap = false)
public interface AccessorToolColorApplicator {

    @Invoker("getColorFromItem")
    AEColor ae2morepainting$getColorFromItem(ItemStack paintBall);
}
