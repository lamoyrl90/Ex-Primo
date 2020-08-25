package redd90.exprimo.item;

import net.minecraft.item.Item;
import redd90.exprimo.registry.ModItems;

public class EssentiaOrbItem extends Item implements IEssentiaContainerItem {

	private final int capacity;
	
	public EssentiaOrbItem(int capacity) {
		super(ModItems.defaultProperties());
		this.capacity = capacity;
	}

	@Override
	public int getCapacity() {
		return capacity;
	}

}
