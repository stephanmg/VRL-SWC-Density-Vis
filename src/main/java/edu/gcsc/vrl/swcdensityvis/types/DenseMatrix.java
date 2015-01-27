/// package's name
package edu.gcsc.vrl.swcdensityvis.types;

/// imports
import java.io.Serializable;
import org.la4j.matrix.dense.Basic2DMatrix;

/**
 * @brief the matrix class (defaults to 3x3 also when we write groovy code for now!)
 * @author stephan grein
 */
public class DenseMatrix extends Basic2DMatrix implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * @brief default ctor
	 * @param rows
	 * @param cols
	 */
	public DenseMatrix(int rows, int cols) {
		super(rows, cols);
	}

	/**
	 * @def ctor
	 */
	public DenseMatrix() {
		super(3, 3);
	}

	/**
	 * @param mat 
	 */
	public DenseMatrix(Basic2DMatrix mat) {
		super(mat);
	}
}
