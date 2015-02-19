/// package's name
package edu.gcsc.vrl.swcdensityvis.importer.XML;

/// imports
import edu.gcsc.vrl.swcdensityvis.importer.DensityVisualizable;
import java.awt.Color;
import java.io.File;
import java.util.ArrayList;

/**
 *
 * @author stephan
 */
public interface XMLDensityVisualizerImplementable extends DensityVisualizable {
	public void setLineGraphColor(Color color);
	public void setScalingFactor(double scalingFactor);
	public void setFiles(ArrayList<File> files);
}
