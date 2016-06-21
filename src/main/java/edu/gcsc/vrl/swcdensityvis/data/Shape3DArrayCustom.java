/// package's name
package edu.gcsc.vrl.swcdensityvis.data;

/// imports
import edu.gcsc.vrl.swcdensityvis.types.LA.DenseMatrix;
import eu.mihosoft.vrl.reflection.Pair;
import eu.mihosoft.vrl.v3d.Shape3DArray;
import javax.vecmath.Vector3f;

/**
 * @brief enhanced Shape3DArray
 * @author stephan <stephan@syntaktischer-zucker.de>
 * @see {@link Shape3DArray}
 */
public class Shape3DArrayCustom extends Shape3DArray {
	/// sVUID
	private static final long serialVersionUID = 1L;
	
	/// members
	private DenseMatrix rotationMatrix;
	private DenseMatrix blurKernel;
	private Pair<Vector3f, Vector3f> boundingBox;
	private boolean scalebarVisible = false;
	private boolean coordinateSystemVisible = false;

	/// setters
	/**
	 * @brief set rotation matrix
	 * @param rotationMatrix 
	 */
	public void set_rotation_matrix(DenseMatrix rotationMatrix) {
		this.rotationMatrix = rotationMatrix;
	}
	
	/**
	 * @brief set bounding box
	 * @param boundingBox 
	 */
	public void set_bounding_box(Pair<Vector3f, Vector3f> boundingBox) {
		this.boundingBox = boundingBox;
	}
	
	/// getters
	/**
	 * @brief get rotation matrix
	 * @return 
	 */
	public DenseMatrix get_rotation_matrix() {
		return this.rotationMatrix;
	}
	
	/**
	 * @brief get bounding box
	 * @return 
	 */
	public Pair<Vector3f, Vector3f> get_bounding_box() {
		return this.boundingBox;
	}

	/**
	 * @brief set dimension
	 * @param center 
	 */
	public void set_dim(Object center) {
	}

	/**
	 * @brief set center
	 * @param center 
	 */
	public void set_center(Object center) {
	}

	/**
	 * @brief set the scale bar visible
	 */
	public void setScalebarVisible() {
		this.scalebarVisible = true;
	}

	/**
	 * @brief set the coordinate system visible
	 */
	public void setCoordinateSystemVisible() {
		this.coordinateSystemVisible = true;
	}
	
	/**
	 * @brief is the coordinate system visible?
	 * @return
	 */
	public boolean isCoordinateSystemVisible() {
		return this.coordinateSystemVisible;
	}
	
	/**
	 * @brief is the scalebar visible?
	 * @return
	 */
	public boolean isScalebarVisible() {
		return this.scalebarVisible;
	}

	/**
	 * @brief set the blurring kernel
	 * @param blurKernel
	 */
	public void set_blurring_kernel(DenseMatrix blurKernel) {
		this.blurKernel = blurKernel;
	}
	
	/**
	 * @brief get the blurring kernel
	 * @return 
	 */
	public DenseMatrix get_blurring_kernel() {
		return this.blurKernel;
	}
}
