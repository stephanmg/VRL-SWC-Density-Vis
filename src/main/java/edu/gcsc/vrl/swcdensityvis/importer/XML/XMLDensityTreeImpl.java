/// package's name
package edu.gcsc.vrl.swcdensityvis.importer.XML;

/// imports
import edu.gcsc.vrl.densityvis.Density;
import edu.gcsc.vrl.densityvis.VoxelImpl;
import edu.gcsc.vrl.densityvis.VoxelSet;
import edu.gcsc.vrl.densityvis.WritableVoxel;
import eu.mihosoft.vrl.reflection.Pair;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.vecmath.Vector3f;

/**
 * @brief Density implementation for internal usage
 * @author stephan
 */
final class XMLDensityTreeImpl implements Density {
	/// the output voxels
	private final ArrayList<WritableVoxel> voxels = new ArrayList<WritableVoxel>();
	private final int voxelWidth, voxelHeight, voxelDepth;
	private final Pair<Vector3f, Vector3f> bounding;
	private final HashMap<Integer, Float> density;

	/**
	 * @brief computes the average density in each voxel subset
	 * @param stack
	 * @param width
	 * @param height
	 * @param depth
	 * @param choice
	 */
	public XMLDensityTreeImpl(HashMap<Integer, Float> density, Pair<Vector3f, Vector3f> bounding, int voxelWidth, int voxelHeight, int voxelDepth) {
		this.density = density;
		this.bounding = bounding;
		this.voxelWidth = voxelWidth;
		this.voxelHeight = voxelHeight;
		this.voxelDepth = voxelDepth;
		compute();
	}

	/**
	 * Computes the average density for each voxel subset.
	 */
	private void compute() {
		/// get the bounding box in physiological units, i. e. µm
		Pair<Vector3f, Vector3f> bounding = this.bounding;
		/// compute the density
		int index = 0;
		for (float x = bounding.getSecond().x; x < bounding.getFirst().x; x += this.voxelWidth) {
			for (float y = bounding.getSecond().y; y < bounding.getFirst().y; y += this.voxelHeight) {
				for (float z = bounding.getSecond().z; z < bounding.getFirst().z; z += this.voxelDepth) {
					if (density.containsKey(index)) {
						/// note: density.get(index) in interval [0, 1] -> thus we multiply by 100, for making in the graphical representation available the density in percentage 0 to 100 %
						voxels.add(new VoxelImpl((int) x, (int) y, (int) z, this.voxelWidth, this.voxelHeight, this.voxelDepth, density.get(index) * 1000));  /// note however, multiplication with 100 is not required!
						System.err.println("density: " + density.get(index) * 1000);
						/** @todo we need to take the default voxel volume into account for a density 
						 *  this means: dividing by the morphological volume! (for now we allow for 1x1x1 volumes which corresponds to 1x1x1 in morphological units, i. e. um^3 here!)
						 */
						  
					} else {
						voxels.add(new VoxelImpl((int) x, (int) y, (int) z, this.voxelWidth, this.voxelHeight, this.voxelDepth, 0));
					}
					index++;
				}
			}
		}
	}

	/**
	 * 
	 * @return 
	 */
	@Override
	@SuppressWarnings("ReturnOfCollectionOrArrayField")
	public List<? extends VoxelSet> getVoxels() {
		return voxels;
	}
}
