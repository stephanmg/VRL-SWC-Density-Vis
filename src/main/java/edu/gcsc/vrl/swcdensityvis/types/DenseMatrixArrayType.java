/// package's name
package edu.gcsc.vrl.swcdensityvis.types;

/// imports
import eu.mihosoft.vrl.annotation.TypeInfo;
import eu.mihosoft.vrl.types.ArrayBaseType;

/**
 * @brief matrix array type
 * @author stephan grein
 */
@TypeInfo(type = DenseMatrix[].class, input = true, output = true, style = "array")
public class DenseMatrixArrayType extends ArrayBaseType {
	private static final long serialVersionUID = 1L;

	public DenseMatrixArrayType() {
        	setValueName("Dense Matrix Array:");
	}
}
