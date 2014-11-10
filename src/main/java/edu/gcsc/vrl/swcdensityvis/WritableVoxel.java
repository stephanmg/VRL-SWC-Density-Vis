/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.gcsc.vrl.swcdensityvis;
import edu.gcsc.vrl.densityvis.VoxelSet;

/**
 * This interface defines write access for voxel sets.
 * @see VoxelSet
 * @author
 * @brief 
 */
interface WritableVoxel extends VoxelSet {

    /**
     * @param depth the depth to set (in image coordinates)
     */
    void setDepth(int depth);

    /**
     * @param height the height to set (in image coordinates)
     */
    void setHeight(int height);

    /**
     * @param value the value to set (in image coordinates)
     */
    void setValue(double value);

    /**
     * @param width the width to set (in image coordinates)
     */
    void setWidth(int width);

    /**
     * @param x the x to set (in image coordinates)
     */
    void setX(int x);

    /**
     * @param y the y to set (in image coordinates)
     */
    void setY(int y);

    /**
     * @param z the z to set (in image coordinates)
     */
    void setZ(int z);
    
}
