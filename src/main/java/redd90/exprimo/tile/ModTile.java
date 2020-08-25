package redd90.exprimo.tile;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;

public class ModTile extends TileEntity {

	public <E extends ModTile> ModTile(TileEntityType<E> type) {
		super(type);
	}
	

}
