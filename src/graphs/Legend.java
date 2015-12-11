
package graphs;

import graphs.DefaultLabel.FontType;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

/**
 * This class is nowhere near finished and I will be changing it so that it
 * doesn't have to rely on miglayout
 * 
 */
class Legend extends JPanel {
	private static final long serialVersionUID = 5725435102513897890L;

	private JLabel legendTitle = null;
	private ArrayList<JLabel> dataSeries = new ArrayList<>();
	private ArrayList<ColorBox> seriesColors = new ArrayList<>();

	private final static String maxStringSize = "Long text sampleaaaaa";
	private int maxLabelSize;

	private Color backgroundColor = new Color(150, 150, 150, 0);
	private int alpha = 0;

	private Graph<?, ?> graph;

	Legend(Graph<?, ?> g) {
		this.graph = g;
		setLayout(new MigLayout("", "[][grow]", "[][]"));
		setBackground(backgroundColor);

		AffineTransform a = new AffineTransform();
		FontRenderContext frc = new FontRenderContext(a, true, true);
		maxLabelSize = (int) g.getFontLoader().getTextFont().getStringBounds(maxStringSize, frc).getWidth();
	}

	public void paintComponent(Graphics g1) {
		Graphics2D g = (Graphics2D) g1;
		super.paintComponent(g);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		RoundRectangle2D.Double rect = new RoundRectangle2D.Double(0, 0, this.getWidth(), this.getHeight(), 25, 25);
		Color oldColor = g.getColor();
		g.setColor(backgroundColor);
		g.fill(rect);
		g.setColor(oldColor);
	}

	public void setBackgroundColor(Color c) {
		backgroundColor = utils.ColorGenerator.convertToAlpha(c, alpha);
		repaint();
	}

	void setTitle(String s) {
		if (legendTitle != null) {
			remove(legendTitle);
		}
		for (JLabel l : dataSeries) {
			remove(l);
		}
		for (ColorBox c : seriesColors) {
			remove(c);
		}
		legendTitle = new DefaultLabel(s, FontType.SUB_TITLE, graph.getFontLoader());
		add(legendTitle, "cell 0 0 2 1,alignx left");

		ArrayList<Color> c = new ArrayList<>();
		for (ColorBox bx : seriesColors) {
			c.add(bx.color);
		}
		ArrayList<String> newSeries = new ArrayList<>();
		for (JLabel l : dataSeries) {
			newSeries.add(l.getText());
		}
		setSeriesNames(newSeries);
		setSeriesColors(c);
	}

	void setSeriesNames(ArrayList<String> series) {
		for (JLabel l : dataSeries) {
			remove(l);
		}
		dataSeries.clear();
		int counter = legendTitle == null ? 0 : 1;
		for (int i = 0; i < series.size(); i++, counter++) {
			JLabel l = new DefaultLabel(series.get(i), FontType.TEXT, graph.getFontLoader());
			Font font = l.getFont();
			l.setText("<html><body style=\fontFamily: " + font.getFamily() + "\" size =\"" + font.getSize() + "\"></font>"
					+ l.getText() + "</html");
			l.setMaximumSize(new Dimension(maxLabelSize, 1080));
			dataSeries.add(l);
			add(l, "cell 1 " + counter + ",alignx left");
		}
	}

	void setSeriesColors(ArrayList<Color> colors) {
		for (ColorBox c : this.seriesColors) {
			remove(c);
		}
		seriesColors.clear();
		int counter = legendTitle == null ? 0 : 1;
		for (int i = 0; i < colors.size(); i++, counter++) {
			ColorBox c = new ColorBox(colors.get(i));
			seriesColors.add(c);
			add(c, "cell 0 " + counter + ",alignx center");
		}
	}

	void setAlphaComponent(double a) {
		int nv = utils.ColorGenerator.convertTo8Bit(a);
		if (nv != alpha) {
			backgroundColor = utils.ColorGenerator.convertToAlpha(backgroundColor, nv);
			alpha = nv;
			changeAlpha(this, alpha);
		}
		repaint();
	}

	public void changeAlpha(Component component, int alpha) {
		if (component != this) {
			component.setBackground(utils.ColorGenerator.convertToAlpha(component.getBackground(), alpha));
		}
		if (component instanceof ColorBox) {
			((ColorBox) component).color = utils.ColorGenerator.convertToAlpha(((ColorBox) component).color, alpha);
		} else if (component instanceof Container) {
			for (Component child : ((Container) component).getComponents()) {
				changeAlpha(child, alpha);
			}
		}
	}

	private class ColorBox extends JPanel {
		private static final long serialVersionUID = 5732330488332905113L;
		private int width = 15;
		private int height = 15;
		boolean resize = false;
		private Color color;

		private ColorBox(Color c) {
			this.color = c;
			this.setPreferredSize(new Dimension(width, height));
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			int hc = this.getWidth() / 2;
			int vc = this.getHeight() / 2;

			g.setColor(color);
			if (!resize) {
				int x = hc - (width / 2);
				int y = vc - (height / 2);
				g.fillRect(x, y, width, height);
			} else {
				g.fillRect(-width / 2, -height / 2, width, height);
			}
		}
	}
}
