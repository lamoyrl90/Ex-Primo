package redd90.exprimo.item;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;

public class SingleItemStackHandler extends ItemStackHandler implements IItemHandler, IItemHandlerModifiable, INBTSerializable<CompoundNBT> {
    
	private TileEntity te;
	
	public SingleItemStackHandler(TileEntity te) {
		super();
		this.te = te;
	}
	
    @Override
    public int getSlotLimit(int slot)
    {
        return 1;
    }
    
    @Override
    protected void onContentsChanged(int slot) {
        te.markDirty();
    }
}
