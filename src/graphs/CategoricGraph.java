
package graphs;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.util.List;

/**
 * Class representing all categoric graphs. The data model it requires is a
 * collection of strings for the independent values, and a collection of
 * collections of doubles for the dependent values.
 */
public abstract class CategoricGraph extends XYGraph<String> {

	private static final long serialVersionUID = 3085485342069260175L;

	/**
	 * The "starting" point for the graph. This is the minimum value to use for
	 * calculating how to scale and draw the graph as a pair of axis.
	 */
	protected double firstPoint = 0d;

	/**
	 * This is the value/independent value to use when calculating how to draw
	 * the graph as a pair of axis and data.
	 */
	protected double valPerIndependent = 1d;

	/**
	 * Called to process the independent data. Simply iterates through the
	 * length of the {@code dataModel}'s independent dataset and adds the
	 * corresponding x value to the {@code xPlotPoint}s list. The x value is
	 * determined by {@code firstPoint}, and {@code valPerIndependent}. Each
	 * independent variable is given an equal amount of space on the chart.
	 * 
	 * @param list
	 *            The List of strings to process as the independent dataset.
	 */
	protected void processStringData(List<String> list) {
		xMinVal = 0d;
		xMaxVal = (double) dataModel.getIndependent().size();
		for (int i = 0; i < dataModel.getIndependent().size(); i++) {
			xPlotPoints.add(convert(i + 1, true));
		}
	}

	/**
	 * Need to check the maximum draw length of the categoric variables first.
	 * If the string is long enough to be rotated 90 degrees, then extra space
	 * needs to be added around the graph to account for this.
	 */
	protected double convert(double d, boolean horizontal) {
		if (drawingPanel.getHeight() != 0) {
			AffineTransform aff = new AffineTransform();
			FontRenderContext fontRenderContext = new FontRenderContext(aff, true, true);
			Font font = fontLoader.getLabelFont();

			double maxTextWidth = 0;
			for (String s : dataModel.getIndependent()) {
				maxTextWidth = Math.max(font.getStringBounds(s, fontRenderContext).getWidth(), maxTextWidth);
			}
			maxTextWidth += 5;
			double yLoc = super.convert(zeroIsInRange(false) ? 0d : yMinVal, false);

			if (yLoc + maxTextWidth > drawingPanel.getHeight()) {
				double columnWidth = super.convert(firstPoint + valPerIndependent, true) - super.convert(firstPoint, true);

				if (maxTextWidth > columnWidth) {
					double range = yMaxVal - yMinVal;
					double stringMargin = (maxTextWidth / drawingPanel.getHeight());

					if (prcntMargin - stringMargin > 0) {
						vPadMin = range * (prcntMargin + stringMargin / 2d);
						vPadMax = range * (prcntMargin - stringMargin / 2d);
					} else {
						double diff = (0 - (prcntMargin - stringMargin)) / 0.5d;
						vPadMin = range * (prcntMargin + diff + stringMargin / 2d);
						vPadMax = range * (prcntMargin + diff - stringMargin / 2d);
					}
				}
			}
		}
		return super.convert(d, horizontal);
	}

	/**
	 * Draws the independent variables as the labels. If the length of the
	 * maximum length string in the independent variables collection is too
	 * long, all strings will be rotated and drawn vertically.
	 */
	protected void drawXLabels(Graphics2D g) {
		utils.GraphicsAuxiliary.setupAA(g);
		g.setStroke(axisStroke);
		g.setColor(axisStrokeColor);
		g.setFont(fontLoader.getLabelFont());
		g.setColor(axisStrokeColor);

		double columnWidth = convert(firstPoint + valPerIndependent * 1, true) - convert(firstPoint, true);
		boolean drawHorizontal = true;
		FontMetrics fm = g.getFontMetrics();

		for (String s : dataModel.getIndependent()) {
			if (fm.stringWidth(s) > columnWidth) {
				drawHorizontal = false;
				break;
			}
		}

		for (int i = 0; i < dataModel.getIndependent().size(); i++) {
			double xLoc = convert(firstPoint + valPerIndependent * i + valPerIndependent / 2d, true);
			double yLoc = convert(zeroIsInRange(false) ? 0d : yMinVal, false);

			String text = dataModel.getIndependent().get(i);

			if (drawHorizontal) {
				g.drawString(text, (int) (xLoc - fm.stringWidth(text) / 2), (int) (yLoc + fm.getHeight()));
			} else {
				AffineTransform aff = g.getTransform();
				double x = xLoc + fm.getHeight() / 4;
				double y = yLoc + fm.stringWidth(text) + 5;
				g.translate(x, y);
				g.rotate(Math.toRadians(-90));
				g.drawString(text, 0, 0);
				g.setTransform(aff);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see graphs.Graph#convertPoints()
	 */
	@Override
	protected void convertPoints() {
		xPlotPoints.clear();
		xRangeAuto = false;

		for (Series aSeries : series) {
			aSeries.getyPlotPoints().clear();
		}

		hpad = null;
		vPadMin = null;
		vPadMax = null;

		processDependents();
		processStringData(dataModel.getIndependent());
		yRangeAuto = false;

		if (yMinVal != null) {
			yMinVal = Math.min(yMinVal, 0);
		}
	}
}
