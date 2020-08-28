package redd90.exprimo.essentia.flow;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraftforge.common.capabilities.ICapabilityProvider;
import redd90.exprimo.essentia.Essentia;
import redd90.exprimo.essentia.EssentiaContainer;
import redd90.exprimo.registry.ModRegistries;

public abstract class EssentiaFlowProvider {

	protected ICapabilityProvider holder;
	protected Set<EssentiaContainer> targetcontainers = new HashSet<>();
	protected Set<EssentiaContainer> sourcecontainers = new HashSet<>();
	
	public EssentiaFlowProvider(ICapabilityProvider holder) {
		this.holder = holder;
	}

	protected abstract Set<EssentiaContainer> gatherSourceContainers();
	
	protected abstract Set<EssentiaContainer> gatherTargetContainers();
	
	public int flow(double factor) {
		Set<EssentiaFlow> flows = calculateFlows();
		List<Pair<Essentia, Integer>> colorfactors = new ArrayList<>();
		float flowcolor = 0;
		int count = flows.size();
		for(EssentiaFlow flow : flows) {
			if(flow.getValue() > 0) {
				int amount = Math.floorDiv((int) Math.floor(flow.getValue()*factor), count);
				flow.getSource().transfer(flow.getEssentia(), flow.getTarget(), amount);
				colorfactors.add(Pair.of(flow.getEssentia(), amount));
			}
		}
		
		int total = 0;
		for(Pair<Essentia,Integer> entry : colorfactors) {
			flowcolor += entry.getLeft().getColor() * entry.getRight();
			total += entry.getRight();
		}
		
		if (total == 0)
			total = 1;
		
		return (int) (flowcolor / total);
	}
	
	protected Set<EssentiaFlow> calculateFlows() {
		Set<EssentiaFlow> flows = new HashSet<>();
		
		for(EssentiaContainer target : targetcontainers) {
			for(Essentia e : ModRegistries.ESSENTIAS) {
				for(EssentiaContainer source : sourcecontainers) {
					int diff = getPressureDiff(source, target, e);
					if (diff > 0) {
						flows.add(new EssentiaFlow(e, source, target, diff));
					}
				}
			}
		}
		
		return flows;
	}
	
	protected int getPressureDiff(EssentiaContainer source, EssentiaContainer target, Essentia essentia) {
		return (source.getInnerPressure(essentia) - target.getInnerPressure(essentia));
	}
}
