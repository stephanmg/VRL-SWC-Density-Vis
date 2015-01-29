/// package's name
package edu.gcsc.vrl.swcdensityvis;

/// imports
import java.util.ArrayList;
import javax.vecmath.Vector3d;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 *
 * @author stephan
 */
@Getter @Setter @ToString
public class Tree {
	private String type;
	private ArrayList<Edge<Vector3d>> edges;
}
