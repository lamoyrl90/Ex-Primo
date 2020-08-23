package redd90.exprimo.registry;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import redd90.exprimo.ExPrimo;
import redd90.exprimo.essentia.Essentia;

public class ModRegistries {

	public static IForgeRegistry<Essentia> ESSENTIAE;
	
	public static void createRegistries(final RegistryEvent.NewRegistry event) {
		RegistryBuilder<Essentia> builder = new RegistryBuilder<>();
		builder.setName(new ResourceLocation(ExPrimo.MODID, "essentiae"));
		builder.setType(Essentia.class);
		ModRegistries.ESSENTIAE = builder.create();
	}
}
