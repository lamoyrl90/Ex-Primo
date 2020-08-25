package redd90.exprimo.essentia;

import java.util.Optional;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.registries.IRegistryDelegate;
import redd90.exprimo.ExPrimo;
import redd90.exprimo.registry.ModRegistries;

public class EssentiaStack {

	private IRegistryDelegate<Essentia> delegate;
	private int amount;
	private Optional<EssentiaContainer> container;
	
	public EssentiaStack(Optional<EssentiaContainer> container, Essentia essentia, int amount) {
		this.container = container;
		this.delegate = essentia.delegate;
		this.amount = amount;
	}
	
	public EssentiaStack(Optional<EssentiaContainer> container, String name, int amount) {
		this.container = container;
		Essentia essentia = ModRegistries.ESSENTIAS.getValue(new ResourceLocation(ExPrimo.MODID, name));
		this.delegate = essentia.delegate;
		this.amount = amount;
	}
	
	public EssentiaStack(Optional<EssentiaContainer> container, Essentia essentia) {
		this(container, essentia, 0);
	}
	
	public EssentiaStack(Optional<EssentiaContainer> container, String name) {
		this(container, name, 0);
	}
	
	public EssentiaStack(Essentia essentia, int amount) {
		this(Optional.empty(), essentia, amount);
	}
	
	public EssentiaStack(String name, int amount) {
		this(Optional.empty(), name, amount);
	}
	
	public EssentiaStack(Essentia essentia) {
		this(Optional.empty(), essentia, 0);
	}
	
	public EssentiaStack(String name) {
		this(Optional.empty(), name, 0);
	}
	
	public Essentia getEssentia() {
		return delegate.get();
	}
	
	public int getAmount() {
		return amount;
	}
		
	public IRegistryDelegate<Essentia> getDelegate(Essentia essentia) {
		if (ModRegistries.ESSENTIAS.getKey(essentia) == null) {
			ExPrimo.LOGGER.fatal("Failed to create an EssentiaStack for an unregistered Essentia {}", essentia.getRegistryName());
			throw new IllegalArgumentException("Cannot create an EssentiaStack for an unregistered Essentia");
		}
		
		return essentia.delegate;
	}

	public void setAmount(int amount) {
		this.amount = amount;
		update();
	}
	
	public void grow(int amount) {
		this.amount += amount;
		update();
	}
	
	public void grow() {
		this.amount += 1;
		update();
	}
	
	public void shrink(int amount) {
		this.amount -= amount;
		update();
	}
	
	public void shrink() {
		this.amount -= 1;
		update();
	}

	public EssentiaContainer getContainer() {
		return container.get();
	}
	
	public void setContainer(EssentiaContainer container) {
		this.container = Optional.of(container);
	}
	
	public void update() {
		if(container.isPresent()) {
			ICapabilityProvider holder = container.get().getHolder();
			if (holder instanceof Chunk) {
				Chunk chunk = (Chunk) holder;
				World world = chunk.getWorld();
				if(!world.isRemote()) {
					chunk.markDirty();
				}
			}
		}
	}
}
