/// package's name
package edu.gcsc.vrl.swcdensityvis.importer.XML;

/// imports
import edu.gcsc.vrl.swcdensityvis.data.Edge;
import edu.gcsc.vrl.swcdensityvis.importer.DensityData;
import java.util.ArrayList;
import java.util.HashMap;
import javax.vecmath.Vector3f;


/**
 *
 * @author stephan
 */
public final class XMLDensityData implements DensityData {
	private final HashMap<String, ArrayList<Edge<Vector3f>>> data;
	
	/**
	 * @param data
	 * @brief 
	 */
	public XMLDensityData(HashMap<String, ArrayList<Edge<Vector3f>>> data) {
		this.data = data;
	}

	/**
	 * 
	 * @return 
	 */
	@Override
	@SuppressWarnings("ReturnOfCollectionOrArrayField")
	public HashMap<String, ArrayList<Edge<Vector3f>>> getDensityData() {
		return this.data;
	}

}
