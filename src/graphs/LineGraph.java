
package graphs;

import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;

public class LineGraph<E extends Number, T extends Number> extends ContinuousGraph<E, T> {
	private static final long serialVersionUID = 830656033876679737L;

	protected boolean drawPoints = true;
	protected double pointRadius = 3d;
	protected static final double MAX_POINT_SIZE = 10d;
	protected static final double MIN_POINT_SIZE = 1d;

	public LineGraph() {
		super();
	}

	@Override
	protected void drawGraph(Graphics2D g) {
		if (xPlotPoints.size() != 0) {
			if (yPlotPoints.size() % xPlotPoints.size() != 0) {
				throw new DataNotSetException();
			}
		}
		Double xPrev = null;
		Double yPrev = null;
		for (int i = 0, y = 0; i < yPlotPoints.size() / xPlotPoints.size(); i++) {
			xPrev = xPlotPoints.get(0);
			yPrev = yPlotPoints.get(y);
			g.setColor(seriesColors.get(i));
			drawPoint(xPrev, yPrev, g);
			y++;
			for (int j = 1; j < xPlotPoints.size(); j++, y++) {
				Line2D.Double line = new Line2D.Double(xPrev, yPrev, xPlotPoints.get(j), yPlotPoints.get(y));
				g.draw(line);
				drawPoint(xPlotPoints.get(j), yPlotPoints.get(y), g);
				xPrev = xPlotPoints.get(j);
				yPrev = yPlotPoints.get(y);
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

	public void drawPoints(boolean b) {
		drawPoints = b;
	}

	public void setPointSize(double d) {
		d = assertInRange(0, 1, d);
		pointRadius = d * (MAX_POINT_SIZE - MIN_POINT_SIZE) + MIN_POINT_SIZE;
		repaint();
	}

}
