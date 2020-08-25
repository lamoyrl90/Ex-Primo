package redd90.exprimo.tile;

import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import redd90.exprimo.item.SingleItemStackHandler;

public class AbstractPedestalTile extends ModTile {

	private SingleItemStackHandler itemhandler = new SingleItemStackHandler(this);
	private LazyOptional<IItemHandler> itemhandleroptional = LazyOptional.of(() -> itemhandler);
	
	public <E extends AbstractPedestalTile> AbstractPedestalTile(TileEntityType<E> type) {
		super(type);
	}

	
}
