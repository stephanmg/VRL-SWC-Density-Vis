/// package's name
package edu.gcsc.vrl.swcdensityvis.types;

/// imports
import eu.mihosoft.vrl.annotation.TypeInfo;

/**
 *
 * @author stephan
 */
@TypeInfo(type = Dense4x4Matrix.class, input = true, output = true, style = "default")
public class Dense4x4MatrixType extends DenseMatrixType {
	private static final long serialVersionUID = 1L;
	
	public Dense4x4MatrixType() {
		super(4, 4);
	}
	/**
	 * @todo implement get view and set view ...  make interface DenseMatrixBaseType
	 */
}
