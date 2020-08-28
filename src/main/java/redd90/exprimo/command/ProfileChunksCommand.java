package redd90.exprimo.command;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import com.mojang.brigadier.CommandDispatcher;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.OctavesNoiseGenerator;
import net.minecraft.world.server.ChunkHolder;
import net.minecraft.world.server.ChunkManager;
import net.minecraft.world.server.ServerChunkProvider;
import net.minecraft.world.server.ServerWorld;
import redd90.exprimo.ExPrimo;
import redd90.exprimo.essentia.flow.ChunkEssentiaFlowManager;
import redd90.exprimo.util.ModMath;

public class ProfileChunksCommand {
	
		private static List<Float> heightavgs = new ArrayList<>();
		private static List<Float> heightvars = new ArrayList<>();
		private static List<Float> tempavgs = new ArrayList<>();
		private static List<Float> humidityavgs = new ArrayList<>();
		private static List<Float> noiseavgs = new ArrayList<>();
		private static Set<Chunk> profiledchunks = new HashSet<>();
		private static OctavesNoiseGenerator noise = null;
		
	public static void register(CommandDispatcher<CommandSource> dispatcher) {
		dispatcher.register(Commands.literal("profile_chunks")
				.requires((source)->{return source.hasPermissionLevel(2);})
				.executes((context) ->
				{return profileChunks(context.getSource());}));
	}
	
	@SuppressWarnings("unchecked")
	private static int profileChunks(CommandSource source) {
		ServerWorld world = source.getWorld();
		ServerChunkProvider provider = world.getChunkProvider();
		ChunkManager manager = provider.chunkManager;
		if (noise == null)
			noise = new OctavesNoiseGenerator(new SharedSeedRandom(world.getSeed()), IntStream.rangeClosed(-15,0));
		
		
		Mutable pos = new Mutable();
		pos.setPos(source.getEntity().getPosition());
		try {
			Iterable<ChunkHolder> loadedChunks = (Iterable<ChunkHolder>) ChunkEssentiaFlowManager.LOADED_CHUNKS.invoke(manager);
			for (ChunkHolder holder : loadedChunks) {
				List<Float> postemps = new ArrayList<>();
				List<Float> poshumidities = new ArrayList<>();
				List<Float> posheights = new ArrayList<>();
				List<Float> posnoises = new ArrayList<>();
				
				Chunk chunk = holder.getChunkIfComplete();
				if (chunk == null)
					continue;
				
				if (profiledchunks.contains(chunk))
					continue;
				//Heightmap heightmap = chunk.getHeightmap(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
				int x = chunk.getPos().getXStart();
				int z = chunk.getPos().getZStart();
				posnoises.add((float) noise.noiseAt(pos.getX()<<8, pos.getZ()<<8, world.getSeaLevel(), 1.0));
				
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
				
				
				
				heightavgs.add(ModMath.getAverage(posheights));
				heightvars.add(ModMath.getVariance(posheights));
				tempavgs.add(ModMath.getAverage(postemps));
				humidityavgs.add(ModMath.getAverage(poshumidities));
				noiseavgs.add(ModMath.getAverage(posnoises));
				profiledchunks.add(chunk);
				
				
			}
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			ExPrimo.LOGGER.fatal(e);
		}
		
		float temptotalavg = ModMath.getAverage(tempavgs);
		float temptotalvar = ModMath.getVariance(tempavgs);
		float humiditytotalavg = ModMath.getAverage(humidityavgs);
		float humiditytotalvar = ModMath.getVariance(humidityavgs);
		float heighttotalavg = ModMath.getAverage(heightavgs);
		float heighttotalvar = ModMath.getVariance(heightavgs);
		float heightvartotalavg = ModMath.getAverage(heightvars);
		float heightvartotalvar = ModMath.getVariance(heightvars);
		float noisetotalavg = ModMath.getAverage(noiseavgs);
		float noisetotalvar = ModMath.getVariance(noiseavgs);
		
		StringTextComponent msg1 = new StringTextComponent("Profiled " + profiledchunks.size() + " chunks");
		StringTextComponent msg2 = new StringTextComponent("Temperature average: " + temptotalavg);
		StringTextComponent msg3 = new StringTextComponent("Temperature variance: " + temptotalvar);
		StringTextComponent msg4 = new StringTextComponent("Humidity average: " + humiditytotalavg);
		StringTextComponent msg5 = new StringTextComponent("Humidity variance: " + humiditytotalvar);
		StringTextComponent msg6 = new StringTextComponent("Height average: " + heighttotalavg);
		StringTextComponent msg7 = new StringTextComponent("Height variance: " + heighttotalvar);
		StringTextComponent msg8 = new StringTextComponent("Height variance average: " + heightvartotalavg);
		StringTextComponent msg9 = new StringTextComponent("Height variance variance: " + heightvartotalvar);
		StringTextComponent msg10 = new StringTextComponent("Noise average: " + noisetotalavg);
		StringTextComponent msg11 = new StringTextComponent("Noise variance: " + noisetotalvar);
		
		source.sendFeedback(msg1, true);
		source.sendFeedback(msg2, true);
		source.sendFeedback(msg3, true);
		source.sendFeedback(msg4, true);
		source.sendFeedback(msg5, true);
		source.sendFeedback(msg6, true);
		source.sendFeedback(msg7, true);
		source.sendFeedback(msg8, true);
		source.sendFeedback(msg9, true);
		source.sendFeedback(msg10, true);
		source.sendFeedback(msg11, true);
		
		return 1;
	}

}
