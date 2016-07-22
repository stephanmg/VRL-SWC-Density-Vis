/// package's name
package edu.gcsc.vrl.swcdensityvis.types.LA;

/// imports
import lombok.extern.log4j.Log4j;

/**
 * @brief utilities for DenseMatrix
 * @author stephanmg <stephan@syntaktischer-zucker.de>
 */
@Log4j
public final class DenseMatrixUtil {
	/**
	 * @brief utility pattern
	 */
	private DenseMatrixUtil() {
		
	}
	
	/**
	 * @brief convert dense matrix to array
	 * @param denseMatrix
	 * @return 
	 */
	public static double[] toArray(final DenseMatrix denseMatrix) {
		double[] vals = new double[denseMatrix.columns()*denseMatrix.rows()];
		
		
		if (denseMatrix != null) {
			for (int i=0; i < denseMatrix.rows(); i++) {
				for (int j = 0; i < denseMatrix.columns(); j++) {
					vals[i*denseMatrix.rows()+j] = denseMatrix.get(i, j);
				}
			}
		} else {
			log.info("Supplied denseMatrix was null!");
		}
		
		return vals;
	}
}
