package redd90.exprimo.network;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerChunkProvider;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import redd90.exprimo.ExPrimo;

public class PacketHandler {
	private static final String PROTOCOL = "1";
	private static final SimpleChannel HANDLER = NetworkRegistry
			.newSimpleChannel(new ResourceLocation(ExPrimo.MODID, "main"), 
					() -> PROTOCOL, PROTOCOL::equals, PROTOCOL::equals);
	
	public static void init() {
		int id = 0;
		HANDLER.registerMessage(id++, EssentiaPacket.class, EssentiaPacket::encode, EssentiaPacket::decode, EssentiaPacket::handle);
	}
		
    public static void sendToAllTracking(Object message, TileEntity tile) {
    	sendToAllTracking(message, tile.getWorld(), tile.getPos());
    }
    
    public static void sendToAllTracking(Object message, World world, BlockPos pos) {
        if (world instanceof ServerWorld) {
        	ServerChunkProvider provider = ((ServerWorld) world).getChunkProvider();
            provider.chunkManager.getTrackingPlayers(new ChunkPos(pos), false).forEach(p -> sendTo(message, p));
        } else {
            HANDLER.send(PacketDistributor.TRACKING_CHUNK.with(() -> world.getChunk(pos.getX() >> 4, pos.getZ() >> 4)), message);
        }
    }
    
    public static void sendTo(Object message, ServerPlayerEntity player) {
        HANDLER.sendTo(message, player.connection.getNetworkManager(), NetworkDirection.PLAY_TO_CLIENT);
    }
}
