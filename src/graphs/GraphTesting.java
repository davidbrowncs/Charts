
package graphs;

import java.awt.Color;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import utils.ColorGenerator;
import data.DataSet;

public class GraphTesting {

	private static LineGraph<Double, Double> g;
	
	public static void main(String[] args) {
		DataSet<Double, Double> d = new DataSet<>();
		ArrayList<Double> xVals = new ArrayList<>();
//		d.addFunction(x -> {
//			return x + Math.exp(- 0.2 * x) * Math.sin(5 * x);
//		});

		Random rand = new Random();
		for (double i = -10; i < 10; i += 0.5d) {
			xVals.add(i);
		}
		d.setInd(xVals);
		
		d.addFunction(x -> {
			return Math.exp(-x * 1d / 15d) * Math.sin(x * Math.PI);
		});
//		d.addFunction(x -> {
//			return (2 * Math.pow(x, 3) - 9 * Math.pow(x, 2) - 24 * x + 2) / 10000d;
//		});

		SwingUtilities.invokeLater(() -> {
			JFrame frame = new JFrame();
			g = new LineGraph<>();
			g.setDataSet(d);
			g.setPointSize(0.5);
			g.setTitle("Damped sin wave");
			g.setLegendTitle("Legend");
			ArrayList<String> s = new ArrayList<>();
			g.setLegendTransparency(0.9d);
			g.setSeriesNames(s);
			g.setLegendBackgroundColor(Color.PINK);
			ArrayList<Color> colors = new ArrayList<>();
			colors.add(ColorGenerator.DARK_CYAN);
			colors.add(ColorGenerator.DARK_CYAN);
			g.setSeriesColors(colors);
			g.setPreferredSize(new Dimension(1000, 1000));
			g.legendLeftSide();
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.add(g);
			frame.pack();
			frame.setLocationRelativeTo(null);
			frame.setVisible(true);
		});
		
	}
	
	private static void sleep() {
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
