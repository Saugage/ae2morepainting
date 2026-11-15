package com.ae2morepainting.mixins.ae2qolextras;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.ae2morepainting.TileColorLogic;
import com.ae2qolextras.tile.TileSpatialLinkChamber;

import appeng.api.implementations.tiles.IColorableTile;
import appeng.api.util.AEColor;
import appeng.tile.grid.AENetworkInvTile;
import io.netty.buffer.ByteBuf;

@Mixin(TileSpatialLinkChamber.class)
public abstract class MixinTileSpatialLinkChamber extends AENetworkInvTile implements IColorableTile {

    @Unique
    private AEColor ae2morepainting$color = AEColor.TRANSPARENT;

    @Override
    public void readFromNBT(final NBTTagCompound data) {
        super.readFromNBT(data);
        final AEColor color = TileColorLogic.readNBT(data);
        if (color != null) {
            this.ae2morepainting$color = color;
            this.getProxy().setColor(color);
        }
    }

    @Override
    public NBTTagCompound writeToNBT(final NBTTagCompound data) {
        final NBTTagCompound result = super.writeToNBT(data);
        TileColorLogic.writeNBT(result, this.ae2morepainting$color);
        return result;
    }

    @Inject(method = "writeToStream", at = @At("TAIL"), remap = false)
    private void ae2morepainting$writeColorStream(final ByteBuf data, final CallbackInfo ci) {
        data.writeByte(this.ae2morepainting$color.ordinal());
    }

    @Inject(method = "readFromStream", at = @At("RETURN"), cancellable = true, remap = false)
    private void ae2morepainting$readColorStream(final ByteBuf data, final CallbackInfoReturnable<Boolean> cir) {
        final AEColor oldColor = this.ae2morepainting$color;
        this.ae2morepainting$color = TileColorLogic.fromOrdinal(data.readByte());
        this.getProxy().setColor(this.ae2morepainting$color);
        if (oldColor != this.ae2morepainting$color) {
            cir.setReturnValue(true);
        }
    }

    @Override
    public AEColor getColor() {
        return this.ae2morepainting$color;
    }

    @Override
    public boolean recolourBlock(final EnumFacing side, final AEColor colour, final EntityPlayer who) {
        if (this.ae2morepainting$color == colour) {
            return false;
        }
        if (this.world == null || this.world.isRemote) {
            return true;
        }

        this.ae2morepainting$color = colour;
        TileColorLogic.applyColor(this, colour);
        return true;
    }
}
