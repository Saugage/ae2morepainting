package com.ae2morepainting.mixins.crazyae;

import org.spongepowered.asm.mixin.Mixin;

import com.ae2morepainting.IColorableBlock;

import dev.beecube31.crazyae2.common.blocks.networking.BlockIOPortImp;

/** The color property itself is added by {@code MixinAEBaseTileBlock}, which only checks for this interface. */
@Mixin(BlockIOPortImp.class)
public abstract class MixinBlockIOPortImp implements IColorableBlock {}
