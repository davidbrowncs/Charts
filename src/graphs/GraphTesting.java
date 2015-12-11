
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
//		d.addFunction(x -> {
//			return x + Math.exp(- 0.2 * x) * Math.sin(5 * x);
//		});

		for (double i = -10; i < 10; i += .005) {
			xVals.add(i);
		}
		d.setInd(xVals);
		
		d.addFunction(x -> {
			return Math.exp(-x * 1d / 15d) * Math.sin(x * Math.PI);
		});
//		d.addFunction(x -> {
//			return 2 * Math.pow(x, 3) - 9 * Math.pow(x, 2) - 24 * x + 2;
//		});

		SwingUtilities.invokeLater(() -> {
			JFrame frame = new JFrame();
			Graph<Double, Double> g = new LineGraph<>();
			g.setDataSet(d);
//			g.setTitle("Damped sin wave");
//			g.setLegendTitle("Legend");
			ArrayList<String> s = new ArrayList<>();
//			s.add("second");
//			s.add("lol");
//			g.setSeriesNames(s);
			ArrayList<Color> colors = new ArrayList<>();
			colors.add(ColorGenerator.DARK_CYAN);
			colors.add(ColorGenerator.DARK_CYAN);
			g.setSeriesColors(colors);
			g.setPreferredSize(new Dimension(1000, 1000));
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.add(g);
			frame.pack();
			frame.setLocationRelativeTo(null);
			frame.setVisible(true);
		});
	}
}
