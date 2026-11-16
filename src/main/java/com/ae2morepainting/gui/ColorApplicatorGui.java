package com.ae2morepainting.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.ae2morepainting.Tags;
import com.ae2morepainting.mixins.AccessorToolColorApplicator;
import com.cleanroommc.modularui.api.IGuiHolder;
import com.cleanroommc.modularui.api.drawable.IDrawable;
import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.drawable.GuiDraw;
import com.cleanroommc.modularui.drawable.GuiTextures;
import com.cleanroommc.modularui.factory.PlayerInventoryGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.ModularScreen;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.value.sync.IntSyncValue;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widgets.ButtonWidget;
import com.cleanroommc.modularui.widgets.layout.Flow;

import appeng.api.AEApi;
import appeng.api.storage.IMEInventory;
import appeng.api.storage.channels.IItemStorageChannel;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.util.AEColor;
import appeng.items.tools.powered.ToolColorApplicator;

/**
 * "Select a color" screen for the Color Applicator. Slot {@code i} is {@code AEColor.values()[i]}: the 16 dye colors
 * followed by {@link AEColor#TRANSPARENT} (Fluix), which the applicator paints with snowballs.
 */
public final class ColorApplicatorGui implements IGuiHolder<PlayerInventoryGuiData> {

    public static final ColorApplicatorGui INSTANCE = new ColorApplicatorGui();

    private static final AEColor[] COLORS = AEColor.values();
    private static final int COLUMNS = 9;
    private static final int BUTTON_SIZE = 20;
    private static final int SELECTED_COLOR = 0xFF55FF55;
    private static final int COUNT_COLOR = 0xFFFFFF;
    private static final int EMPTY_COUNT_COLOR = 0xA0A0A0;

    private ColorApplicatorGui() {}

    @Override
    @SideOnly(Side.CLIENT)
    public ModularScreen createScreen(final PlayerInventoryGuiData data, final ModularPanel mainPanel) {
        return new ModularScreen(Tags.MODID, mainPanel);
    }

    @Override
    public ModularPanel buildUI(final PlayerInventoryGuiData data, final PanelSyncManager syncManager,
            final UISettings settings) {
        final IntSyncValue[] counts = new IntSyncValue[COLORS.length];
        for (int i = 0; i < COLORS.length; i++) {
            final int index = i;
            counts[i] = new IntSyncValue(() -> countStored(data.getUsedItemStack())[index]);
            syncManager.syncValue("count", i, counts[i]);
        }
        final IntSyncValue selected = new IntSyncValue(
                () -> getSelectedIndex(data.getUsedItemStack()),
                index -> selectColor(data.getUsedItemStack(), index));
        syncManager.syncValue("selected", selected);

        final Flow rows = Flow.col().coverChildren().childPadding(2);
        Flow row = null;
        for (int i = 0; i < COLORS.length; i++) {
            if (i % COLUMNS == 0) {
                row = Flow.row().coverChildren().childPadding(2);
                rows.child(row);
            }
            row.child(createColorButton(i, counts[i], selected));
        }

        return new ModularPanel("color_applicator")
                .coverChildren()
                .padding(7)
                .child(Flow.col()
                        .coverChildren()
                        .childPadding(5)
                        .child(IKey.lang("ae2morepainting.gui.color_applicator.title").asWidget())
                        .child(rows));
    }

    private static ButtonWidget<?> createColorButton(final int index, final IntSyncValue count,
            final IntSyncValue selected) {
        final AEColor color = COLORS[index];
        final ItemStack icon = color == AEColor.TRANSPARENT
                ? new ItemStack(Items.SNOWBALL)
                : AEApi.instance().definitions().items().coloredPaintBall().stack(color, 1);
        final IDrawable slot = (context, x, y, width, height, widgetTheme) -> drawSlot(icon,
                count.getIntValue(), selected.getIntValue() == index, x, y, width, height);
        final IDrawable background = (context, x, y, width, height, widgetTheme) ->
                (count.getIntValue() > 0 ? GuiTextures.MC_BUTTON : GuiTextures.MC_BUTTON_DISABLED)
                        .draw(context, x, y, width, height, widgetTheme);
        final IDrawable hoverBackground = (context, x, y, width, height, widgetTheme) ->
                (count.getIntValue() > 0 ? GuiTextures.MC_BUTTON_HOVERED : GuiTextures.MC_BUTTON_DISABLED)
                        .draw(context, x, y, width, height, widgetTheme);

        return new ButtonWidget<>()
                .size(BUTTON_SIZE)
                .background(background)
                .hoverBackground(hoverBackground)
                .overlay(slot)
                .tooltipDynamic(tooltip -> tooltip
                        .addLine(IKey.lang(color.unlocalizedName))
                        .addLine(IKey.lang("ae2morepainting.gui.color_applicator.stored", count.getIntValue())))
                .onMousePressed(mouseButton -> {
                    if (mouseButton != 0 || count.getIntValue() <= 0) {
                        return false;
                    }
                    selected.setIntValue(index);
                    return true;
                });
    }

    @SideOnly(Side.CLIENT)
    private static void drawSlot(final ItemStack icon, final int count, final boolean isSelected, final int x,
            final int y, final int width, final int height) {
        final int itemX = x + (width - 16) / 2;
        final int itemY = y + (height - 16) / 2;
        GuiDraw.drawItem(icon, itemX, itemY, 16, 16, 0);

        // Vanilla's renderItemOverlayIntoGUI re-enables lighting, which darkens every button drawn after it.
        final Minecraft mc = Minecraft.getMinecraft();
        final String text = String.valueOf(count);
        GlStateManager.disableLighting();
        GlStateManager.disableDepth();
        mc.fontRenderer.drawStringWithShadow(text, itemX + 17 - mc.fontRenderer.getStringWidth(text), itemY + 9,
                count > 0 ? COUNT_COLOR : EMPTY_COUNT_COLOR);
        if (isSelected) {
            GuiDraw.drawBorderInsideXYWH(x, y, width, height, 1f, SELECTED_COLOR);
        }
        GlStateManager.enableDepth();
        GlStateManager.color(1f, 1f, 1f, 1f);
    }

    private static int[] countStored(final ItemStack applicator) {
        final int[] counts = new int[COLORS.length];
        final IMEInventory<IAEItemStack> inv = getInventory(applicator);
        if (inv == null) {
            return counts;
        }
        final IItemStorageChannel channel = AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class);
        final AccessorToolColorApplicator accessor = (AccessorToolColorApplicator) applicator.getItem();
        for (final IAEItemStack stored : inv.getAvailableItems(channel.createList())) {
            final AEColor color = accessor.ae2morepainting$getColorFromItem(stored.getDefinition());
            if (color != null) {
                counts[color.ordinal()] = (int) Math.min(Integer.MAX_VALUE,
                        counts[color.ordinal()] + stored.getStackSize());
            }
        }
        return counts;
    }

    private static int getSelectedIndex(final ItemStack applicator) {
        if (!(applicator.getItem() instanceof ToolColorApplicator)) {
            return -1;
        }
        final AEColor color = ((ToolColorApplicator) applicator.getItem()).getActiveColor(applicator);
        return color == null ? -1 : color.ordinal();
    }

    private static void selectColor(final ItemStack applicator, final int index) {
        if (!(applicator.getItem() instanceof ToolColorApplicator) || index < 0 || index >= COLORS.length
                || countStored(applicator)[index] <= 0) {
            return;
        }
        ((ToolColorApplicator) applicator.getItem()).setActiveColor(applicator, COLORS[index]);
    }

    private static IMEInventory<IAEItemStack> getInventory(final ItemStack applicator) {
        if (!(applicator.getItem() instanceof ToolColorApplicator)) {
            return null;
        }
        return AEApi.instance().registries().cell().getCellInventory(applicator, null,
                AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class));
    }
}
