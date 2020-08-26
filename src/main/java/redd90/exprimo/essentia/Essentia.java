package redd90.exprimo.essentia;

import net.minecraftforge.registries.ForgeRegistryEntry;

public class Essentia extends ForgeRegistryEntry<Essentia> {
	
	private final String key;
	private final String translationkey;
	
	public Essentia(String key) {
		this.key = key;
		this.translationkey = "essentia." + key;
	}
	
	public String getKey() {
		return key;
	}
	
	public String getTranslationKey() {
		return translationkey;
	}
}
