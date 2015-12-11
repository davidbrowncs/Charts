
package graphs;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import utils.ColorGenerator;
import data.DataSet;

public abstract class Graph<E, T extends Number> extends JPanel {
	private static final long serialVersionUID = 9103378308225496616L;

	protected static final double ZERO_FACTOR = Double.MIN_VALUE * Math.pow(10, 4);
	protected static final int MAX_POW_10 = (int) Math.pow(10, 4);

	protected static int graphCounter = 0;
	protected int id = graphCounter++;

	protected DataSet<E, T> dataSet;

	// Graphs drawn to this, not the Graph panel itself
	protected JPanel drawingPanel = new JPanel() {
		private static final long serialVersionUID = -8697262090210209523L;

		@Override
		public void paintComponent(Graphics g1) {
			Graphics2D g = (Graphics2D) g1;
			updated();
			draw(g);
		}
	};

	/************************ Drawing vars ******************************/

	protected boolean antiAliasing = true;
	protected double prcntMargin = 0.10d;

	// Is multiplied by prcnt margin, so the amount extra for gridlines is (max
	// or mind data set val) + or - prcntMargin * axisPadding
	protected double axisPadding = 0.3d;

	protected BasicStroke stroke = new BasicStroke(1.5f);
	protected BasicStroke axisStroke = new BasicStroke(1.0f);
	protected BasicStroke gridLineStroke = new BasicStroke(1.0f);

	/*********** Colours **********/

	protected static final Color DEFAULT_STROKE_COLOR = Color.BLACK;
	protected static final Color DEFAULT_AXIS_STROKE_COLOR = Color.BLACK;
	protected static final Color DEFAULT_BACKGROUND_COLOR = UIManager.getColor("Panel.background");
	protected static final Color DEFAULT_GRIDLINE_COLOR = new Color(176, 176, 176, 200);

	protected Color strokeColor = DEFAULT_STROKE_COLOR;
	protected Color axisStrokeColor = DEFAULT_AXIS_STROKE_COLOR;
	protected Color backgroundColor = DEFAULT_BACKGROUND_COLOR;
	protected Color gridLineColor = DEFAULT_GRIDLINE_COLOR;

	protected int alpha = 255;

	/************************* Swing vars *******************************/
	protected JLabel title = null;
	protected JLabel xLabel = null;
	protected JLabel yLabel = null;
	protected Legend legend = new Legend();

	protected Font font = null;

	protected int numSeries = 0;
	protected ArrayList<Color> seriesColors = new ArrayList<>();
	protected boolean manualColors = false;

	protected ArrayList<String> seriesNames = new ArrayList<>();

	protected boolean debug = utils.Debug.isDebug();

	protected abstract void draw(Graphics2D g);

	protected abstract void drawGraph(Graphics2D g);

	protected abstract void convertPoints();

	public abstract void setDataSet(DataSet<E, T> dataSet);

	public Graph() {
		FontLoader.init();
		this.setLayout(new BorderLayout());
		add(drawingPanel, BorderLayout.CENTER);
		add(legend, BorderLayout.EAST);
		this.setBackground(backgroundColor);
	}

	public void setAA(boolean b) {
		antiAliasing = b;
	}

	public void setPrcntMargin(double d) {
		prcntMargin = d;
	}

	public void setLegendTransparency(double d) {// TODO Set
													// transparency
		final double val = assertInRange(d, 0, 1);

	}

	public DataSet<E, T> getDataSet() {
		return dataSet;
	}

	public void removeDataSet() {
		this.dataSet = null;
	}

	public void setTransparency(double d) {
		alpha = utils.ColorGenerator.convertToRGB(assertInRange(0, 1, d));
	}

	protected double assertInRange(double a, double b, double d) {
		double max = Math.max(a, b);
		double min = Math.min(a, b);
		return d > max ? max : d < min ? min : d;
	}

	public void setLegendTitle(String title) {
		legend.setTitle(title);
		updated();
	}

	public void setSeriesNames(ArrayList<String> s) {
		if (s.size() < dataSet.getDependent().size()) {
			throw new IllegalArgumentException("Not enough series names, needed: " + dataSet.getDependent().size()
					+ ", received " + s.size());
		}
		this.seriesNames = new ArrayList<>(s.subList(0, dataSet.getDependent().size()));
		legend.setSeriesNames(seriesNames);
		updated();
	}

	public void setSeriesColors(ArrayList<Color> colors) {
		if (colors.size() < dataSet.getDependent().size()) {
			throw new IllegalArgumentException("Not enough colors, needed: " + dataSet.getDependent().size() + ", received "
					+ colors.size());
		}
		this.seriesColors = new ArrayList<>(colors.subList(0, dataSet.getDependent().size()));
		manualColors = true;
		updated();
	}

	public void autoColors() {
		manualColors = false;
		updated();
	}

	protected boolean closeEnough(double a, double b) {
		return Math.abs(a - b) < ZERO_FACTOR;
	}

	public void updated() {
		if (dataSet != null) {
			convertPoints();
		}
		numSeries = dataSet.getDependent().size();
		if (!manualColors) {
			if (numSeries > seriesColors.size()) {
				int sz = numSeries - seriesColors.size();
				for (int i = 0; i < sz; i++) {
					seriesColors.add(ColorGenerator.getColor(alpha));
				}
			}
		}
		if (numSeries < seriesColors.size()) {
			int sz = seriesColors.size() - numSeries;
			for (int i = 0; i < sz; i++) {
				seriesColors.remove(seriesColors.size() - 1);
			}
		}
		if (numSeries < seriesNames.size()) {
			int sz = seriesNames.size() - numSeries;
			for (int i = 0; i < sz; i++) {
				seriesNames.remove(seriesNames.size() - 1);
			}
		}
	}

	public void setTitle(String title) {
		removeOldLabel(this.title);
		this.title = new DefaultLabel(title, SwingConstants.CENTER, DefaultLabel.TITLE);
		add(this.title, BorderLayout.NORTH);
	}

	public void setXLabel(String lbl) {
		removeOldLabel(this.xLabel);
		this.xLabel = new DefaultLabel(lbl, SwingConstants.CENTER, DefaultLabel.TEXT);
		add(this.xLabel, BorderLayout.SOUTH);
	}

	public void setYLabel(String lbl) {
		removeOldLabel(this.yLabel);
		this.yLabel = new DefaultLabel(lbl, SwingConstants.CENTER, DefaultLabel.TEXT);
		add(this.yLabel, BorderLayout.WEST);
	}

	protected void removeOldLabel(JLabel old) {
		if (old != null) {
			remove(old);
		}
	}

	protected void checkAA(Graphics2D g) {
		if (antiAliasing) {
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
			g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
			g.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
			g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
			g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
		} else {
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Graph))
			return false;
		@SuppressWarnings("rawtypes")
		Graph other = (Graph) obj;
		if (id != other.id)
			return false;
		return true;
	}
}
