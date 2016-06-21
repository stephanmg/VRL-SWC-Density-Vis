/// package's name
package edu.gcsc.vrl.swcdensityvis.importer.XML;

/// imports
import edu.gcsc.vrl.swcdensityvis.data.Edge;
import edu.gcsc.vrl.swcdensityvis.importer.DensityData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.vecmath.Vector3f;

/**
 * @brief data structure to store density data
 * @author stephan <stephan@syntaktischer-zucker.de>
 */
public final class XMLDensityData implements DensityData {
	/// density data
	private final HashMap<String, ArrayList<Edge<Vector3f>>> data;
	
	/**
	 * @brief set the density data
	 * @param data
	 */
	public XMLDensityData(HashMap<String, ArrayList<Edge<Vector3f>>> data) {
		this.data = data;
	}

	/**
	 * @brief get the density ddata
	 * @return 
	 */
	@Override
	@SuppressWarnings("ReturnOfCollectionOrArrayField")
	public HashMap<String, ArrayList<Edge<Vector3f>>> getDensityData() {
		return this.data;
	}
	
	/**
	 * @brief checks if there is really *no* density / geometry to be visualized
	 * @return 
	 */
	public boolean isEmpty() {
		for (Map.Entry<String, ArrayList<Edge<Vector3f>>> tree : this.data.entrySet()) {
			if (!tree.getValue().isEmpty()) {
				return false;
			}
		}
		return true;
	}
}
