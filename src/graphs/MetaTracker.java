package graphs;

import java.util.ArrayList;
import java.util.Arrays;

public class MetaTracker {

	private ArrayList<Integer> currentRGBs = new ArrayList<>();
	private ArrayList<String> currentNames = new ArrayList<>();
	
	public boolean colorsUpdated(ArrayList<Series> series) {
		ArrayList<Integer> compare = new ArrayList<>();
		for (Series s : series) {
			compare.add(s.getColor().getRGB());
		}
		if (!Arrays.equals(currentRGBs.toArray(), compare.toArray())) {
			currentRGBs = compare;
			return true;
		} else {
			return false;
		}
	}
	
	public boolean namesUpdated(ArrayList<Series> series) {
		ArrayList<String> compare = new ArrayList<>();
		for (Series s : series) {
			compare.add(s.getName());
		}
		if (!Arrays.equals(compare.toArray(), currentNames.toArray())) {
			currentNames = compare;
			return true;
		} else {
			return false;
		}
	}
	
}
