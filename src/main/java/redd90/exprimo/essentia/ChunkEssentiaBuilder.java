package redd90.exprimo.essentia;

import java.util.HashMap;
import java.util.stream.IntStream;

import net.minecraft.util.RegistryKey;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.OctavesNoiseGenerator;
import net.minecraft.world.server.ServerWorld;

public class ChunkEssentiaBuilder {

	private static final int MAX_VALUE = 10000;
	private static HashMap<RegistryKey<World>, HashMap<String, OctavesNoiseGenerator>> generators = new HashMap<>();
	
	private EssentiaContainer container;
	private ServerWorld world;
	private final long seed;
	private final SharedSeedRandom seedRandom;
	private final HashMap<String, OctavesNoiseGenerator> noises;
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
		
		this.container = new EssentiaContainer(chunk);
		this.noises = getOrCreateGenerators(worldkey);
	}
	
	private HashMap<String, OctavesNoiseGenerator> getOrCreateGenerators(RegistryKey<World> worldkey) {
		if(generators.containsKey(worldkey))
			return generators.get(worldkey);
		
		HashMap<String, OctavesNoiseGenerator> map = new HashMap<>();
		for (EssentiaStack stack : container.getStackSet().values()) {
			map.put(stack.getEssentia().getKey(), new OctavesNoiseGenerator(this.seedRandom, IntStream.rangeClosed(-15,0)));
		}
		generators.put(worldkey, map);
		return map;
	}
	
	public EssentiaContainer createContainer() {
		this.applyNoise();
		return container;
	}

	private ChunkEssentiaBuilder applyNoise() {
		for(EssentiaStack stack : container.getStackSet().values()) {
			int point = (int) Math.floor(noises.get(stack.getEssentia().getKey()).noiseAt(x<<8, z<<8, world.getSeaLevel(), 1.0) * MAX_VALUE);
			if (point > 0)
				stack.setAmount(point);
		}
		return this;
	}
}
