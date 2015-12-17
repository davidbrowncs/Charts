
package graphs;

import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.util.List;

public class LineGraph extends ContinuousGraph {
	private static final long serialVersionUID = 830656033876679737L;

	protected boolean drawPoints = true;
	protected double pointRadius = 3d;
	protected static final double MAX_POINT_SIZE = 10d;
	protected static final double MIN_POINT_SIZE = 1d;

	@Override
	protected void drawGraph(Graphics2D g) {
		Double xPrev = null;
		Double yPrev = null;
		for (Series set : series) {
			List<Double> yPlotPoints = set.getyPlotPoints();
			xPrev = xPlotPoints.get(0);
			yPrev = yPlotPoints.get(0);
			g.setColor(set.getColor());
			drawPoint(xPrev, yPrev, g);
			for (int j = 1; j < xPlotPoints.size(); j++) {
				Line2D.Double line = new Line2D.Double(xPrev, yPrev, xPlotPoints.get(j), yPlotPoints.get(j));
				g.draw(line);
				drawPoint(xPlotPoints.get(j), yPlotPoints.get(j), g);
				xPrev = xPlotPoints.get(j);
				yPrev = yPlotPoints.get(j);
			}
		}
	}

	private void drawPoint(double x, double y, Graphics2D g) {
		if (drawPoints) {
			Ellipse2D.Double circle = new Ellipse2D.Double(x - pointRadius, y - pointRadius, pointRadius * 2,
					pointRadius * 2);
			g.fill(circle);
		}
	}

	public void setDrawPoints(boolean b) {
		drawPoints = b;
	}

	public void setPointSize(double d) {
		d = assertInRange(0, 1, d);
		pointRadius = d * (MAX_POINT_SIZE - MIN_POINT_SIZE) + MIN_POINT_SIZE;
		repaint();
	}

}
