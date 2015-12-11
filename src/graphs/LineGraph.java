
package graphs;

import java.awt.Graphics2D;
import java.awt.geom.Line2D;

public class LineGraph<E extends Number, T extends Number> extends ContinuousGraph<E, T> {
	private static final long serialVersionUID = 830656033876679737L;

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
			y++;
			for (int j = 1; j < xPlotPoints.size(); j++, y++) {
				Line2D.Double line = new Line2D.Double(xPrev, yPrev, xPlotPoints.get(j), yPlotPoints.get(y));
				g.draw(line);
				xPrev = xPlotPoints.get(j);
				yPrev = yPlotPoints.get(y);
			}
		}
	}
}
