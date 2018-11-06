/// package's name
package edu.gcsc.vrl.swcdensityvis.types.LA;

/// imports
import eu.mihosoft.vrl.annotation.ParamInfo;
import eu.mihosoft.vrl.annotation.TypeInfo;
import eu.mihosoft.vrl.types.ArrayBaseType;
import groovy.lang.Script;
import java.lang.annotation.Annotation;

/**
 * @brief matrix array type
 * @author stephanmg <stephan@syntaktischer-zucker.de>
 */
@TypeInfo(type = DenseMatrix[].class, input = true, output = true, style = "matrix-array-silent")
public class DenseMatrixArraySilentType extends ArrayBaseType {

	private static final long serialVersionUID = 1L;

	public DenseMatrixArraySilentType() {
		setValueName("Dense Matrix Array:");
		setElementInputInfo(null);
	}

	@Override
	public void evaluationRequest(Script script) {
		super.evaluationRequest(script);

		Object property = script.getProperty("rows");

		final int elemRowSize;
		final int elemColSize;

		if (property != null) {
			elemRowSize = (Integer) property;
		} else {
			elemRowSize = 3;
		}

		property = script.getProperty("cols");

		if (property != null) {
			elemColSize = (Integer) property;
		} else {
			elemColSize = 3;
		}

		setElementInputInfo(new ParamInfo() {
			@Override
			public String name() {
				return "DenseMatrixArrayType";
			}

			@Override
			public String style() {
				return "matrix-silent";
			}

			@Override
			public boolean nullIsValid() {
				return false;
			}

			@Override
			public String options() {
				return "cols=" + elemColSize + ";rows=" + elemRowSize;
			}

			@Override
			public String typeName() {
				return "DenseMatrixArrayType";
			}

			@Override
			public Class<? extends Annotation> annotationType() {
				return null;
			}
		});

		/**
		 * Note: on multi-methods branch of VRL we can do:
		 * setElementInputInfo(new DefaultParamInfo("", "default",
		 * "elemSize=" + elemRowSize, false));
		 */
	}
}
