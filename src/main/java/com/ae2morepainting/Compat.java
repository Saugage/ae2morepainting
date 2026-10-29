package com.ae2morepainting;

import net.minecraftforge.fml.common.Loader;

/** Optional mod integrations. */
public final class Compat {

    public static final String AE2FC_MODID = "ae2fc";

    private Compat() {}

    public static boolean isAE2FCLoaded() {
        return Loader.isModLoaded(AE2FC_MODID);
    }
}
