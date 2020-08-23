package redd90.exprimo.network;

import java.util.function.Supplier;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import redd90.exprimo.essentia.EssentiaContainer;
import redd90.exprimo.essentia.EssentiaContainerCap;

public class EssentiaStackPacket {

	private final String name;
	private final int amount;
	
	public EssentiaStackPacket(String name, int amount) {
		this.name = name;
		this.amount = amount;
	}
	
	public static void encode(EssentiaStackPacket packet, PacketBuffer buffer) {
		buffer.writeString(packet.name);
		buffer.writeInt(packet.amount);
	}
	
	public static EssentiaStackPacket decode(PacketBuffer buffer) {
		String name = buffer.readString();
		int amount = buffer.readInt();
		
		return new EssentiaStackPacket(name, amount);
	}
	
	public static void handle(EssentiaStackPacket packet, Supplier<Context> context) {
		context.get().enqueueWork( () -> {
		ServerPlayerEntity player = context.get().getSender();
		ServerWorld world = player.getServerWorld();
		Chunk chunk = world.getChunkAt(player.getPosition());
		EssentiaContainer container = (EssentiaContainer) chunk.getCapability(EssentiaContainerCap.ESSENTIA_CONTAINER).orElseThrow(() -> new IllegalArgumentException("Invalid Essentia Container"));
		
		container.getStack(packet.name).setWithoutUpdate(packet.amount);});
		context.get().setPacketHandled(true);
	}
	
}
