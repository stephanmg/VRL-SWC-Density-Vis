/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.gcsc.vrl.swcdensityvis;

import edu.gcsc.vrl.densityvis.Density;
import edu.gcsc.vrl.densityvis.ImageVoxels;
import edu.gcsc.vrl.densityvis.VoxelSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.vecmath.Vector3f;

/**
 * Internal density implementation. This class must not be exported through
 * public API.
 *
 * @author 
 * @brief
 */
class DensityImpl implements Density {

    private ImageVoxels cube; // ImageJ input
    private HashMap<String, ArrayList<SWCCompartmentInformation>> input; // input from swc import ... do this in ImageVoxels
    private int voxelWidth;
    private int voxelHeight;
    private int voxelDepth;
    private ArrayList<WritableVoxel> voxels = new ArrayList<WritableVoxel>();

    /**
     * Constructor. <b>Note:</b> computes the average density for each voxel
     * subset.
     *
     * @param imageVoxels image voxels (usually from .tif-stack)
     * @param voxelSetWidth width of the voxel set (in image coordinates)
     * @param voxelSetHeight height of the voxel set (in image coordinates)
     * @param voxelSetDepth depth of the voxel set (in image coordinates)
     */
    public DensityImpl(ImageVoxels imageVoxels,
            int voxelWidth, int voxelHeight, int voxelDepth) {
        this.cube = imageVoxels;
        this.voxelWidth = voxelWidth;
        this.voxelHeight = voxelHeight;
        this.voxelDepth = voxelDepth;

        compute();
    }

    /**
     * Computes the average density for each voxel subset.
     */
    private void compute() {
	   /// TODO move this into ImageVoxels
	Vector3f dimensions = SWCUtility.getDimensions(input);
        if (voxelWidth > dimensions.x) {
            voxelWidth = (int) dimensions.x;
        }
			
		if (voxelHeight > dimensions.y) {
            voxelHeight = (int) dimensions.y;
        }
		
		if (voxelHeight > dimensions.z) {
            voxelDepth = (int) dimensions.z;
        }

		/// implement from here TODO
        for (int z = 0; z < cube.getDepth(); z += voxelDepth) {

            int depth = voxelDepth;

            if (z + depth > cube.getDepth() - 1) {
                depth = cube.getDepth() - z;
            }

            for (int y = 0; y < cube.getHeight(); y += voxelHeight) {

                int height = voxelHeight;

                if (y + height > cube.getHeight() - 1) {
                    height = cube.getHeight() - y;
                }

                for (int x = 0; x < cube.getWidth(); x += voxelWidth) {

                    int width = voxelWidth;

                    if (x + width > cube.getWidth() - 1) {
                        width = cube.getWidth() - x;
                    }

                    double value = 0;

                    int numVoxel = (depth) * (height) * (width);

                    for (int zz = z; zz < z + depth; zz++) {
                        for (int yy = y; yy < y + height; yy++) {
                            for (int xx = x; xx < x + width; xx++) {

                                value += cube.getData()[zz][xx][yy];
//                                value += cube.getVoxel(xx, yy, zz);
                            }
                        }
                    }

                    value /= numVoxel;
                    value /= 255.0; // scale to [0,1], image has 8-bit (min=0,max=255)

                    voxels.add(new VoxelImpl(
                            x, y, z, width, height, depth, value));

                } // for x
            } // for y
        } // for z
    }

    @Override
    public List<? extends VoxelSet> getVoxels() {
        return voxels;
    }
}
