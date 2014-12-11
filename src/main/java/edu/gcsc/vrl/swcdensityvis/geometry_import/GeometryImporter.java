/// package's name
package edu.gcsc.vrl.swcdensityvis.geometry_import;

/// imports
import java.util.HashMap;

/**
 *
 * @author stephan
 */
public abstract class GeometryImporter implements GeometryImporterInterface {
	/**
	 * @brief template method
	 * @todo implement
	 * @return
	 */
	@Override
	public HashMap<Integer, Float> computeDensity() {
		prepare();
		getBoundingBox();
		/**
		 * @todo here compute the density must be implemented
		 */
		finish();
		return new HashMap<Integer, Float>();
	}
	
	/**
	 * @brief prepare
	 */
	protected void prepare() {
		
	}
	
	/**
	 * @brief finish
	 */
	protected void finish() {
		
	}
	
	
}
	
	