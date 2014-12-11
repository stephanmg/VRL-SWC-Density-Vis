/// package's name
package edu.gcsc.vrl.swcdensityvis.geometry_import;

/// imports
import edu.wlu.cs.levy.CG.KDTree;
import eu.mihosoft.vrl.reflection.Pair;
import java.util.ArrayList;
import java.util.HashMap;
import javax.vecmath.Vector3f;

/**
 *
 * @author stephan
 */
public interface GeometryImporterInterface {
	/// load the cells 
	void load_cells(); 
	
	/// return the cells
	HashMap<String, ArrayList<CompartmentInformation>> get_cells();
	
	/// get the indicents
	HashMap<Vector3f, ArrayList<Vector3f>> getIndicents();
	
	/// build the KD tree
	KDTree<ArrayList<Vector3f>> buildKDTree();
	
	/// compute density
	HashMap<Integer, Float> computeDensity();
	
	/// get the bounding box (may be a utility function)
	Pair<Vector3f, Vector3f> getBoundingBox();
	
	/// get the dimensions (may be a utility function)
	Vector3f getDimensions();
}
