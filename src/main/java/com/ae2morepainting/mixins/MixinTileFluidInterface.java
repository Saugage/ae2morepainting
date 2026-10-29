package com.ae2morepainting.mixins;

import java.io.IOException;

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

import appeng.api.implementations.tiles.IColorableTile;
import appeng.api.util.AEColor;
import appeng.fluids.tile.TileFluidInterface;
import appeng.tile.grid.AENetworkTile;
import io.netty.buffer.ByteBuf;

@Mixin(TileFluidInterface.class)
public abstract class MixinTileFluidInterface extends AENetworkTile implements IColorableTile {

    @Unique
    private AEColor ae2morepainting$color = AEColor.TRANSPARENT;

    @Inject(method = "readFromNBT", at = @At("TAIL"))
    private void ae2morepainting$readColor(final NBTTagCompound data, final CallbackInfo ci) {
        final AEColor color = TileColorLogic.readNBT(data);
        if (color != null) {
            this.ae2morepainting$color = color;
            this.getProxy().setColor(color);
        }
    }

    @Inject(method = "writeToNBT", at = @At("RETURN"))
    private void ae2morepainting$writeColor(final NBTTagCompound data,
            final CallbackInfoReturnable<NBTTagCompound> cir) {
        TileColorLogic.writeNBT(data, this.ae2morepainting$color);
    }

    @Override
    protected boolean readFromStream(final ByteBuf data) throws IOException {
        final boolean changed = super.readFromStream(data);
        final AEColor oldColor = this.ae2morepainting$color;
        this.ae2morepainting$color = TileColorLogic.fromOrdinal(data.readByte());
        this.getProxy().setColor(this.ae2morepainting$color);
        return changed || oldColor != this.ae2morepainting$color;
    }

    @Override
    protected void writeToStream(final ByteBuf data) throws IOException {
        super.writeToStream(data);
        data.writeByte(this.ae2morepainting$color.ordinal());
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
