package redd90.exprimo.essentia;

import java.util.HashMap;
import java.util.Map.Entry;

import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;
import redd90.exprimo.registry.ModRegistries;

public class Equilibrium implements INBTSerializable<CompoundNBT> {
	private HashMap<Essentia,Integer> map = new HashMap<>();
	
	public Equilibrium(HashMap<Essentia,Integer> mapIn) {
		this.map = mapIn;
	}
	
	public Equilibrium(int global) {
		for(Essentia e : ModRegistries.ESSENTIAS) {
			this.map.put(e, global);
		}
	}
	
	public Equilibrium() {
		for(Essentia e : ModRegistries.ESSENTIAS) {
			this.map.put(e, 0);
		}
	}
	
	public Equilibrium(Equilibrium eqIn) {
		this();
		setValues(eqIn);
	}
	
	public void setValues(Equilibrium eqIn) {
		for (Entry<Essentia,Integer> entry : map.entrySet()) {
			Essentia e = entry.getKey();
			map.put(e, eqIn.getValue(e));
		}
	}
	
	public int getValue(Essentia essentia) {
		return map.get(essentia);
	}
	
	public void setValue(Essentia essentia, int value) {
		map.put(essentia, value);
	}

	@Override
	public CompoundNBT serializeNBT() {
		CompoundNBT ret = new CompoundNBT();
		for (Entry<Essentia,Integer> entry : map.entrySet()) {
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
			map.put(e, v);
		}
	}
}
