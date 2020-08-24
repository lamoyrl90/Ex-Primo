package redd90.exprimo.essentia;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import redd90.exprimo.ExPrimo;

public class EssentiaContainerCap {

	@CapabilityInject(IEssentiaContainer.class)
	public static Capability<IEssentiaContainer> ESSENTIA_CONTAINER = null;
	
	public static final IEssentiaContainer EMPTY = new EssentiaContainer();
	public static final ResourceLocation LOCATION = new ResourceLocation(ExPrimo.MODID, "essentia_container");
	
	public static void register() {
		CapabilityManager.INSTANCE.register(IEssentiaContainer.class, new Capability.IStorage<IEssentiaContainer>() {

			@Override
			public INBT writeNBT(Capability<IEssentiaContainer> capability, IEssentiaContainer instance,
					Direction side) {
				return ((EssentiaContainer)instance).serializeNBT();
			}

			@Override
			public void readNBT(Capability<IEssentiaContainer> capability, IEssentiaContainer instance, Direction side,
					INBT nbt) {
				((EssentiaContainer)instance).deserializeNBT((CompoundNBT) nbt);
			}
			
		}, () -> new EssentiaContainer());
	}
	
	public static boolean canAttachTo(ICapabilityProvider o) {
		return (o instanceof PlayerEntity || o instanceof Chunk || 
				(o instanceof ItemStack && isValidItemStack((ItemStack) o))) 
				&& !o.getCapability(ESSENTIA_CONTAINER).isPresent();
	}
	
	private static boolean isValidItemStack(ItemStack stack) {
		return false;
	}
}
