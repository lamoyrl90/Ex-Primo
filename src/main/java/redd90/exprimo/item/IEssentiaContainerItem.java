package redd90.exprimo.item;

import net.minecraft.item.ItemStack;

public interface IEssentiaContainerItem {
	int getCapacity();
	double getPushFactor();
	double getPullFactor();
	int getColor(ItemStack stack);

}
