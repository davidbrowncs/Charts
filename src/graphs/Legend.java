
package graphs;

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
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;
import fileHandling.FontLoader;
import graphs.DefaultLabel.FontType;

/**
 * This class is nowhere near finished and I will be changing it so that it
 * doesn't have to rely on miglayout
 * 
 */
class Legend<T extends Number> extends JPanel {
	private static final long serialVersionUID = 5725435102513897890L;

	private JLabel legendTitle = null;

	private List<Series<T>> series = null;

	private ArrayList<JLabel> labels = new ArrayList<>();
	private ArrayList<String> labelTexts = new ArrayList<>();

	private ArrayList<ColorBox> colorBoxes = new ArrayList<>();
	private ArrayList<Integer> colorboxColors = new ArrayList<>();

	private final static String maxStringSize = "Long text sampleaaaaa";
	private int maxLabelSize;

	private Color backgroundColor = new Color(150, 150, 150, 0);
	private int alpha = 0;

	private FontLoader fontLoader;

	Legend(FontLoader loader) {
		this.fontLoader = loader;
		setLayout(new MigLayout("", "[][grow]", "[][][][][][][][][]"));
		setBackground(backgroundColor);
		AffineTransform a = new AffineTransform();
		FontRenderContext frc = new FontRenderContext(a, true, true);
		maxLabelSize = (int) fontLoader.getTextFont().getStringBounds(maxStringSize, frc).getWidth();
	}

	public void setSeries(ArrayList<Series<T>> series2) {
		this.series = series2;
	}

	public void paintComponent(Graphics g1) {
		Graphics2D g = (Graphics2D) g1;
		super.paintComponent(g1);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		RoundRectangle2D.Double rect = new RoundRectangle2D.Double(0, 0, this.getWidth(), this.getHeight(), 25, 25);
		Color oldColor = g.getColor();
		g.setColor(backgroundColor);
		g.fill(rect);
		g.setColor(oldColor);

	}

	public void setBackgroundColor(Color c) {
		backgroundColor = utils.ColorGenerator.convertToAlpha(c, alpha);
		this.repaint();
	}

	void setTitle(String s) {
		if (legendTitle != null) {
			remove(legendTitle);
		}
		removeAll();
		legendTitle = new DefaultLabel(s, FontType.SUB_TITLE, fontLoader);
		add(legendTitle, "cell 0 0 2 1,alignx left");
		if (series != null) {
			updateSeriesNames();
			updateSeriesColors();
		}
	}

	boolean updateSeriesNames() {
		if (series == null) {
			return false;
		}
		int changeCounter = 0;
		if (series.size() == labelTexts.size()) {
			for (int i = 0; i < series.size(); i++) {
				if (!labelTexts.get(i).equals(series.get(i).getName())) {
					changeCounter++;
				}
			}
		} else {
			changeCounter = 1;
		}
		if (changeCounter == 0) {
			return false;
		}

		for (JLabel l : labels) {
			remove(l);
		}
		labels.clear();
		labelTexts.clear();
		int counter = legendTitle == null ? 0 : 1;
		for (int i = 0; i < series.size(); i++, counter++) {
			JLabel l = new DefaultLabel(series.get(i).getName(), FontType.TEXT, fontLoader);
			Font font = l.getFont();
			l.setText("<html><body style=\fontFamily: " + font.getFamily() + "\" size =\"" + font.getSize() + "\"></font>"
					+ l.getText() + "</html");
			labelTexts.add(series.get(i).getName());
			l.setMaximumSize(new Dimension(maxLabelSize, 1080));
			labels.add(l);
			add(l, "cell 1 " + counter + ",alignx left");
		}
		 this.revalidate();
		 this.repaint();
		 return true;
	}

	boolean updateSeriesColors() {
		if (series == null) {
			return false;
		}
		int changeCounter = 0;
		if (series.size() == colorboxColors.size()) {
			for (int i = 0; i < series.size(); i++) {
				if (series.get(i).getColor().getRGB() != colorboxColors.get(i)) {
					changeCounter++;
				}
			}
		} else {
			changeCounter = 1;
		}
		if (changeCounter == 0) {
			return false;
		}

		for (ColorBox c : colorBoxes) {
			remove(c);
		}
		colorboxColors.clear();
		colorBoxes.clear();
		int counter = legendTitle == null ? 0 : 1;
		for (int i = 0; i < series.size(); i++, counter++) {
			ColorBox c = new ColorBox(series.get(i).getColor());
			colorBoxes.add(c);
			colorboxColors.add(series.get(i).getColor().getRGB());
			add(c, "cell 0 " + counter + " ,alignx center");
		}
		 this.revalidate();
		 this.repaint();
		 return true;
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

	@SuppressWarnings("unchecked")
	private void changeAlpha(Component component, int alpha) {
		if (component != this) {
			component.setBackground(utils.ColorGenerator.convertToAlpha(component.getBackground(), alpha));
		}
		if (component instanceof Legend.ColorBox) {
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
