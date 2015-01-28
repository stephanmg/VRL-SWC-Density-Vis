/// package's name
package edu.gcsc.vrl.swcdensityvis.types;

/// imports
import eu.mihosoft.vrl.annotation.ComponentInfo;
import eu.mihosoft.vrl.annotation.OutputInfo;
import eu.mihosoft.vrl.annotation.ParamInfo;
import java.io.Serializable;
import org.la4j.matrix.dense.Basic2DMatrix;

/**
 * @brief test matrix matrix actions on components for visual representation 
 *        (defaults to 3x3 and 3x1 matrices and vectors)
 * @author stephan grein
 */
@ComponentInfo(name="DenseMatrixVectorTestComponent", category="Neuro/Common/LA/Test")
public class DenseMatrixVectorTestComponent implements Serializable {
	private static final long serialVersionUID = 1L;
	/**
	 * @brief mul
	 * @param m1
	 * @param m2
	 * @return
	 */
	@OutputInfo(name="Result 3x1 Dense Vector", options="cols=1; rows=3")
	public DenseMatrix mul(
	@ParamInfo(name="Input 3x3 Dense Matrix", options="cols=3; rows=3")
	DenseMatrix m1, 
	@ParamInfo(name = "Input 3x1 Dense Vector", style="default", options="cols=1; rows=3")
	DenseMatrix m2
	) {
		return new DenseMatrix(new Basic2DMatrix(m1.multiply(m2)));
	}
}

