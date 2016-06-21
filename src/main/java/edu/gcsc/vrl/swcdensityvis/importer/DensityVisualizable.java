/// package's name
package edu.gcsc.vrl.swcdensityvis.importer;

/// imports
import edu.gcsc.vrl.swcdensityvis.data.Compartment;
import eu.mihosoft.vrl.v3d.Shape3DArray;
import java.awt.Color;
import java.io.File;
import java.util.ArrayList;

/**
 * @brief The DensityVisulizable interface
 * @author stephanmg <stephan@syntaktischer-zucker.de>
 * Note: This interfaces (DensityVisualizable) is used to implement
 * different DensityVisualizers, e.g. XML, SWC or ASC - 
 * if additional functionality is required for e.g. for ASC or 
 * XML then a derived interface e.g. ASCDensityVisualizable or 
 * XMLDensityvisualizable should be considered for abstraction
 *
 * Some implementation notes for programmers:
 * Note I.: Could use an abstract factory to be more flexible 
 * Note II.: Each implementation can make use of the utility class, 
 * 	     and also of the density util and distance util
 * Note III.: For computing the Density and DensityAlternative
 * 	      we use a strategy design pattern
 */
public interface DensityVisualizable extends DensityComputationStrategy {
	void setFiles(ArrayList<File> files);
	void parse();
	void parseStack();
	Shape3DArray calculateGeometry();
	void setContext(DensityComputationContext context);
	void prepare(Color color, double scale, Compartment compartment);
}
