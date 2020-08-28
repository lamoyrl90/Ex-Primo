package redd90.exprimo.essentia.flow;

import redd90.exprimo.essentia.Essentia;
import redd90.exprimo.essentia.EssentiaContainer;

class EssentiaFlow {
	private static final double GLOBAL_FLOW_RATE = 0.1;
	
	private final EssentiaContainer source;
	private final EssentiaContainer target;
	private final Essentia essentia;
	private final int value;
	
	protected EssentiaFlow(Essentia essentia, EssentiaContainer source, EssentiaContainer target, int value) {
		this.essentia = essentia;
		this.source = source;
		this.target = target;
		this.value = (int) Math.floor(value * GLOBAL_FLOW_RATE);
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

	public int getValue() {
		return value;
	}
}