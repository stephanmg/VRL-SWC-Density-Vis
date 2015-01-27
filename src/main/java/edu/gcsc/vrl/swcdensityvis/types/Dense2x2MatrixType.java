/// package's name
package edu.gcsc.vrl.swcdensityvis.types;

/// imports
import eu.mihosoft.vrl.annotation.TypeInfo;

/**
 *
 * @author stephan
 */
@TypeInfo(type = Dense2x2Matrix.class, input = true, output = true, style = "default")
public class Dense2x2MatrixType extends DenseMatrixType {
	private static final long serialVersionUID = 1L;
	
	public Dense2x2MatrixType() {
		super(2, 2);
	}
	/**
	 * @todo implement get view and set view ...  make interface DenseMatrixBaseType
	 */
}
