package redd90.exprimo.event;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import redd90.exprimo.essentia.ChunkEssentiaBuilder;
import redd90.exprimo.essentia.EssentiaContainer;
import redd90.exprimo.essentia.EssentiaContainerCap;

public class ModEventHandler {
	
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
    
}
