package redd90.exprimo.util;

import java.util.List;

public class ModMath {
	public static float getVariance(List<Float> list) {
		float sum = 0f;
		int count = 0;
		float avg = getAverage(list);
		for (Float num : list) {
			sum += (num - avg) * (num - avg);
			count ++;
		}
		
		return (float) (sum / count);
	}
	
	public static float getAverage(List<Float> list) {
		float sum = 0f;
		int count = 0;
		for (float num : list) {
			sum += (float) num;
			count++;
		}
		
		return (float) (sum / count);
	}
	
	public static double getNormalP(double z) {
		double oversqrt2pi = 0.3989422804;
		
		double phi = oversqrt2pi * Math.exp(-z*z);
		double[] b = {0.2316419, 0.319381530, (-0.356563782), 1.781477937, (-1.821255978), 1.330274429};
		double t = (1/(1 + b[0] * z));
		double ret = 1 - phi * (b[0] * t + b[1] * t * t + b[2] * Math.pow(t, 3) + b[4] * Math.pow(t, 4) + b[5] * Math.pow(t, 5));
		
		return ret;
	}
}
