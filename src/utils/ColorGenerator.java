
package utils;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public final class ColorGenerator {

	private static ArrayList<Color> colors;
	private static boolean initialised = false;
	private static int counter = 0;

	public static final Color SKY_BLUE = new Color(105, 210, 231);
	public static final Color IVORY = new Color(224, 228, 204);
	public static final Color ORANGE = new Color(243, 134, 48);
	public static final Color DARK_ORANGE = new Color(250, 105, 0);
	public static final Color ORANGE_RED = new Color(219, 51, 64);
	public static final Color ORANGE_RED_2 = new Color(209, 77, 40);
	public static final Color GOLDEN_ROD = new Color(232, 183, 26);
	public static final Color SEA_GREEN = new Color(31, 218, 154);
	public static final Color DEEP_SEA_GREEN = new Color(40, 171, 227);
	public static final Color DARK_KHAKI = new Color(176, 164, 114);
	public static final Color GOLD = new Color(245, 223, 101);
	public static final Color DARK_GREEN = new Color(43, 148, 100);
	public static final Color DARK_CYAN = new Color(89, 200, 223);

	private static HashMap<String, Color> colorMap = new HashMap<>();

	private ColorGenerator() {}

	private static void initialise() {
		colors = new ArrayList<>();
		colors.add(SKY_BLUE);
		colorMap.put("SKY_BLUE", SKY_BLUE);

		colors.add(IVORY);
		colorMap.put("IVORY", IVORY);

		colors.add(ORANGE);
		colorMap.put("ORANGE", ORANGE);

		colors.add(DARK_ORANGE);
		colorMap.put("DARK_ORANGE", DARK_ORANGE);

		colors.add(ORANGE_RED);
		colorMap.put("DARK_ORANGE", DARK_ORANGE);

		colors.add(GOLDEN_ROD);
		colorMap.put("GOLDEN_ROD", GOLDEN_ROD);

		colors.add(SEA_GREEN);
		colorMap.put("SEA_GREEN", SEA_GREEN);

		colors.add(DEEP_SEA_GREEN);
		colorMap.put("DEEP_SEA_GREEN", DEEP_SEA_GREEN);

		colors.add(DARK_KHAKI);
		colorMap.put("DARK_KHAKI", DARK_KHAKI);

		colors.add(GOLD);
		colorMap.put("GOLD", GOLD);

		colors.add(DARK_GREEN);
		colorMap.put("DARK_GREEN", DARK_GREEN);

		colors.add(DARK_CYAN);
		colorMap.put("DARK_CYAN", DARK_CYAN);

		colors.add(ORANGE_RED_2);
		colorMap.put("ORANGE_RED_2", ORANGE_RED_2);

		Collections.shuffle(colors);
		initialised = true;
	}

	public static Color getColor() {
		if (!initialised) {
			initialise();
			return getColor();
		} else {
			Color c = colors.get(counter++ % colors.size());
			return c;
		}
	}

	public static Color getColor(int alpha) {
		return convertToAlpha(getColor(), alpha);
	}

	public static Color getColor(String name) {
		return colorMap.get(name);
	}

	public static int convertTo8Bit(double factor) {
		int ret = (int) (255 * factor);
		if (ret < 0) {
			ret = 0;
		} else if (ret > 255) {
			ret = 255;
		}
		return ret;
	}

	public static Color convertToAlpha(Color c, int alpha) {
		int rgb = c.getRGB();
		int r = (rgb >> 16) & 0x000000FF;
		int g = (rgb >> 8) & 0x000000FF;
		int b = (rgb) & 0x000000FF;
		return new Color(r, g, b, alpha);
	}
}
