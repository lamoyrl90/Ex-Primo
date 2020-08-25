package redd90.exprimo.essentia.flow;

import java.util.HashSet;
import java.util.Set;

import net.minecraftforge.common.capabilities.ICapabilityProvider;
import redd90.exprimo.essentia.EssentiaContainer;
import redd90.exprimo.essentia.EssentiaContainerCap;
import redd90.exprimo.essentia.EssentiaStack;

public abstract class EssentiaFlowProvider {

	protected ICapabilityProvider holder;
	protected Set<EssentiaContainer> targetcontainers = new HashSet<>();
	protected Set<EssentiaContainer> sourcecontainers = new HashSet<>();
	
	public EssentiaFlowProvider(ICapabilityProvider holder) {
		this.holder = holder;
		this.sourcecontainers.add((EssentiaContainer) this.holder.getCapability(EssentiaContainerCap.ESSENTIA_CONTAINER).orElse(EssentiaContainerCap.EMPTY));
		this.targetcontainers = getTargetContainers();
	}

	protected abstract Set<EssentiaContainer> getTargetContainers();
	
	public void flow() {
		Set<EssentiaFlow> flows = calculateFlows();
		int count = flows.size();
		for(EssentiaFlow flow : flows) {
			if(flow.getValue() != 0)
				flow.getSource().transfer(flow.getKey(), flow.getTarget(), Math.floorDiv(flow.getValue(), count));
		}
	}
	
	protected Set<EssentiaFlow> calculateFlows() {
		Set<EssentiaFlow> flows = new HashSet<>();
		
		for(EssentiaContainer target : targetcontainers) {
			for(EssentiaStack stack : target.getStackSet().values()) {
				String essentiakey = stack.getEssentia().getName();
				for(EssentiaContainer source : sourcecontainers) {
					if (!source.getStackSet().containsKey(essentiakey))
						continue;
					int diff = getPressureDiff(source, target, essentiakey);
					if (diff > 0) {
						flows.add(new EssentiaFlow(essentiakey, source, target, diff));
					}
				}
			}
		}
		
		return flows;
	}
	
	protected int getPressureDiff(EssentiaContainer source, EssentiaContainer target, String essentiakey) {
		return source.getInnerPressure(essentiakey) - target.getInnerPressure(essentiakey);
	}
}