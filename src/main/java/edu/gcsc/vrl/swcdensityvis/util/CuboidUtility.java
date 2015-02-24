/// package's name
package edu.gcsc.vrl.swcdensityvis.util;

/// imports
import edu.gcsc.vrl.swcdensityvis.data.Cuboid;
import eu.mihosoft.vrl.reflection.Pair;

/**
 * @brief cuboid utilities
 * @author stephan
 */
public final class CuboidUtility {

	/**
	 * @brief private ctor for utility pattern
	 */
	private CuboidUtility() {
	}
 
	/**
	 * @brief get index of sampling cube within the bounding box 
	 * @param bounding
	 * @param sample
	 * @param step_x
	 * @param step_y
	 * @param step_z
	 * @return 
	 */
	public static int[] getCuboidId(Cuboid bounding, Cuboid sample) {
		if ( 
			(sample.getX() + sample.getWidth()  > bounding.getX() + bounding.getWidth()) ||
			(sample.getY() + sample.getHeight() > bounding.getY() + bounding.getHeight()) ||
			(sample.getZ() + sample.getDepth()  > bounding.getZ() + bounding.getDepth())
		) {
			System.err.println("Sample box out of bounding box!");
			return new int[] {0, 0, 0};
		}
		return new int[]{
			(int) Math.abs( (bounding.getX() + sample.getX())),
			(int) Math.abs( (bounding.getY() + sample.getY())),
			(int) Math.abs( (bounding.getZ() + sample.getZ()))
		};

	}

	/**
	 * @brief get cuboid by id
	 * @todo implement
	 *
	 * @param bounding
	 * @param id
	 * @param step_x
	 * @param step_y
	 * @param step_z
	 * @return
	 */
	public static Cuboid getCuboidbyId(Cuboid bounding, int[] id, float step_x, float step_y, float step_z) {
		return new Cuboid(id[0], id[1], id[2], step_x, step_y, step_z);
	}

	/**
	 * @brief get bounding indices of of sample cube; with this indices we
	 * can iterate over the whole geometry bounding in a sparse sense...
	 * 
	 * @note we don't allow for non-integer cube width, height or depth for now!
	 *
	 * @param bounding the bounding box of the geometry
	 * @param min the minimum coordinates of the sampling cube
	 * @param max the maximum coordinates of the sampling cube
	 * @param step_x only necessary if we allow non-integer cube width
	 * @param step_y only necessary if we allow non-integer cube height
	 * @param step_z only necessary if we allow non-integer cube depth
	 * @return
	 */
	public static Pair<int[], int[]> getSampleCuboidBoundingIndices(Cuboid bounding, Cuboid min, Cuboid max, float step_x, float step_y, float step_z) {
		int[] lo = getCuboidId(bounding, new Cuboid(min.getX(), min.getY(), min.getZ(), min.getWidth(), min.getY(), min.getZ()));
		int[] hi = getCuboidId(bounding, new Cuboid(max.getX(), max.getY(), max.getZ(), max.getWidth(), max.getHeight(), max.getDepth()));
		return new Pair<int[], int[]>(lo, hi);
	}
}
