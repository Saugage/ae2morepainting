package com.ae2morepainting;

import net.minecraft.block.Block;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import appeng.api.implementations.tiles.IColorableTile;
import appeng.block.networking.BlockController;
import appeng.items.tools.powered.ToolColorApplicator;

/**
 * Paintable machines open their GUI on right-click, which would stop the Color Applicator's {@code onItemUse} from
 * ever running. Denying block activation lets the applicator paint them instead.
 */
@Mod.EventBusSubscriber(modid = Tags.MODID)
public final class ApplicatorInteractionHandler {

    private ApplicatorInteractionHandler() {}

    @SubscribeEvent
    public static void onRightClickBlock(final PlayerInteractEvent.RightClickBlock event) {
        if (event.getEntityPlayer().isSneaking()
                || !(event.getItemStack().getItem() instanceof ToolColorApplicator)) {
            return;
        }
        final Block block = event.getWorld().getBlockState(event.getPos()).getBlock();
        if ((ColorableBlocks.isColorable(block) || block instanceof BlockController)
                && event.getWorld().getTileEntity(event.getPos()) instanceof IColorableTile) {
            event.setUseBlock(Event.Result.DENY);
        }
    }
}
