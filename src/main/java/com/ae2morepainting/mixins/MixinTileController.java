package com.ae2morepainting.mixins;

import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.ae2morepainting.TileColorLogic;

import appeng.api.implementations.tiles.IColorableTile;
import appeng.api.util.AEColor;
import appeng.tile.grid.AENetworkPowerTile;
import appeng.tile.networking.TileController;
import io.netty.buffer.ByteBuf;

@Mixin(value = TileController.class, remap = false)
public abstract class MixinTileController extends AENetworkPowerTile implements IColorableTile {

    @Unique
    private AEColor ae2morepainting$color = AEColor.TRANSPARENT;

    @Shadow
    public abstract void onNeighborChange(boolean force);

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
        super.writeToNBT(data);
        TileColorLogic.writeNBT(data, this.ae2morepainting$color);
        return data;
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
        this.onNeighborChange(true);
        return true;
    }

    @Inject(method = "checkController", at = @At("RETURN"), cancellable = true)
    private void ae2morepainting$requireMatchingColor(final BlockPos pos, final CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValueZ()) {
            final TileEntity te = this.world.getTileEntity(pos);
            if (te instanceof IColorableTile && !((IColorableTile) te).getColor().matches(this.ae2morepainting$color)) {
                cir.setReturnValue(false);
            }
        }
    }
}
