/// package's name
package edu.gcsc.vrl.swcdensityvis.importer;

/// imports
import edu.gcsc.vrl.densityvis.Density;

/**
 *
 * @author stephan
 */
public interface DensityComputationStrategy {
	Density computeDensity();
	void setDensityData(DensityData data);
	Object getDimension();
	Object getCenter();
}
