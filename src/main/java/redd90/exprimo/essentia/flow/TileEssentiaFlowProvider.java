package redd90.exprimo.essentia.flow;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.server.ServerWorld;
import redd90.exprimo.essentia.EssentiaContainer;
import redd90.exprimo.essentia.EssentiaContainerCap;
import redd90.exprimo.item.IEssentiaContainerItem;
import redd90.exprimo.tile.TileWithInventory;

public class TileEssentiaFlowProvider extends EssentiaFlowProvider {

	
	public TileEssentiaFlowProvider(ServerWorld world, TileEntity holder) {
		super(holder);
		this.sourcecontainers = gatherSourceContainers();
		this.targetcontainers = gatherTargetContainers();
	}

	@Override
	protected Set<EssentiaContainer> gatherSourceContainers() {
		Set<EssentiaContainer> sources = new HashSet<>();
		if (holder instanceof TileWithInventory) {
			TileWithInventory tile = (TileWithInventory) holder;
			for (ItemStack stack : tile.getInventory().getStacks()) {
				if (stack.getItem() instanceof IEssentiaContainerItem) {
					EssentiaContainer itemcontainer = (EssentiaContainer) stack.getCapability(EssentiaContainerCap.ESSENTIA_CONTAINER).orElse(null);
					if (itemcontainer != null)
						sources.add(itemcontainer);
				}
			}
		}
		
		return sources;
	}
	
	@Override
	protected Set<EssentiaContainer> gatherTargetContainers() {
		Set<EssentiaContainer> targets = new HashSet<>();
		TileEntity tile = (TileEntity) holder;
		Chunk chunk = (Chunk) tile.getWorld().getChunk(tile.getPos());
		EssentiaContainer chunkcontainer = (EssentiaContainer) chunk.getCapability(EssentiaContainerCap.ESSENTIA_CONTAINER).orElse(null);
		if (chunkcontainer == null)
			return targets;
		
		targets.add(chunkcontainer);
		
		return targets;
	}

}
