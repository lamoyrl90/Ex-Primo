package redd90.exprimo.essentia.flow;

import redd90.exprimo.essentia.Essentia;
import redd90.exprimo.essentia.EssentiaContainer;

class EssentiaFlow {
	private static final double GLOBAL_FLOW_RATE = 0.2;
	
	private final EssentiaContainer source;
	private final EssentiaContainer target;
	private final Essentia essentia;
	private final double value;
	
	protected EssentiaFlow(Essentia essentia, EssentiaContainer source, EssentiaContainer target, double value) {
		this.essentia = essentia;
		this.source = source;
		this.target = target;
		this.value = value * GLOBAL_FLOW_RATE;
	}

	public Essentia getEssentia() {
		return essentia;
	}

	public EssentiaContainer getSource() {
		return source;
	}

	public EssentiaContainer getTarget() {
		return target;
	}

	public double getValue() {
		return value;
	}
}