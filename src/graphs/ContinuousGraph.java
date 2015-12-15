
package graphs;

import java.util.Objects;

import data.CategoricDataSet;
import data.ContinuousDataSet;

/**
 * @param <E>
 * @param <T>
 */
public abstract class ContinuousGraph extends XYGraph {
	private static final long serialVersionUID = 1805212588655879298L;

	public ContinuousGraph() {
		super();
	}

	@Override
	protected void convertPoints() {
		xPlotPoints.clear();
		for (Series aSeries : series) {
			aSeries.getyPlotPoints().clear();
		}

		// Reset padding around the graph drawing
		hpad = null;
		vpad = null;

		// Do x points, since there is only ever one set
		processNumberData(((ContinuousDataSet) dataSet).getIndependent(), xRangeAuto, xPlotPoints, true);
		processDependents();
	}

	protected void setDataSet(ContinuousDataSet dataSet) {
		Objects.requireNonNull(dataSet);
		if (!dataSet.equals(this.dataSet)) {
			this.dataSet = dataSet;
			dataSet.addObserver(this);
			updated();
			legend.setSeries(series);
		}
	}

	protected void setDataSet(CategoricDataSet dataSet) {
		throw new UnsupportedOperationException();
	}

	public void setDrawXLabels(boolean b) {
		drawXLabels = b;
		repaint();
	}

	public void setDrawYLabels(boolean b) {
		drawYLabels = b;
		repaint();
	}

	public void setDrawHorizontalAxis(boolean b) {
		drawXAxis = b;
		repaint();
	}

	public void setDrawVerticalAxis(boolean b) {
		drawYAxis = b;
		repaint();
	}

	public void setXMin(double x) {
		xRangeAuto = false;
		xMinVal = x;
		repaint();
	}

	public void setXMax(double x) {
		xRangeAuto = false;
		xMaxVal = x;
		repaint();
	}

	public void setYMin(double y) {
		yRangeAuto = false;
		yMinVal = y;
		repaint();
	}

	public void autoCalculateXScale() {
		xRangeAuto = true;
		repaint();
	}

	public void autoCalculateYScale() {
		yRangeAuto = true;
		repaint();
	}

	public void setYMax(double y) {
		yRangeAuto = false;
		yMaxVal = y;
		repaint();
	}

	public void drawVerticalGridLines(boolean b) {
		drawXGridLines = b;
		repaint();
	}

	public void drawHorizontalGridLines(boolean b) {
		drawYGridLines = b;
		repaint();
	}

	public boolean xRangeAuto() {
		return xRangeAuto;
	}

	public boolean yRangeAuto() {
		return yRangeAuto;
	}
}
