
package graphs;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import data.ContinuousDataSet;
import data.DataSet;

public class GraphTesting {

	private static LineGraph g;

	public static void main(String[] args) {
		ContinuousDataSet d = new ContinuousDataSet();
		ArrayList<Double> xVals = new ArrayList<>();

		Random rand = new Random();
		for (double x = 1; x <= 10; x += 0.01d) {
			xVals.add(x);
		}
		d.setIndependent(xVals);

		((ContinuousDataSet) d).addFunction(x -> {
			return Math.sin(x.doubleValue());
		});

		SwingUtilities.invokeLater(() -> {
			JFrame frame = new JFrame("Graph example");
			g = new AreaLineGraph();
			g.setDataSet(d);
			g.drawPoints(false);
			g.setTitle("A title");
			g.setLegendTitle("Legend");
			g.setSeriesName("Series 1", 0);
			g.setPreferredSize(new Dimension(1000, 1000));

			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.add(g);
			frame.pack();
			frame.setLocationRelativeTo(null);
			frame.setVisible(true);
		});

	}
}