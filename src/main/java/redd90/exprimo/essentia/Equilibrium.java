package redd90.exprimo.essentia;

import java.util.HashMap;
import java.util.Map.Entry;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.INBTSerializable;
import redd90.exprimo.ExPrimo;
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
		ListNBT list = new ListNBT();
		for (Entry<Essentia,Integer> entry : map.entrySet()) {
			CompoundNBT tag1 = new CompoundNBT();
			tag1.putString("essentia", entry.getKey().getKey());
			CompoundNBT tag2 = new CompoundNBT();
			tag2.putInt("value", entry.getValue());
			list.add(tag1);
			list.add(tag2);
		}
		CompoundNBT ret = new CompoundNBT();
		ret.put("equilibrium", list);
		return ret;
	}

	@Override
	public void deserializeNBT(CompoundNBT nbt) {
		ListNBT list = nbt.getList("equilibrium", 10);
		for (INBT tag : list) {
			CompoundNBT nbttag = (CompoundNBT) tag;
			Essentia e = ModRegistries.ESSENTIAS.getValue(new ResourceLocation(ExPrimo.MODID, nbttag.getString("essentia")));
			int v = nbttag.getInt("value");
			map.put(e,v);
		}
	}
}
