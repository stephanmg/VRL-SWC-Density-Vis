/// package's name
package edu.gcsc.vrl.swcdensityvis.importer.XML;

/// imports
import edu.gcsc.vrl.swcdensityvis.data.Edge;
import edu.gcsc.vrl.swcdensityvis.importer.DensityData;
import java.util.ArrayList;
import java.util.HashMap;
import javax.vecmath.Vector3f;

/**
 * @brief data structure to store density data
 * @author stephanmg <stephan@syntaktischer-zucker.de>
 */
public final class XMLDensityData implements DensityData {
	/// density data
	private final ArrayList<HashMap<String, ArrayList<Edge<Vector3f>>>> data;
	
	/**
	 * @brief set the density data
	 * @param data
	 */
	public XMLDensityData(ArrayList<HashMap<String, ArrayList<Edge<Vector3f>>>> data) {
		this.data = data;
	}
	
	/**
	 * @brief default ctor
	 */
	public XMLDensityData() {
		this.data = new ArrayList<HashMap<String, ArrayList<Edge<Vector3f>>>>();
	}

	/**
	 * @brief get the density ddata
	 * @return 
	 */
	@Override
	@SuppressWarnings("ReturnOfCollectionOrArrayField")
	public ArrayList<HashMap<String, ArrayList<Edge<Vector3f>>>> getDensityData() {
		return this.data;
	}
	
	/**
	 * @brief check if there is at least one cell with some trees
	 * @return 
	 */
	public boolean isEmpty() {
		return this.data.isEmpty();
	}
}
