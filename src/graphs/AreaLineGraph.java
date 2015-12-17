
package graphs;

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.util.List;

/**
 * Class to draw a line graph with the area under a line connected chart filled
 * in. Usually the area is bounded by y = 0, but if 0 is not in range, the area
 * is instead bounded by the minimum y value.
 */
public class AreaLineGraph extends LineGraph {

	private static final long serialVersionUID = -9116139559301144472L;

	/**
	 * Value to multiply the graph's alpha value by to obtain the alpha value
	 * for drawing the area under the line.
	 */
	protected double alphaFraction = 0.3d;

	/**
	 * Override the line-graphs drawGraph method to add filling the area under
	 * the line. Done so by creating a polygon between one point, the next
	 * point, and the corresponding points where y = 0 or y = minVal and the x
	 * values are kept the same.
	 */
	@Override
	protected void drawGraph(Graphics2D g) {
		Composite composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER);
		Composite oldComposite = g.getComposite();
		g.setComposite(composite);

		Double xPrev = null;
		Double yPrev = null;
		double yOrigin = convert(Math.max(0d, yMinVal), false);

		for (int i = 0; i < series.size(); i++) {
			Polygon p;
			Series set = series.get(i);
			List<Double> yPlotPoints = set.getyPlotPoints();

			xPrev = xPlotPoints.get(0);
			yPrev = yPlotPoints.get(0);
			g.setColor(utils.ColorGenerator.convertToAlpha(set.getColor(), (int) (alpha * alphaFraction)));
			for (int j = 1; j < xPlotPoints.size(); j++) {
				p = new Polygon();

				double nx = xPlotPoints.get(j);
				double ny = yPlotPoints.get(j);

				p.addPoint((int) Math.rint(xPrev), (int) Math.rint(yPrev));
				p.addPoint((int) Math.rint(nx), (int) Math.rint(ny));
				p.addPoint((int) Math.rint(nx), (int) Math.rint(yOrigin));
				p.addPoint((int) Math.rint(xPrev), (int) Math.rint(yOrigin));

				xPrev = nx;
				yPrev = ny;
				g.fill(p);
			}
		}
		g.setComposite(oldComposite);
		super.drawGraph(g);
	}

	/**
	 * Sets the alpha fraction of the graph's alpha value for drawing the area
	 * under a line chart. If the value is outside the range of 0-1, the value
	 * is reset to within this range. The new alphaFraction value is calculated
	 * by multiplying 0.5 with the given value.
	 * 
	 * @param d
	 *            The value to use for the {@code alphaFraction}
	 */
	public void setAreaAlpha(double d) {
		d = assertInRange(0, 1, d);
		alphaFraction = 0.5d * d;
	}
}
