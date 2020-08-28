package redd90.exprimo.event;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.util.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import redd90.exprimo.ExPrimo;
import redd90.exprimo.essentia.Essentia;
import redd90.exprimo.essentia.EssentiaContainerCap;
import redd90.exprimo.essentia.IEssentiaContainer;
import redd90.exprimo.essentia.flow.ChunkEssentiaFlowManager;
import redd90.exprimo.registry.ModRegistries;

public class ClientEventHandler {

	public static void onDebugRender(RenderGameOverlayEvent.Text event) {
		Minecraft mc = Minecraft.getInstance();
		ClientPlayerEntity player = mc.player;
		
		RegistryKey<World> dimkey = player.getEntityWorld().getDimensionKey();
		ServerWorld world = mc.getIntegratedServer().getWorld(dimkey);
		
		Chunk chunk = (Chunk) world.getChunk(player.getPosition());
		IEssentiaContainer container = chunk.getCapability(EssentiaContainerCap.ESSENTIA_CONTAINER).orElse(EssentiaContainerCap.EMPTY);
		List<String> left = event.getLeft();
		String prefix = "[" + ExPrimo.MODID + "] ";

		left.add(prefix);
		
		for (Essentia e : ModRegistries.ESSENTIAS) {
			left.add(e.getKey() + ": " + container.getStack(e));
		}
		left.add("enqueued: " + ChunkEssentiaFlowManager.getNumberChunksInQueue());
		
	}
	
}
