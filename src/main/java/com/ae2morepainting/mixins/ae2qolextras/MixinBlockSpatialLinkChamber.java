package com.ae2morepainting.mixins.ae2qolextras;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.ae2morepainting.ColorableBlocks;
import com.ae2morepainting.IColorableBlock;
import com.ae2qolextras.block.BlockSpatialLinkChamber;

import appeng.api.implementations.tiles.IColorableTile;
import appeng.api.util.AEColor;

@Mixin(BlockSpatialLinkChamber.class)
public abstract class MixinBlockSpatialLinkChamber extends Block implements IColorableBlock {

    private MixinBlockSpatialLinkChamber() {
        super(Material.IRON);
    }

    @Inject(method = "createBlockState", at = @At("RETURN"), cancellable = true)
    private void ae2morepainting$addColorProperty(final CallbackInfoReturnable<BlockStateContainer> cir) {
        cir.setReturnValue(ColorableBlocks.withColor(this, cir.getReturnValue()));
    }

    @Override
    public IBlockState getExtendedState(final IBlockState state, final IBlockAccess world, final BlockPos pos) {
        return ColorableBlocks.addColor(super.getExtendedState(state, world, pos), world, pos);
    }

    /** The Color Applicator paints non-AE2 blocks through this, so forward it to the tile like AEBaseTileBlock. */
    @Override
    public boolean recolorBlock(final World world, final BlockPos pos, final EnumFacing side,
            final EnumDyeColor color) {
        final TileEntity te = world.getTileEntity(pos);
        if (!(te instanceof IColorableTile)) {
            return super.recolorBlock(world, pos, side, color);
        }
        final IColorableTile colorable = (IColorableTile) te;
        final AEColor newColor = AEColor.values()[color.ordinal()];
        if (colorable.getColor() == newColor) {
            return false;
        }
        colorable.recolourBlock(side, newColor, null);
        return true;
    }
}
