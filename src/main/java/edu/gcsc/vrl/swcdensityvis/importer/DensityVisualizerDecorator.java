/// package's name
package edu.gcsc.vrl.swcdensityvis.importer;

/**
 * @brief empty DensityVisualizer decorator
 * @author stephanmg <stephan@syntaktischer-zucker.de>
 */
public abstract class DensityVisualizerDecorator implements Decorator {
	@SuppressWarnings("ProtectedField")
	protected DensityVisualizable impl;

	/**
	 * 
	 * @param impl 
	 */
	public DensityVisualizerDecorator(DensityVisualizable impl) {
		this.impl = impl;
	}
}
