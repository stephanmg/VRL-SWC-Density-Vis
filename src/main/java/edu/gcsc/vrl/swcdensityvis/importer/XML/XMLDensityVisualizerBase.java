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
	protected XMLDensityVisualizerImpl impl;

	/**
	 *
	 * @param impl
	 */
	public XMLDensityVisualizerBase(XMLDensityVisualizerImpl impl) {
		this.impl = impl;
	}

	/**
	 *
	 * @param impl
	 */
	public void setImpl(XMLDensityVisualizerImpl impl) {
		this.impl = impl;
	}

	/**
	 *
	 * @return
	 */
	public XMLDensityVisualizerImpl getImpl() {
		return this.impl;
	}
}
