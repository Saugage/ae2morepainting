package com.ae2morepainting;

import java.util.Arrays;
import java.util.List;

import net.minecraftforge.fml.common.Loader;

import zone.rong.mixinbooter.ILateMixinLoader;

@SuppressWarnings("unused")
public class LateMixinLoader implements ILateMixinLoader {

    private static final String AE2FC_CONFIG = "mixins." + Tags.MODID + ".ae2fc.json";

    @Override
    public List<String> getMixinConfigs() {
        return Arrays.asList("mixins." + Tags.MODID + ".json", AE2FC_CONFIG);
    }

    @Override
    public boolean shouldMixinConfigQueue(final String mixinConfig) {
        return !AE2FC_CONFIG.equals(mixinConfig) || Loader.isModLoaded(Compat.AE2FC_MODID);
    }
}
