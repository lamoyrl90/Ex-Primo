package redd90.exprimo.essentia;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.IntStream;

import net.minecraft.util.RegistryKey;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.PerlinNoiseGenerator;
import net.minecraft.world.server.ServerWorld;

public class ChunkEssentiaBuilder {

	private static final int MAX_VALUE = 10000;
	private static HashMap<RegistryKey<World>, List<PerlinNoiseGenerator>> generators = new HashMap<>();
	
	private EssentiaContainer container;
	private ServerWorld world;
	private final long seed;
	private final SharedSeedRandom seedRandom;
	private final List<PerlinNoiseGenerator> noises;
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
	
	private List<PerlinNoiseGenerator> getOrCreateGenerators(RegistryKey<World> worldkey) {
		if(generators.containsKey(worldkey))
			return generators.get(worldkey);
		
		List<PerlinNoiseGenerator> list = new ArrayList<>();
		for (int i=0;i<container.getStackSet().size();i++) {
			list.add(new PerlinNoiseGenerator(this.seedRandom, IntStream.rangeClosed(-1,0)));
		}
		generators.put(worldkey, list);
		return list;
	}
	
	public EssentiaContainer createContainer() {
		this.applyNoise();
		return container;
	}

	private ChunkEssentiaBuilder applyNoise() {
		int i=0;
		for(EssentiaStack stack : container.getStackSet().values()) {
			int point = (int) Math.floor(noises.get(i).noiseAt(x >> 4, world.getSeaLevel() >> 4, z >> 4, 0.0) * MAX_VALUE);
			if (point > 0)
				stack.setWithoutUpdate(point);
			i++;
		}
		return this;
	}
	
}
