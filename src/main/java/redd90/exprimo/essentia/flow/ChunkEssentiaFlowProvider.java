package redd90.exprimo.essentia.flow;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunk;
import redd90.exprimo.ExPrimo;
import redd90.exprimo.essentia.EssentiaContainer;
import redd90.exprimo.essentia.EssentiaContainerCap;

public class ChunkEssentiaFlowProvider extends EssentiaFlowProvider {
	

		
	public ChunkEssentiaFlowProvider(Chunk chunk) {
		super(chunk);
		this.sourcecontainers = gatherSourceContainers();
		this.targetcontainers = gatherTargetContainers();
	}
	
	public ChunkEssentiaFlowProvider(TileEntity tile, Set<EssentiaContainer> targets) {
		super((Chunk) tile.getWorld().getChunk(tile.getPos()));
		this.sourcecontainers = gatherSourceContainers();
		this.targetcontainers = targets;
	}
	
	@Override
	protected Set<EssentiaContainer> gatherSourceContainers() {
		Set<EssentiaContainer> sourcecontainers = new HashSet<>();
		sourcecontainers.add((EssentiaContainer) this.holder.getCapability(EssentiaContainerCap.ESSENTIA_CONTAINER).orElse(EssentiaContainerCap.EMPTY));
		return sourcecontainers;
	}
	
	protected Set<EssentiaContainer> gatherTargetContainers() {
		Set<EssentiaContainer> targetcontainers = new HashSet<>();
		Chunk chunk = (Chunk) holder;
		
		for(int i=0;i<4;i++) {
			Direction dir = Direction.byHorizontalIndex(i);
			World world = chunk.getWorld();
			if(world.isRemote())
				continue;
			BlockPos pos = chunk.getPos().asBlockPos();
			BlockPos pos1 = pos.offset(dir, 16);
			IChunk ichunk = world.getChunk(pos1);
			if (!(ichunk instanceof Chunk) || !world.isAreaLoaded(pos1, 0))
				continue;
			Chunk chunk1 = (Chunk) ichunk;
			try {
				EssentiaContainer container1 = (EssentiaContainer) chunk1.getCapability(EssentiaContainerCap.ESSENTIA_CONTAINER).orElseThrow(()-> new IllegalArgumentException("Invalid Chunk Essentia Container"));
				targetcontainers.add(container1);
			} catch (IllegalArgumentException e) {
				ExPrimo.LOGGER.error(e);
				continue;
			}
		}
		
		return targetcontainers;
	}


}
