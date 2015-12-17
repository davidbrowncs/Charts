
package graphs;

import java.awt.Dimension;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import data.CategoricDataSet;

public class GraphTesting {

	private static BarGraph g;

	public static void main(String[] args) {
		CategoricDataSet d = new CategoricDataSet();
		ArrayList<String> xVals = new ArrayList<>();

		for (double x = 1; x <= 10; x += 1d) {
			xVals.add("Something " + Double.toString(x));
		}
		d.setIndependent(xVals);
		
		ArrayList<Double> y = new ArrayList<>();
		for (int i = 0; i < 10; i++) {
			y.add(Math.sin(i));
		}
		d.addDependentSet(y);

		SwingUtilities.invokeLater(() -> {
			
			JFrame frame = new JFrame("Graph example");
			g = new BarGraph();
			g.setDataModel(d);
			g.setTitle("A title");
			g.setLegendTitle("Legend");
			g.setSeriesName(0, "Series 1");
			g.setPreferredSize(new Dimension(1000, 1000));

			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.add(g);
			frame.pack();
			frame.setLocationRelativeTo(null);
			frame.setVisible(true);
		});
	}
}