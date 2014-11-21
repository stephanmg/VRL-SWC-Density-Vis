/// package's name
package edu.gcsc.vrl.swcdensityvis;

/**
 * @brief cuboid utilities
 * @author stephan
 */
public final class CuboidUtility {
	/**
	 * @brief private ctor for utility pattern
	 */
	private CuboidUtility() {
		
	};
 
	/**
	 * @brief get index of sampling cube within the bounding box 
	 * @param bounding
	 * @param sample
	 * @param step_x
	 * @param step_y
	 * @param step_z
	 * @return 
	 */
	public int getCuboidId(Cuboid bounding, Cuboid sample, float step_x, float step_y, float step_z) {
		return (int)( sample.getX() / ((bounding.getWidth() - bounding.getX()) / step_x)) +
		       (int)( sample.getY() / ((bounding.getHeight() - bounding.getY()) / step_y)) + 
		       (int)( sample.getZ() / ((bounding.getDepth() - bounding.getZ()) / step_z));
	}
}
