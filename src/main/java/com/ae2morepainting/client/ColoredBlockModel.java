package com.ae2morepainting.client;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BakedQuadRetextured;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.property.IExtendedBlockState;

import org.apache.commons.lang3.tuple.Pair;

import com.ae2morepainting.ColorProperty;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import appeng.api.util.AEColor;

/**
 * Wraps an AE2 block model and swaps every sprite that has a painted variant. Quads without one (lights, cells,
 * overlays) are passed through unchanged.
 */
public class ColoredBlockModel implements IBakedModel {

    private final IBakedModel base;
    /** Keyed by quad identity, since the wrapped models return different quads per rotation and drive cell state. */
    private final Cache<BakedQuad, BakedQuad[]> recolored = CacheBuilder.newBuilder().weakKeys().build();

    public ColoredBlockModel(final IBakedModel base) {
        this.base = base;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable final IBlockState state, @Nullable final EnumFacing side,
            final long rand) {
        final List<BakedQuad> quads = this.base.getQuads(state, side, rand);
        if (!(state instanceof IExtendedBlockState)) {
            return quads;
        }

        final AEColor color = ((IExtendedBlockState) state).getValue(ColorProperty.INSTANCE);
        if (color == null || color == AEColor.TRANSPARENT) {
            return quads;
        }

        final List<BakedQuad> result = new ArrayList<>(quads.size());
        for (final BakedQuad quad : quads) {
            result.add(this.recolor(quad, color));
        }
        return result;
    }

    private BakedQuad recolor(final BakedQuad quad, final AEColor color) {
        final TextureAtlasSprite colored = ColoredModelEvents.getColoredSprite(quad.getSprite(), color);
        if (colored == null) {
            return quad;
        }

        final BakedQuad[] variants;
        try {
            variants = this.recolored.get(quad, () -> new BakedQuad[AEColor.values().length]);
        } catch (final ExecutionException e) {
            throw new IllegalStateException(e);
        }

        BakedQuad variant = variants[color.ordinal()];
        if (variant == null) {
            variant = new BakedQuadRetextured(quad, colored);
            variants[color.ordinal()] = variant;
        }
        return variant;
    }

    @Override
    public boolean isAmbientOcclusion() {
        return this.base.isAmbientOcclusion();
    }

    @Override
    public boolean isAmbientOcclusion(final IBlockState state) {
        return this.base.isAmbientOcclusion(state);
    }

    @Override
    public boolean isGui3d() {
        return this.base.isGui3d();
    }

    @Override
    public boolean isBuiltInRenderer() {
        return this.base.isBuiltInRenderer();
    }

    @Override
    public TextureAtlasSprite getParticleTexture() {
        return this.base.getParticleTexture();
    }

    @Override
    @SuppressWarnings("deprecation")
    public ItemCameraTransforms getItemCameraTransforms() {
        return this.base.getItemCameraTransforms();
    }

    @Override
    public ItemOverrideList getOverrides() {
        return this.base.getOverrides();
    }

    @Override
    public Pair<? extends IBakedModel, Matrix4f> handlePerspective(
            final ItemCameraTransforms.TransformType cameraTransformType) {
        final Pair<? extends IBakedModel, Matrix4f> pair = this.base.handlePerspective(cameraTransformType);
        return pair.getLeft() == this.base ? Pair.of(this, pair.getRight()) : pair;
    }
}
