package redd90.exprimo.essentia;

import java.util.HashMap;

public interface IEssentiaContainer {

	EssentiaStack getStack(Essentia essentia);
	
	EssentiaStack getStack(String name);
	
	HashMap<String, EssentiaStack> getStackSet();
		
}
