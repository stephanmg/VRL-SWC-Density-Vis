/// package's name
package edu.gcsc.vrl.swcdensityvis.types.common;

/// imports
import edu.gcsc.vrl.swcdensityvis.data.CompartmentInfo;

/**
 * @brief observer interface
 * @author stephanmg <stephan@syntaktischer-zucker.de>
 */
public interface LoadCompartmentObserver {
	public void update(CompartmentInfo data);
}
