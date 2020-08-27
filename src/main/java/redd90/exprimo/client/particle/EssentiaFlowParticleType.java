package redd90.exprimo.client.particle;

import com.mojang.serialization.Codec;

import net.minecraft.client.particle.IAnimatedSprite;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.ParticleType;

public class EssentiaFlowParticleType extends ParticleType<EssentiaFlowParticleData> {

	public EssentiaFlowParticleType() {
		super(false, EssentiaFlowParticleData.DESERIALIZER);
	}

	@Override
	public Codec<EssentiaFlowParticleData> func_230522_e_() {
		return EssentiaFlowParticleData.CODEC;
	}

	public static class Factory implements IParticleFactory<EssentiaFlowParticleData> {
		private final IAnimatedSprite sprite;
		
		public Factory(IAnimatedSprite sprite) {
			this.sprite = sprite;
		}

		@Override
		public Particle makeParticle(EssentiaFlowParticleData data, ClientWorld world, double x, double y, double z,
				double xSpeed, double ySpeed, double zSpeed) {
			Particle particle = new EssentiaFlowParticle(world, x, y, z, xSpeed, ySpeed, zSpeed, sprite);
			particle.setColor((float)data.r, (float)data.g, (float)data.b);
			return particle;
		}
		
	}
}
