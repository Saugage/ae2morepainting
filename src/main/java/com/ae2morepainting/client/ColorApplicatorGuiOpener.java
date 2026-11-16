package com.ae2morepainting.client;

import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.ae2morepainting.gui.ColorApplicatorGuiFactory;

@SideOnly(Side.CLIENT)
public final class ColorApplicatorGuiOpener {

    private ColorApplicatorGuiOpener() {}

    /**
     * A failed paint attempt on a block falls through to the item right-click, so only open when aiming at nothing.
     *
     * @return whether the GUI was opened
     */
    public static boolean tryOpen(final EnumHand hand) {
        final RayTraceResult target = Minecraft.getMinecraft().objectMouseOver;
        if (target != null && target.typeOfHit != RayTraceResult.Type.MISS) {
            return false;
        }
        ColorApplicatorGuiFactory.INSTANCE.openFromHandClient(hand);
        return true;
    }
}
