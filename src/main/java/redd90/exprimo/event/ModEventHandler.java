package redd90.exprimo.event;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.world.ChunkEvent;
import redd90.exprimo.ExPrimo;
import redd90.exprimo.command.ProfileChunksCommand;
import redd90.exprimo.command.SetChunkEssentiaCommand;
import redd90.exprimo.essentia.ChunkEssentiaBuilder;
import redd90.exprimo.essentia.EssentiaContainer;
import redd90.exprimo.essentia.EssentiaContainerCap;
import redd90.exprimo.essentia.flow.ChunkEssentiaFlowManager;
import redd90.exprimo.util.ColorUtil;

public class ModEventHandler {
	
	public static void onAttachChunkCaps(AttachCapabilitiesEvent<Chunk> event) {
		Chunk chunk = event.getObject() instanceof Chunk ? event.getObject() : null;
		
		if(chunk != null && EssentiaContainerCap.canAttachTo(chunk)) {
			World world = chunk.getWorld();
			if (!world.isRemote()) {
				event.addCapability(EssentiaContainerCap.LOCATION, new EssentiaContainer(chunk));
			}
		}
	}
	
	public static void onAttachEntityCaps(AttachCapabilitiesEvent<Entity> event) {
		PlayerEntity player = event.getObject() instanceof PlayerEntity ? (PlayerEntity) event.getObject() : null;
		if(player != null && EssentiaContainerCap.canAttachTo(player)) {
			event.addCapability(EssentiaContainerCap.LOCATION, new EssentiaContainer(player));
		}
	}
	
	public static void onAttachItemCaps(AttachCapabilitiesEvent<ItemStack> event) {
		ItemStack stack = event.getObject() instanceof ItemStack ? event.getObject() : null;
		if(stack != null && EssentiaContainerCap.canAttachTo(stack)) {
			event.addCapability(EssentiaContainerCap.LOCATION, new EssentiaContainer(stack));
		}
	}
	
	public static void onServerWorldTick(TickEvent.WorldTickEvent event) {
		if (event.world.isRemote())
			return;
		ServerWorld world = (ServerWorld) event.world;
		ColorUtil.updateRenderTime(world);
        world.getProfiler().startSection(ExPrimo.MODID + ":onWorldTick");
        ChunkEssentiaFlowManager.onServerWorldTick(world);
        event.world.getProfiler().endSection();
	}
    
	public static void onRegisterCommands(final RegisterCommandsEvent event) {
		SetChunkEssentiaCommand.register(event.getDispatcher());
		ProfileChunksCommand.register(event.getDispatcher());
	}
	
	public static void onChunkLoad(ChunkEvent.Load event) {
		if (!event.getWorld().isRemote()) {
			ServerWorld world = (ServerWorld) event.getWorld();
			if (event.getChunk() instanceof Chunk) {
				Chunk chunk = (Chunk) event.getChunk();
				ChunkEssentiaBuilder.validateContainer(world, chunk);
			}
		}
	}
}
