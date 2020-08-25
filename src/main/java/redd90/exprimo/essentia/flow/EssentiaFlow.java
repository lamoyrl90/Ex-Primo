package redd90.exprimo.essentia.flow;

import redd90.exprimo.essentia.EssentiaContainer;

class EssentiaFlow {
	private final String essentiakey;
	private final EssentiaContainer source;
	private final EssentiaContainer target;
	private final int value;
	
	protected EssentiaFlow(String essentiakey, EssentiaContainer source, EssentiaContainer target, int value) {
		this.essentiakey = essentiakey;
		this.source = source;
		this.target = target;
		this.value = value;
	}

	public String getKey() {
		return essentiakey;
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