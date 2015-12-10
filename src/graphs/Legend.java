
package graphs;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
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

	private double alpha;

	Legend() {
		setLayout(new MigLayout("", "[][grow]", "[][]"));
		setOpaque(false);
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
		legendTitle = new DefaultLabel(s, DefaultLabel.SUB_TITLE);
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
			JLabel l = new DefaultLabel(series.get(i), DefaultLabel.TEXT);
			dataSeries.add(l);
			add(l, "cell 1 " + counter + ",alignx left");
		}
	}

	void setSeriesColors(ArrayList<Color> colors) {
		for (ColorBox c : this.seriesColors) {
			remove(c);
		}
		seriesColors.clear();
		System.out.println("Num series colors: " + colors.size());
		int counter = legendTitle == null ? 0 : 1;
		for (int i = 0; i < colors.size(); i++, counter++) {
			ColorBox c = new ColorBox(colors.get(i));
			this.seriesColors.add(c);
			add(c, "cell 0 " + counter + ",alignx center");
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
