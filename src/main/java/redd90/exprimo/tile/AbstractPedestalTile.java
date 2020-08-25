package redd90.exprimo.tile;

import net.minecraft.inventory.Inventory;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.world.server.ServerWorld;
import redd90.exprimo.item.SingleItemInventory;

public abstract class AbstractPedestalTile extends ModTile implements ITickableTileEntity {

	private SingleItemInventory itemhandler = new SingleItemInventory();
	private int tickInterval = 40;
	private final long timePlaced;
	
	public <E extends AbstractPedestalTile> AbstractPedestalTile(TileEntityType<E> type) {
		super(type);
		if (this.getWorld() != null && !this.getWorld().isRemote()) {
			this.timePlaced = this.getWorld().getGameTime();
		} else {
			this.timePlaced = 0;
		}
		itemhandler.addListener(i -> markDirty());
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
	
	public Inventory getItemHandler() {
		return itemhandler;
	}
}
