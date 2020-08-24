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
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import redd90.exprimo.ExPrimo;
import redd90.exprimo.essentia.ChunkEssentiaBuilder;
import redd90.exprimo.essentia.EssentiaContainer;
import redd90.exprimo.essentia.EssentiaContainerCap;
import redd90.exprimo.essentia.flow.ChunkEssentiaFlow;

public class ModEventHandler {
	
	private static final Method LOADED_CHUNKS = ObfuscationReflectionHelper.findMethod(ChunkManager.class, "func_223491_f");
	
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
	
	@SuppressWarnings("unchecked")
	public static void onServerTick(TickEvent.WorldTickEvent event) {
		if (event.world.isRemote())
			return;
		ServerWorld world = (ServerWorld) event.world;
		
		if (world.getGameTime() % 20 == 0) {
            world.getProfiler().startSection(ExPrimo.MODID + ":onWorldTick");
            try {
            	ServerChunkProvider chunkprovider = world.getChunkProvider();
                ChunkManager manager = chunkprovider.chunkManager;
                Iterable<ChunkHolder> chunks = (Iterable<ChunkHolder>) LOADED_CHUNKS.invoke(manager);
                for (ChunkHolder holder : chunks) {
                    Chunk chunk = holder.getChunkIfComplete();
                    if (chunk == null)
                        continue;
                    ChunkEssentiaFlow chunkessentiaflow = new ChunkEssentiaFlow(chunk);
                    chunkessentiaflow.flow();
                }
            } catch (IllegalAccessException | InvocationTargetException e) {
                ExPrimo.LOGGER.fatal(e);
            }
            event.world.getProfiler().endSection();
		}
	}
    
}
