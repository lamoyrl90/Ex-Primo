package redd90.exprimo.client.render.tile;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;

public interface IModTER <E extends TileEntity> {
	void register(TileEntityType<E> type);
}
