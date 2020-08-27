package redd90.exprimo.tile;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.world.server.ServerWorld;
import redd90.exprimo.essentia.flow.TileEssentiaFlowManager;
import redd90.exprimo.item.IEssentiaContainerItem;
import redd90.exprimo.item.inventory.ModItemStackHandler;

public abstract class AbstractPedestalTile extends TileWithInventory implements ITickableTileEntity {

	private ModItemStackHandler itemhandler = new ModItemStackHandler(1, this::markDirtyAndDispatch);
	private int tickInterval = 40;
	private final long timePlaced;
	private double pushfactor = 1.0;
	private double pullfactor = 1.0;
	
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
		return itemhandler;
	}
	
	@Override
	public void tick() {
		if (!world.isRemote()) {
			if (!holdingEssentiaTickable())
				return;
			ServerWorld world = (ServerWorld) this.world;
			int time = (int) (world.getGameTime() - timePlaced);
			if (time % getTickInterval() == 0) {
				TileEssentiaFlowManager.onTileTick(this);
			}
		}
	}
	
	private int getTickInterval() {
		return tickInterval;
	}

	public double getPushFactor() {
		return pushfactor;
	}
	
	public double getPullFactor() {
		return pullfactor;
	}
	
	public boolean holdingEssentiaTickable() {
		for (ItemStack stack : itemhandler.getStacks()) {
			if (stack.getItem() instanceof IEssentiaContainerItem)
				return true;
		}
		return false;
	}
}
