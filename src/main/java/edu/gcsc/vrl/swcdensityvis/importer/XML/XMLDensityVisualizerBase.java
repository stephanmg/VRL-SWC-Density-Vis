/// package's name
package edu.gcsc.vrl.swcdensityvis.importer.XML;

/// imports
import edu.gcsc.vrl.swcdensityvis.importer.DensityVisualizable;

/**
 *
 * @author stephan
 */
public abstract class XMLDensityVisualizerBase implements DensityVisualizable {

	@SuppressWarnings("ProtectedField")
	protected XMLDensityVisualizerImplementable impl;

	/**
	 *
	 * @param impl
	 */
	public XMLDensityVisualizerBase(XMLDensityVisualizerImplementable impl) {
		this.impl = impl;
	}

	/**
	 *
	 * @param impl
	 */
	public void setImpl(XMLDensityVisualizerImplementable impl) {
		this.impl = impl;
	}

	/**
	 *
	 * @return
	 */
	public XMLDensityVisualizerImplementable getImpl() {
		return this.impl;
	}
}
