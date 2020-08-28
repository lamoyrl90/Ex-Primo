package redd90.exprimo.essentia;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.IntStream;

import net.minecraft.util.RegistryKey;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.OctavesNoiseGenerator;
import net.minecraft.world.server.ServerWorld;
import redd90.exprimo.registry.Essentias;
import redd90.exprimo.util.ModConstants;
import redd90.exprimo.util.ModMath;

public class ChunkEssentiaBuilder {
	
	private static HashMap<RegistryKey<World>, OctavesNoiseGenerator> primordiumNoises = new HashMap<>();
	
	private EssentiaContainer container;
	private ServerWorld world;
	private final long seed;
	private final SharedSeedRandom seedRandom;
	private OctavesNoiseGenerator noise;
	private final int x;
	private final int z;
	private Chunk chunk;
	
	public ChunkEssentiaBuilder(World worldIn, Chunk chunk) {
		RegistryKey<World> worldkey = worldIn.getDimensionKey();
		this.world = worldIn.getServer().getWorld(worldkey);
		this.chunk = chunk;
		this.seed = world.getSeed();
		this.seedRandom = new SharedSeedRandom(seed);
		this.x = this.chunk.getPos().getXStart();
		this.z = this.chunk.getPos().getZStart();
		
		this.container = new EssentiaContainer();
		this.noise = getOrCreateGenerator(worldkey);
	}
	
	public static boolean isGenerated(EssentiaContainer containerIn) {
		return containerIn.isChunkGen();
	}
	
	private OctavesNoiseGenerator getOrCreateGenerator(RegistryKey<World> worldkey) {
		if(primordiumNoises.containsKey(worldkey))
			return primordiumNoises.get(worldkey);
		
		OctavesNoiseGenerator gen = new OctavesNoiseGenerator(this.seedRandom, IntStream.rangeClosed(-15,0));
		primordiumNoises.put(worldkey, gen);
		
		return gen;
	}
	
	public static void validateContainer(ServerWorld world, Chunk chunk) {
		EssentiaContainer containerIn = (EssentiaContainer) chunk.getCapability(EssentiaContainerCap.ESSENTIA_CONTAINER).orElse(null); 
		if (containerIn != null && !isGenerated(containerIn)) {
			ChunkEssentiaBuilder builder = new ChunkEssentiaBuilder(world, chunk);
			builder.profileChunk();
			containerIn.setStackSet(builder.container.getStackSet());
			containerIn.setEquilibrium(builder.container.getEquilibrium());
			containerIn.setChunkGen(true);
		}
	}

	private ChunkEssentiaBuilder profileChunk() {
		Mutable pos = new Mutable();
		pos.setPos(chunk.getPos().getXStart(), world.getSeaLevel(), chunk.getPos().getZStart());
		List<Float> postemps = new ArrayList<>();
		List<Float> poshumidities = new ArrayList<>();
		List<Float> posheights = new ArrayList<>();
		
		for (int i=0; i<16; i++) {
			pos.setX(x + i);
			for (int j=0; j<16; j++) {
				pos.setZ(z + j);
				int y = chunk.getTopBlockY(Heightmap.Type.MOTION_BLOCKING, x+i,z+j);
				if (y < 0 || y > 255)
					continue;
				pos.setY(y);
				
				Biome b = world.getBiome(pos);
				postemps.add(b.getTemperature(pos));
				poshumidities.add(b.getDownfall());
				posheights.add((float)y);
			}
		}
		
		float heightavg = ModMath.getAverage(posheights);
		float heightvar = ModMath.getVariance(posheights);
		float humidityavg = ModMath.getAverage(poshumidities);
		float tempavg = ModMath.getAverage(postemps);
		double primordiumNoise = noise.noiseAt(x<<8, z<<8, world.getSeaLevel(), 1.0);
		
		double ignisz = (tempavg - ModConstants.TEMPERATURE_AVG) / ModConstants.TEMPERATURE_STD_DEV;
		double aquaz = (humidityavg - ModConstants.HUMIDITY_AVG) / ModConstants.HUMIDITY_STD_DEV;
		double terraz = (heightavg - ModConstants.HEIGHT_AVG) / ModConstants.HEIGHT_STD_DEV;
		double aerz = (ModConstants.HEIGHT_VAR_AVG - heightvar) / ModConstants.HEIGHT_VAR_STD_DEV;
		double primordiumz = (primordiumNoise - ModConstants.PRIMORDIUM_NOISE_AVG) / ModConstants.PRIMORDIUM_NOISE_STD_DEV;
		
		int igniseq = (int) Math.max(0,Math.floor(ModMath.getNormalP(ignisz) * 2000));
		int aquaeq = (int) Math.max(0,Math.floor(ModMath.getNormalP(aquaz) * 2000));
		int terraeq = (int) Math.max(0,Math.floor(ModMath.getNormalP(terraz) * 2000));
		int aereq = (int) Math.max(0,Math.floor(ModMath.getNormalP(aerz) * 2000));
		int primordiumeq = (int) Math.max(0,Math.floor(ModMath.getNormalP(primordiumz) * 2000));
		
		HashMap<Essentia, Integer> eqmap = new HashMap<>();
		eqmap.put(Essentias.IGNIS.get(), igniseq);
		eqmap.put(Essentias.AQUA.get(), aquaeq);
		eqmap.put(Essentias.TERRA.get(), terraeq);
		eqmap.put(Essentias.AER.get(), aereq);
		eqmap.put(Essentias.PRIMORDIUM.get(), primordiumeq);
		
		Equilibrium eq = new Equilibrium(eqmap);
		container.setEquilibrium(eq);
		
		container.setStack(Essentias.IGNIS.get(), igniseq+1000);
		container.setStack(Essentias.AQUA.get(), aquaeq+1000);
		container.setStack(Essentias.TERRA.get(), terraeq+1000);
		container.setStack(Essentias.AER.get(), aereq+1000);
		container.setStack(Essentias.PRIMORDIUM.get(), primordiumeq+1000);
		
		return this;
	}
}
