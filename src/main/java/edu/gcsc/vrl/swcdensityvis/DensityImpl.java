/// package's name
package edu.gcsc.vrl.swcdensityvis;

/// imports
import edu.gcsc.vrl.densityvis.Density;
import edu.gcsc.vrl.densityvis.VoxelSet;
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
    /// the SWC "stack"
    private final HashMap<String, ArrayList<SWCCompartmentInformation>> stack;
    private final int voxelWidth;
    private final int voxelHeight;
    private final int voxelDepth;
    
    /// the output voxels
    private final ArrayList<WritableVoxel> voxels = new ArrayList<WritableVoxel>();

    /**
     * @brief computes the average density in each voxel subset
     * @param stack
     * @param width
     * @param height
     * @param depth 
     */
    public DensityImpl(HashMap<String, ArrayList<SWCCompartmentInformation>> stack, int voxelWidth, int voxelHeight, int voxelDepth) {
	this.stack = stack;
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
	Pair<Vector3f, Vector3f> bounding = SWCUtility.getBoundingBox(stack);
	/// compute the density
	HashMap<Integer, Float> density = SWCUtility.computeDensity(stack);
	
	int index = 0;
	for (float x = bounding.getSecond().x; x < bounding.getFirst().x; x+=this.voxelWidth) {
		for (float y = bounding.getSecond().y; y < bounding.getFirst().y; y+=this.voxelHeight) {
		 	  for (float z = bounding.getSecond().z; z < bounding.getFirst().z; z+=this.voxelDepth) {
				  if (density.containsKey(index)) {
			   	voxels.add(new VoxelImpl((int)x, (int)y, (int)z, this.voxelWidth, this.voxelHeight, this.voxelDepth,  density.get(index) * 255));
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
