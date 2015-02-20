/// package's name
package edu.gcsc.vrl.swcdensityvis.importer.XML;

/// imports
import java.awt.Color;
import java.util.ArrayList;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 *
 * @author stephan
 * @param <T>
 */
@Getter @Setter @ToString
public class Contour<T> {
	private String name;
	private ArrayList<T> points;
	private Color color;
	private boolean closed;
	private String shape;
}
