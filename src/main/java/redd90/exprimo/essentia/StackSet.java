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

public class StackSet implements INBTSerializable<CompoundNBT>{
	private HashMap<Essentia, Integer> stacks = new HashMap<>();
	
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
	}
		
	public void setStacks(StackSet stackset) {
		for (Entry<Essentia,Integer> entry : stackset.stacks.entrySet()) {
			stacks.put(entry.getKey(), entry.getValue());
		}
	}
	
	public HashMap<Essentia, Integer> getStacks() {
		return stacks;
	}

	@Override
	public CompoundNBT serializeNBT() {
		ListNBT list = new ListNBT();
		for (Entry<Essentia,Integer> entry : stacks.entrySet()) {
			CompoundNBT tag1 = new CompoundNBT();
			CompoundNBT tag2 = new CompoundNBT();
			String essentia = entry.getKey().getKey();
			tag1.putString("essentia", essentia);
			int amount = entry.getValue();
			tag2.putInt("amount", amount);
			list.add(tag1);
			list.add(tag2);
		}
		CompoundNBT ret = new CompoundNBT();
		ret.put("stackset", list);
		
		return ret;
	}

	@Override
	public void deserializeNBT(CompoundNBT nbt) {
		ListNBT list = nbt.getList("stackset", 10);
		for(INBT itag : list) {
			CompoundNBT tag = (CompoundNBT) itag;
			String key = tag.getString("essentia");
			Essentia e = ModRegistries.ESSENTIAS.getValue(new ResourceLocation(ExPrimo.MODID, key));
			int amount = tag.getInt("amount");
			stacks.put(e, amount);
		}
	}
}
