/// package's name
package edu.gcsc.vrl.swcdensityvis.importer.XML;

/// imports
import java.awt.Color;
import java.util.ArrayList;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @brief represents a contour
 * @author stephanmg <stephan@syntaktischer-zucker.de>
 * @param <T> type
 */
@Getter @Setter @ToString
public class Contour<T> {
	private String name;
	private ArrayList<T> points;
	private Color color;
	private boolean closed;
	private String shape;
}
