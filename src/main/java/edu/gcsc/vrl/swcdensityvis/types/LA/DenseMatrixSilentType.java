/// package's name
package edu.gcsc.vrl.swcdensityvis.types.LA;

/// imports
import eu.mihosoft.vrl.annotation.TypeInfo;
import eu.mihosoft.vrl.reflection.TypeRepresentationBase;
import groovy.lang.Script;

/**
 * @brief dense matrix type for VRL (defaults to 3x3)
 * @author stephan grein
 */
@TypeInfo(type = DenseMatrix.class, input = true, output = true, style = "matrix-silent")
public final class DenseMatrixSilentType extends TypeRepresentationBase {
	private static final long serialVersionUID = 1L;
	
	private final DenseMatrix matrix;
	private Integer rows = 3;
	private Integer cols = 3;
	
	public DenseMatrixSilentType() {
		setValueName("DenseMatrix:");
		this.matrix = new DenseMatrix(rows, cols);
	}
	
	public DenseMatrixSilentType(int rows, int cols) {
		setValueName("DenseMatrix:");
		this.matrix = new DenseMatrix(rows, cols);
	}
	
	@Override
	protected void evaluationRequest(Script script) {
		Object property = null;
		if (getValueOptions() != null) {

			if (getValueOptions().contains("rows")) {
				property = script.getProperty("rows");
			}

			if (property != null) {
				setRowCount((Integer) property);
			}

			if (getValueOptions().contains("cols")) {
				property = script.getProperty("cols");
			}

			if (property != null) {
				setColCount((Integer) property);
			}
		}
	}

	private void setRowCount(Integer integer) {
		this.rows = integer;
	}

	private void setColCount(Integer integer) {
		this.cols = integer;
	}
}
