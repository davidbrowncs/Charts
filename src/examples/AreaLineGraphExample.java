
package examples;

import graphs.AreaLineGraph;
import graphs.Graph;

import java.awt.Dimension;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import data.DataSet;

public class AreaLineGraphExample {
	private static Graph<Double, Double> g;

	public static void main(String[] args) {
		DataSet<Double, Double> d = new DataSet<>();
		ArrayList<Double> xVals = new ArrayList<>();
		d.addFunction(x -> {
			return Math.sin(x);
		});

		for (double i = 0; i <= 4 * Math.PI; i += 0.2d) {
			xVals.add(i);
		}
		d.setInd(xVals);

		SwingUtilities.invokeLater(() -> {
			JFrame frame = new JFrame();
			g = new AreaLineGraph<>();
			g.setDataSet(d);
			g.setLegendTitle("A series");
			g.setTitle("Area Line graph example");
			g.setLegendTransparency(0.5d);
			g.setSeriesName("Oooh a sin wave", 0);
			g.setPreferredSize(new Dimension(1000, 1000));
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.add(g);
			frame.pack();
			frame.setLocationRelativeTo(null);
			frame.setVisible(true);
		});

	}

}
