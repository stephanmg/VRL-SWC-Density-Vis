/// package's name
package edu.gcsc.vrl.swcdensityvis;

/// imports
import edu.gcsc.vrl.swcdensityvis.importer.SWC.SWCCompartmentInformation;
import edu.gcsc.vrl.densityvis.Density;
import java.util.ArrayList;
import java.util.HashMap;

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
	 * @param cells
	 * @param width
	 * @param height
	 * @param depth
	 * @param choice
	 * @return 
	 */
	public static Density computeDensity(HashMap<String, ArrayList<SWCCompartmentInformation>> cells, int width, int height, int depth, String choice) {
	    return new DensityImpl(cells, width, height, depth, choice);
    }
}
