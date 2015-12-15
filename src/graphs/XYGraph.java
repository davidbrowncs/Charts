
package graphs;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import utils.ColorGenerator;
import utils.DoubleCheck;
import utils.GraphicsAuxiliary;

public abstract class XYGraph extends Graph {
	private static final long serialVersionUID = -5744987843100969137L;

	protected boolean drawXLabels = true;
	protected boolean drawYLabels = true;

	protected boolean xRangeAuto = true;
	protected Double xMinVal = null;
	protected Double xMaxVal = null;

	protected boolean yRangeAuto = true;
	protected Double yMinVal = null;
	protected Double yMaxVal = null;

	protected int numAxisLines = 10;
	protected int axisLabelSize = 6;

	protected boolean drawXGridLines = true;
	protected int numXGridLines = 10;

	protected boolean drawYGridLines = true;
	protected int numYGridLines = 10;

	protected ArrayList<Double> xPlotPoints = new ArrayList<>();
	// Y plot points stored in series arraylist

	/**
	 * Amount of horizontal "padding" to add around the graph being drawn based
	 * on {@code prcntMargin}. This is in terms of the data values, it isn't a
	 * pixel amount of space
	 */
	protected Double hpad;

	/**
	 * Amount of vertical "padding" to add around the graph being drawn based on
	 * {@code prcntMargin}. This is in terms of the data values, it isn't a
	 * pixel amount of space
	 */
	protected Double vpad;

	protected boolean drawXAxis = true;
	protected boolean drawYAxis = true;

	public XYGraph() {
		super();
	}

	protected void drawXGridLines(Graphics2D g) {
		drawGridLines(g, true);
	}

	protected void drawYGridLines(Graphics2D g) {
		drawGridLines(g, false);
	}

	protected void drawGridLines(Graphics2D g, boolean h) {
		g.setStroke(gridLineStroke);
		g.setColor(gridLineColor);
		g.setColor(gridLineColor);

		double max = h ? xMaxVal : yMaxVal;
		double min = h ? xMinVal : yMinVal;
		double dif = (max - min) / (numAxisLines);

		double screenStart = convert(h ? yMinVal : xMinVal, !h);
		double screenEnd = convert(h ? yMaxVal : xMaxVal, !h);

		if (!zeroIsInRange(h)) {
			double current = min;
			int num = h ? numXGridLines : numYGridLines;
			for (int i = 0; i <= num; i++, current += dif) {
				double val = convert(current, h);
				Line2D.Double line = h ? new Line2D.Double(val, screenStart, val, screenEnd) : new Line2D.Double(
						screenStart, val, screenEnd, val);
				g.draw(line);
			}
		} else {
			if (h) {
				double current = 0;
				while (current <= max + 0.01d * dif) {
					double val = convert(current, h);
					Line2D.Double line = new Line2D.Double(val, screenStart, val, screenEnd);
					g.draw(line);
					current += dif;
				}
				current = 0 - dif;
				while (current >= min - 0.01d * dif) {
					double val = convert(current, h);
					Line2D.Double line = new Line2D.Double(val, screenStart, val, screenEnd);
					g.draw(line);
					current -= dif;
				}
			} else {
				double current = 0;
				while (current <= max + 0.01d * dif) {
					double val = convert(current, h);
					Line2D.Double line = new Line2D.Double(screenStart, val, screenEnd, val);
					g.draw(line);
					current += dif;
				}
				current = 0 - dif;
				while (current >= min - 0.01d * dif) {
					double val = convert(current, h);
					Line2D.Double line = new Line2D.Double(screenStart, val, screenEnd, val);
					g.draw(line);
					current -= dif;
				}
			}
		}

	}

	protected void drawXLabel(Graphics2D g) {
		g.setFont(fontLoader.getTextFont());
		double extra = axisPadding * prcntMargin;
		double yExtra = (yMaxVal - yMinVal) * extra;
		double min = convert(yMinVal - yExtra, false);
		float x = (float) convert((xMaxVal - xMinVal) / 2 + xMinVal, true) - g.getFontMetrics().stringWidth(xLabel) / 2;
		float y = (float) (min + axisLabelOffset + g.getFontMetrics().getHeight() / 2);
		if (y > drawingPanel.getHeight() - 5) {
			y = drawingPanel.getHeight() - 5;
		}
		g.drawString(xLabel, x, y);
	}

	@Override
	protected void draw(Graphics2D g) {
		if (dataSet != null) {
			int depCount = 0;
			if (dataSet.getDependent().size() != 0) {
				for (ArrayList<Double> l : dataSet.getDependent()) {
					if (l.size() != 0) {
						depCount++;
						break;
					}
				}
			}
			if (dataSet.getIndependent().size() != 0 && depCount != 0) {
				updated();
				g.setColor(backgroundColor);
				GraphicsAuxiliary.setupAA(g);
				if (drawXGridLines) {
					drawXGridLines(g);
				}
				if (drawYGridLines) {
					drawYGridLines(g);
				}
				drawGraph(g);
				if (drawXAxis) {
					drawXAxis(g);
				}
				if (drawYAxis) {
					drawYAxis(g);
				}
				if (drawXLabels) {
					drawXLabels(g);
				}
				if (drawYLabels) {
					drawYLabels(g);
				}
				if (xLabel != null) {
					drawXLabel(g);
				}
				if (yLabel != null) {
					drawYLabel(g);
				}
			}
		}
	}

	protected void drawXAxis(Graphics2D g) {
		drawAxis(g, false);
	}

	protected void drawYAxis(Graphics2D g) {
		drawAxis(g, true);
	}

	protected boolean zeroIsInRange(boolean h) {
		return h ? xMinVal <= 0 && xMaxVal >= 0 : yMinVal <= 0 && yMaxVal >= 0;
	}

	protected void drawAxis(Graphics2D g, boolean h) {
		GraphicsAuxiliary.setupAA(g);
		Color prevColor = g.getColor();
		Stroke previousStroke = g.getStroke();

		g.setColor(axisStrokeColor);
		g.setStroke(axisStroke);

		// Don't do anything if there's not 0 to be drawn to in range
		if (!zeroIsInRange(h)) {
			return;
		}
		double loc = convert(0d, h);

		// 15 is the amount of "extra" to add to the end of the gridline, if
		// this wasn't added the gridline would be drawn to the maximum/minimum
		// points exactly and that just looks silly
		double max = convert(h ? yMaxVal : xMaxVal, !h) + (h ? -15 : 15);
		double min = convert(h ? yMinVal : xMinVal, !h) + (h ? 15 : -15);

		Line2D.Double line = h ? new Line2D.Double(loc, min, loc, max) : new Line2D.Double(min, loc, max, loc);
		g.draw(line);

		g.setColor(prevColor);
		g.setStroke(previousStroke);
	}

	protected void drawYLabel(Graphics2D g) {
		utils.GraphicsAuxiliary.setupAA(g);
		g.setFont(fontLoader.getLabelFont());
		double loc = convert(Math.min(xMinVal, 0d), true);
		int place = (int) (loc - g.getFontMetrics().stringWidth("2222222") + g.getFontMetrics().getHeight());

		g.setFont(fontLoader.getTextFont());

		float x = place - g.getFontMetrics().getHeight();
		if (x - g.getFontMetrics().getHeight() < 0) {
			x = g.getFontMetrics().getHeight();
		}

		float y = (float) convert((yMaxVal - yMinVal) / 2 + yMinVal, false) + g.getFontMetrics().stringWidth(yLabel) / 2;

		AffineTransform aff = g.getTransform();
		g.translate(x, y);
		g.rotate(Math.toRadians(-90));
		g.drawString(yLabel, 0, 0);
		g.setTransform(aff);
	}

	protected void drawXLabels(Graphics2D g) {
		drawLabels(g, true);
	}

	protected void drawYLabels(Graphics2D g) {
		drawLabels(g, false);
	}

	protected void setupLabelGraphics(Graphics2D g) {
		g.setStroke(axisStroke);
		g.setColor(axisStrokeColor);
		g.setFont(fontLoader.getLabelFont());
	}

	protected void drawLabels(Graphics2D g, boolean h) {
		g.setStroke(axisStroke);
		g.setColor(axisStrokeColor);
		g.setFont(fontLoader.getLabelFont());
		g.setColor(axisStrokeColor);
		DecimalFormat format = new DecimalFormat("0.###E0");

		double max = h ? xMaxVal : yMaxVal;
		double min = h ? xMinVal : yMinVal;
		double loc = !h ? convert(Math.max(xMinVal, 0d), !h) : convert(Math.max(yMinVal, 0d), !h);

		ArrayList<Double> labelLocs = new ArrayList<>();
		ArrayList<Double> vals = new ArrayList<>();
		double dif = (max - min) / numAxisLines;
		double current;
		if (!zeroIsInRange(h)) {
			current = min;
			for (int i = 0; i <= numAxisLines; i++) {
				vals.add(current);
				labelLocs.add(convert(current, h));
				current += dif;
			}
		} else {
			current = 0;
			while (current <= max + 0.01d * dif) {
				vals.add(current);
				labelLocs.add(convert(current, h));
				current += dif;
			}
			current = 0 - dif;
			while (current >= min - 0.01d * dif) {
				vals.add(current);
				labelLocs.add(convert(current, h));
				current -= dif;
			}
		}
		if (h) {
			for (int c = 0; c < labelLocs.size(); c++) {
				double i = labelLocs.get(c);
				Line2D.Double line = new Line2D.Double(i, loc - axisLabelSize / 2d, i, loc + axisLabelSize / 2d);
				g.draw(line);

				double val = DoubleCheck.roundToSignificantFigures(vals.get(c), 4);

				// Check if value is close to 0 wrt. the range of data
				if (DoubleCheck.rangeDifferenceFactor(val, 0, xMaxVal - xMinVal) < 0.001d) {
					val = 0d;
				}

				String display = Math.abs(val) > MAX_POW_10 || Math.abs(val) < (1d / MAX_POW_10) && val != 0d ? format
						.format(val) : Double.toString(val);
				g.drawString(display, (int) i, (int) (loc + 15));
			}
		} else {
			for (int c = 0; c < labelLocs.size(); c++) {
				double i = labelLocs.get(c);
				Line2D.Double line = new Line2D.Double(loc - axisLabelSize / 2d, i, loc + axisLabelSize / 2d, i);
				g.draw(line);

				double val = DoubleCheck.roundToSignificantFigures(vals.get(c), 4);

				// Check if value is close to 0 wrt. the range of data
				if (DoubleCheck.rangeDifferenceFactor(val, 0, yMaxVal - yMinVal) < 0.001d) {
					val = 0d;
				}

				String display = Math.abs(val) > MAX_POW_10 || Math.abs(val) < (1d / MAX_POW_10) && val != 0d ? format
						.format(val) : Double.toString(val);

				int place = (int) (loc - g.getFontMetrics().stringWidth(display) - 2);
				if (place < 0) {
					place = 0;
				}
				g.drawString(display, place, (int) i - 5);
			}
		}
	}

	protected void processDependents() {
		// Check if the current series does not agree with the data set's
		// dependent variable size
		if (series.size() <= dataSet.getDependent().size()) {
			for (int i = 0; i < series.size(); i++) {
				series.get(i).setValues(dataSet.getDependent().get(i));
			}
			for (int i = series.size(); i < dataSet.getDependent().size(); i++) {
				series.add(new Series(dataSet.getDependent().get(i), "Series - " + (i + 1), DEFAULT_STROKE, ColorGenerator
						.getColor(alpha)));
			}
		} else if (series.size() > dataSet.getDependent().size()) {
			for (int i = series.size() - 1; i >= dataSet.getDependent().size(); i--) {
				series.remove((int) i);
			}
			for (int i = 0; i < series.size(); i++) {
				series.get(i).setValues(dataSet.getDependent().get(i));
			}
		}

		for (int i = 0; i < dataSet.getDependent().size(); i++) {
			processNumberData(series.get(i).getValues(), yRangeAuto, series.get(i).getyPlotPoints(), false);
		}
	}

	protected void processNumberData(List<? extends Number> list, boolean auto, List<Double> processed, boolean horizontal) {
		ArrayList<Double> all = new ArrayList<>();
		for (Number n : list) {
			all.add(n.doubleValue());
		}

		// Find the max vals from this data set
		double tmpMin = Double.MAX_VALUE;
		double tmpMax = -Double.MAX_VALUE;
		for (Double d : all) {
			if (d < tmpMin && !Double.isInfinite(d) && !Double.isNaN(d)) {
				tmpMin = d;
			}
			if (d > tmpMax && !Double.isInfinite(d) && !Double.isNaN(d)) {
				tmpMax = d;
			}
		}
		if (auto) {
			setRangeVals(horizontal, tmpMin, tmpMax);
		}

		// Round to nice numbers if they're close enough (within 3%)
		if (xMaxVal != null) { // Only check one since if max is set min will
								// also be set
			xMinVal = DoubleCheck.rangeDifferenceFactor(xMinVal, Math.rint(xMinVal), xMaxVal - xMinVal) < 0.03d ? Math
					.rint(xMinVal) : xMinVal;
			xMaxVal = DoubleCheck.rangeDifferenceFactor(xMaxVal, Math.rint(xMaxVal), xMaxVal - xMinVal) < 0.03d ? Math
					.rint(xMaxVal) : xMaxVal;
		}
		if (yMaxVal != null) {
			yMinVal = DoubleCheck.rangeDifferenceFactor(yMinVal, Math.rint(yMinVal), yMaxVal - yMinVal) < 0.03d ? Math
					.rint(yMinVal) : yMinVal;
			yMaxVal = DoubleCheck.rangeDifferenceFactor(yMaxVal, Math.rint(yMaxVal), yMaxVal - yMinVal) < 0.03d ? Math
					.rint(yMaxVal) : yMaxVal;
		}

		// Now for the whole data set, convert each value to a screen position
		// rather than just a value
		for (Double d : all) {
			processed.add(convert(d, horizontal));
		}
	}

	protected void setRangeVals(boolean horizontal, double tmpMin, double tmpMax) {
		if (closeEnough(tmpMax, tmpMin)) {
			if (!closeEnough(0, tmpMax)) {
				checkRange(horizontal, true, tmpMax, 0);
				checkRange(horizontal, false, tmpMax, 0);
			} else if (!closeEnough(tmpMin, 0)) {
				checkRange(horizontal, true, tmpMin, 0);
				checkRange(horizontal, false, tmpMin, 0);
			} else {
				checkRange(horizontal, true, -1d, Double.MAX_VALUE);
				checkRange(horizontal, false, 1d, -Double.MAX_VALUE);
			}
		} else {
			checkRange(horizontal, true, tmpMin, Double.MAX_VALUE);
			checkRange(horizontal, false, tmpMax, -Double.MAX_VALUE);
		}
	}

	protected void checkRange(boolean horizontal, boolean minimise, double val1, double val2) {
		if (horizontal) {
			if (minimise) {
				xMinVal = xMinVal == null ? Math.min(val1, val2) : Math.min(Math.min(val1, val2), xMinVal);
			} else {
				xMaxVal = xMaxVal == null ? Math.max(val1, val2) : Math.max(Math.max(val1, val2), xMaxVal);
			}
		} else {
			if (minimise) {
				yMinVal = yMinVal == null ? Math.min(val1, val2) : Math.min(Math.min(val1, val2), yMinVal);
			} else {
				yMaxVal = yMaxVal == null ? Math.max(val1, val2) : Math.max(Math.max(val1, val2), yMaxVal);
			}
		}
	}

	protected double zeroInRange(double d, boolean horizontal) {
		return horizontal ? (0 > xMinVal && 0 < xMaxVal ? 0 : xMinVal) : (0 > yMinVal && 0 < yMaxVal ? 0 : yMaxVal);
	}

	protected double convert(double d, boolean horizontal) {
		if (Double.isNaN(d)) {
			return convert(horizontal ? zeroInRange(xMinVal, horizontal) : zeroInRange(yMinVal, horizontal), horizontal);
		} else if (Double.isInfinite(d)) {
			return convert(horizontal ? zeroInRange(xMinVal, horizontal) : zeroInRange(yMinVal, horizontal), horizontal);
		}
		if (horizontal) {
			if (hpad == null) {
				if (yLabel != null) {
					// Add more padding for the ylabel
					hpad = (xMaxVal - xMinVal) * prcntMargin / 0.5d;
				} else {
					hpad = (xMaxVal - xMinVal) * prcntMargin;
				}
			}
			double tmpXMin = xMinVal - hpad;
			double tmpXMax = xMaxVal + hpad;

			if (!closeEnough(tmpXMax, tmpXMin)) {
				double pp = drawingPanel.getWidth() / (tmpXMax - tmpXMin);
				double val = (d - tmpXMin) * pp;
				return val;
			} else {
				return drawingPanel.getWidth() / 2;
			}
		} else {
			if (vpad == null) {
				vpad = (yMaxVal - yMinVal) * prcntMargin;
			}
			double tmpYMin = yMinVal - vpad;
			double tmpYMax = yMaxVal + vpad;

			if (!closeEnough(tmpYMax, tmpYMin)) {
				double pp = drawingPanel.getHeight() / (tmpYMax - tmpYMin);
				double val = (d - tmpYMin) * pp;
				val = drawingPanel.getHeight() - val;
				return val;
			} else {
				return drawingPanel.getHeight() / 2;
			}
		}
	}

}
