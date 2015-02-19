/// package's name
package edu.gcsc.vrl.swcdensityvis.importer.XML;

/// imports
import edu.gcsc.vrl.swcdensityvis.data.Edge;
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
public class Tree<T> {
	private String type;
	private ArrayList<Edge<T>> edges;
}
