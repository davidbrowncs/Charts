
package graphs;

import java.awt.Color;
import java.awt.Dimension;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import utils.ColorGenerator;
import data.DataSet;

public class GraphTesting {

	public static void main(String[] args) {
		DataSet<Double, Double> d = new DataSet<>();
		ArrayList<Double> xVals = new ArrayList<>();
		d.addFunction(x -> {
			return 0.5d;
		});

		for (double i = -5; i < 5; i += 0.01) {
			xVals.add(i);
		}
		d.setInd(xVals);
//		d.addFunction(x -> {
//			return 2 * Math.pow(x, 3) - 9 * Math.pow(x, 2) - 24 * x + 2;
//		});

		SwingUtilities.invokeLater(() -> {
			JFrame frame = new JFrame();
			Graph<Double, Double> g = new AreaFilledLineGraph<>();
			g.setDataSet(d);
			g.setTitle("Graph Title");
			g.setLegendTitle("Legend");
			ArrayList<String> s = new ArrayList<>();
			s.add("second");
			s.add("lol");
			g.setSeriesNames(s);
			ArrayList<Color> colors = new ArrayList<>();
			colors.add(ColorGenerator.ORANGE_RED_2);
			colors.add(ColorGenerator.DARK_CYAN);
			g.setSeriesColors(colors);
			g.setPreferredSize(new Dimension(600, 600));
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.add(g);
			frame.pack();
			frame.setLocationRelativeTo(null);
			frame.setVisible(true);
		});
	}
}
