package redd90.exprimo.tile;

import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import redd90.exprimo.item.SingleItemStackHandler;

public class AbstractPedestalTile extends ModTile implements ITickableTileEntity {

	private SingleItemStackHandler itemhandler = new SingleItemStackHandler(this);
	private LazyOptional<IItemHandler> itemhandleroptional = LazyOptional.of(() -> itemhandler);
	private int tickInterval = 40;
	
	public <E extends AbstractPedestalTile> AbstractPedestalTile(TileEntityType<E> type) {
		super(type);
	}

	@Override
	public void tick() {
		if (!this.world.isRemote()) {
			ServerWorld world = (ServerWorld) this.world;
			int gametime = (int) world.getGameTime();
			if (gametime % this.tickInterval == 0) {
				
			}
		}
	}
	
	
}
