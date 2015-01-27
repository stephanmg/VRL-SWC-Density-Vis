/// package's name
package edu.gcsc.vrl.swcdensityvis.types;

/// imports
import eu.mihosoft.vrl.annotation.TypeInfo;

/**
 *
 * @author stephan
 */
@TypeInfo(type = Dense1x1Matrix.class, input = true, output = true, style = "default")
public class Dense1x1MatrixType extends DenseMatrixType {
	private static final long serialVersionUID = 1L;
	
	public Dense1x1MatrixType() {
		super(1, 1);
	}
	/**
	 * @todo implement get view and set view ...  make interface DenseMatrixBaseType
	 */
}
