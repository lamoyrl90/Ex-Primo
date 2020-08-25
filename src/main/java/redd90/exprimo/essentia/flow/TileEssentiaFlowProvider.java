package redd90.exprimo.essentia.flow;

import java.util.Set;

import net.minecraftforge.common.capabilities.ICapabilityProvider;
import redd90.exprimo.essentia.EssentiaContainer;

public class TileEssentiaFlowProvider extends EssentiaFlowProvider {

	public TileEssentiaFlowProvider(ICapabilityProvider holder) {
		super(holder);
	}

	@Override
	protected Set<EssentiaContainer> getTargetContainers() {
		return null;
	}

	
}
