package redd90.exprimo.essentia;

import net.minecraftforge.registries.ForgeRegistryEntry;

public class Essentia extends ForgeRegistryEntry<Essentia> {
	
	private final String name;
	
	public Essentia(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
}
