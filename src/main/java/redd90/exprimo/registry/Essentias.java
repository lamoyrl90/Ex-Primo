package redd90.exprimo.registry;

import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import redd90.exprimo.ExPrimo;
import redd90.exprimo.essentia.Essentia;

public class Essentias {
	public static final DeferredRegister<Essentia> ESSENTIAS = DeferredRegister.create(Essentia.class, ExPrimo.MODID);
	
	public static final RegistryObject<Essentia> IGNIS = register("ignis", 30);
	public static final RegistryObject<Essentia> AQUA = register("aqua", 180);
	public static final RegistryObject<Essentia> AER = register("aer", 60);
	public static final RegistryObject<Essentia> TERRA = register("terra", 120);
	public static final RegistryObject<Essentia> PRIMORDIUM = register("primordium", 270);
	
	private static RegistryObject<Essentia> register(String name, int hue) {
		RegistryObject<Essentia> e = ESSENTIAS.register(name, () -> new Essentia(name, hue));
		return e;
	}
}
