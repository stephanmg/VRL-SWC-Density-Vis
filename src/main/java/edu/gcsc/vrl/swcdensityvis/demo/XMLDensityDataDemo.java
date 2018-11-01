/// package's name
package edu.gcsc.vrl.swcdensityvis.demo;

/// imports
import edu.gcsc.vrl.swcdensityvis.data.Edge;
import edu.gcsc.vrl.swcdensityvis.importer.XML.XMLDensityData;
import java.util.ArrayList;
import java.util.HashMap;
import javax.vecmath.Vector3f;

/**
 * @brief density data demo
 * @author stephanmg <stephan@syntaktischer-zucker.de>
 */
public class XMLDensityDataDemo {
	public static void main(String... args) {
		new XMLDensityData(new ArrayList<HashMap<String, ArrayList<Edge<Vector3f>>>>()).getDensityData();
	}
}
