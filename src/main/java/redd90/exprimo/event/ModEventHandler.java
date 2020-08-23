package redd90.exprimo.event;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.server.ChunkHolder;
import net.minecraft.world.server.ChunkManager;
import net.minecraft.world.server.ServerChunkProvider;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import redd90.exprimo.ExPrimo;
import redd90.exprimo.essentia.ChunkEssentiaBuilder;
import redd90.exprimo.essentia.EssentiaContainer;
import redd90.exprimo.essentia.EssentiaContainerCap;
import redd90.exprimo.essentia.EssentiaStack;

public class ModEventHandler {
	private static final Method GET_LOADED_CHUNKS_METHOD = ObfuscationReflectionHelper.findMethod(ChunkManager.class, "getLoadedChunksIterable");
	
	public static void onAttachChunkCaps(AttachCapabilitiesEvent<Chunk> event) {
		Chunk chunk = event.getObject() instanceof Chunk ? event.getObject() : null;
		
		if(chunk != null && EssentiaContainerCap.canAttachTo(chunk)) {
			World world = chunk.getWorld();
			if (!world.isRemote()) {
				ChunkEssentiaBuilder builder = new ChunkEssentiaBuilder(world, chunk);
				EssentiaContainer container = builder.createContainer();
				event.addCapability(EssentiaContainerCap.LOCATION, container);
			}
		}
	}
	
	public static void onAttachEntityCaps(AttachCapabilitiesEvent<Entity> event) {
		PlayerEntity player = event.getObject() instanceof PlayerEntity ? (PlayerEntity) event.getObject() : null;
		if(player != null && EssentiaContainerCap.canAttachTo(player)) {
			event.addCapability(EssentiaContainerCap.LOCATION, new EssentiaContainer(player));
		}
	}
	/*
	public static void onAttachItemCaps(AttachCapabilitiesEvent<ItemStack> event) {
		ItemStack stack = event.getObject() instanceof ItemStack ? event.getObject() : null;
		if(stack != null && EssentiaContainerCap.canAttachTo(stack)) {
			event.addCapability(EssentiaContainerCap.LOCATION, new EssentiaContainer(stack));
		}
	}*/
	
	
	public static void onWorldTick(TickEvent.WorldTickEvent event) {
		World world = event.world;
		if(!world.isRemote() && event.phase == TickEvent.Phase.END) {
			if(world.getGameTime() % 20 == 0) {
				event.world.getProfiler().startSection(ExPrimo.MODID + ":onWorldTick");
				try {
					@SuppressWarnings("resource") //Inside try/catch
					ChunkManager manager = ((ServerChunkProvider) world.getChunkProvider()).chunkManager;
					@SuppressWarnings("unchecked") //Type safe inside reflection
					Iterable<ChunkHolder> chunks = (Iterable<ChunkHolder>) GET_LOADED_CHUNKS_METHOD.invoke(manager);
					for (ChunkHolder holder : chunks) {
                        Chunk chunk = holder.getChunkIfComplete();
                        if (chunk == null)
                            continue;
                        EssentiaContainer container = (EssentiaContainer) chunk.getCapability(EssentiaContainerCap.ESSENTIA_CONTAINER, null).orElse(null);
                        if (container != null)
                        	for(EssentiaStack stack : container.getStackSet().values()) {
                        		stack.update();
                        	}
                    }
				} catch (IllegalAccessException | InvocationTargetException e) {
                    ExPrimo.LOGGER.fatal(e);
                }
				event.world.getProfiler().endSection();
			}
		}
    }
    
}
