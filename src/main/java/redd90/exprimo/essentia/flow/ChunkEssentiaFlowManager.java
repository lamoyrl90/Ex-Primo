package redd90.exprimo.essentia.flow;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.server.ChunkHolder;
import net.minecraft.world.server.ChunkManager;
import net.minecraft.world.server.ServerChunkProvider;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import redd90.exprimo.ExPrimo;

public class ChunkEssentiaFlowManager {

	private static final Method LOADED_CHUNKS = ObfuscationReflectionHelper.findMethod(ChunkManager.class, "func_223491_f");
	private static Queue<Chunk> loadedchunkqueue = new LinkedList<>();
	private static Queue<ChunkEssentiaFlowProvider> tiletickqueue = new LinkedList<>();
	private static final int MAX_ENQUEUED = 1000;
	private static final int CHUNKS_PER_TICK = 25;
	
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
			chunkessentiaflow.flow();
		}
		
		while(!tiletickqueue.isEmpty()) {
			ChunkEssentiaFlowProvider tileflow = tiletickqueue.poll();
			tileflow.flow();
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
