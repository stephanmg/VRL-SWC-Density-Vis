/// package's name
package edu.gcsc.vrl.swcdensityvis.types.LA;

/// imports
import eu.mihosoft.vrl.annotation.TypeInfo;
import eu.mihosoft.vrl.types.ArrayBaseType;
import groovy.lang.Script;

/**
 * @brief implement methods from DenseMatrixType which are utilized to
 *        create the visual representation of the DenseMatrix ...
 *        note: DenseMatrixType should be split into DenseMatrixType,
 *             DenseMatrixAbstractType (interface fo DenseMatrixType and
 *             and DenseMatrixArrayType) and DenseMatrixArrayType
 * 
 *        this means: call super.setViewValue() and then implement the 
 *          setViewValue function from old DenseMatrixType here again 
 *          (therefore we need an DenseMatrixAbstractType interface or
 *           we make DenseMatrixAbstractType an abstract class). with the
 *           interface we could remove the DenseMatrixArrayBaseType and
 *           make: DenseMatrixArrayType extends ArrayBaseType implements DenseMatrixAbstractType...
 * 
 * 
 * 	  this is to be done for all methods we have added in DenseMatrixType!
 * 
 * as previous: if we just use ArrayBaseType, the default representation of
 * DenseMatrix will be used, which is hardcoded in DenseMatrixType, and the
 * cols and rows options aren't considered in the ArrayBaseType of course!
 * @author stephan
 */
@TypeInfo(type = Object[].class, style = "matrix-array")
public class DenseMatrixArrayBaseType extends ArrayBaseType {

	private static final long serialVersionUID = 1L;
	private Integer cols = 3;
	private Integer rows = 3;

	@Override
	public void evaluationRequest(Script script) {
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

	public DenseMatrixArrayBaseType() {
		super();
		setValueName("DenseMatrixArray");
	}

	private void setColCount(Integer integer) {
		this.cols = integer;
	}

	private void setRowCount(Integer integer) {
		this.rows = integer;
	}
}
