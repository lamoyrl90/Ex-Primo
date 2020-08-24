package redd90.exprimo.essentia.flow;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.server.ChunkHolder;
import net.minecraft.world.server.ChunkManager;
import net.minecraft.world.server.ServerChunkProvider;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import redd90.exprimo.ExPrimo;

public class ChunkEssentiaFlowManager {

	private static final Method LOADED_CHUNKS = ObfuscationReflectionHelper.findMethod(ChunkManager.class, "func_223491_f");
	
	@SuppressWarnings("unchecked")
	public static void manageChunkFlows(ServerWorld world) {
		try {
        	ServerChunkProvider chunkprovider = world.getChunkProvider();
            ChunkManager manager = chunkprovider.chunkManager;
            List<ChunkHolder> loadedchunks = StreamSupport.stream(((Iterable<ChunkHolder>) LOADED_CHUNKS.invoke(manager)).spliterator(), false).collect(Collectors.toList());
            Set<Chunk> chunkstoflow = new HashSet<>();
            int numberloadedchunks = loadedchunks.size();
            if (numberloadedchunks != 0) {
            	Chunk randomFromLoaded = loadedchunks.get(world.getRandom().nextInt(numberloadedchunks)).getChunkIfComplete();
            	if (randomFromLoaded != null)
            		chunkstoflow.add(randomFromLoaded);
            }
            for (Chunk chunk : chunkstoflow) {
                ChunkEssentiaFlow chunkessentiaflow = new ChunkEssentiaFlow(chunk);
                chunkessentiaflow.flow();
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            ExPrimo.LOGGER.fatal(e);
        }
	}
	
}
