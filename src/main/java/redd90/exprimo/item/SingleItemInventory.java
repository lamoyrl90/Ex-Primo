package redd90.exprimo.item;

import net.minecraft.inventory.Inventory;

public class SingleItemInventory extends Inventory {
    
	public SingleItemInventory() {
		super(1);
	}
	
	@Override
	public int getInventoryStackLimit() {
		return 1;
	}
    
}
