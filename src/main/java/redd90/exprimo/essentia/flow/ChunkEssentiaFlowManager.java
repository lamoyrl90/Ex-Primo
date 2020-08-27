package redd90.exprimo.essentia.flow;

import java.awt.Color;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.Set;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.server.ChunkHolder;
import net.minecraft.world.server.ChunkManager;
import net.minecraft.world.server.ServerChunkProvider;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import redd90.exprimo.ExPrimo;
import redd90.exprimo.client.particle.EssentiaFlowParticleData;

public class ChunkEssentiaFlowManager {

	private static final Method LOADED_CHUNKS = ObfuscationReflectionHelper.findMethod(ChunkManager.class, "func_223491_f");
	private static Queue<Chunk> loadedchunkqueue = new LinkedList<>();
	private static Queue<ChunkEssentiaFlowProvider> tiletickqueue = new LinkedList<>();
	private static final int MAX_ENQUEUED = 1000;
	private static final int CHUNKS_PER_TICK = 10;
	
	public static void onServerWorldTick(ServerWorld world) {
		updateLoadedChunksList(world);
		Set<Chunk> chunkstoflow = new HashSet<>();
		for(int i=0;i<CHUNKS_PER_TICK;i++) {
			Chunk nextFromQueue = loadedchunkqueue.poll();
			if (nextFromQueue != null)
				chunkstoflow.add(nextFromQueue);
		}
		
		for (Chunk chunk : chunkstoflow) {
			ChunkEssentiaFlowProvider chunkessentiaflow = new ChunkEssentiaFlowProvider(chunk);
			chunkessentiaflow.flow(1.0);
		}
		
		while(!tiletickqueue.isEmpty()) {
			ChunkEssentiaFlowProvider tileflow = tiletickqueue.poll();
			Color color = new Color(tileflow.flow(tileflow.getFactor()));
			double b = (double) color.getBlue() / 255;
			double g = (double) color.getGreen() / 255;
			double r = (double) color.getRed() / 255;
			TileEntity tile = tileflow.getTile();
			if (r == 0 && g == 0 && b == 0) {
				continue;
			}
			if (tile != null) {
				BlockPos pos = tile.getPos();
				Random rand = world.getRandom();
				
				double px = pos.getX() + rand.nextDouble();
				double py = pos.getY() + rand.nextDouble() + 1.0;
				double pz = pos.getZ() + rand.nextDouble();
				
				world.spawnParticle(new EssentiaFlowParticleData(r, g, b), 
						px, 
						py, 
						pz, 
						1, (px - pos.getX())/10, (py - pos.getY()-1)/10, (pz - pos.getZ())/10, 0.0D);
			}
		}
	}
	
	
	@SuppressWarnings("unchecked") //Type-safe through invocation
	private static void updateLoadedChunksList(ServerWorld world) {
		try {
			ServerChunkProvider chunkprovider = world.getChunkProvider();
            ChunkManager manager = chunkprovider.chunkManager;
			Iterable<ChunkHolder> serverloadedchunks = (Iterable<ChunkHolder>) LOADED_CHUNKS.invoke(manager);
			for (ChunkHolder holder : serverloadedchunks) {
				Chunk chunk = holder.getChunkIfComplete();
				if (chunk == null)
					continue;
				if (!loadedchunkqueue.contains(chunk) && getNumberChunksInQueue() <= MAX_ENQUEUED)
					loadedchunkqueue.add(chunk);
			}
		} catch (IllegalAccessException | InvocationTargetException e) {
            ExPrimo.LOGGER.fatal(e);
        }
	}
	
	public static int getNumberChunksInQueue() {
		return loadedchunkqueue.size();
	}
	
	public static void scheduleTileTick(ChunkEssentiaFlowProvider provider) {
		tiletickqueue.add(provider);
	}
}
