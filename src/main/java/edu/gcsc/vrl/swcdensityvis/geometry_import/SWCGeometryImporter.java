/// package's name
package edu.gcsc.vrl.swcdensityvis.geometry_import;

/// imports
import edu.wlu.cs.levy.CG.KDTree;
import eu.mihosoft.vrl.reflection.Pair;
import java.util.ArrayList;
import java.util.HashMap;
import javax.vecmath.Vector3f;
import lombok.Getter;
import lombok.Setter;


/**
 *
 * @author stephan
 */
@Getter @Setter
public final class SWCGeometryImporter extends GeometryImporter {
	/// sampling cube dimensions
	private float width;
	private float height;
	private float depth;

	/**
	 * @brief ctor
	 * 
	 * @param width
	 * @param height
	 * @param depth 
	 */
	public SWCGeometryImporter(float width, float height, float depth) {
		this.width = width;
		this.height = height;
		this.depth = depth;
	}
	
	@Override
	public void load_cells() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public HashMap<String, ArrayList<CompartmentInformation>> get_cells() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public HashMap<Vector3f, ArrayList<Vector3f>> getIndicents() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public KDTree<ArrayList<Vector3f>> buildKDTree() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public Pair<Vector3f, Vector3f> getBoundingBox() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public Vector3f getDimensions() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

}
