
package graphs;

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.util.ArrayList;

public class AreaLineGraph<E extends Number, T extends Number> extends LineGraph<E, T> {
	private static final long serialVersionUID = -9116139559301144472L;

	protected double alphaFraction = 0.3d;
	
	public AreaLineGraph() {
		super();
	}

	@Override
	protected void drawGraph(Graphics2D g) {
		AlphaComposite a = AlphaComposite.getInstance(AlphaComposite.SRC_OVER);
		Composite c = g.getComposite();
		g.setComposite(a);
		Double xPrev = null;
		Double yPrev = null;
		double yOrigin = convert(Math.max(0d, yMinVal), false);
		for (int i = 0; i < series.size(); i++) {
			Polygon p;
			Series<T> set = series.get(i);
			ArrayList<Double> yPlotPoints = set.getyPlotPoints();
			
			xPrev = xPlotPoints.get(0);
			yPrev = yPlotPoints.get(0);
			g.setColor(utils.ColorGenerator.convertToAlpha(set.getColor(), (int) (alpha * alphaFraction)));
			for (int j = 1; j < xPlotPoints.size(); j++) {
				p = new Polygon();
				double nx = xPlotPoints.get(j);
				double ny = yPlotPoints.get(j);
				p.addPoint(xPrev.intValue(), yPrev.intValue());
				p.addPoint((int) nx, (int) ny);
				p.addPoint((int) nx, (int) yOrigin);
				p.addPoint(xPrev.intValue(), (int) yOrigin);
				xPrev = nx;
				yPrev = ny;
				g.fillPolygon(p);
			}
		}
		g.setComposite(c);
		super.drawGraph(g);
	}
}
