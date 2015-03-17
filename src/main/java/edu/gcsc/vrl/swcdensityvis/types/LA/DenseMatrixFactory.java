/// package's name
package edu.gcsc.vrl.swcdensityvis.types.LA;

/// imports
import eu.mihosoft.vrl.annotation.ComponentInfo;
import eu.mihosoft.vrl.annotation.OutputInfo;
import eu.mihosoft.vrl.annotation.ParamInfo;
import java.io.Serializable;
import org.la4j.matrix.dense.Basic2DMatrix;

/**
 * @brief DenseMatrixFactory
 * @author stephan grein
 */
@ComponentInfo(name="DenseMatrixFactory", category="Neuro/Common/LA")
public class DenseMatrixFactory implements Serializable {
	private static final long serialVersionUID = 1L;
	
	/**
	 * @brief 2x2 matrix
	 * @param d
	 * @param selection
	 * @param times
	 * @param scale
	 * @param factor
	 * @return 
	 */
	@OutputInfo(name="2x2 Matrix", options="cols=2; rows=2", style="default")
	public final DenseMatrix matrix2x2(
		@ParamInfo(name="2x2 Matrix", options="cols=2; rows=2", style="default")
		DenseMatrix d,
		@ParamInfo(name="Operation", style="selection", options="value=[\"id\", \"pot\"]")
		String selection,
		@ParamInfo(name="Times")
		int times,
		@ParamInfo(name="Scale", style="default")
		boolean scale,
		@ParamInfo(name="Factor", style="default")
		Double factor) {
			return op(d, selection, scale, factor, times);
		}
	
	/**
	 * @brief 3x3 matrix
	 * @param d
	 * @param selection
	 * @param times
	 * @param scale
	 * @param factor
	 * @return 
	 */
	@OutputInfo(name="3x3 Matrix", options="cols=3; rows=3", style="default")
	public final DenseMatrix matrix3x3(
		@ParamInfo(name="3x3 Matrix", options="cols=3; rows=3", style="default")
		DenseMatrix d,
		@ParamInfo(name="Operation", style="selection", options="value=[\"id\", \"pot\"]")
		String selection,
		@ParamInfo(name="Times")
		int times,
		@ParamInfo(name="Scale", style="default")
		boolean scale,
		@ParamInfo(name="Factor", style="default")
		Double factor) {
			return op(d, selection, scale, factor, times);
		}
	
	/**
	 * @brief 4x4 matrix
	 * @param d
	 * @param selection
	 * @param times
	 * @param scale
	 * @param factor
	 * @return 
	 */
	@OutputInfo(name="4x4 Matrix", options="cols=4; rows=4", style="default")
	public final DenseMatrix matrix4x4(
		@ParamInfo(name="4x4Matrix", options="cols=4; rows=4", style="default")
		DenseMatrix d,
		@ParamInfo(name="Operation", style="selection", options="value=[\"id\", \"pot\"]")
		String selection,
		@ParamInfo(name="Times")
		int times,
		@ParamInfo(name="Scale", style="default")
		boolean scale,
		@ParamInfo(name="Factor", style="default")
		Double factor) {
			return op(d, selection, scale, factor, times);
		}
	
	/**
	 * @brief performs the operation
	 * @param m
	 * @param selection
	 * @param scale
	 * @param factor
	 * @param times
	 * @return 
	 */
	private DenseMatrix op(DenseMatrix m, String selection, boolean scale, Double factor, int times) {
		if (selection.equalsIgnoreCase("id")) {
			if (scale) {
				return new DenseMatrix(new Basic2DMatrix(m.multiply(factor)));
			}
		} else {
			if (scale) {
				return new DenseMatrix(new Basic2DMatrix(m.multiply(m).multiply(factor)));
			} else {
				return new DenseMatrix(new Basic2DMatrix(m.multiply(m)));
			}
		}
		return m;
	}
}

