package redd90.exprimo.essentia.flow;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.server.ChunkHolder;
import net.minecraft.world.server.ChunkManager;
import net.minecraft.world.server.ServerChunkProvider;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import redd90.exprimo.ExPrimo;
import redd90.exprimo.essentia.EssentiaContainer;
import redd90.exprimo.essentia.EssentiaContainerCap;

public class ChunkEssentiaFlowManager {

	private static final Method LOADED_CHUNKS = ObfuscationReflectionHelper.findMethod(ChunkManager.class, "func_223491_f");
	private static Queue<Chunk> loadedchunkqueue = new LinkedList<>();
	private static Set<Chunk> scheduled = new HashSet<>();
	private static final int MAX_ENQUEUED = 1000;
	private static final int CHUNKS_PER_TICK = 50;
	
	public static void onServerWorldTick(ServerWorld world) {
		updateLoadedChunksList(world);
		Set<Chunk> chunkstoflow = new HashSet<>();
		for(int i=0;i<CHUNKS_PER_TICK;i++) {
			Chunk nextFromQueue = loadedchunkqueue.poll();
			if (nextFromQueue != null)
				chunkstoflow.add(nextFromQueue);
		}
		for(Chunk chunk : scheduled) {
			ChunkPos chunkpos = chunk.getPos();
			if(!world.chunkExists(chunkpos.x, chunkpos.z))
				continue;
			
			EssentiaContainer container = (EssentiaContainer) chunk.getCapability(EssentiaContainerCap.ESSENTIA_CONTAINER).orElse(null);
			if (container != null) {
				if (container.shouldTick(world)) {
					chunkstoflow.add(chunk);
				}
			}
		}
		for (Chunk chunk : chunkstoflow) {
			ChunkEssentiaFlowProvider chunkessentiaflow = new ChunkEssentiaFlowProvider(chunk);
			chunkessentiaflow.flow();
		}
	}
	
	public static void scheduleChunk(Chunk chunk) {
		scheduled.add(chunk);
	}
	
	public static void unscheduleChunk(Chunk chunk) {
		if (scheduled.contains(chunk))
			scheduled.remove(chunk);
		else {
			ExPrimo.LOGGER.error("Cannot unschedule chunk {}: it is not on the scheduled chunk list", chunk);
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
	
	public class PeriodicFlowTick implements IFlowTickPredicate {
		private final int interval;
		
		public PeriodicFlowTick(int interval) {
			this.interval = interval;
		}

		@Override
		public boolean test(IWorld world) {
			if (world instanceof ServerWorld) {
				return (((ServerWorld) world).getGameTime() % interval == 0);
			}
			return false;
		}
	}
}
