package redd90.exprimo.registry;

import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import redd90.exprimo.ExPrimo;
import redd90.exprimo.essentia.Essentia;

public class Essentias {
	public static final DeferredRegister<Essentia> ESSENTIAS = DeferredRegister.create(Essentia.class, ExPrimo.MODID);

	public static final RegistryObject<Essentia> IGNIS = register("ignis");
	public static final RegistryObject<Essentia> AQUA = register("aqua");
	public static final RegistryObject<Essentia> AER = register("aer");
	public static final RegistryObject<Essentia> TERRA = register("terra");
	public static final RegistryObject<Essentia> PRIMORDIUM = register("primordium");
	
	private static RegistryObject<Essentia> register(String name) {
		return ESSENTIAS.register(name, () -> new Essentia(name));
	}
}
