package redd90.exprimo.essentia.flow;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunk;
import redd90.exprimo.ExPrimo;
import redd90.exprimo.essentia.EssentiaContainer;
import redd90.exprimo.essentia.EssentiaContainerCap;
import redd90.exprimo.essentia.EssentiaStack;

public class ChunkEssentiaFlow {
	
	private Chunk chunk;
	private Set<EssentiaContainer> targetcontainers;
	private EssentiaContainer container;
		
	public ChunkEssentiaFlow(Chunk chunk) {
		this.chunk = chunk;
		this.container = (EssentiaContainer) this.chunk.getCapability(EssentiaContainerCap.ESSENTIA_CONTAINER).orElse(EssentiaContainerCap.EMPTY);
		this.targetcontainers = getTargetContainers();
	}
	
	public void flow() {
		Set<Flow> flows = calculateFlows();
		int count = flows.size();
		for(Flow flow : flows) {
			if(flow.value != 0)
				flow.source.transfer(flow.essentiakey, flow.target, Math.floorDiv(flow.value, count));
		}
	}
	
	private Set<EssentiaContainer> getTargetContainers() {
		Set<EssentiaContainer> targetcontainers = new HashSet<>();
		
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
		
		for(EssentiaContainer linked : container.getLinked()) {
			targetcontainers.add(linked);
		}
		
		return targetcontainers;
	}
	
	private int getPressureDiff(EssentiaContainer target, String essentiakey) {
		return container.getInnerPressure(essentiakey) - target.getInnerPressure(essentiakey);
	}
	
	private Set<Flow> calculateFlows() {
		Set<Flow> flows = new HashSet<>();
		
		for(EssentiaContainer target : targetcontainers) {
			for(EssentiaStack stack : target.getStackSet().values()) {
				String essentiakey = stack.getEssentia().getName();
				if (!container.getStackSet().containsKey(essentiakey))
					continue;
				int diff = getPressureDiff(target, essentiakey);
				if (diff > 0) {
					flows.add(new Flow(essentiakey, container, target, diff));
				}
			}
		}
		
		return flows;
	}
	
	private class Flow {
		private final String essentiakey;
		private final EssentiaContainer source;
		private final EssentiaContainer target;
		private final int value;
		
		protected Flow(String essentiakey, EssentiaContainer source, EssentiaContainer target, int value) {
			this.essentiakey = essentiakey;
			this.source = source;
			this.target = target;
			this.value = value;
		}
	}
}
