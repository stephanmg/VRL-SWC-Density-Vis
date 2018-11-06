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
 * @brief the density implementation for internal usage adopted from Density-Vis
 * @author stephanmg <stephan@syntaktischer-zucker.de>
 */
final class XMLDensityEdgeImpl implements Density {
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
	public XMLDensityEdgeImpl(HashMap<Integer, Float> density, Pair<Vector3f, Vector3f> bounding, int voxelWidth, int voxelHeight, int voxelDepth) {
		this.density = density;
		this.bounding = bounding;
		this.voxelWidth = voxelWidth;
		this.voxelHeight = voxelHeight;
		this.voxelDepth = voxelDepth;
		compute();
	}

	/**
	 * @brief computes the average density for each voxel subset.
	 */
	private void compute() {
		/// get the bounding box in physiological units, i. e. µm
		Pair<Vector3f, Vector3f> bounding = this.bounding;
		/// compute the density
		int index = 0;
		int voxelDim = this.voxelDepth * this.voxelHeight * this.voxelHeight; 
		float totalDim = Math.abs((bounding.getFirst().x - bounding.getSecond().x) * (bounding.getFirst().y - bounding.getSecond().y) * (bounding.getFirst().z - bounding.getSecond().z)); 
		for (float x = bounding.getSecond().x; x < bounding.getFirst().x; x += this.voxelWidth) {
			for (float y = bounding.getSecond().y; y < bounding.getFirst().y; y += this.voxelHeight) {
				for (float z = bounding.getSecond().z; z < bounding.getFirst().z; z += this.voxelDepth) {
					/// Note: Is the index handling really correct here?
					index = (int) x * (int) y * (int) z;
					///System.err.println("xyz: " + index);
					if (density.containsKey(index)) {
						/// Note: density.get(index) in interval [0, 1], multiply by 100 -> percentage 
						voxels.add(new VoxelImpl((int) x, (int) y, (int) z, this.voxelWidth, this.voxelHeight, this.voxelDepth, density.get(index) * 100));  /// note however, multiplication with 100 is not required!
	//					System.err.println("density: " + density.get(index) * 100);
						/// TODO: Scale voxels with physiological length
					} else {
						voxels.add(new VoxelImpl((int) x, (int) y, (int) z, this.voxelWidth, this.voxelHeight, this.voxelDepth, 0));
					}
					index++;
				}
			}
		}
	}

	/**
	 * @brief get the voxels back
	 * @return 
	 */
	@Override
	@SuppressWarnings("ReturnOfCollectionOrArrayField")
	public List<? extends VoxelSet> getVoxels() {
		return voxels;
	}

}
