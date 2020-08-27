package redd90.exprimo.registry;

import net.minecraft.client.Minecraft;
import net.minecraft.particles.ParticleType;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.event.RegistryEvent;
import redd90.exprimo.client.particle.EssentiaFlowParticleData;
import redd90.exprimo.client.particle.EssentiaFlowParticleType;

public class ModParticles {

	public static final ParticleType<EssentiaFlowParticleData> ESSENTIA_FLOW = new EssentiaFlowParticleType();
	
	public static void onRegisterParticleTypes(RegistryEvent.Register<ParticleType<?>> event) {
		event.getRegistry().register(ESSENTIA_FLOW.setRegistryName("essentia_flow"));
	}
	
	public static void onRegisterParticleFactories(ParticleFactoryRegisterEvent event) {
		Minecraft mc = Minecraft.getInstance();
		mc.particles.registerFactory(ESSENTIA_FLOW, EssentiaFlowParticleType.Factory::new);
	}
	
}
