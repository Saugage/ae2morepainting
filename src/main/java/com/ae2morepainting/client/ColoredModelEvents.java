package com.ae2morepainting.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

import com.ae2morepainting.Compat;
import com.ae2morepainting.Tags;

import appeng.api.util.AEColor;

@Mod.EventBusSubscriber(modid = Tags.MODID, value = Side.CLIENT)
public final class ColoredModelEvents {

    private static final String AE2_MODID = "appliedenergistics2";

    /** Block models (as {@code namespace:path}) whose quads get recolored. */
    private static final Set<String> COLORED_MODELS = new HashSet<>();

    /** Painted texture folders in this mod's assets, each mapped to the base textures it has variants for. */
    private static final List<TextureFolder> COLORED_TEXTURES = new ArrayList<>();

    static {
        for (final String model : new String[] { "controller", "interface", "fluid_interface", "io_port", "drive" }) {
            COLORED_MODELS.add(AE2_MODID + ":" + model);
        }

        COLORED_TEXTURES.add(new TextureFolder("controller", AE2_MODID, "controller", "controller_powered",
                "controller_column", "controller_column_powered"));
        COLORED_TEXTURES.add(new TextureFolder("interface", AE2_MODID, "interface", "interface_alternate",
                "interface_alternate_arrow"));
        COLORED_TEXTURES.add(new TextureFolder("fluid_interface", AE2_MODID, "fluid_interface"));
        COLORED_TEXTURES.add(new TextureFolder("io_port", AE2_MODID, "io_port", "io_port_off", "io_port_side",
                "io_port_side_off", "io_port_bottom"));
        COLORED_TEXTURES.add(new TextureFolder("drive", AE2_MODID, "drive_side", "drive_top", "drive_bottom",
                "drive_front", "drive_inside"));

        if (Compat.isAE2FCLoaded()) {
            COLORED_MODELS.add(Compat.AE2FC_MODID + ":dual_interface");
            COLORED_TEXTURES.add(new TextureFolder("dual_interface", Compat.AE2FC_MODID, "interface",
                    "interface_alternate", "interface_alternate_arrow"));
        }
    }

    /** Base sprite name to colored sprites, indexed by {@link AEColor#ordinal()}. */
    private static final Map<String, TextureAtlasSprite[]> COLORED_SPRITES = new HashMap<>();

    private ColoredModelEvents() {}

    @SubscribeEvent
    public static void onTextureStitch(final TextureStitchEvent.Pre event) {
        COLORED_SPRITES.clear();
        final IResourceManager resources = Minecraft.getMinecraft().getResourceManager();
        for (final TextureFolder folder : COLORED_TEXTURES) {
            for (final String base : folder.bases) {
                final TextureAtlasSprite[] sprites = new TextureAtlasSprite[AEColor.values().length];
                boolean any = false;
                for (final AEColor color : AEColor.VALID_COLORS) {
                    final String path = "blocks/" + folder.folder + "/" + base + "_"
                            + color.name().toLowerCase(Locale.ROOT);
                    if (exists(resources, new ResourceLocation(Tags.MODID, "textures/" + path + ".png"))) {
                        sprites[color.ordinal()] = event.getMap().registerSprite(new ResourceLocation(Tags.MODID, path));
                        any = true;
                    }
                }
                if (any) {
                    COLORED_SPRITES.put(folder.namespace + ":blocks/" + base, sprites);
                }
            }
        }
    }

    private static boolean exists(final IResourceManager resources, final ResourceLocation location) {
        try (IResource ignored = resources.getResource(location)) {
            return true;
        } catch (final IOException e) {
            return false;
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onModelBake(final ModelBakeEvent event) {
        final List<ModelResourceLocation> toWrap = new ArrayList<>();
        for (final ModelResourceLocation location : event.getModelRegistry().getKeys()) {
            if (COLORED_MODELS.contains(location.getNamespace() + ":" + location.getPath())
                    && !"inventory".equals(location.getVariant())) {
                toWrap.add(location);
            }
        }

        for (final ModelResourceLocation location : toWrap) {
            final IBakedModel model = event.getModelRegistry().getObject(location);
            if (model != null && !(model instanceof ColoredBlockModel)) {
                event.getModelRegistry().putObject(location, new ColoredBlockModel(model));
            }
        }
    }

    @Nullable
    static TextureAtlasSprite getColoredSprite(final TextureAtlasSprite base, final AEColor color) {
        final TextureAtlasSprite[] sprites = COLORED_SPRITES.get(base.getIconName());
        return sprites == null ? null : sprites[color.ordinal()];
    }

    private static final class TextureFolder {

        final String folder;
        final String namespace;
        final String[] bases;

        TextureFolder(final String folder, final String namespace, final String... bases) {
            this.folder = folder;
            this.namespace = namespace;
            this.bases = bases;
        }
    }
}
