
package graphs;

import java.awt.Color;

import javax.swing.JLabel;

public class DefaultLabel extends JLabel {
	private static final long serialVersionUID = 2377995574299523154L;

	public static final int TITLE = 0;
	public static final int SUB_TITLE = 1;
	public static final int TEXT = 2;

	private int type;

	protected DefaultLabel(String s, int i, int type) {
		super(s, i);
		this.type = type;
		setup();
	}

	protected DefaultLabel(String s, int type) {
		super(s);
		this.type = type;
		setup();
	}

	protected DefaultLabel(int type) {
		super();
		this.type = type;
		setup();
	}

	private void setup() {
		if (utils.Debug.isDebug()) {
			this.setOpaque(true);
			this.setBackground(new Color(0, 255, 204, 100));
		}

		switch (type) {
			case TITLE:
				setFont(FontLoader.getTitleFont());
			break;
			case SUB_TITLE:
				setFont(FontLoader.getSubTitleFont());
			break;
			case TEXT:
				setFont(FontLoader.getTextFont());
			break;
		}
	}
}
