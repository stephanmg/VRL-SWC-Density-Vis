/// package's name
package edu.gcsc.vrl.swcdensityvis.types;

/// imports
import java.io.Serializable;

/**
 * @brief the matrix class
 * @todo  decide if we want to have a hardcoded 3x3 matrix,
 *        or if we want to allow arbitrary matrices. then give the matrix also
 *        a functionality depending on our uses, but for now 3x3 seems adequate.
 *        note however, we need to generalize for float and int matrizes maybe!
 * @author stephan grein
 */
public class Matrix extends javax.vecmath.Matrix3d implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * @brief default ctor
	 */
	public Matrix() {
	}
}
