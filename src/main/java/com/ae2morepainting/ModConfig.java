package com.ae2morepainting;

import java.io.File;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Loader;

/**
 * Read directly through {@link Configuration} instead of {@code @Config}, because the values are needed while late
 * mixins are queued, before Forge syncs annotation-based configs.
 */
public final class ModConfig {

    private static final String CATEGORY_GENERAL = "general";
    private static final String CATEGORY_INTEGRATIONS = "integrations";

    private static boolean loaded;
    private static int colorApplicatorBytes = 512;
    private static boolean ae2fcIntegration = true;
    private static boolean ae2qolextrasIntegration = true;
    private static boolean crazyaeImprovedDrive = true;
    private static boolean crazyaeImprovedIOPort = true;
    private static boolean topIntegration = true;

    private ModConfig() {}

    public static int getColorApplicatorBytes() {
        load();
        return colorApplicatorBytes;
    }

    public static boolean isAE2FCIntegrationEnabled() {
        load();
        return ae2fcIntegration;
    }

    public static boolean isAE2QoLExtrasIntegrationEnabled() {
        load();
        return ae2qolextrasIntegration;
    }

    public static boolean isCrazyAEImprovedDriveEnabled() {
        load();
        return crazyaeImprovedDrive;
    }

    public static boolean isCrazyAEImprovedIOPortEnabled() {
        load();
        return crazyaeImprovedIOPort;
    }

    public static boolean isTOPIntegrationEnabled() {
        load();
        return topIntegration;
    }

    private static synchronized void load() {
        if (loaded) {
            return;
        }
        loaded = true;

        final Configuration config = new Configuration(new File(Loader.instance().getConfigDir(), Tags.MODID + ".cfg"));
        config.load();
        config.setCategoryComment(CATEGORY_GENERAL, "General settings.");

        colorApplicatorBytes = config.getInt("colorApplicatorBytes", CATEGORY_GENERAL, 512, 8, Integer.MAX_VALUE,
                "Total byte capacity of the AE2 Color Applicator's internal storage.");

        config.setCategoryComment(CATEGORY_INTEGRATIONS,
                "Toggles for optional mod integrations.");
        config.setCategoryRequiresMcRestart(CATEGORY_INTEGRATIONS, true);

        ae2fcIntegration = config.getBoolean("enableAE2FCIntegration", CATEGORY_INTEGRATIONS, true,
                "Enable the ae2fc integration (dual interface).");
        ae2qolextrasIntegration = config.getBoolean("enableAE2QoLExtrasIntegration", CATEGORY_INTEGRATIONS, true,
                "Enable ae2qolextras integration (spatial link chamber).");
        crazyaeImprovedDrive = config.getBoolean("enableCrazyAEImprovedDrive", CATEGORY_INTEGRATIONS, true,
                "Enable CrazyAE integration (improved drive).");
        crazyaeImprovedIOPort = config.getBoolean("enableCrazyAEImprovedIOPort", CATEGORY_INTEGRATIONS, true,
                "Enable CrazyAE integration (improved IO port).");
        topIntegration = config.getBoolean("enableTheOneProbeIntegration", CATEGORY_INTEGRATIONS, true,
                "Enable The One Probe integration (shows component color).");

        if (config.hasChanged()) {
            config.save();
        }
    }
}
