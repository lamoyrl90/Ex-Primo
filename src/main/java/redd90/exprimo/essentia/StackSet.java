package redd90.exprimo.essentia;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Optional;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.util.INBTSerializable;
import redd90.exprimo.ExPrimo;
import redd90.exprimo.registry.ModRegistries;

public class StackSet implements INBTSerializable<CompoundNBT>{
	private HashMap<Essentia, Integer> stacks = new HashMap<>();
	private Optional<EssentiaContainer> holder;
	
	public StackSet() {
		for (Essentia e : ModRegistries.ESSENTIAS) {
			stacks.put(e, 0);
		}
	}
	
	public int getAmount(Essentia essentia) {
		return stacks.get(essentia);
	}
	
	public int getAmount(String key) {
		return stacks.get(ModRegistries.ESSENTIAS.getValue(new ResourceLocation(ExPrimo.MODID, key)));
	}
	
	public void setAmount(Essentia essentia, int amount) {
		stacks.put(essentia, amount);
		markDirty();
	}
		
	public void setStacks(StackSet stackset) {
		for (Entry<Essentia,Integer> entry : stackset.stacks.entrySet()) {
			setAmount(entry.getKey(), entry.getValue());
		}
	}
	
	public HashMap<Essentia, Integer> getStacks() {
		return stacks;
	}

	@Override
	public CompoundNBT serializeNBT() {
		CompoundNBT ret = new CompoundNBT();
		for (Entry<Essentia,Integer> entry : stacks.entrySet()) {
			String e = entry.getKey().getKey();
			int v = entry.getValue();
			ret.putInt(e, v);
		}
		return ret;
	}

	@Override
	public void deserializeNBT(CompoundNBT nbt) {
		for (Essentia e : ModRegistries.ESSENTIAS) {
			int v = nbt.getInt(e.getKey());
			stacks.put(e, v);
		}
	}

	public EssentiaContainer getHolder() {
		if(holder.isPresent())
			return holder.get();
		else return null;
	}

	public void setHolder(EssentiaContainer container) {
		this.holder = Optional.of(container);
	}
	
	private void markDirty() {
		if(getHolder().getHolder() instanceof Chunk) {
			Chunk holder = (Chunk) getHolder().getHolder();
			holder.markDirty();
		}
	}
}
