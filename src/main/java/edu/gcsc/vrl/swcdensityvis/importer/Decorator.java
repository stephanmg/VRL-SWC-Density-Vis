package edu.gcsc.vrl.swcdensityvis.importer;

import edu.gcsc.vrl.densityvis.Density;

/**
 * @brief marker interface for decorators
 * @author stephanmg <stephan@syntaktischer-zucker.de>
 */
public interface Decorator {
	Density computeDensity();
}
