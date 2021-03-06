
package graphs;

import java.awt.Color;
import java.awt.Stroke;
import java.util.ArrayList;
import java.util.List;

public class Series {

	private String seriesName;
	private List<Double> yPlotPoints = new ArrayList<>();
	private List<Double> values = new ArrayList<>();
	private boolean drawing = true;
	private Stroke stroke;
	private Color color;

	public Series(List<Double> vals, String name, Stroke s, Color c) {
		values = vals;
		this.seriesName = name;
		this.stroke = s;
		this.color = c;
	}

	/**
	 * @return the seriesName
	 */
	public String getName() {
		return seriesName;
	}

	/**
	 * @param seriesName
	 *            the seriesName to set
	 */
	public void setName(String seriesName) {
		this.seriesName = seriesName;
	}

	/**
	 * @return the xPlotPoints
	 */
	public List<Double> getyPlotPoints() {
		return yPlotPoints;
	}

	/**
	 * @param xPlotPoints
	 *            the xPlotPoints to set
	 */
	public void setyPlotPoints(ArrayList<Double> xPlotPoints) {
		this.yPlotPoints = xPlotPoints;
	}

	/**
	 * @return the values
	 */
	public List<Double> getValues() {
		return values;
	}

	/**
	 * @param values
	 *            the values to set
	 */
	public void setValues(List<Double> values) {
		this.values = values;
	}

	/**
	 * @return the drawing
	 */
	public boolean isDrawing() {
		return drawing;
	}

	/**
	 * @param drawing
	 *            the drawing to set
	 */
	public void setDrawing(boolean drawing) {
		this.drawing = drawing;
	}

	/**
	 * @return the stroke
	 */
	public Stroke getStroke() {
		return stroke;
	}

	/**
	 * @param stroke
	 *            the stroke to set
	 */
	public void setStroke(Stroke stroke) {
		this.stroke = stroke;
	}

	/**
	 * @return the color
	 */
	public Color getColor() {
		return color;
	}

	/**
	 * @param color
	 *            the color to set
	 */
	public void setColor(Color color) {
		this.color = color;
	}

}
