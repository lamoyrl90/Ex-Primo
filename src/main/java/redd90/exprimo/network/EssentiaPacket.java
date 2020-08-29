package redd90.exprimo.network;

import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import redd90.exprimo.ExPrimo;
import redd90.exprimo.essentia.Essentia;
import redd90.exprimo.essentia.EssentiaContainer;
import redd90.exprimo.essentia.EssentiaContainerCap;
import redd90.exprimo.registry.ModRegistries;

public class EssentiaPacket {

	private final ItemStack stack;
	private final BlockPos pos;
	private final String essentia;
	private final int amount;
	
	public EssentiaPacket(ItemStack stack, BlockPos pos, String essentia, int amount) {
		this.stack = stack;
		this.pos = pos;
		this.essentia = essentia;
		this.amount = amount;
	}
	
	public static void encode(EssentiaPacket packet, PacketBuffer buffer) {
		buffer.writeItemStack(packet.stack);
		buffer.writeBlockPos(packet.pos);
		buffer.writeString(packet.essentia);
		buffer.writeInt(packet.amount);
	}
	
	public static EssentiaPacket decode(PacketBuffer buffer) {
		ItemStack stack = buffer.readItemStack();
		BlockPos pos = buffer.readBlockPos();
		String essentia = buffer.readString();
		int amount = buffer.readInt();
		EssentiaPacket ret = new EssentiaPacket(stack, pos, essentia, amount);
		return ret;
	}
	
	public static void handle(EssentiaPacket packet, Supplier<NetworkEvent.Context> context) {
		if (!context.get().getDirection().getReceptionSide().isClient())
			return;
		context.get().enqueueWork(() -> {
				
			Minecraft mc = Minecraft.getInstance();
			ClientWorld world = mc.world;
			TileEntity te = world.getTileEntity(packet.pos);
			ItemStack stack = packet.stack;
			EssentiaContainer container = (EssentiaContainer) stack.getCapability(EssentiaContainerCap.ESSENTIA_CONTAINER).orElse(null);
			Essentia e = ModRegistries.ESSENTIAS.getValue(new ResourceLocation(ExPrimo.MODID, packet.essentia));
			if (container == null)
				return;
			IItemHandler inv = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).orElse(null);
			if (inv == null)
				return;
			
			for(int i=0;i<inv.getSlots();i++) {
				if(inv.getStackInSlot(i).getItem() == stack.getItem()) {
					container.setStack(e, packet.amount);
				}
			}
				
		});
		context.get().setPacketHandled(true);
	}
	
}
