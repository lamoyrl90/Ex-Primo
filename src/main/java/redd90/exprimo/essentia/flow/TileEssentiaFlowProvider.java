package redd90.exprimo.essentia.flow;

import java.util.List;
import java.util.Set;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.server.ServerWorld;
import redd90.exprimo.essentia.EssentiaContainer;

public class TileEssentiaFlowProvider extends EssentiaFlowProvider {

	private IChunk chunk;
	private List<ItemStack> stacks;
	
	public TileEssentiaFlowProvider(ServerWorld world, TileEntity holder) {
		super(holder);
		this.chunk = world.getChunk(holder.getPos());
		this.sourcecontainers = gatherSourceContainers();
		this.targetcontainers = gatherTargetContainers();
	}

	@Override
	protected Set<EssentiaContainer> gatherSourceContainers() {
		return null;
	}
	
	@Override
	protected Set<EssentiaContainer> gatherTargetContainers() {
		return null;
	}

}
