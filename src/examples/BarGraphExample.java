
package examples;

import graphs.BarGraph;

import java.awt.Color;
import java.awt.Dimension;
import java.text.DecimalFormat;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import data.CategoricDataSet;
import data.DataSet;

public class BarGraphExample {

	private static BarGraph g;

	public static void main(String[] args) {
		DataSet d = new CategoricDataSet();
		ArrayList<String> xVals = new ArrayList<>();

		// First example data set
		// Size does not need to be the same as the x variables, the graph will
		// only draw the smallest set, so if x is smaller, it will draw the size
		// of x bars, if the size of y is smaller, the graph will draw the size
		// of y bars
		ArrayList<Double> y1 = new ArrayList<>();
		for (int i = 0; i < 1000; i++) {
			y1.add(Math.sin(i));
		}
		d.addDependentSet(y1);

		// Second example data set
		ArrayList<Double> y2 = new ArrayList<>();
		for (int i = 0; i < 1000; i++) {
			y2.add(Math.cos(i));
		}
		d.addDependentSet(y2);

		DecimalFormat df = new DecimalFormat("#.##");
		for (int i = 0; i <= 11; i++) {
			xVals.add("X: " + df.format(i));
		}
		d.setIndependent(xVals);

		SwingUtilities.invokeLater(() -> {
			JFrame frame = new JFrame();
			g = new BarGraph();
			g.setDataSet(d);
			g.setHighlightColor(Color.BLACK);
			g.setTitle("Bar graph example");
			g.setLegendTransparency(0.5d);
			g.setXLabel("x");
			g.setYLabel("Value of trigonometric functions");
			g.setSeriesName("Sin(x)", 0);
			g.setSeriesName("Sin(y)", 1);
			g.setPreferredSize(new Dimension(1000, 1000));
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.add(g);
			frame.pack();
			frame.setLocationRelativeTo(null);
			frame.setVisible(true);
		});

	}

}
