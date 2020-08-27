package redd90.exprimo.essentia;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.IntStream;

import net.minecraft.util.RegistryKey;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.OctavesNoiseGenerator;
import net.minecraft.world.server.ServerWorld;
import redd90.exprimo.registry.Essentias;
import redd90.exprimo.registry.ModRegistries;

public class ChunkEssentiaBuilder {
	
	private static final Essentia AQUA = Essentias.AQUA.get();
	private static final Essentia AER = Essentias.AER.get();
	private static final Essentia IGNIS = Essentias.IGNIS.get();
	private static final Essentia TERRA = Essentias.TERRA.get();
	private static final Essentia PRIMORDIUM = Essentias.PRIMORDIUM.get();

	private static HashMap<RegistryKey<World>, HashMap<Essentia, OctavesNoiseGenerator>> generators = new HashMap<>();
	
	private HashMap<Essentia, Double> builtweights = new HashMap<>();
	private EssentiaContainer container;
	private ServerWorld world;
	private final long seed;
	private final SharedSeedRandom seedRandom;
	private HashMap<Essentia, OctavesNoiseGenerator> noises;
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
		this.noises = getOrCreateGenerators(worldkey);
	}
	
	public static boolean isGenerated(EssentiaContainer containerIn) {
		return containerIn.isChunkGen();
	}
	
	private HashMap<Essentia, OctavesNoiseGenerator> getOrCreateGenerators(RegistryKey<World> worldkey) {
		if(generators.containsKey(worldkey))
			return generators.get(worldkey);
		
		HashMap<Essentia, OctavesNoiseGenerator> map = new HashMap<>();
		for (EssentiaStack stack : container.getStackSet().values()) {
			map.put(stack.getEssentia(), new OctavesNoiseGenerator(this.seedRandom, IntStream.rangeClosed(-15,0)));
		}
		generators.put(worldkey, map);
		return map;
	}
	
	public static void validateContainer(ServerWorld world, Chunk chunk) {
		EssentiaContainer containerIn = (EssentiaContainer) chunk.getCapability(EssentiaContainerCap.ESSENTIA_CONTAINER).orElse(null); 
		if (containerIn != null && !isGenerated(containerIn)) {
			ChunkEssentiaBuilder builder = new ChunkEssentiaBuilder(world, chunk);
			builder.applyWeights();
			builder.applyNoise();
			builder.initializeAmounts();
			containerIn.setStackSet(builder.container.getStackSet());
			containerIn.setEssentiaWeights(builder.container.getEssentiaWeights());
			containerIn.setChunkGen(true);
		}
		//container.setEssentiaWeights(builtweights);
		//return container;
	}

	private ChunkEssentiaBuilder applyNoise() {
		for(EssentiaStack stack : container.getStackSet().values()) {
			Essentia essentia = stack.getEssentia();
			double point = Math.floor(noises.get(essentia).noiseAt(x<<8, z<<8, world.getSeaLevel(), 1.0));
			if (point > 2) {
				double newweight = builtweights.get(essentia);
				newweight *= 1 + (point-2);
				builtweights.put(essentia, newweight);
			}
		}
		return this;
	}
	
	private ChunkEssentiaBuilder applyWeights() {
		HashMap<Essentia, Double> weights = new HashMap<>();
		HashMap<Essentia, List<Integer>> factors = new HashMap<>();
		
		for (Essentia essentia : ModRegistries.ESSENTIAS) {
			factors.put(essentia, new ArrayList<>());
		}
		
		int sealevel = world.getSeaLevel();
		
		Mutable npos = new Mutable();
		npos.setPos(x, sealevel, z);
		for (int x1=0;x1 < 16;x1++) {
			npos.setX(x+x1);
			for(int z1=0;z1 < 16;z1++) {
				npos.setZ(z+z1);
				Biome b = world.getNoiseBiome(npos.getX(), npos.getY(), npos.getZ());
				Biome.Category cat = b.getCategory();
				switch (cat) {
				case BEACH:
					factors.get(AQUA).add(2);
					break;
				case TAIGA:
					factors.get(AER).add(3);
					factors.get(IGNIS).add(-1);
					break;
				case THEEND:
					break;
				case SWAMP:
					factors.get(AQUA).add(1);
					factors.get(TERRA).add(1);
					break;
				case SAVANNA:
					factors.get(IGNIS).add(2);
					factors.get(AER).add(1);
					factors.get(AQUA).add(-1);
					break;
				case RIVER:
					factors.get(AQUA).add(2);
					break;
				case PLAINS:
					factors.get(AER).add(2);
					break;
				case OCEAN:
					factors.get(AQUA).add(2);
					break;
				case NONE:
					break;
				case NETHER:
					break;
				case MUSHROOM:
					factors.get(TERRA).add(2);
					break;
				case MESA:
					factors.get(TERRA).add(2);
					factors.get(IGNIS).add(2);
					factors.get(AQUA).add(-2);
					break;
				case JUNGLE:
					factors.get(TERRA).add(1);
					factors.get(AQUA).add(1);
					break;
				case ICY:
					factors.get(AER).add(2);
					factors.get(AQUA).add(2);
					factors.get(IGNIS).add(-2);
				case FOREST:
					factors.get(TERRA).add(2);
				case EXTREME_HILLS:
					factors.get(TERRA).add(4);
					factors.get(AER).add(2);
					factors.get(IGNIS).add(-2);
					factors.get(AQUA).add(-2);
					break;
				case DESERT:
					factors.get(IGNIS).add(4);
					factors.get(AER).add(1);
					factors.get(AQUA).add(-3);
				default:
					break;
				}
			}
		}
		
		int aquatotal = 1000 + sumFactors(factors.get(AQUA));
		int ignistotal = 1000 +sumFactors(factors.get(IGNIS));
		int aertotal = 1000 + sumFactors(factors.get(AER));
		int terratotal = 1000 + sumFactors(factors.get(TERRA));
		double primordium = 1000;
		
		double total = aquatotal + ignistotal + aertotal + terratotal + primordium;
		
		weights.put(AQUA, (5 * ((double)aquatotal / total)));
		weights.put(AER, (5 * ((double)aertotal / total)));
		weights.put(IGNIS, (5 * ((double)ignistotal / total)));
		weights.put(TERRA, (5 * ((double)terratotal / total)));
		weights.put(PRIMORDIUM, (5 * ((double)primordium / total)));
		
		container.setEssentiaWeights(weights);
		
		for (Entry<Essentia, Double> entry : weights.entrySet()) {
			builtweights.put(entry.getKey(), entry.getValue());
		}
		
		return this;
	}
	
	private int sumFactors(List<Integer> list) {
		int sum = 0;
		for (Integer i : list) {
			sum += i;
		}
		return sum;
	}
	
	private ChunkEssentiaBuilder initializeAmounts() {
		for (EssentiaStack stack : container.getStackSet().values()) {
			int amount = 1000;//(int) (1000 * builtweights.get(stack.getEssentia()));
			container.getStack(stack.getEssentia().getKey()).setAmount(amount);
		}
		
		return this;
	}
}
