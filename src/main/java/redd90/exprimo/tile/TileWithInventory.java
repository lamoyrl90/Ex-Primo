package redd90.exprimo.tile;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import redd90.exprimo.item.inventory.ModItemStackHandler;

public abstract class TileWithInventory extends ModTile {

	public TileWithInventory(TileEntityType<?> type) {
		super(type);
	}
	
	public abstract ModItemStackHandler getInventory();

    @Override
    public void read(BlockState state, CompoundNBT tag) {
        super.read(state, tag);
        this.getInventory().deserializeNBT(tag);
    }

    @Override
    public CompoundNBT write(CompoundNBT tag) {
        super.write(tag);
        tag.merge(this.getInventory().serializeNBT());
        return tag;
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        if (!this.isRemoved() && cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.orEmpty(cap, LazyOptional.of(this::getInventory));
        }

        return super.getCapability(cap, side);
    }

    public boolean isUsableByPlayer(PlayerEntity player) {
        BlockPos pos = this.getPos();
        return player.getDistanceSq(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) <= 64;
    }
}
