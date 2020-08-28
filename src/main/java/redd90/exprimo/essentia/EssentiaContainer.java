package redd90.exprimo.essentia;

import java.util.Optional;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import redd90.exprimo.item.IEssentiaContainerItem;

public class EssentiaContainer implements IEssentiaContainer, ICapabilitySerializable<CompoundNBT> {

	private StackSet stackset = new StackSet();
	private final Optional<ICapabilityProvider> holder;
	private boolean chunkGen = false;
	private Equilibrium equilibrium = new Equilibrium();
	
	public EssentiaContainer(ICapabilityProvider holder) {
		this.holder = Optional.of(holder);
		if (holder instanceof ItemStack) {
			Item item = ((ItemStack) holder).getItem();
			if (item instanceof IEssentiaContainerItem)
				this.setEquilibrium(new Equilibrium(((IEssentiaContainerItem)item).getCapacity()));
		}
	}
	
	public EssentiaContainer() {
		this.holder = Optional.empty();
	}
	
	public EssentiaContainer(ICapabilityProvider holder, StackSet stackset) {
		this.holder = Optional.of(holder);
		this.setStackSet(stackset);
	}
		
	public int getStack(Essentia essentia) {
		return stackset.getAmount(essentia);
	}
	
	public void setStack(Essentia essentia, int amount) {
		stackset.setAmount(essentia, amount);;
	}
	
	public StackSet getStackSet() {
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
		CompoundNBT compound = new CompoundNBT();
		compound.putBoolean("chunkgen", chunkGen);
		compound.put("equilibrium", equilibrium.serializeNBT());
		compound.put("stackset", stackset.serializeNBT());
		
		return compound;
	}

	@Override
	public void deserializeNBT(CompoundNBT nbt) {
		this.chunkGen = nbt.getBoolean("chunkgen");
		equilibrium.deserializeNBT(nbt.getCompound("equilibrium"));
		stackset.deserializeNBT(nbt.getCompound("stackset"));
	}

	public ICapabilityProvider getHolder() {
		if (holder.isPresent()) {
			return holder.get();
		} else {
			return null;
		}
	}
	
	public void setStackSet(StackSet stacksetIn) {
		stackset.setStacks(stacksetIn);
	}
	
	public EssentiaContainer copyEssentiaAndCapacity() {
		StackSet stackset = new StackSet();
		EssentiaContainer copy = new EssentiaContainer(this.getHolder(), stackset);
		copy.setStackSet(this.stackset);
		copy.setEquilibrium(this.equilibrium);
		return copy;
	}

	public int getInnerPressure(Essentia essentia) {
		int eq = equilibrium.getValue(essentia);
		int value = stackset.getAmount(essentia);
		if (eq == 0)
			eq = 1;
		return (int) Math.floorDiv(value * value,eq);
	}
	
	
	public void transfer(Essentia essentia, EssentiaContainer target, int amount) {
		int stackamt = stackset.getAmount(essentia);
		amount = Math.max(0, Math.min(stackamt, amount));
		shrinkStack(essentia, amount);
		target.growStack(essentia, amount);
	}

	public void shrinkStack(Essentia essentia, int amount) {
		setStack(essentia, getStack(essentia)-amount);
	}
	
	public void growStack(Essentia essentia, int amount) {
		setStack(essentia, getStack(essentia)+amount);
	}
	
	public boolean isChunkGen() {
		return chunkGen;
	}

	public void setChunkGen(boolean chunkGen) {
		this.chunkGen = chunkGen;
	}

	public Equilibrium getEquilibrium() {
		return equilibrium;
	}

	public void setEquilibrium(Equilibrium equilibrium) {
		this.equilibrium = equilibrium;
	}
	
	public void setEquilibrium(int value) {
		setEquilibrium(new Equilibrium(value));
	}
	
	public void setEquilibrium(Essentia essentia, int value) {
		equilibrium.setValue(essentia, value);
	}
}
