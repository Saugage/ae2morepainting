package com.ae2morepainting;

import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;

import zone.rong.mixinbooter.ILateMixinLoader;

@SuppressWarnings("unused")
public class LateMixinLoader implements ILateMixinLoader {

    private static final String AE2FC_CONFIG = "mixins." + Tags.MODID + ".ae2fc.json";
    private static final String AE2QOLEXTRAS_CONFIG = "mixins." + Tags.MODID + ".ae2qolextras.json";
    private static final String CRAZYAE_CONFIG = "mixins." + Tags.MODID + ".crazyae.json";

    @Override
    public List<String> getMixinConfigs() {
        return Arrays.asList("mixins." + Tags.MODID + ".json", AE2FC_CONFIG, AE2QOLEXTRAS_CONFIG, CRAZYAE_CONFIG);
    }

    @Override
    public boolean shouldMixinConfigQueue(final String mixinConfig) {
        if (AE2FC_CONFIG.equals(mixinConfig)) {
            return shouldQueue(mixinConfig, "AE2FC", Compat.isAE2FCLoaded(), Compat.isAE2FCEnabled());
        }
        if (AE2QOLEXTRAS_CONFIG.equals(mixinConfig)) {
            return shouldQueue(mixinConfig, "AE2UEL QOL Extras", Compat.isAE2QoLExtrasLoaded(),
                    Compat.isAE2QoLExtrasEnabled());
        }
        if (CRAZYAE_CONFIG.equals(mixinConfig)) {
            return shouldQueue(mixinConfig, "CrazyAE", Compat.isCrazyAELoaded(), Compat.isCrazyAEEnabled());
        }
        return true;
    }

    private static boolean shouldQueue(final String mixinConfig, final String name, final boolean loaded,
            final boolean enabled) {
        if (loaded && !enabled) {
            LogManager.getLogger(Tags.MODID).info("{} integration disabled by config; skipping {}", name, mixinConfig);
        }
        return enabled;
    }
}
