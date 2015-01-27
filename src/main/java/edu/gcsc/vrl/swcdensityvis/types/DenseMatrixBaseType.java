/// package's name
package edu.gcsc.vrl.swcdensityvis.types;

/// imports
import eu.mihosoft.vrl.reflection.TypeRepresentationBase;

/**
 * @todo factor out common components in DenseMatrixType and bring them to this class
 * especially we need to implement the getview and setview functions accordingly,
 * maybe some other methods must also be abstract
 * @author stephan
 */
public abstract class DenseMatrixBaseType extends TypeRepresentationBase {
	private static final long serialVersionUID = 1L;
}
