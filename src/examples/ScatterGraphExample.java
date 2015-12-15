
package examples;

import graphs.ScatterGraph;
import graphs.ScatterGraph.Shape;

import java.awt.Color;
import java.util.ArrayList;

import javax.swing.SwingUtilities;

import utils.ColorGenerator;
import data.ContinuousDataSet;

public class ScatterGraphExample {

	public static void main(String[] args) {
		// DataSet object, stores an independent dataset and can store many
		// dependent datasets
		ContinuousDataSet d = new ContinuousDataSet();

		// Doesn't matter if functions are added after the independent data set
		d.addFunction(x -> {
			return Math.pow(x, 2) / 5d;
		});

		d.addFunction(x -> {
			return Math.sin(x);
		});

		// Doesn't have to be maths functions, graphs probably used more for
		// recorded results and simply to display them rather than generate
		// results
		ArrayList<Double> nonFunctionResults = new ArrayList<>();
		for (double y = -5; y < 5; y += 0.25) {
			nonFunctionResults.add(y);
		}
		d.addDependentSet(nonFunctionResults);

		ArrayList<Double> xVals = new ArrayList<>();
		for (double i = -5; i < 5; i += 0.25) {
			xVals.add(i);
		}
		d.setIndependent(xVals);

		// Graphs extend JPanels so need to treat them like a swing object still
		SwingUtilities.invokeLater(() -> {
			ScatterGraph g = new ScatterGraph();
			g.setDataSet(d);
			g.setTitle("Scatter Graph Example");

			ArrayList<Color> colors = new ArrayList<>();
			colors.add(ColorGenerator.ORANGE_RED_2);
			colors.add(ColorGenerator.DARK_CYAN);
			colors.add(ColorGenerator.GOLD);

			// Type of shape, which series to draw with that shape, in the order
			// the series were added.
			// Doesn't have to be set manually, can be automatically chosen
			g.setDrawShape(Shape.CIRCLE, 0);
			g.setDrawShape(Shape.TRIANGLE, 1);
			g.setDrawShape(Shape.SQUARE, 2);

			// Between 0 and 1
			g.setShapeSize(0.3d);
			g.setSeriesColors(colors);
			new JFrameWrapper(g);
		});
	}

}
