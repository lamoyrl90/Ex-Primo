package redd90.exprimo.network;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import redd90.exprimo.ExPrimo;

public class ModPacketHandler {
	private static final String PROTOCOL_VERSION = "1";
	public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
	    new ResourceLocation(ExPrimo.MODID, "main"),
	    () -> PROTOCOL_VERSION,
	    PROTOCOL_VERSION::equals,
	    PROTOCOL_VERSION::equals
	);
	private int index = 0;
	
	public void init() {
		registerC2S(EssentiaStackPacket.class, EssentiaStackPacket::encode, EssentiaStackPacket::decode, EssentiaStackPacket::handle);
	}
	
	private <MSG> void registerC2S(Class<MSG> type, BiConsumer<MSG, PacketBuffer> encoder, 
			Function<PacketBuffer, MSG> decoder, BiConsumer<MSG, Supplier<Context>> consumer) {
		INSTANCE.registerMessage(index++, type, encoder, decoder, consumer, Optional.of(NetworkDirection.PLAY_TO_SERVER));
	}
	
	public <MSG> void sendTo(MSG packet, ServerPlayerEntity player) {
		INSTANCE.sendTo(packet, player.connection.getNetworkManager(), NetworkDirection.PLAY_TO_CLIENT);
	}
	
	public <MSG> void sendToAllLoaded(MSG packet, World world, BlockPos pos) {
		Chunk chunk = world.getChunkAt(pos);
		INSTANCE.send(PacketDistributor.TRACKING_CHUNK.with(() -> chunk), packet);
	}
}
