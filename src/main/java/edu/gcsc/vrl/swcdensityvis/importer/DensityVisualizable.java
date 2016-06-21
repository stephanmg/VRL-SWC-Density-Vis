/// package's name
package edu.gcsc.vrl.swcdensityvis.importer;

/// imports
import edu.gcsc.vrl.swcdensityvis.data.Compartment;
import eu.mihosoft.vrl.v3d.Shape3DArray;
import java.awt.Color;
import java.io.File;
import java.util.ArrayList;

/**
 * NOTE: this interfaces (DensityVisualizable) is used to implement
 * different DensityVisualizers, i.e. XML, SWC or ASC - if additional
 * functions are used for e.g. ASC or XML then a derived interface
 * e.g. ASCDensityVisualizable or XMLDensityvisualizble may be used
 * @brief density visualizable interface
 * @author stephan
 *
 * @note use an abstract factory to be more flexible in the end
 * @note each implementation can make use of the utility class, and also of the
 * density util and distance util
 * @note for computing the Density and DensityAlternative we use a strategy
 * pattern
 */
public interface DensityVisualizable extends DensityComputationStrategy {
	void setFiles(ArrayList<File> files);
	
	void parse();

	void parseStack();

	/**
	 * @todo for now we use ALL geometries in stack to 
	 * calculate the consensus geometry! (maybe just use by conventions
	 * the first in the list of xml/geometry files?!)
	 * @return 
	 */
	Shape3DArray calculateGeometry();

	void setContext(DensityComputationContext context);

	void prepare(Color color, double scale, Compartment compartment);
}
