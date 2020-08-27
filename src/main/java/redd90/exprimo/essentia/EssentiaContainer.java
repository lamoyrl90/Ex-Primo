package redd90.exprimo.essentia;

import java.util.Collection;
import java.util.HashMap;
import java.util.Optional;
import java.util.Map.Entry;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import redd90.exprimo.ExPrimo;
import redd90.exprimo.item.IEssentiaContainerItem;
import redd90.exprimo.registry.ModRegistries;

public class EssentiaContainer implements IEssentiaContainer, ICapabilitySerializable<CompoundNBT> {

	private HashMap<String, EssentiaStack> stackset = createEmptyStackSet();
	private HashMap<Essentia, Double> essentiaweights;
	private final Optional<ICapabilityProvider> holder;
	private int capacity = 1000;
	
	public EssentiaContainer(ICapabilityProvider holder) {
		this.holder = Optional.of(holder);
		if (holder instanceof ItemStack) {
			Item item = ((ItemStack) holder).getItem();
			if (item instanceof IEssentiaContainerItem)
				this.capacity = ((IEssentiaContainerItem)item).getCapacity();
		}
		this.essentiaweights = initializeWeights();
	}
	
	public EssentiaContainer() {
		this.holder = Optional.empty();
		this.essentiaweights = initializeWeights();
	}
	
	public EssentiaContainer(ICapabilityProvider holder, HashMap<String, EssentiaStack> stackset) {
		this.holder = Optional.of(holder);
		this.setStackSet(stackset);
		this.essentiaweights = initializeWeights();
	}
	
	private HashMap<Essentia, Double> initializeWeights() {
		HashMap<Essentia, Double> map = new HashMap<>();
		for (Essentia essentia : ModRegistries.ESSENTIAS) {
			map.put(essentia, 1.0);
		}
		return map;
	}
	
	public HashMap<String, EssentiaStack> createEmptyStackSet() {
		HashMap<String, EssentiaStack> stackset = new HashMap<>();
		
		if (ModRegistries.ESSENTIAS.getEntries().isEmpty()) {
			String message = "Essentiae failed to register - cannot create new Essentia Container!";
			ExPrimo.LOGGER.fatal(message);
			return stackset;
		}
		
		
		for (Essentia e : ModRegistries.ESSENTIAS) {
			stackset.put(e.getKey(), new EssentiaStack(Optional.of(this), e, 0));
		}
		this.claimStacks(stackset.values());
		return stackset;
	}
	
	private void claimStacks(Collection<EssentiaStack> stacks) {
		for (EssentiaStack stack : stacks) {
			stack.setContainer(this);
		}
	}
	
	public EssentiaStack getStack(Essentia essentia) {
		return stackset.get(essentia.getKey());
	}
	
	public EssentiaStack getStack(String name) {
		return stackset.get(name);
	}
	
	public void setStack(Essentia essentia, int amount) {
		getStack(essentia).setAmount(amount);
	}
	
	public void setStack(String name, int amount) {
		getStack(name).setAmount(amount);
	}
	
	public HashMap<String, EssentiaStack> getStackSet() {
		return stackset;
	}

	@SuppressWarnings("unchecked") //It's checked, Java
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		if (cap == EssentiaContainerCap.ESSENTIA_CONTAINER)
			return LazyOptional.of(() -> (T) this);
		return LazyOptional.empty();
	}

	@Override
	public CompoundNBT serializeNBT() {
		ListNBT list = new ListNBT();
		for(Entry<String, EssentiaStack> entry : stackset.entrySet()) {
			CompoundNBT tag = new CompoundNBT();
			tag.putString("type", entry.getValue().getEssentia().getKey());
			tag.putInt("amount", entry.getValue().getAmount());
			list.add(tag);
		}
		CompoundNBT compound = new CompoundNBT();
		compound.put("essentia_stacks", list);
		return compound;
	}

	@Override
	public void deserializeNBT(CompoundNBT nbt) {
		ListNBT list = nbt.getList("essentia_stacks", 10);
		for (INBT entry : list) {
			CompoundNBT tag = (CompoundNBT) entry;
			String type = tag.getString("type");
			int amount = tag.getInt("amount");
			this.getStack(type).setAmount(amount);
		}
	}

	public ICapabilityProvider getHolder() {
		if (holder.isPresent()) {
			return holder.get();
		} else {
			throw new IllegalArgumentException("Essentia Container has no holder!");
		}
	}
	
	public void setStackSet(HashMap<String, EssentiaStack> stackset) {
		for (EssentiaStack stack : stackset.values()) {
			this.setStack(stack.getEssentia().getKey(), stack.getAmount());
		}
	}
	
	public EssentiaContainer copyEssentiaAndCapacity() {
		HashMap<String, EssentiaStack> stackset = createEmptyStackSet();
		EssentiaContainer copy = new EssentiaContainer(this.getHolder(), stackset);
		copy.setStackSet(this.stackset);
		copy.setCapacity(this.capacity);
		return copy;
	}

	private void setCapacity(int capacity) {
		this.capacity = capacity;
	}

	public int getInnerPressure(Essentia essentia) {
		int value = getStack(essentia).getAmount();
		if (value == 0)
			return 0;
		double divisor = essentiaweights.get(essentia);
		divisor = divisor == 0 ? 1 : divisor;
		int pressure = (int) Math.floor(value / essentiaweights.get(essentia));
		return pressure;
		/*
		for (EssentiaStack stack : getStackSet().values()) {
			if(stack.getEssentia().getKey() != essentiakey) {
				//pressure += stack.getAmount();
			} else {
				//pressure -= (int) Math.floor(Math.sqrt(stack.getAmount()));
				pressure += value;
				//pressure += calculateOverfilled(stack.getAmount());
			}
		}*/
		//int divisor = 1;//pressure + value == 0 ? 1 : pressure + value;
		//int dividend = pressure;// * value;
		//return Math.floorDiv(dividend,divisor);
		//return Math.floorDiv(pressure * value, divisor);
	}
	
	/*
	private int calculateOverfilled(int amount) {
		if (amount < capacity)
			return 0;
		int diff = amount - capacity;
		diff = (int) Math.pow(amount, 2);
		return diff;
	}*/
	
	public void transfer(Essentia essentia, EssentiaContainer target, int amount) {
		if(getStackSet().containsKey(essentia.getKey()) && target.getStackSet().containsKey(essentia.getKey())) {
			int stackamt = getStack(essentia).getAmount();
			amount = Math.max(0, Math.min(stackamt, amount));
			getStack(essentia).shrink(amount);
			target.getStack(essentia).grow(amount);
		}
	}

	public double getEssentiaweight(Essentia essentia) {
		return essentiaweights.get(essentia);
	}

	public void setEssentiaWeights(HashMap<Essentia, Double> weights) {
		this.essentiaweights = weights;
	}
	
}
