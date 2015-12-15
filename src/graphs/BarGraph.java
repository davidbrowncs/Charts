
package graphs;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Path2D;
import java.util.ArrayList;

public class BarGraph extends CategoricGraph {

	private static final long serialVersionUID = -1204446283392122805L;

	protected double barGap = 0.1d;
	protected int barAlpha = 150;
	protected int barHighlightAlpha = 255;
	protected Color highlightColor = null;
	protected Stroke highlightStroke = new BasicStroke(0.5f);

	public BarGraph() {
		super();
		drawXGridLines = false;
	}

	public void setHighlightColor(Color c) {
		this.highlightColor = c;
	}

	public void setHighlightStroke(Stroke s) {
		highlightStroke = s;
	}

	public void setBarHighlightAlpha(double d) {
		barHighlightAlpha = (int) (assertInRange(0, 1, d) * 255);
	}

	// Must be between 0 and 1 inclusive
	public void setBarAlpha(double d) {
		barAlpha = (int) (255 * assertInRange(0, 1, d));
	}

	/**
	 * Sets the size of the gap between each set of bars for a dataset. All bars
	 * are drawn directly next to each other for each independent variable, this
	 * changes the gaps between each group of bars. If the given value is
	 * outside the range 0 - 0.5. it is reset to within this range.
	 * 
	 * @param d
	 *            The new fraction of space to be allocated to gaps between
	 *            independent variable bar groups.
	 */
	public void setBarGap(double d) {
		barGap = assertInRange(0, 0.5, d);
	}

	@Override
	protected void drawGraph(Graphics2D g) {
		int numDependents = dataSet.getDependent().size();

		double barWidth = (valPerIndependent - 2 * barGap) / numDependents;
		for (int i = 0; i < series.size(); i++) {
			Series set = series.get(i);
			ArrayList<Double> yPlotPoints = set.getyPlotPoints();

			Color alphaColor = utils.ColorGenerator.convertToAlpha(set.getColor(), barAlpha);
			Color fullColor = utils.ColorGenerator.convertToAlpha(highlightColor == null ? set.getColor() : highlightColor,
					barHighlightAlpha);

			int lim = Math.min(yPlotPoints.size(), dataSet.getIndependent().size());
			for (int j = 0; j < lim; j++) {
				Path2D.Double p = new Path2D.Double();

				double yTop = yPlotPoints.get(j).intValue();
				double yBase = convert(0, false);

				double x1 = (convert(j * valPerIndependent + barGap + i * barWidth, true));
				double x2 = (convert(j * valPerIndependent + barGap + (i + 1) * barWidth, true));

				p.moveTo(x1, yTop);
				p.lineTo(x2, yTop);
				p.lineTo(x2, yBase);
				p.lineTo(x1, yBase);
				p.closePath();

				g.setColor(alphaColor);
				g.fill(p);

				p = new Path2D.Double();
				p.moveTo(x1, yBase);
				p.lineTo(x1, yTop);
				p.lineTo(x2, yTop);
				p.lineTo(x2, yBase);

				g.setStroke(highlightStroke);
				g.setColor(fullColor);
				g.draw(p);
			}
		}
	}

}
