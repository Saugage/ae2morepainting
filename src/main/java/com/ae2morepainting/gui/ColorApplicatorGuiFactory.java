package com.ae2morepainting.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.jetbrains.annotations.NotNull;

import com.ae2morepainting.Tags;
import com.cleanroommc.modularui.api.IGuiHolder;
import com.cleanroommc.modularui.factory.AbstractUIFactory;
import com.cleanroommc.modularui.factory.GuiManager;
import com.cleanroommc.modularui.factory.PlayerInventoryGuiData;
import com.cleanroommc.modularui.factory.inventory.InventoryType;
import com.cleanroommc.modularui.factory.inventory.InventoryTypes;
import com.cleanroommc.modularui.utils.Platform;

import appeng.items.tools.powered.ToolColorApplicator;

/**
 * Opens {@link ColorApplicatorGui} for a Color Applicator in the player's inventory. AE2's item doesn't implement
 * {@link IGuiHolder}, so ModularUI's own player inventory factory can't be used.
 */
public final class ColorApplicatorGuiFactory extends AbstractUIFactory<PlayerInventoryGuiData> {

    // ModularUI limits factory names to 32 characters.
    public static final ColorApplicatorGuiFactory INSTANCE = new ColorApplicatorGuiFactory();

    private ColorApplicatorGuiFactory() {
        super(Tags.MODID + ":color_applicator");
    }

    public static void register() {
        GuiManager.registerFactory(INSTANCE);
    }

    @SideOnly(Side.CLIENT)
    public void openFromHandClient(final EnumHand hand) {
        final EntityPlayer player = Platform.getClientPlayer();
        final int slot = hand == EnumHand.OFF_HAND ? 40 : player.inventory.currentItem;
        GuiManager.openFromClient(this, new PlayerInventoryGuiData(player, InventoryTypes.PLAYER, slot));
    }

    @Override
    public @NotNull IGuiHolder<PlayerInventoryGuiData> getGuiHolder(final PlayerInventoryGuiData data) {
        return ColorApplicatorGui.INSTANCE;
    }

    @Override
    public boolean canInteractWith(final EntityPlayer player, final PlayerInventoryGuiData guiData) {
        return player == guiData.getPlayer() && guiData.getUsedItemStack().getItem() instanceof ToolColorApplicator;
    }

    @Override
    public void writeGuiData(final PlayerInventoryGuiData guiData, final PacketBuffer buffer) {
        guiData.getInventoryType().write(buffer);
        buffer.writeVarInt(guiData.getSlotIndex());
    }

    @Override
    public @NotNull PlayerInventoryGuiData readGuiData(final EntityPlayer player, final PacketBuffer buffer) {
        return new PlayerInventoryGuiData(player, InventoryType.read(buffer), buffer.readVarInt());
    }
}
