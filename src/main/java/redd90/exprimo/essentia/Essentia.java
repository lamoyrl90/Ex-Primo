package redd90.exprimo.essentia;

import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class Essentia extends ForgeRegistryEntry<Essentia> {
	
	private final String key;
	private final TranslationTextComponent translationkey;
	
	public Essentia(String key, TranslationTextComponent translationkey) {
		this.key = key;
		this.translationkey = translationkey;
	}
	
	public String getKey() {
		return key;
	}
	
	public TranslationTextComponent getTranslationKey() {
		return translationkey;
	}
}
