package redd90.exprimo.client.particle;

import java.util.Locale;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.network.PacketBuffer;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleType;
import net.minecraftforge.registries.ForgeRegistries;
import redd90.exprimo.registry.ModParticles;

public class EssentiaFlowParticleData implements IParticleData {

	public static final Codec<EssentiaFlowParticleData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.DOUBLE.fieldOf("r").forGetter(d -> d.r),
			Codec.DOUBLE.fieldOf("g").forGetter(d -> d.g),
			Codec.DOUBLE.fieldOf("b").forGetter(d -> d.b)
	).apply(instance, EssentiaFlowParticleData::new));
	
	public final double r;
	public final double g;
	public final double b;
	
	public EssentiaFlowParticleData(double r, double g, double b) {
		this.r = r;
		this.g = g;
		this.b = b;
	}
	
	@Override
	public ParticleType<?> getType() {
		return ModParticles.ESSENTIA_FLOW;
	}

	@Override
	public void write(PacketBuffer buffer) {
		buffer.writeDouble(r);
		buffer.writeDouble(g);
		buffer.writeDouble(b);
	}

	@Override
	public String getParameters() {
		return String.format(Locale.ROOT, "%s %.2f %.2f %.2f",
				ForgeRegistries.PARTICLE_TYPES.getKey(this.getType()), this.r, this.g, this.b);
	}
	
	@SuppressWarnings("deprecation")
	public static final IDeserializer<EssentiaFlowParticleData> DESERIALIZER = new IDeserializer<EssentiaFlowParticleData>() {

		@Override
		public EssentiaFlowParticleData deserialize(ParticleType<EssentiaFlowParticleData> particleTypeIn,
				StringReader reader) throws CommandSyntaxException {
			reader.expect(' ');
			double r = reader.readDouble();
			reader.expect(' ');
			double g = reader.readDouble();
			reader.expect(' ');
			double b = reader.readDouble();
			return new EssentiaFlowParticleData(r, g, b);
		}

		@Override
		public EssentiaFlowParticleData read(ParticleType<EssentiaFlowParticleData> particleTypeIn,
				PacketBuffer buffer) {
			return new EssentiaFlowParticleData(buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
		}
		
	};

}
