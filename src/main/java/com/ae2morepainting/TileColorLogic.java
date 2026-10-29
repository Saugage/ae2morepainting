package com.ae2morepainting;

import net.minecraft.nbt.NBTTagCompound;

import appeng.api.networking.IGridNode;
import appeng.api.util.AEColor;
import appeng.me.helpers.IGridProxyable;
import appeng.tile.AEBaseTile;
import appeng.util.Platform;

public final class TileColorLogic {

    private static final String NBT_KEY = "paintedColor";

    private TileColorLogic() {}

    public static AEColor fromOrdinal(final int ordinal) {
        final AEColor[] values = AEColor.values();
        return ordinal >= 0 && ordinal < values.length ? values[ordinal] : AEColor.TRANSPARENT;
    }

    public static AEColor readNBT(final NBTTagCompound data) {
        return data.hasKey(NBT_KEY) ? fromOrdinal(data.getByte(NBT_KEY)) : null;
    }

    public static void writeNBT(final NBTTagCompound data, final AEColor color) {
        data.setByte(NBT_KEY, (byte) color.ordinal());
    }

    public static <T extends AEBaseTile & IGridProxyable> void applyColor(final T tile, final AEColor color) {
        tile.getProxy().setColor(color);
        final IGridNode node = tile.getProxy().getNode();
        if (node != null) {
            node.updateState();
        }
        Platform.notifyBlocksOfNeighbors(tile.getWorld(), tile.getPos());
        tile.saveChanges();
        tile.markForUpdate();
    }
}
