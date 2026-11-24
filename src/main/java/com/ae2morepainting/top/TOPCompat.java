package com.ae2morepainting.top;

import javax.annotation.Nullable;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import com.ae2morepainting.ColorableBlocks;
import com.ae2morepainting.Tags;
import com.google.common.base.Function;

import appeng.api.implementations.tiles.IColorableTile;
import appeng.api.util.AEColor;
import appeng.tile.networking.TileController;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.IProbeInfoProvider;
import mcjty.theoneprobe.api.ITheOneProbe;
import mcjty.theoneprobe.api.ProbeMode;

/** Shows the painted color of this mod's colorable components in The One Probe. */
public class TOPCompat implements IProbeInfoProvider {

    public static class GetTheOneProbe implements Function<ITheOneProbe, Void> {

        @Nullable
        @Override
        public Void apply(@Nullable final ITheOneProbe probe) {
            if (probe != null) {
                probe.registerProvider(new TOPCompat());
            }
            return null;
        }
    }

    @Override
    public String getID() {
        return Tags.MODID + ":color";
    }

    @Override
    public void addProbeInfo(final ProbeMode mode, final IProbeInfo probeInfo, final EntityPlayer player,
            final World world, final IBlockState blockState, final IProbeHitData data) {
        final TileEntity te = world.getTileEntity(data.getPos());
        if (!(te instanceof IColorableTile)) {
            return;
        }
        if (!(te instanceof TileController) && !ColorableBlocks.isColorable(blockState.getBlock())) {
            return;
        }
        final AEColor color = ((IColorableTile) te).getColor();
        if (color != AEColor.TRANSPARENT) {
            probeInfo.text(getTextColor(color) + "{*" + color.unlocalizedName + "*}");
        }
    }

    private static TextFormatting getTextColor(final AEColor color) {
        switch (color) {
            case WHITE:
                return TextFormatting.WHITE;
            case ORANGE:
                return TextFormatting.GOLD;
            case MAGENTA:
                return TextFormatting.LIGHT_PURPLE;
            case LIGHT_BLUE:
                return TextFormatting.AQUA;
            case YELLOW:
                return TextFormatting.YELLOW;
            case LIME:
                return TextFormatting.GREEN;
            case PINK:
                return TextFormatting.RED;
            case GRAY:
                return TextFormatting.DARK_GRAY;
            case LIGHT_GRAY:
                return TextFormatting.GRAY;
            case CYAN:
                return TextFormatting.DARK_AQUA;
            case PURPLE:
                return TextFormatting.DARK_PURPLE;
            case BLUE:
                return TextFormatting.BLUE;
            case BROWN:
            case RED:
                return TextFormatting.DARK_RED;
            case GREEN:
                return TextFormatting.DARK_GREEN;
            case BLACK:
                return TextFormatting.BLACK;
            default:
                return TextFormatting.WHITE;
        }
    }
}
