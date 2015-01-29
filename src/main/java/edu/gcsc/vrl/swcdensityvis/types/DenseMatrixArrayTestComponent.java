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
@ComponentInfo(name="DenseMatrixArrayTestComponent", category="Neuro/Common/LA/Test")
public class DenseMatrixArrayTestComponent implements Serializable {
	private static final long serialVersionUID = 1L;
	
	/**
	 * @brief tests the visual representation of Dense Matrix Arrays
	 * @param matrix 
	 */
	public void test(
	@ParamInfo(name = "DenseMatrixArray", style="matrix-array", options="cols=2; rows=2")
	DenseMatrix[] matrix
	) {
	}
}

