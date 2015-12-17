
package examples;

import graphs.AreaLineGraph;
import graphs.LineGraph;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import data.ContinuousDataSet;

/**
 * Demonstrates using an area line graph to show a sin wave. 
 */
public class AreaLineGraphExample {
	
	private static LineGraph g;

	public static void main(String[] args) {
		ContinuousDataSet d = new ContinuousDataSet();
		ArrayList<Double> xVals = new ArrayList<>();
		d.addFunction(x -> {
			return Math.sin((double) x);
		});

		for (double i = 0; i <= 4 * Math.PI; i += 0.01d) {
			xVals.add(i);
		}
		d.setIndependent(xVals);

		SwingUtilities.invokeLater(() -> {
			JFrame frame = new JFrame();
			g = new AreaLineGraph();
			g.setDataModel(d);
			g.setLegendTitle("A series");
			g.setTitle("Area Line graph example");
			g.setLegendTransparency(0.5d);
			g.setDrawPoints(false);
			g.setSeriesName(0, "Oooh a sin wave");
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			g.setPreferredSize(new Dimension((int) screenSize.getWidth() - 100, (int) screenSize.getHeight() - 100));
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.add(g);
			frame.pack();
			frame.setLocationRelativeTo(null);
			frame.setVisible(true);
		});
	}
}
