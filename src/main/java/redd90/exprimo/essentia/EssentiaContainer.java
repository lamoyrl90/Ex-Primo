package redd90.exprimo.essentia;

import java.util.Collection;
import java.util.HashMap;
import java.util.Optional;
import java.util.Map.Entry;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import redd90.exprimo.ExPrimo;
import redd90.exprimo.registry.ModRegistries;

public class EssentiaContainer implements IEssentiaContainer, ICapabilitySerializable<CompoundNBT> {

	private HashMap<String, EssentiaStack> stackset = createEmptyStackSet();
	private final ICapabilityProvider holder;
	
	public EssentiaContainer(ICapabilityProvider holder) {
		this.holder = holder;
	}
	
	public EssentiaContainer(ICapabilityProvider holder, HashMap<String, EssentiaStack> stackset) {
		this.holder = holder;
		this.setStackSet(stackset);
	}
	
	public HashMap<String, EssentiaStack> createEmptyStackSet() {
		HashMap<String, EssentiaStack> stackset = new HashMap<>();
		
		if (ModRegistries.ESSENTIAE.getEntries().isEmpty()) {
			String message = "Essentiae failed to register - cannot create new Essentia Container!";
			ExPrimo.LOGGER.fatal(message);
			return stackset;
		}
		
		
		for (Essentia e : ModRegistries.ESSENTIAE) {
			stackset.put(e.getName(), new EssentiaStack(Optional.of(this), e, 0));
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
		return stackset.get(essentia.getName());
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
			tag.putString("type", entry.getValue().getEssentia().getName());
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
		return holder;
	}
	
	public void setStackSet(HashMap<String, EssentiaStack> stackset) {
		for (EssentiaStack stack : stackset.values()) {
			this.setStack(stack.getEssentia().getName(), stack.getAmount());
		}
	}
	
	public EssentiaContainer copy() {
		HashMap<String, EssentiaStack> stackset = createEmptyStackSet();
		EssentiaContainer copy = new EssentiaContainer(this.holder, stackset);
		copy.setStackSet(this.stackset);
		return copy;
	}

}
