package redd90.exprimo.essentia.flow;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.server.ServerWorld;
import redd90.exprimo.item.IEssentiaContainerItem;
import redd90.exprimo.tile.AbstractPedestalTile;

public class TileEssentiaFlowManager {
	public static void onTileTick(TileEntity te) {
		if (!te.getWorld().isRemote()) {
			ServerWorld world = (ServerWorld) te.getWorld();
			TileEssentiaFlowProvider tileflow = new TileEssentiaFlowProvider(world, te);
			double pushfactor = 1;
			double pullfactor = 1;
			
			if (te instanceof AbstractPedestalTile) {
				AbstractPedestalTile pedestal = (AbstractPedestalTile) te;
				pushfactor *= pedestal.getPushFactor();
				pullfactor *= pedestal.getPullFactor();
				
				for (ItemStack stack : pedestal.getInventory().getStacks()) {
					if (stack.getItem() instanceof IEssentiaContainerItem) {
						pushfactor *= ((IEssentiaContainerItem) stack.getItem()).getPushFactor();
						pullfactor *= ((IEssentiaContainerItem) stack.getItem()).getPullFactor();
					}
				}
			}
			
			ChunkEssentiaFlowProvider chunkflow = new ChunkEssentiaFlowProvider(te, tileflow.sourcecontainers, pullfactor);
			ChunkEssentiaFlowManager.scheduleTileTick(chunkflow);
				
			tileflow.flow(pushfactor);
		}
	}
}
