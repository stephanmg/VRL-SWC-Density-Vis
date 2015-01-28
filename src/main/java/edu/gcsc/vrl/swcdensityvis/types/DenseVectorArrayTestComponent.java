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
@ComponentInfo(name="DenseVectorArrayTestComponent")
public class DenseVectorArrayTestComponent implements Serializable {
	private static final long serialVersionUID = 1L;
	
	/**
	 * @brief tests the visual representation of Dense Matrix Arrays
	 * @param matrix 
	 */
	public void test(
	@ParamInfo(name = "DenseMatrixArray", style="array", options="cols=3; rows=1")
	DenseMatrix[] matrix
	) {
	}
}

