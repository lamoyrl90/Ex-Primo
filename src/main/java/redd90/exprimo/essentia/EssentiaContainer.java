package redd90.exprimo.essentia;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.Direction;
import net.minecraft.world.IWorld;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import redd90.exprimo.ExPrimo;
import redd90.exprimo.essentia.flow.ChunkEssentiaFlowManager;
import redd90.exprimo.essentia.flow.IFlowTickPredicate;
import redd90.exprimo.item.IEssentiaContainerItem;
import redd90.exprimo.registry.ModRegistries;

public class EssentiaContainer implements IEssentiaContainer, ICapabilitySerializable<CompoundNBT> {

	private Set<EssentiaContainer> linkedContainers = new HashSet<>();
	private HashMap<String, EssentiaStack> stackset = createEmptyStackSet();
	private final Optional<ICapabilityProvider> holder;
	private IFlowTickPredicate tickPredicate = new DefaultFlowTickPredicate();
	private int capacity = 10000;
	
	public EssentiaContainer(ICapabilityProvider holder) {
		this.holder = Optional.of(holder);
		if (holder instanceof ItemStack) {
			Item item = ((ItemStack) holder).getItem();
			if (item instanceof IEssentiaContainerItem)
				this.capacity = ((IEssentiaContainerItem)item).getCapacity();
		}
	}
	
	public EssentiaContainer() {
		this.holder = Optional.empty();
	}
	
	public EssentiaContainer(ICapabilityProvider holder, HashMap<String, EssentiaStack> stackset) {
		this.holder = Optional.of(holder);
		this.setStackSet(stackset);
	}
	
	public HashMap<String, EssentiaStack> createEmptyStackSet() {
		HashMap<String, EssentiaStack> stackset = new HashMap<>();
		
		if (ModRegistries.ESSENTIAS.getEntries().isEmpty()) {
			String message = "Essentiae failed to register - cannot create new Essentia Container!";
			ExPrimo.LOGGER.fatal(message);
			return stackset;
		}
		
		
		for (Essentia e : ModRegistries.ESSENTIAS) {
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
		if (holder.isPresent()) {
			return holder.get();
		} else {
			throw new IllegalArgumentException("Essentia Container has no holder!");
		}
	}
	
	public void setStackSet(HashMap<String, EssentiaStack> stackset) {
		for (EssentiaStack stack : stackset.values()) {
			this.setStack(stack.getEssentia().getName(), stack.getAmount());
		}
	}
	
	public EssentiaContainer copy() {
		HashMap<String, EssentiaStack> stackset = createEmptyStackSet();
		EssentiaContainer copy = new EssentiaContainer(this.getHolder(), stackset);
		copy.setStackSet(this.stackset);
		return copy;
	}

	public int getInnerPressure(String essentiakey) {
		int value = getStack(essentiakey).getAmount();
		int pressure = 0;
		if (value == 0)
			return 0;
		for (EssentiaStack stack : getStackSet().values()) {
			if(stack.getEssentia().getName() != essentiakey) {
				pressure += stack.getAmount();
			} else {
				pressure -= (int) Math.floor(Math.sqrt(stack.getAmount()));
				pressure += calculateOverfilled(stack.getAmount());
			}
		}
		int divisor = value + pressure == 0 ? 1 : value + pressure;
		return Math.floorDiv(value * pressure, divisor);
	}
	
	
	private int calculateOverfilled(int amount) {
		int diff = Math.max(0, amount - capacity);
		return diff * diff;
	}
	
	public void transfer(String essentiakey, EssentiaContainer target, int amount) {
		if(getStackSet().containsKey(essentiakey) && target.getStackSet().containsKey(essentiakey)) {
			int stackamt = getStack(essentiakey).getAmount();
			amount = Math.max(0, Math.min(stackamt, amount));
			getStack(essentiakey).shrink(amount);
			target.getStack(essentiakey).grow(amount);
		}
	}
	
	public void link(EssentiaContainer target) {
		this.linkedContainers.add(target);
	}
	
	public static void linkTwoWays(EssentiaContainer container1, EssentiaContainer container2) {
		container1.link(container2);
		container2.link(container1);
	}
	
	public Set<EssentiaContainer> getLinked() {
		return linkedContainers;
	}
	
	public boolean shouldTick(IWorld world) {
		return tickPredicate.test(world);
	}
	
	public void scheduleChunkFlowTicks(IFlowTickPredicate predicate) {
		Chunk holder = null;
		if (getHolder() instanceof Chunk)
			holder = (Chunk) getHolder();
		if (holder != null) {
			this.tickPredicate = predicate;
			ChunkEssentiaFlowManager.scheduleChunk(holder);
		}
	}
	
	public void unscheduleChunkFlowTicks() {
		Chunk holder = null;
		if (getHolder() instanceof Chunk)
			holder = (Chunk) getHolder();
		if (holder != null) {
			this.tickPredicate = new DefaultFlowTickPredicate();
			ChunkEssentiaFlowManager.unscheduleChunk(holder);
		}
	}
	
	public class DefaultFlowTickPredicate implements IFlowTickPredicate {

		@Override
		public boolean test(IWorld world) {
			return false;
		}
		
	}
	
}
