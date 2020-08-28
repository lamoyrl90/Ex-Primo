package redd90.exprimo.essentia;

import java.util.HashMap;
import java.util.Map.Entry;

public class Equilibrium {
	private final HashMap<Essentia,Integer> map;
	
	public Equilibrium(HashMap<Essentia,Integer> mapIn) {
		this.map = mapIn;
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
}
