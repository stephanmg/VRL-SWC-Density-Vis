/// package's name
package edu.gcsc.vrl.swcdensityvis.data;

/// imports
import edu.gcsc.vrl.swcdensityvis.types.LA.DenseMatrix;
import eu.mihosoft.vrl.reflection.Pair;
import eu.mihosoft.vrl.v3d.Shape3DArray;
import javax.vecmath.Vector3f;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @brief enhanced Shape3DArray
 * @author stephan <stephan@syntaktischer-zucker.de>
 * @see {@link Shape3DArray}
 */
@Data @EqualsAndHashCode(callSuper=false)
public class Shape3DArrayCustom extends Shape3DArray {
	/// sVUID
	private static final long serialVersionUID = 1L;
	
	/// members
	private boolean scalebarVisible;
	private boolean coordinateSystemVisible;
	private int fps;
	private int spf;
	private double[] rotationParams;
	private String videoFormat;
	private Pair<Vector3f, Vector3f> boundingBox;
	private DenseMatrix blurKernel;
}