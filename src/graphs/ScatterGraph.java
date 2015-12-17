
package graphs;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.function.Function;

public class ScatterGraph extends ContinuousGraph {

	private static final long serialVersionUID = -2096068392172639822L;

	protected int shapeSize = 5;
	protected static final int MIN_SHAPE_SIZE = 5;
	protected static final int MAX_SHAPE_SIZE = 50;

	protected ArrayList<Shape> shapes = new ArrayList<>();

	public ScatterGraph() {
		shapes = new ArrayList<>(Arrays.asList(Shape.values()));
		Collections.shuffle(shapes);
	}

	@Override
	protected void drawGraph(Graphics2D g) {
		g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_DEFAULT);
		for (int i = 0; i < series.size(); i++) {
			// for (int i = 0, y = 0; i < yPlotPoints.size() /
			// xPlotPoints.size(); i++) {
			Function<ShapeWrapper, java.awt.Shape> f = null;
			final Shape shape = shapes.get(i % Shape.values().length);
			Color color = series.get(i).getColor();
			// Color color = seriesColors.get(i % seriesColors.size());
			g.setColor(color);
			switch (shape) {
				case SQUARE:
					f = s -> {
						return new Rectangle2D.Double(s.x - shapeSize / 2, s.y - shapeSize / 2, shapeSize, shapeSize);
					};
				break;
				case CIRCLE:
					f = s -> {
						return new Ellipse2D.Double(s.x - shapeSize / 2, s.y - shapeSize / 2, shapeSize, shapeSize);
					};
				break;
				case TRIANGLE:
					f = s -> {
						double sz = shapeSize * 2d / 3d;

						Path2D.Double p = new Path2D.Double();

						double h = Math.tan(30 * (Math.PI / 180)) * (sz * 2 / 2);
						double w = sz * 2 * Math.sin(60 * Math.PI / 180) - h;

						double x1 = s.x - sz;
						double y1 = s.y + h;

						double x2 = s.x + sz;
						double y2 = s.y + h;

						double x3 = s.x;
						double y3 = s.y - w;

						p.moveTo(x1, y1);
						p.lineTo(x2, y2);
						p.lineTo(x3, y3);
						p.closePath();

						return p;
					};
				break;
			}

			for (int j = 0; j < xPlotPoints.size(); j++) {
				java.awt.Shape tmpShape = f
						.apply(new ShapeWrapper(xPlotPoints.get(j), series.get(i).getyPlotPoints().get(j)));
				g.fill(tmpShape);
			}
		}
		g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
	}

	public void setDrawShape(Shape shape, int seriesNum) {
		shapes.set(seriesNum, shape);
	}

	public void setShapeSize(double d) {
		final double x = assertInRange(0, 1, d);
		shapeSize = (int) ((MAX_SHAPE_SIZE - MIN_SHAPE_SIZE) * x);
	}

	protected class ShapeWrapper {
		private ShapeWrapper(double x, double y) {
			this.x = x;
			this.y = y;
		}

		double x;
		double y;
	}

	public enum Shape {
		SQUARE(0),
		CIRCLE(1),
		TRIANGLE(2);

		int type;

		private Shape(int type) {
			this.type = type;
		}
	}
}
