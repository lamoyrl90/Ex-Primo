package redd90.exprimo.item;

import java.util.List;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import redd90.exprimo.essentia.EssentiaContainer;
import redd90.exprimo.essentia.EssentiaContainerCap;
import redd90.exprimo.essentia.EssentiaStack;
import redd90.exprimo.registry.ModItems;

public class EssentiaOrbItem extends Item implements IEssentiaContainerItem {

	private final int capacity;
	private final int vacuumpressure;
	
	public EssentiaOrbItem(int capacity) {
		super(ModItems.defaultProperties().maxStackSize(1));
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

	@Override
	public void addInformation(ItemStack stack, World world, List<ITextComponent> list, ITooltipFlag flags) {
		if(world != null && world.isRemote()) {
			EssentiaContainer essentia = (EssentiaContainer) stack.getCapability(EssentiaContainerCap.ESSENTIA_CONTAINER).orElse(null);
			
			if (essentia != null) {
				for(EssentiaStack essentiastack : essentia.getStackSet().values()) {
					list.add(new StringTextComponent(essentiastack.getEssentia().getTranslationKey() + ": " + essentiastack.getAmount()));
				}
			}
		}
	}
	
}
