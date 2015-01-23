/// package's name
package edu.gcsc.vrl.swcdensityvis.types;

/// imports
import eu.mihosoft.vrl.annotation.ComponentInfo;
import eu.mihosoft.vrl.annotation.ParamInfo;
import java.io.Serializable;


/**
 *
 * @author stephan
 */
@ComponentInfo(name="MatrixComponent")
public class MatrixComponent implements Serializable {
	private static final long serialVersionUID = 1L;
	/**
	 * @todo for now we test only 3x3 matrices... 
	 */
	public void dummy(
	@ParamInfo(name = "Matrix", style="default", options="cols=3; rows=3")
	Matrix matrix
	) {
	}
}

