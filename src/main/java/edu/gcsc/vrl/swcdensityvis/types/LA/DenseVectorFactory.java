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
@ComponentInfo(name="DenseVectorFactory", category="Neuro/Common/LA")
public class DenseVectorFactory implements Serializable {
	private static final long serialVersionUID = 1L;
	/**
	 * @brief 1x1 vector
	 * @param d
	 * @param selection
	 * @param times
	 * @param scale
	 * @param factor
	 * @return 
	 */
	@OutputInfo(name="1x1 Vector", options="cols=1; rows=1")
	public DenseMatrix vector1x1(
		@ParamInfo(name="1x1 Vector", style="default", options="cols=1; rows=1")
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
	 * @brief 1x2 vector
	 * @param d
	 * @param selection
	 * @param times
	 * @param scale
	 * @param factor
	 * @return 
	 */
	@OutputInfo(name="1x2 Vector", options="cols=1; rows=2")
	public DenseMatrix vector1x2(
		@ParamInfo(name="1x2 Vector", style="default", options="cols=1; rows=2")
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
	 * @brief 1x3 vector
	 * @param d
	 * @param selection
	 * @param times
	 * @param scale
	 * @param factor
	 * @return 
	 */
	@OutputInfo(name="1x3 Vector", options="cols=1; rows=3")
	public DenseMatrix vector1x3(
		@ParamInfo(name="1x3 Vector", style="default", options="cols=1; rows=3")
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
	 * @brief 1x4 vector
	 * @param d
	 * @param selection
	 * @param times
	 * @param scale
	 * @param factor
	 * @return 
	 */
	@OutputInfo(name="1x4 Vector", options="cols=1; rows=4")
	public DenseMatrix vector1x4(
		@ParamInfo(name="1x4 Vector", style="default", options="cols=1; rows=4")
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

