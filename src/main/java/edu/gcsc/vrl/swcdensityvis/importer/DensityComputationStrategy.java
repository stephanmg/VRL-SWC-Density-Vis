/// package's name
package edu.gcsc.vrl.swcdensityvis.importer;

/// imports
import edu.gcsc.vrl.densityvis.Density;

/**
 * @brief interface for density computation strategies
 * @author stephan <stephan@syntaktischer-zucker.de>
 */
public interface DensityComputationStrategy {
	Density computeDensity();
	void setDensityData(DensityData data);
	Object getDimension();
	Object getCenter();
}
