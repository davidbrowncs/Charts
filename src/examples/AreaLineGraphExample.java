
package examples;

import graphs.AreaLineGraph;
import graphs.Graph;
import graphs.LineGraph;

import java.awt.Dimension;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import data.ContinuousDataSet;
import data.DataSet;

public class AreaLineGraphExample {
	private static Graph g;

	public static void main(String[] args) {
		DataSet d = new ContinuousDataSet();
		ArrayList<Double> xVals = new ArrayList<>();
		((ContinuousDataSet) d).addFunction(x -> {
			return Math.sin((double) x);
		});

		for (double i = 0; i <= 4 * Math.PI; i += 0.01d) {
			xVals.add(i);
		}
		d.setIndependent(xVals);

		SwingUtilities.invokeLater(() -> {
			JFrame frame = new JFrame();
			g = new AreaLineGraph();
			g.setDataSet(d);
			g.setLegendTitle("A series");
			g.setTitle("Area Line graph example");
			g.setLegendTransparency(0.5d);
			((LineGraph) g).drawPoints(false);
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
