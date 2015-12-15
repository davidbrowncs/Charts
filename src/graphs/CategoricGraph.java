
package graphs;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.util.List;
import java.util.Objects;

import data.CategoricDataSet;
import data.ContinuousDataSet;

public abstract class CategoricGraph extends XYGraph {

	private static final long serialVersionUID = 3085485342069260175L;

	protected double firstPoint = 0d;
	protected double valPerIndependent = 1d;

	public CategoricGraph() {
		super();
	}

	// Only called for independents
	protected void processStringData(List<String> list) {
		xMinVal = 0d;
		xMaxVal = (double) dataSet.getIndependent().size();
		for (int i = 0; i < dataSet.getIndependent().size(); i++) {
			xPlotPoints.add(convert(i + 1, true));
		}
	}

	protected void setDataSet(CategoricDataSet dataSet) {
		Objects.requireNonNull(dataSet);
		if (!dataSet.equals(this.dataSet)) {
			this.dataSet = dataSet;
			dataSet.addObserver(this);
			updated();
			legend.setSeries(series);
		}
	}

	protected void setDataSet(ContinuousDataSet dataSet) {
		throw new UnsupportedOperationException();
	}

	@SuppressWarnings("unchecked")
	protected double convert(double d, boolean horizontal) {
		if (drawingPanel.getHeight() != 0) {
			if (vpad == null) {
				AffineTransform aff = new AffineTransform();
				FontRenderContext fontRenderContext = new FontRenderContext(aff, true, true);
				Font font = fontLoader.getLabelFont();
				double maxTextWidth = 0;
				// Get max
				for (String s : (List<String>) dataSet.getIndependent()) {
					double val = font.getStringBounds(s, fontRenderContext).getWidth();
					if (val > maxTextWidth) {
						maxTextWidth = val;
					}
				}
				if (maxTextWidth + 5 > super.convert(firstPoint + valPerIndependent, true) - super.convert(firstPoint, true)) {
					double margin = prcntMargin + (maxTextWidth / drawingPanel.getHeight());
					vpad = (yMaxVal - yMinVal) * margin;
				}
			}
		}
		return super.convert(d, horizontal);
	}

	@SuppressWarnings("unchecked")
	public void drawXLabels(Graphics2D g) {
		utils.GraphicsAuxiliary.setupAA(g);
		g.setStroke(axisStroke);
		g.setColor(axisStrokeColor);
		g.setFont(fontLoader.getLabelFont());
		g.setColor(axisStrokeColor);

		double columnWidth = convert(firstPoint + valPerIndependent * 1, true) - convert(firstPoint, true);
		boolean drawHorizontal = true;
		FontMetrics fm = g.getFontMetrics();

		// Only way a dataset can be assigned to a graph is through the
		// "setDataSet" method, which accepts the super DataSet as an argument.
		// which will call either the "setDataSet" accepting a CategoricDataSet
		// as an argument, or ContinuousDataSet as an argument. If the dataset
		// is not a CategoricdataSet, an exception will be thrown, so the only
		// possible independent variables in a CategoricGraph is a list of
		// Strings, since a CategoricDataSet has a collection of strings for its
		// independent variables.
		for (String s : (List<String>) dataSet.getIndependent()) {
			if (fm.stringWidth(s) > columnWidth) {
				drawHorizontal = false;
				break;
			}
		}

		for (int i = 0; i < dataSet.getIndependent().size(); i++) {
			double xLoc = convert(firstPoint + valPerIndependent * i + valPerIndependent / 2d, true);
			double yLoc = convert(zeroIsInRange(false) ? 0d : yMinVal, false);

			String text = (String) dataSet.getIndependent().get(i);

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

	@SuppressWarnings("unchecked")
	@Override
	protected void convertPoints() {
		xPlotPoints.clear();
		xRangeAuto = false;

		for (Series aSeries : series) {
			aSeries.getyPlotPoints().clear();
		}

		hpad = null;
		vpad = null;

		processStringData((List<String>) dataSet.getIndependent());
		processDependents();
		yRangeAuto = false;

		if (yMinVal != null) {
			yMinVal = Math.min(yMinVal, 0);
		}
	}
}
