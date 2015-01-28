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
 *        (defaults to 3x3 matrices)
 * @author stephan grein
 */
@ComponentInfo(name="DenseMatrixMatrixTestComponent", category="Neuro/Common/LA/Test")
public class DenseMatrixMatrixTestComponent implements Serializable {
	private static final long serialVersionUID = 1L;
	/**
	 * @brief add
	 * @param m1
	 * @param m2
	 * @return
	 */
	@OutputInfo(name="Result 3x3 Dense Matrix")
	public DenseMatrix add(
	@ParamInfo(name = "Input 3x3 Dense Matrix A", style="default")
	DenseMatrix m1, 
	@ParamInfo(name = "Input 3x3 Dense Matrix B", style="default")
	DenseMatrix m2
	) {
		return new DenseMatrix(new Basic2DMatrix(m1.add(m2)));
	}
	
	/**
	 * @brief sub
	 * @param m1
	 * @param m2
	 * @return 
	 */
	@OutputInfo(name="Result 3x3 Dense Matrix")
	public DenseMatrix sub(
	@ParamInfo(name = "Input 3x3 Dense Matrix A", style="default")
	DenseMatrix m1, 
	@ParamInfo(name = "Input 3x3 Dense Matrix B", style="default")
	DenseMatrix m2
	) {
		return new DenseMatrix(new Basic2DMatrix(m1.subtract(m2)));
	}
	
	/**
	 * @brief mul
	 * @param m1
	 * @param m2
	 * @return 
	 */
	@OutputInfo(name="Result 3x3 Dense Matrix")
	public DenseMatrix mul(
	@ParamInfo(name = "Input 3x3 Dense Matrix A", style="default")
	DenseMatrix m1, 
	@ParamInfo(name = "Input 3x3 Dense Matrix B", style="default")
	DenseMatrix m2
	) {
		return new DenseMatrix(new Basic2DMatrix(m1.multiply(m2)));
	}
}

