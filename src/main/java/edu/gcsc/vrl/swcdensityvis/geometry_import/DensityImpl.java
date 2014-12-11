/// package's name
package edu.gcsc.vrl.swcdensityvis.geometry_import;

/// imports
import edu.gcsc.vrl.densityvis.Density;
import edu.gcsc.vrl.densityvis.VoxelSet;
import edu.gcsc.vrl.densityvis.VoxelImpl;
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
final class DensityImpl implements Density { 
    private final GeometryImporter gi;
    private final int voxelWidth;
    private final int voxelHeight;
    private final int voxelDepth;
    private final ArrayList<WritableVoxel> voxels = new ArrayList<WritableVoxel>();

    /**
     * @brief computes the average density in each voxel subset
     * @param stack
     * @param width
     * @param height
     * @param depth 
     */
    public DensityImpl(GeometryImporter gi, int voxelWidth, int voxelHeight, int voxelDepth) {
	this.gi = gi;
        this.voxelWidth = voxelWidth;
        this.voxelHeight = voxelHeight;
        this.voxelDepth = voxelDepth;
	compute();
    }

    /**
     * Computes the average density for each voxel subset.
     */
    private void compute() {
	gi.load_cells();
	Pair<Vector3f, Vector3f> bounding = gi.getBoundingBox();
	HashMap<Integer, Float> density = gi.computeDensity();
	
	int index = 0;
	for (float x = bounding.getSecond().x; x < bounding.getFirst().x; x+=this.voxelWidth) {
		for (float y = bounding.getSecond().y; y < bounding.getFirst().y; y+=this.voxelHeight) {
		 	  for (float z = bounding.getSecond().z; z < bounding.getFirst().z; z+=this.voxelDepth) {
				  if (density.containsKey(index)) {
			   	voxels.add(new VoxelImpl((int)x, (int)y, (int)z, this.voxelWidth, this.voxelHeight, this.voxelDepth,  density.get(index) * 255));
				/**
				 * @todo multiplication with 255 not necessary, since we have no colors in input image
				 */
				  } else {
			   	voxels.add(new VoxelImpl((int)x, (int)y, (int)z, this.voxelWidth, this.voxelHeight, this.voxelDepth,  0));
					  }
			   	/// note: density.get(index) in interval [0, 1] -> thus we multiply by 255 to have a color between 0 and 255
			   	index++;
			   }
		 }
 	}
    }

    @Override
    @SuppressWarnings("ReturnOfCollectionOrArrayField")
    public List<? extends VoxelSet> getVoxels() {
        return voxels;
    }
}
