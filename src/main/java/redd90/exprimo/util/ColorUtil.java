package redd90.exprimo.util;

import java.awt.Color;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.minecraft.world.World;
import redd90.exprimo.essentia.Essentia;
import redd90.exprimo.essentia.StackSet;

public class ColorUtil {

	public static int WORLD_RENDER_TIME = 0;
	
	public static void updateRenderTime(World world) {
		if (WORLD_RENDER_TIME != 11519)
			WORLD_RENDER_TIME += 1;
		else WORLD_RENDER_TIME = 0;
	}
	
	public static Color stacksetTimeColor(StackSet stackset) {
		List<Map.Entry<Essentia, Integer>> sortedstacks = sortStacksByHue(stackset);
		int n = sortedstacks.size();
		int[] hueweight = new int[n];
		double[] huepos = new double[n];
		double[] huewidth = new double[n];
		int hue[] = new int[n];
		
		for (int i=0;i<n;i++) {
			int j = i+1;
			if (j == n)
				j=0;
			hue[i] = sortedstacks.get(i).getKey().getHue();
			hueweight[i] = sortedstacks.get(i).getValue();
		}
		
		int sumweight = ModMath.sum(hueweight);
		if (sumweight == 0) {
			sumweight = 1;
			Color ret = new Color(Color.HSBtoRGB(0, 0, 255));
			return ret;
		}
		
		for(int i=0; i<n; i++) {
			int j = i+1;
			if (j == n)
				j=0;
			huewidth[i] = (double) (hue[j] - hue[i]);
			huewidth[i] += huewidth[i] < 0 ? 360 : 0;
			
			if (i==0)
				huepos[i] = 0;
			else
				huepos[i] = huepos[i-1] + huewidth[i-1] * hueweight[i-1] / sumweight;
		}
		
		int nextindex = nextElementIndex(WORLD_RENDER_TIME / 32, huepos);
		int previndex = nextindex == 0 ? n-1 : nextindex - 1;
		double t = WORLD_RENDER_TIME / 32 - huepos[previndex];
		double u = huepos[nextindex] - huepos[previndex];
		double w = hue[nextindex] - hue[previndex];
		t += t < 0 ? 360 : 0;
		u += u < 0 ? 360 : 0;
		w += w < 0 ? 360 : 0;
		double v = t/u;
		double x = w * v;
		
		
		int evalhue = (int) (x + hue[previndex]);
		
		
		Color ret = new Color(Color.HSBtoRGB(((float) evalhue)/360, 1.0f, 1.0f));
		
		return ret;
	}
	
	public static List<Map.Entry<Essentia, Integer>> sortStacksByHue(StackSet stackset) {
		List<Map.Entry<Essentia, Integer>> list = new LinkedList<>(stackset.getStacks().entrySet());
		
		Collections.sort(list, new Comparator<Map.Entry<Essentia, Integer> >() { 
            public int compare(Map.Entry<Essentia, Integer> o1,  
                               Map.Entry<Essentia, Integer> o2) 
            { 
                return (new Integer(o1.getKey().getHue())).compareTo(new Integer(o2.getKey().getHue())); 
            } 
        }); 
		return list;
	}
	
	public static int nextElementIndex(int value, double[] array) {
		int n = array.length;
		int j = 0;
		for(int i=0;i<n;i++) {
			if(i==0)
				j = n-1;
			else
				j = i-1;
			if(array[j]<=value && value < array[i])
				return i;
		}
		return 0;
	}
	
	public static int hueToRGB(int hue) {
		return Color.HSBtoRGB((float) ((float) hue/360), 1.0f, 1.0f);
	}
	
	public static Color stacksetAverageColor(StackSet stackset) {
		float[] compsum = new float[3];
		int sum = 0;
		for(Map.Entry<Essentia, Integer> stack : stackset.getStacks().entrySet()) {
			Color color = new Color(hueToRGB(stack.getKey().getHue()));
			int v = stack.getValue();
			sum += v;
			float[] rgb = color.getRGBColorComponents(null);
			for(int i=0;i<3;i++) {
				compsum[i] += rgb[i] * v;
			}
		}
		if (sum == 0)
			sum = 1;
		
		for(int i=0;i<3;i++) {
			compsum[i] = (compsum[i] / sum);
		}
		
		return new Color(compsum[0], compsum[1], compsum[2]);
	}
}
