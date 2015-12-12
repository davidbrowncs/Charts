
package graphs;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import data.DataSet;

public class GraphTesting {

	private static LineGraph<Double, Double> g;

	public static void main(String[] args) {
		DataSet<Double, Double> d = new DataSet<>();
		ArrayList<Double> xVals = new ArrayList<>();
		d.addFunction(x -> {
			return x + Math.log(x);
		});

		Random rand = new Random();
		for (double i = 0; i <= 11; i += 0.01d) {
			xVals.add(i);
		}
		d.setInd(xVals);

		SwingUtilities.invokeLater(() -> {
			JFrame frame = new JFrame();
			g = new AreaLineGraph<>();
			g.setDataSet(d);
			g.drawPoints(false);
			g.setLegendTransparency(0.7d);
			g.setPreferredSize(new Dimension(1000, 1000));
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.add(g);
			frame.pack();
			frame.setLocationRelativeTo(null);
			frame.setVisible(true);
		});

	}
}