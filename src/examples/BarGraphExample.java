
package examples;

import graphs.BarGraph;

import java.awt.Color;
import java.awt.Dimension;
import java.text.DecimalFormat;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import data.CategoricDataSet;

public class BarGraphExample {

	private static BarGraph g;

	public static void main(String[] args) {
		CategoricDataSet d = new CategoricDataSet();
		ArrayList<String> xVals = new ArrayList<>();

		ArrayList<Double> y1 = new ArrayList<>();
		for (int i = 0; i < 10; i++) {
			y1.add(Math.sin(i));
		}
		d.addDependentSet(y1);

		// Second example data set
		ArrayList<Double> y2 = new ArrayList<>();
		for (int i = 0; i < 10; i++) {
			y2.add((double) (i + 1));
		}
		d.addDependentSet(y2);

		for (int i = 0; i <= 9; i++) {
			xVals.add("X " + i);
		}
		d.setIndependent(xVals);

		SwingUtilities.invokeLater(() -> {
			JFrame frame = new JFrame();
			g = new BarGraph();
			g.setDataModel(d);
			g.setHighlightColor(Color.BLACK);
			g.setTitle("Bar graph example");
			g.setLegendTransparency(0.5d);
			g.setXLabel("X label");
			g.setYLabel("Example data in units");
			g.setSeriesName(0, "Sin(x)");
			g.setSeriesName(1, "Sin(y)");
			g.setPreferredSize(new Dimension(1000, 1000));
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.add(g);
			frame.pack();
			frame.setLocationRelativeTo(null);
			frame.setVisible(true);
		});

	}

}
