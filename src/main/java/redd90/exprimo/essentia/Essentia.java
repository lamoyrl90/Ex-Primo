package redd90.exprimo.essentia;

import net.minecraftforge.registries.ForgeRegistryEntry;

public class Essentia extends ForgeRegistryEntry<Essentia> {
	
	private final String key;
	private final String translationkey;
	private final int hue;
	
	public Essentia(String key, int hue) {
		this.key = key;
		this.translationkey = "essentia." + key;
		this.hue = hue;
	}
	
	public String getKey() {
		return key;
	}
	
	public String getTranslationKey() {
		return translationkey;
	}
	
	public int getHue() {
		return hue;
	}
}
