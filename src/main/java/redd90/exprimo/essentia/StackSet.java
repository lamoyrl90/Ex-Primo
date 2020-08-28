package redd90.exprimo.essentia;

import java.util.HashMap;

import redd90.exprimo.registry.ModRegistries;

public class StackSet {
	private HashMap<String,EssentiaStack> stacks = new HashMap<>();
	
	public StackSet() {
		for (Essentia e : ModRegistries.ESSENTIAS) {
			stacks.put(e.getKey(), new EssentiaStack(e,0));
		}
	}
	
	public EssentiaStack getStack(String key) {
		return stacks.get(key);
	}
}
