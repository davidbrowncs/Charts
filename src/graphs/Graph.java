
package graphs;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import utils.ColorGenerator;
import data.DataSet;
import fileHandling.FontLoader;
import graphs.DefaultLabel.FontType;

public abstract class Graph<E, T extends Number> extends JPanel {
	private static final long serialVersionUID = 9103378308225496616L;

	protected static final double ZERO_FACTOR = Double.MIN_VALUE * Math.pow(10, 4);
	protected static final int MAX_POW_10 = (int) Math.pow(10, 4);

	protected static int graphCounter = 0;
	protected int id = graphCounter++;

	protected DataSet<E, T> dataSet;

	protected FontLoader fontLoader = new FontLoader();

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

	protected double prcntMargin = 0.10d;

	// Is multiplied by prcnt margin, so the amount extra for gridlines is (max
	// or mind data set val) + or - prcntMargin * axisPadding
	protected double axisPadding = 0.3d;

	protected Stroke stroke = new BasicStroke(1.5f);
	protected Stroke axisStroke = new BasicStroke(1.0f);
	protected Stroke gridLineStroke = new BasicStroke(1.0f);

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
	protected Legend legend = null;
	protected JPanel legendContainer = new JPanel();
	protected boolean legendVisible = false;

	protected Font font = null;

	protected int numSeries = 0;
	protected ArrayList<Color> seriesColors = new ArrayList<>();
	protected boolean autoColors = true;

	protected ArrayList<String> seriesNames = new ArrayList<>();

	protected boolean debug = utils.Debug.isDebug();

	protected abstract void draw(Graphics2D g);

	protected abstract void drawGraph(Graphics2D g);

	protected abstract void convertPoints();

	public abstract void setDataSet(DataSet<E, T> dataSet);

	public Graph() {
		fontLoader.init();
		this.setLayout(new BorderLayout());
		add(drawingPanel, BorderLayout.CENTER);
		this.setBackground(backgroundColor);
	}

	public void setPrcntMargin(double d) {
		prcntMargin = d;
		updated();
	}

	public void setTitleFontSize(float f) {
		fontLoader.setTitleFontSize(f);
		repaint();
	}

	public void setSubTitleFontSize(float f) {
		fontLoader.setSubTitleFontSize(f);
		repaint();
	}

	public void setTextFontSize(float f) {
		fontLoader.setTextFontSize(f);
		repaint();
	}

	public void setLabelFontSize(float f) {
		fontLoader.setLabelFontSize(f);
		repaint();
	}

	public void setFont(Font f) {
		if (fontLoader != null) {
			fontLoader.setFont(f);
		}
		super.setFont(f);
		changeFont(this, f);
	}

	public void changeFont(Component component, Font font) {
		if (component != this) {
			component.setFont(font);
		}
		if (component instanceof Container) {
			for (Component child : ((Container) component).getComponents()) {
				changeFont(child, font);
			}
		}
	}

	public void setLegendTransparency(double d) {
		getLegend().setAlphaComponent(assertInRange(d, 0, 1));
	}

	public DataSet<E, T> getDataSet() {
		return dataSet;
	}

	public void removeDataSet() {
		this.dataSet = null;
	}

	public void setTransparency(double d) {
		alpha = utils.ColorGenerator.convertTo8Bit(assertInRange(0, 1, d));
	}

	protected double assertInRange(double a, double b, double d) {
		double max = Math.max(a, b);
		double min = Math.min(a, b);
		return d > max ? max : d < min ? min : d;
	}

	/**
	 * Since legend needs a reference to this, and can't initialise legend
	 * before this is created, used this method instead of accessing legend
	 * directly to ensure it is initialised
	 */
	private Legend getLegend() {
		if (legend == null) {
			legend = new Legend(this);
			legendContainer.add(legend);
			add(legendContainer, BorderLayout.EAST);
		}
		legend.setVisible(legendVisible);
		return legend;
	}

	public void legendLeftSide() {
		remove(legendContainer);
		add(legendContainer, BorderLayout.WEST);
		revalidate();
	}

	public void legendRightSide() {
		remove(legendContainer);
		add(legendContainer, BorderLayout.EAST);
		revalidate();
	}

	public void setLegendVisible(boolean b) {
		legend.setVisible((legendVisible = b));
	}

	public void setLegendTitle(String title) {
		setLegendVisible(true);
		getLegend().setTitle(title);
	}

	@SuppressWarnings("unchecked")
	public void setSeriesNames(ArrayList<String> s) {
		setLegendVisible(true);
		this.seriesNames = (ArrayList<String>) s.clone();
		updated();
		getLegend().setSeriesNames(seriesNames);
		repaint();
	}

	@SuppressWarnings("unchecked")
	public void setSeriesColors(ArrayList<Color> colors) {
		this.seriesColors = (ArrayList<Color>) colors.clone();
		autoColors = false;
		updated();
		getLegend().setSeriesColors(seriesColors);
		repaint();
	}

	public void autoColors() {
		if (autoColors) {
			return;
		}
		seriesColors = new ArrayList<>();
		autoColors = true;
		updated();
		repaint();
	}

	public void setLegendBackgroundColor(Color c) {
		getLegend().setBackgroundColor(c);
	}

	FontLoader getFontLoader() {
		return fontLoader;
	}

	protected boolean closeEnough(double a, double b) {
		return Math.abs(a - b) < ZERO_FACTOR;
	}

	public void updated() {
		if (dataSet != null) {
			convertPoints();
		}
		numSeries = dataSet.getDependent().size();
		if (numSeries > seriesColors.size()) {
			int sz = numSeries - seriesColors.size();
			for (int i = 0; i < sz; i++) {
				seriesColors.add(ColorGenerator.getColor(alpha));
			}
			getLegend().setSeriesColors(seriesColors);
		} else if (numSeries < seriesColors.size()) {
			int sz = seriesColors.size() - numSeries;
			for (int i = 0; i < sz; i++) {
				seriesColors.remove(seriesColors.size() - 1);
			}
			getLegend().setSeriesColors(seriesColors);
		}
		if (numSeries > seriesNames.size()) {
			int sz = numSeries - seriesNames.size();
			for (int i = 0; i < sz; i++) {
				seriesNames.add("Series - " + Integer.toString(seriesNames.size() + 1));
			}
			getLegend().setSeriesNames(seriesNames);
		} else if (numSeries < seriesNames.size()) {
			int sz = seriesNames.size() - numSeries;
			for (int i = 0; i < sz; i++) {
				seriesNames.remove(seriesNames.size() - 1);
			}
			getLegend().setSeriesNames(seriesNames);
		}
	}

	public void setTitle(String title) {
		removeOldLabel(this.title);
		this.title = new DefaultLabel(title, SwingConstants.CENTER, FontType.TITLE, fontLoader);
		add(this.title, BorderLayout.NORTH);
	}

	public void setXLabel(String lbl) {
		removeOldLabel(this.xLabel);
		this.xLabel = new DefaultLabel(lbl, SwingConstants.CENTER, FontType.TEXT, fontLoader);
		add(this.xLabel, BorderLayout.SOUTH);
	}

	public void setYLabel(String lbl) {
		removeOldLabel(this.yLabel);
		this.yLabel = new DefaultLabel(lbl, SwingConstants.CENTER, FontType.TEXT, fontLoader);
		add(this.yLabel, BorderLayout.WEST);
	}

	protected void removeOldLabel(JLabel old) {
		if (old != null) {
			remove(old);
		}
	}

	protected void checkAA(Graphics2D g) {
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
		g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
		g.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
		g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
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
