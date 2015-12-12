package examples;

import graphs.AreaLineGraph;
import graphs.LineGraph;

import java.awt.Dimension;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import data.DataSet;

public class LineGraphExample {
	private static LineGraph<Double, Double> g;

	public static void main(String[] args) {
		DataSet<Double, Double> d = new DataSet<>();
		ArrayList<Double> xVals = new ArrayList<>();
		d.addFunction(x -> {
			return x + Math.log(x);
		});

		for (double i = 0.00005; i <= 11.00005; i += 0.2d) {
			xVals.add(i);
		}
		d.setInd(xVals);

		SwingUtilities.invokeLater(() -> {
			JFrame frame = new JFrame();
			g = new LineGraph<>();
			g.setDataSet(d);
			g.setLegendTitle("Only one series");
			g.setTitle("Line graph example");
			g.setLegendTransparency(0.5d);
			g.setXLabel("Independent variable");
			g.setYLabel("log(independent)");
			g.setSeriesName("x + log(x)", 0);
			g.setPreferredSize(new Dimension(1000, 1000));
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.add(g);
			frame.pack();
			frame.setLocationRelativeTo(null);
			frame.setVisible(true);
		});

	}
}
