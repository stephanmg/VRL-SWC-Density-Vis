/// package's name
package edu.gcsc.vrl.swcdensityvis.types;

/// imports
import eu.mihosoft.vrl.annotation.ComponentInfo;
import eu.mihosoft.vrl.annotation.OutputInfo;
import eu.mihosoft.vrl.annotation.ParamInfo;
import java.io.Serializable;
import org.la4j.matrix.Matrix;
import org.la4j.matrix.dense.Basic2DMatrix;


/**
 * @brief test matrix component for visual representation (defaults to 3x3)
 * @author stephan grein
 */
@ComponentInfo(name="DenseMatrixComponent")
public class DenseMatrixComponent implements Serializable {
	private static final long serialVersionUID = 1L;
	/**
	 * @brief add
	 * @param m1
	 * @param m2
	 * @return
	 */
	public DenseMatrix add(
	@ParamInfo(name = "DenseMatrix", style="default")
	DenseMatrix m1, 
	@ParamInfo(name = "DenseMatrix", style="default")
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
	public DenseMatrix sub(
	@ParamInfo(name = "DenseMatrix", style="default")
	DenseMatrix m1, 
	@ParamInfo(name = "DenseMatrix", style="default")
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
	public DenseMatrix mul(
	@ParamInfo(name = "DenseMatrix", style="default")
	DenseMatrix m1, 
	@ParamInfo(name = "DenseMatrix", style="default")
	DenseMatrix m2
	) {
		return new DenseMatrix(new Basic2DMatrix(m1.multiply(m2)));
	}
}

