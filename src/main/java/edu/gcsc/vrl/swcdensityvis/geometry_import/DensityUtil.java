/// package's name
package edu.gcsc.vrl.swcdensityvis.geometry_import;

/// imports
import edu.gcsc.vrl.densityvis.Density;

/**
 * @brief density utility class
 * @author stephan
 */
public final class DensityUtil {
	/**
	 * @brief private ctor
	 */
	private DensityUtil() {
	}
	
        /**
	 * @brief computes the density for the stack of SWC files
	 * @param igg
	 * @param width
	 * @param height
	 * @param depth
	 * @return 
	 */
	public static Density computeDensity(GeometryImporter gi, int width, int height, int depth) {
	    return new edu.gcsc.vrl.swcdensityvis.geometry_import.DensityImpl(gi, width, height, depth);
    }
}
