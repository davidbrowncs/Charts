package examples;

import graphs.LineGraph;

import java.awt.Dimension;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import data.ContinuousDataSet;

public class LineGraphExample {
	private static LineGraph g;

	public static void main(String[] args) {
		ContinuousDataSet d = new ContinuousDataSet();
		ArrayList<Double> xVals = new ArrayList<>();
		d.addFunction(x -> {
			return x + Math.log(x);
		});

		for (double i = 0.00005; i <= 11.00005; i += 0.2d) {
			xVals.add(i);
		}
		d.setIndependent(xVals);

		SwingUtilities.invokeLater(() -> {
			JFrame frame = new JFrame();
			g = new LineGraph();
			g.setDataModel(d);
			g.setLegendTitle("Only one series");
			g.setTitle("Line graph example");
			g.setLegendTransparency(0.5d);
			g.setXLabel("Independent variable");
			g.setYLabel("log(independent)");
			g.setSeriesName(0, "x + log(x)");
			g.setPreferredSize(new Dimension(1000, 1000));
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.add(g);
			frame.pack();
			frame.setLocationRelativeTo(null);
			frame.setVisible(true);
		});

	}
}
