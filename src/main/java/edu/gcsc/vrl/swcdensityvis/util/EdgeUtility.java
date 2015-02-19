/// package's name
package edu.gcsc.vrl.swcdensityvis.util;

/// imports
import edu.gcsc.vrl.swcdensityvis.data.Edge;
import eu.mihosoft.vrl.reflection.Pair;
import java.util.Arrays;
import java.util.Collections;
import javax.vecmath.Vector3f;

/**
 *
 * @author stephan
 */
public final class EdgeUtility {
	/**
	 * @brief private ctor
	 */
	private EdgeUtility() {
		
	}
	
	/**
	 * @brief get bounding box of edge
	 * @param edge
	 * @return 
	 */
	public static Pair<Vector3f, Vector3f> getBounding(Edge<Vector3f> edge) {
		return new Pair<Vector3f, Vector3f>(
			   new Vector3f(
				Collections.min(Arrays.asList(edge.getFrom().x, edge.getTo().x)),
				Collections.min(Arrays.asList(edge.getFrom().y, edge.getTo().y)),
				Collections.min(Arrays.asList(edge.getFrom().z, edge.getTo().z))
			   ),
			   new Vector3f(
				Collections.max(Arrays.asList(edge.getFrom().x, edge.getTo().x)),
				Collections.max(Arrays.asList(edge.getFrom().y, edge.getTo().y)),
				Collections.max(Arrays.asList(edge.getFrom().z, edge.getTo().z))
			   )
		      );
   	}
}
