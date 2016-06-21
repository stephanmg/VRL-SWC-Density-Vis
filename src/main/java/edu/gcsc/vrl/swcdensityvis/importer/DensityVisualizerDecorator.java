/// package's name
package edu.gcsc.vrl.swcdensityvis.importer;

/**
 * @brief empty DensityVisualizer decorator
 * @author stephanmg <stephan@syntaktischer-zucker.de>
 */
public abstract class DensityVisualizerDecorator implements DensityVisualizable {
	private DensityVisualizable impl;

	/**
	 * 
	 * @param impl 
	 */
	public DensityVisualizerDecorator(DensityVisualizable impl) {
		this.impl = impl;
	}

	/**
	 *
	 * @param impl
	 */
	public void setImpl(DensityVisualizable impl) {
		this.impl = impl;
	}

	/**
	 *
	 * @return
	 */
	public DensityVisualizable getImpl() {
		return this.impl;
	}
}
