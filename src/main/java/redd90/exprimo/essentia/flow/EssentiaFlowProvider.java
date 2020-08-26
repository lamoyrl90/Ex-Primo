package redd90.exprimo.essentia.flow;

import java.util.HashSet;
import java.util.Set;

import net.minecraftforge.common.capabilities.ICapabilityProvider;
import redd90.exprimo.essentia.EssentiaContainer;
import redd90.exprimo.essentia.EssentiaStack;

public abstract class EssentiaFlowProvider {

	protected ICapabilityProvider holder;
	protected Set<EssentiaContainer> targetcontainers = new HashSet<>();
	protected Set<EssentiaContainer> sourcecontainers = new HashSet<>();
	
	public EssentiaFlowProvider(ICapabilityProvider holder) {
		this.holder = holder;
	}

	protected abstract Set<EssentiaContainer> gatherSourceContainers();
	
	protected abstract Set<EssentiaContainer> gatherTargetContainers();
	
	public void flow(double factor) {
		Set<EssentiaFlow> flows = calculateFlows();
		int count = flows.size();
		for(EssentiaFlow flow : flows) {
			if(flow.getValue() > 0)
				flow.getSource().transfer(flow.getKey(), flow.getTarget(), Math.floorDiv((int) (flow.getValue()*factor), count));
		}
	}
	
	protected Set<EssentiaFlow> calculateFlows() {
		Set<EssentiaFlow> flows = new HashSet<>();
		
		for(EssentiaContainer target : targetcontainers) {
			for(EssentiaStack stack : target.getStackSet().values()) {
				String essentiakey = stack.getEssentia().getKey();
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
		return (source.getInnerPressure(essentiakey) - target.getInnerPressure(essentiakey));
	}
}
