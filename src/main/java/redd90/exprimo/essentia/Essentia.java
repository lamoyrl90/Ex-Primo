package redd90.exprimo.essentia;

import net.minecraftforge.registries.ForgeRegistryEntry;

public class Essentia extends ForgeRegistryEntry<Essentia> {
	
	private final String key;
	private final String translationkey;
	private final int color;
	
	public Essentia(String key, int color) {
		this.key = key;
		this.translationkey = "essentia." + key;
		this.color = color;
	}
	
	public String getKey() {
		return key;
	}
	
	public String getTranslationKey() {
		return translationkey;
	}
	
	public int getColor() {
		return color;
	}
}
