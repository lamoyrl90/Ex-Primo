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
	private static Queue<Chunk> loadedchunks = new LinkedList<>();
	private static final int MAX_ENQUEUED = 1000;
	private static final int CHUNKS_PER_TICK = 50;
	
	public static void onServerWorldTick(ServerWorld world) {
		updateLoadedChunksList(world);
		Set<Chunk> chunkstoflow = new HashSet<>();
		for(int i=0;i<CHUNKS_PER_TICK;i++) {
			Chunk nextFromQueue = loadedchunks.poll();
			if (nextFromQueue != null)
				chunkstoflow.add(nextFromQueue);
		}
		for (Chunk chunk : chunkstoflow) {
			ChunkEssentiaFlow chunkessentiaflow = new ChunkEssentiaFlow(chunk);
			chunkessentiaflow.flow();
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
				if (!loadedchunks.contains(chunk) && getNumberChunksInQueue() <= MAX_ENQUEUED)
					loadedchunks.add(chunk);
			}
		} catch (IllegalAccessException | InvocationTargetException e) {
            ExPrimo.LOGGER.fatal(e);
        }
	}
	
	public static int getNumberChunksInQueue() {
		return loadedchunks.size();
	}
}
