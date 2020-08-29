package redd90.exprimo.util;

import java.util.List;

import net.minecraft.item.DyeColor;

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
	
	public static int getAverageColor(List<DyeColor> colors, List<Integer> weightsIn) {
		int size = colors.size();
		float[] reds = new float[size];
		float[] greens = new float[size];
		float[] blues = new float[size];
		int[] weights = new int[size];
		
		for(int i=0;i<size;i++) {
			float[] comps = colors.get(i).getColorComponentValues();
			reds[i] = comps[0];
			greens[i] = comps[1];
			blues[i] = comps[2];
			weights[i] = weightsIn.get(i);
		}
		
		int red = (int) (getWeightedAverage(reds, weights) * 255);
		int green = (int) (getWeightedAverage(greens, weights) * 255);
		int blue = (int) (getWeightedAverage(blues, weights) * 255);
		
		int ret = (red << 16) + (green << 8) + (blue);
		
		return ret;
	}
	
	public static float[] getAverageColorRGB(List<DyeColor> colors, List<Integer> weightsIn) {
		int size = colors.size();
		float[] reds = new float[size];
		float[] greens = new float[size];
		float[] blues = new float[size];
		int[] weights = new int[size];
		
		for(int i=0;i<size;i++) {
			float[] comps = colors.get(i).getColorComponentValues();
			reds[i] = comps[0];
			greens[i] = comps[1];
			blues[i] = comps[2];
			weights[i] = weightsIn.get(i);
		}
		
		float red = getWeightedAverage(reds, weights);
		float green = getWeightedAverage(greens, weights);
		float blue = getWeightedAverage(blues, weights);
		
		float[] ret = {red, green, blue};
		
		return ret;
	}
	
	public static float getWeightedAverage(float[] values, int[] weights) {
		float sum = 0;
		int totalweight = 0;
		for(int i=0;i<values.length;i++) {
			sum += values[i] * weights[i];
			totalweight += weights[i];
		}
		return (sum / (totalweight));
	}
	
	public static double sum(double[] nums) {
		double sum = 0;
		for(double num : nums) {
			sum += num;
		}
		return sum;
	}
	
	public static int sum(int[] nums) {
		int sum = 0;
		for(int i=0;i<nums.length;i++) {
			sum += nums[i];
		}
		return sum;
	}
	
}
