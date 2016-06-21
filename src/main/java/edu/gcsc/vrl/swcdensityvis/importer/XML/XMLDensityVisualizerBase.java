/// package's name
package edu.gcsc.vrl.swcdensityvis.importer.XML;

/// imports
import edu.gcsc.vrl.swcdensityvis.data.Compartment;
import edu.gcsc.vrl.swcdensityvis.importer.DensityVisualizable;
import java.awt.Color;

/**
 * @brief the basis for the XML density visualizers
 * Specialized XML density visualizers may be derived starting
 * from this basic implementation to meet specific requirements.
 * @author stephan <stephan@syntaktischer-zucker.de>
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
	
	/**
	 *
	 * @param color
	 * @param scalingFactor
	 * @param compartment
	 */
	@Override
	public void prepare(Color color, double scalingFactor, Compartment compartment) {
		impl.setLineGraphColor(color);
		impl.setScalingFactor(scalingFactor);
		impl.setExcludedCompartments(compartment);
	}

}
