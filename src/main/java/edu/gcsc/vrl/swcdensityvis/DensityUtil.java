/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.gcsc.vrl.swcdensityvis;

import edu.gcsc.vrl.densityvis.Density;
import edu.gcsc.vrl.densityvis.ImageVoxels;

/**
 *
 * @author stephan
 * @brief 
 */
public class DensityUtil {
    /**
     * Computes the density distribution in the specified image voxel data. The
     * average density of each voxel set is stored in the density information
     * object that is returned.
     *
     * @param imageVoxels image voxels (usually from .tif-stack)
     * @param voxelSetWidth width of the voxel set (in image coordinates)
     * @param voxelSetHeight height of the voxel set (in image coordinates)
     * @param voxelSetDepth depth of the voxel set (in image coordinates)
     * @return density information
     */
    public static Density computeDensity(ImageVoxels imageVoxels,
            int voxelSetWidth, int voxelSetHeight, int voxelSetDepth) {
        return new DensityImpl(
                imageVoxels, voxelSetWidth, voxelSetHeight, voxelSetDepth);
    }
}
