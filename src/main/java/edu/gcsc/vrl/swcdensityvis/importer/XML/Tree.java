/// package's name
package edu.gcsc.vrl.swcdensityvis.importer.XML;

/// imports
import edu.gcsc.vrl.swcdensityvis.data.Edge;
import java.awt.Color;
import java.util.ArrayList;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @brief represents a tree 
 * @author stephanmg <stephan@syntaktischer-zucker.de>
 * @param <T> type
 */
@Getter @Setter @ToString
public class Tree<T> {
	private String type;
	private ArrayList<Edge<T>> edges;
	private Color color;
	private String leaf;
}
