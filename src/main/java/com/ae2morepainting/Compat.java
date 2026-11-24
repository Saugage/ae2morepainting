package com.ae2morepainting;

import net.minecraftforge.fml.common.Loader;

/** Optional mod integrations. */
public final class Compat {

    public static final String AE2FC_MODID = "ae2fc";
    public static final String AE2QOLEXTRAS_MODID = "ae2qolextras";
    public static final String CRAZYAE_MODID = "crazyae";

    private Compat() {}

    public static boolean isAE2FCLoaded() {
        return Loader.isModLoaded(AE2FC_MODID);
    }

    public static boolean isAE2QoLExtrasLoaded() {
        return Loader.isModLoaded(AE2QOLEXTRAS_MODID);
    }

    public static boolean isCrazyAELoaded() {
        return Loader.isModLoaded(CRAZYAE_MODID);
    }

    public static boolean isAE2FCEnabled() {
        return isAE2FCLoaded() && ModConfig.isAE2FCIntegrationEnabled();
    }

    public static boolean isAE2QoLExtrasEnabled() {
        return isAE2QoLExtrasLoaded() && ModConfig.isAE2QoLExtrasIntegrationEnabled();
    }

    public static boolean isCrazyAEEnabled() {
        return isCrazyAELoaded() && ModConfig.isCrazyAEImprovedDriveEnabled();
    }
}
