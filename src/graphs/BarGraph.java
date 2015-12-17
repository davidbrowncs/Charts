
package graphs;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Path2D;
import java.util.List;

/**
 * Class for drawing bar graphs. Extends {@code CategoricGraph} so the dataset
 * this class requires is a collection of strings for the independent variables,
 * and a list of lists of doubles.
 */
public class BarGraph extends CategoricGraph {

	private static final long serialVersionUID = -1204446283392122805L;

	/**
	 * The fraction of a unit of space allocated for each independent variable
	 * to be a gap. The gap is between the last bar of one independent
	 * variable's group and the first bar of the next independent variable's
	 * group.
	 */
	protected double barGap = 0.1d;

	/**
	 * The alpha value for filling the of each value with.
	 */
	protected int barAlpha = 150;

	/**
	 * The alpha value used by the color which "highlights" each bar with.
	 */
	protected int barHighlightAlpha = 255;

	/**
	 * Color to highlight each bar with. If this value is null, then the color
	 * used is derived from each series' color using {@code barHighlightAlpha}.
	 */
	protected Color highlightColor = null;

	/**
	 * The stroke used for highlighting each bar with.
	 */
	protected Stroke highlightStroke = new BasicStroke(0.5f);

	/**
	 * Initialise the bar chart with drawing x gridlines to false. This can be
	 * changed but does not make much sense when using a bar chart.
	 */
	public BarGraph() {
		drawXGridLines = false;
	}

	/**
	 * Sets the color to use when highlighting bars. By default, the color used
	 * is derived from a {@code Series}' color. Using this method sets the
	 * "global" color to use, so all bars are highlighted with this color. To
	 * change an individual series color, it can be changed via the
	 * {@code setSeriesColor} method in {@code Graph}
	 * 
	 * @param c
	 *            The color to use when highlighting bars.
	 */
	public void setHighlightColor(Color c) {
		this.highlightColor = c;
	}

	/**
	 * Sets the {@code Stroke} to use when highlighting bars.
	 * 
	 * @param s
	 *            The new stroke to highlight bars with.
	 */
	public void setHighlightStroke(Stroke s) {
		highlightStroke = s;
	}

	/**
	 * Changes the highlighting alpha value to use. The value must be between 0
	 * and 1, if it is not, it is reset to within this range, and then this
	 * value is multiplied by 255 to obtain the alpha value to use for the
	 * highlighting color.
	 * 
	 * @param d
	 *            The fraction to multiply 255 by.
	 */
	public void setBarHighlightAlpha(double d) {
		barHighlightAlpha = (int) (assertInRange(0, 1, d) * 255);
	}

	/**
	 * Sets the alpha value to use when filling each bar with color. This value
	 * must be between 0 and 1, if it is not, it is reset to within this range
	 * and then multiplied by 255 to obtain the alpha value to use when filling
	 * bars with a color.
	 * 
	 * @param d
	 *            The fraction to multiply 255 by.
	 */
	public void setBarAlpha(double d) {
		barAlpha = (int) (255 * assertInRange(0, 1, d));
	}

	/**
	 * Sets the size of the gap between each set of bars for a dataset. All bars
	 * are drawn directly next to each other for each independent variable, this
	 * changes the gaps between each group of bars. If the given value is
	 * outside the range 0 - 1. it is reset to within this range.
	 * 
	 * @param d
	 *            The new fraction of space to be allocated to gaps between
	 *            independent variable bar groups.
	 */
	public void setBarGap(double d) {
		barGap = assertInRange(0, 1, d);
	}

	/**
	 * Draws rectangles from either the origin, or the minimum dependent value
	 * to each dependent value.
	 */
	@Override
	protected void drawGraph(Graphics2D g) {
		int numDependents = dataModel.getDependent().size();

		double barWidth = (valPerIndependent - 2 * barGap) / numDependents;
		for (int i = 0; i < series.size(); i++) {
			Series set = series.get(i);
			List<Double> yPlotPoints = set.getyPlotPoints();

			Color alphaColor = utils.ColorGenerator.convertToAlpha(set.getColor(), barAlpha);
			Color fullColor = utils.ColorGenerator.convertToAlpha(highlightColor == null ? set.getColor() : highlightColor,
					barHighlightAlpha);

			int lim = Math.min(yPlotPoints.size(), dataModel.getIndependent().size());
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
