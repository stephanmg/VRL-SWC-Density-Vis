/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.gcsc.vrl.swcdensityvis;

import java.io.File;

/**
 * Image voxels contains the complete voxel data of stack of SWC files
 * @todo implement
 * @author stephan
 */
public class ImageVoxels {

    private int[][][] data;
    private int width;
    private int height;
    private int depth;
    
    /**
     * Constructor.
     * @param stack image stack
     * @param data voxel data
     * @param width width (in image coordinates)
     * @param height height (in image coordinates)
     * @param depth depth (in image coordinates)
     */
    private ImageVoxels() {
    }

    /**
     * Loads an image stack from file.
     * @param image image file
     * @return an <code>ImageVoxels</code> object from file
     */
    public static ImageVoxels load(File image) {
	return new ImageVoxels();
    }

    /**
     * Returns the voxel data.
     * @return voxel data
     */
    public int[][][] getData() {
        return data;
    }
    
    /**
     * Returns the value of the specified voxel.
     * @param x x (in image coordinates)
     * @param y y (in image coordinates)
     * @param z z (in image coordinates)
     * @return value of the specified voxel
     */
    public double getVoxel(int x, int y, int z) {
	return 0;
    }

    /**
     * @return the width (in image coordinates)
     */
    public int getWidth() {
        return width;
    }

    /**
     * @return the height (in image coordinates)
     */
    public int getHeight() {
        return height;
    }

    /**
     * @return the depth (in image coordinates)
     */
    public int getDepth() {
        return depth;
    }
}
