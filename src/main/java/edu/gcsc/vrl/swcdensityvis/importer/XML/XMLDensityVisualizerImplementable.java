/// package's name
package edu.gcsc.vrl.swcdensityvis.importer.XML;

/// imports
import edu.gcsc.vrl.swcdensityvis.importer.DensityVisualizable;
import edu.gcsc.vrl.swcdensityvis.data.Compartment;
import java.awt.Color;

/**
 * @brief additional interfaces functions for *XML* density visualizers 
 * The XML density visualizers may use the additional functions in this
 * interface to provide the functionality for visualizing the XML files.
 * A XMLDensityUtil creates the different implementables by a factory.
 * @author stephanmg <stephan@syntaktischer-zucker.de>
 */
public interface XMLDensityVisualizerImplementable extends DensityVisualizable {
	public void setLineGraphColor(Color color);
	public void setScalingFactor(double scalingFactor);
	public void setExcludedCompartments(Compartment compartment);
}
