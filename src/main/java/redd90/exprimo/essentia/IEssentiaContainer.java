package redd90.exprimo.essentia;

import java.util.HashMap;

public interface IEssentiaContainer {

	EssentiaStack getStack(Essentia essentia);
	
	HashMap<String, EssentiaStack> getStackSet();
		
}
