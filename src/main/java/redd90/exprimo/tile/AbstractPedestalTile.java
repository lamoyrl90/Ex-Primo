package redd90.exprimo.tile;

import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.world.server.ServerWorld;
import redd90.exprimo.item.inventory.ModItemStackHandler;

public abstract class AbstractPedestalTile extends TileWithInventory implements ITickableTileEntity {

	private ModItemStackHandler itemhandler = new ModItemStackHandler(1, this::markDirtyAndDispatch);
	private int tickInterval = 40;
	private final long timePlaced;
	
	public <E extends AbstractPedestalTile> AbstractPedestalTile(TileEntityType<E> type) {
		super(type);
		this.itemhandler.setDefaultSlotLimit(1);
		if (this.getWorld() != null && !this.getWorld().isRemote()) {
			this.timePlaced = this.getWorld().getGameTime();
		} else {
			this.timePlaced = 0;
		}
	}

	public ModItemStackHandler getInventory() {
		return this.itemhandler;
	}
	
	@Override
	public void tick() {
		if (!this.world.isRemote()) {
			ServerWorld world = (ServerWorld) this.world;
			int time = (int) (world.getGameTime() - timePlaced);
			if (time % this.tickInterval == 0) {
				
			}
		}
	}
}
