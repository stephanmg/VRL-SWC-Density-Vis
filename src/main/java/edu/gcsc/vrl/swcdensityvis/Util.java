/// package's name
package edu.gcsc.vrl.swcdensityvis;

/// imports
import edu.gcsc.vrl.densityvis.DensityResult;
import edu.gcsc.vrl.densityvis.VoxelSet;
import eu.mihosoft.vrl.reflection.Pair;
import java.util.ArrayList;
import java.util.Collections;
import javax.vecmath.Vector3f;

/**
 * @brief
 * @author stephanmg <stephan@syntaktischer-zucker.de>
 */
public class Util {
	/**
	 * @brief calculates the bounding box of the the density
	 * @param density
	 * @return 
	 */
	public static Pair<Vector3f, Vector3f> CalculateBoundingBox(DensityResult density) {
		ArrayList<Integer> temp_x = new ArrayList<Integer>();
		ArrayList<Integer> temp_y = new ArrayList<Integer>();
		ArrayList<Integer> temp_z = new ArrayList<Integer>();
		for (VoxelSet v : density.getDensity().getVoxels()) {
			temp_x.add(v.getX());
			temp_y.add(v.getY());
			temp_z.add(v.getZ());
		}
		
		return new Pair<Vector3f, Vector3f>(
			new Vector3f(Collections.min(temp_x), Collections.min(temp_y), Collections.min(temp_z)),
			new Vector3f(Collections.max(temp_x), Collections.max(temp_y), Collections.max(temp_z))
		);
	}
	/**
	 * @brief
	 */
	private Util() {
		
	}
}
