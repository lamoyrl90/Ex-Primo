package redd90.exprimo.util;

import java.awt.Color;
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
		
		double phi = oversqrt2pi * Math.exp(-Math.pow(z, 2)/2);
		double[] b = {0.2316419, 0.319381530, (-0.356563782), 1.781477937, (-1.821255978), 1.330274429};
		double t = (1/(1 + b[0] * z));
		double ret = 1 - phi * (b[1] * t + b[2] * Math.pow(t, 2) + b[3] * Math.pow(t, 3) + b[4] * Math.pow(t, 4) + b[5] * Math.pow(t, 5));
		
		return ret;
	}
	
	public static int getAverageColor(List<Color> colors, List<Integer> weightsIn) {
		int size = colors.size();
		int[] reds = new int[size];
		int[] greens = new int[size];
		int[] blues = new int[size];
		int[] weights = weightsIn.stream().mapToInt(i->i).toArray();
		
		for(int i=0;i<size;i++) {
			Color color = colors.get(i);
			reds[i] = color.getRed();
			greens[i] = color.getGreen();
			blues[i] = color.getBlue();
		}
		
		int red = (int) getWeightedAverage(reds, weights);
		int green = (int) getWeightedAverage(greens, weights);
		int blue = (int) getWeightedAverage(blues, weights);
		
		return red << 16 + green << 8 + blue;
	}
	
	public static double getWeightedAverage(int[] values, int[] weights) {
		int sum = 0;
		int totalweight = 0;
		for(int i=0;i<values.length;i++) {
			sum += values[i] * weights[i];
			totalweight += weights[i];
		}
		return sum / (values.length * totalweight);
	}
}
