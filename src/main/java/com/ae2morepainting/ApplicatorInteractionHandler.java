package com.ae2morepainting;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import com.ae2morepainting.client.ColorApplicatorGuiOpener;

import appeng.api.implementations.tiles.IColorableTile;
import appeng.api.util.AEColor;
import appeng.block.networking.BlockController;
import appeng.items.tools.powered.ToolColorApplicator;

/**
 * Paintable machines open their GUI on right-click, which would stop the Color Applicator's {@code onItemUse} from
 * ever running. Denying block activation lets the applicator paint them instead.
 */
@Mod.EventBusSubscriber(modid = Tags.MODID)
public final class ApplicatorInteractionHandler {

    private ApplicatorInteractionHandler() {}

    private static boolean isPaintable(final World world, final BlockPos pos) {
        final Block block = world.getBlockState(pos).getBlock();
        return (ColorableBlocks.isColorable(block) || block instanceof BlockController)
                && world.getTileEntity(pos) instanceof IColorableTile;
    }

    @SubscribeEvent
    public static void onRightClickBlock(final PlayerInteractEvent.RightClickBlock event) {
        if (event.getEntityPlayer().isSneaking()
                || !(event.getItemStack().getItem() instanceof ToolColorApplicator)) {
            return;
        }
        if (isPaintable(event.getWorld(), event.getPos())) {
            event.setUseBlock(Event.Result.DENY);
        }
    }

    /**
     * Placing a paintable block with a Color Applicator in the offhand paints it, matching AE2's cable behavior.
     */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onBlockPlaced(final BlockEvent.PlaceEvent event) {
        final World world = event.getWorld();
        if (world.isRemote) {
            return;
        }
        final EntityPlayer player = event.getPlayer();
        final ItemStack stack = player.getHeldItem(EnumHand.OFF_HAND);
        if (stack.isEmpty() || !(stack.getItem() instanceof ToolColorApplicator)) {
            return;
        }
        final BlockPos pos = event.getPos();
        if (!isPaintable(world, pos)) {
            return;
        }
        final ToolColorApplicator applicator = (ToolColorApplicator) stack.getItem();
        final AEColor color = applicator.getActiveColor(stack);
        final IColorableTile tile = (IColorableTile) world.getTileEntity(pos);
        if (color != null && color != tile.getColor() && applicator.consumeColor(stack, color, true)) {
            if (tile.recolourBlock(EnumFacing.UP, color, player) && !player.isCreative()) {
                applicator.consumeColor(stack, color, false);
            }
        }
    }

    /**
     * Right-clicking the air opens the color picker. Sneak-clicking still cycles colors.
     */
    @SubscribeEvent
    public static void onRightClickItem(final PlayerInteractEvent.RightClickItem event) {
        if (!event.getWorld().isRemote
                || event.getEntityPlayer().isSneaking()
                || !(event.getItemStack().getItem() instanceof ToolColorApplicator)) {
            return;
        }
        if (ColorApplicatorGuiOpener.tryOpen(event.getHand())) {
            event.setCanceled(true);
            event.setCancellationResult(EnumActionResult.SUCCESS);
        }
    }
}
