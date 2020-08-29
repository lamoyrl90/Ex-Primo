package redd90.exprimo.item;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import redd90.exprimo.client.text.ModText;
import redd90.exprimo.essentia.Essentia;
import redd90.exprimo.essentia.EssentiaContainer;
import redd90.exprimo.essentia.EssentiaContainerCap;
import redd90.exprimo.registry.ModItems;
import redd90.exprimo.registry.ModRegistries;
import redd90.exprimo.util.ModMath;

public class EssentiaOrbItem extends Item implements IEssentiaContainerItem {

	private final int capacity;
	private final double pushfactor;
	private final double pullfactor;
	
	public EssentiaOrbItem(int capacity) {
		super(ModItems.defaultProperties().maxStackSize(1));
		this.capacity = capacity;
		this.pushfactor = 1.0;
		this.pullfactor = 1.0;
	}
	
	public EssentiaOrbItem(int capacity, double pushfactor, double pullfactor) {
		super(ModItems.defaultProperties());
		this.capacity = capacity;
		this.pushfactor = pushfactor;
		this.pullfactor = pullfactor;
	}

	@Override
	public int getCapacity() {
		return capacity;
	}

	@Override
	public void addInformation(ItemStack stack, World world, List<ITextComponent> list, ITooltipFlag flags) {
		if(world != null && world.isRemote()) {
			EssentiaContainer essentia = (EssentiaContainer) stack.getCapability(EssentiaContainerCap.ESSENTIA_CONTAINER).orElse(null);
			
			if (essentia != null) {
				for(Essentia e : ModRegistries.ESSENTIAS) {
					String essentiakey = e.getTranslationKey();
					ITextComponent amount = new StringTextComponent(": " + essentia.getStack(e));
					IFormattableTextComponent text = ModText.withColor(new TranslationTextComponent(essentiakey), e.getColor()).append(amount);
					list.add(text);
				}
			}
		}
	}

	@Override
	public double getPushFactor() {
		return pushfactor;
	}

	@Override
	public double getPullFactor() {
		return pullfactor;
	}

	@Override
	public int getColor(ItemStack stack) {
		EssentiaContainer container = (EssentiaContainer) stack.getCapability(EssentiaContainerCap.ESSENTIA_CONTAINER).orElse(null);
		if (container == null)
			return -1;
		List<Color> colors = new ArrayList<>();
		for(Essentia e : ModRegistries.ESSENTIAS) {
			Color color = new Color(e.getColor());
			colors.add(color);
		}
		
		return ModMath.getAverageColor(colors);
	}
	
}
