/// package's name
package edu.gcsc.vrl.swcdensityvis.util;

/// imports
import java.awt.Color;
import javax.vecmath.Color3f;
import javax.vecmath.Color4f;

/**
 *
 * @author stephan
 */
public final class ColorUtil {

	/**
	 *
	 */
	private ColorUtil() {

	}

	/**
	 *
	 * @param c
	 * @param w
	 * @return
	 */
	public static Color4f color2Color4f(Color c, int w) {
		return new Color4f(c.getRed() / 255f,
			c.getGreen() / 255f,
			c.getBlue() / 255f,
			(1 - w / 100f));
	}

	/**
	 *
	 * @param c
	 * @return
	 */
	public static Color3f color2Color3f(Color c) {
		return new Color3f(c.getRed() / 255f, c.getGreen() / 255f, c.getBlue() / 255f);
	}
}
