
package graphs;

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.geom.Path2D;
import java.util.ArrayList;

public class AreaLineGraph extends LineGraph {
	private static final long serialVersionUID = -9116139559301144472L;

	protected double alphaFraction = 0.3d;

	public AreaLineGraph() {
		super();
	}

	@Override
	protected void drawGraph(Graphics2D g) {
		AlphaComposite composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER);
		Composite oldComposite = g.getComposite();
		g.setComposite(composite);

		Double xPrev = null;
		Double yPrev = null;
		double yOrigin = convert(Math.max(0d, yMinVal), false);

		for (int i = 0; i < series.size(); i++) {
			Path2D.Double path;
			Series set = series.get(i);
			ArrayList<Double> yPlotPoints = set.getyPlotPoints();

			xPrev = xPlotPoints.get(0);
			yPrev = yPlotPoints.get(0);
			g.setColor(utils.ColorGenerator.convertToAlpha(set.getColor(), (int) (alpha * alphaFraction)));
			for (int j = 1; j < xPlotPoints.size(); j++) {
				path = new Path2D.Double();

				double nx = xPlotPoints.get(j);
				double ny = yPlotPoints.get(j);

				path.moveTo(xPrev, yPrev);
				path.lineTo(nx, ny);
				path.lineTo(nx, yOrigin);
				path.lineTo(xPrev, yOrigin);
				path.closePath();

				xPrev = nx;
				yPrev = ny;
				g.fill(path);
			}
		}
		g.setComposite(oldComposite);
		super.drawGraph(g);
	}
}
