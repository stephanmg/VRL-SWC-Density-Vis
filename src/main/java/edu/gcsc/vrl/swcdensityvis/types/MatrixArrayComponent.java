/// package's name
package edu.gcsc.vrl.swcdensityvis.types;

/// imports
import eu.mihosoft.vrl.annotation.ComponentInfo;
import eu.mihosoft.vrl.annotation.ParamInfo;
import java.io.Serializable;


/**
 * @brief test matrix component for visual representation
 * @author stephan grein
 */
@ComponentInfo(name="MatrixArrayComponent")
public class MatrixArrayComponent implements Serializable {
	private static final long serialVersionUID = 1L;
	/**
	 * @param matrix 
	 */
	public void dummy(
	@ParamInfo(name = "Matrix", style="array", options="cols=3; rows=3")
	Matrix[] matrix
	) {
	}
}

