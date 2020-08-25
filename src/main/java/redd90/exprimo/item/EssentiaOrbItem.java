package redd90.exprimo.item;

import net.minecraft.item.Item;
import redd90.exprimo.registry.ModItems;

public class EssentiaOrbItem extends Item implements IEssentiaContainerItem {

	private final int capacity;
	private final int vacuumpressure;
	
	public EssentiaOrbItem(int capacity) {
		super(ModItems.defaultProperties());
		this.capacity = capacity;
		this.vacuumpressure = 0;
	}
	
	public EssentiaOrbItem(int capacity, int vacuumpressure) {
		super(ModItems.defaultProperties());
		this.capacity = capacity;
		this.vacuumpressure = vacuumpressure;
	}

	@Override
	public int getCapacity() {
		return capacity;
	}

	@Override
	public int getVacuumPressure() {
		return vacuumpressure;
	}

}
