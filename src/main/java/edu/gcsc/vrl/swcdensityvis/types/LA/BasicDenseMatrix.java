/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/// package's name
package edu.gcsc.vrl.swcdensityvis.types.LA;

/// imports
import org.la4j.matrix.dense.Basic2DMatrix;


/**
 *
 * @author stephan
*/
@SuppressWarnings("serial")
abstract class BasicDenseMatrix extends Basic2DMatrix implements AbstractDenseMatrix {
	/**
	 * @brief default ctor
	 * @param rows
	 * @param cols
	 */
	public BasicDenseMatrix(int rows, int cols) {
		super(rows, cols);
	}

	/**
	 * @def ctor
	 */
	public BasicDenseMatrix() {
		super(3, 3);
	}

	/**
	 * @param mat 
	 */
	public BasicDenseMatrix(Basic2DMatrix mat) {
		super(mat);
	}

}
