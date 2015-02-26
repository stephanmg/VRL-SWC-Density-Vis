/// package's name
package edu.gcsc.vrl.swcdensityvis.demo;

/// imports
import edu.gcsc.vrl.swcdensityvis.data.Edge;
import edu.gcsc.vrl.swcdensityvis.importer.XML.XMLDensityData;
import java.util.ArrayList;
import java.util.HashMap;
import javax.vecmath.Vector3f;

/**
 *
 * @author stephan
 */
public class XMLDensityDataDemo {
	public static void main(String... args) {
		new XMLDensityData(new HashMap<String, ArrayList<Edge<Vector3f>>>()).getDensityData();
	}

}
