package redd90.exprimo.registry;

import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import redd90.exprimo.ExPrimo;
import redd90.exprimo.essentia.Essentia;

public class Essentias {
	public static final DeferredRegister<Essentia> ESSENTIAS = DeferredRegister.create(Essentia.class, ExPrimo.MODID);

	public static final RegistryObject<Essentia> IGNIS = register("ignis", 16731904);
	public static final RegistryObject<Essentia> AQUA = register("aqua", 65535);
	public static final RegistryObject<Essentia> AER = register("aer", 16776960);
	public static final RegistryObject<Essentia> TERRA = register("terra", 65280);
	public static final RegistryObject<Essentia> PRIMORDIUM = register("primordium", 8388736);
	
	private static RegistryObject<Essentia> register(String name, int color) {
		return ESSENTIAS.register(name, () -> new Essentia(name, color));
	}
}
