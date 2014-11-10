/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.gcsc.vrl.swcdensityvis;

/*
 * @brief
 * @author 
 */
class VoxelImpl implements WritableVoxel {

    private int x;
    private int y;
    private int z;
    private int width;
    private int height;
    private int depth;
    private double value;

    /**
     * Constructor.
     */
    public VoxelImpl() {
    }

    /**
     * Constructor.
     *
     * @param x x coordinate (in image coordinates)
     * @param y y coordinate (in image coordinates)
     * @param z z coordinate (in image coordinates)
     * @param width width of the set (in image coordinates)
     * @param height height of the set (in image coordinates)
     * @param depth depth of the set (in image coordinates)
     * @param value the value associated with this set (e.g. average voxel
     * density)
     */
    public VoxelImpl(int x, int y, int z, int width, int height, int depth, double value) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.width = width;
        this.height = height;
        this.depth = depth;
        this.value = value;
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public void setX(int x) {
        this.x = x;
    }

    @Override
    public int getY() {
        return y;
    }

    @Override
    public void setY(int y) {
        this.y = y;
    }

    @Override
    public int getZ() {
        return z;
    }

    @Override
    public void setZ(int z) {
        this.z = z;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public void setWidth(int width) {
        this.width = width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public int getDepth() {
        return depth;
    }

    @Override
    public void setDepth(int depth) {
        this.depth = depth;
    }

    @Override
    public double getValue() {
        return value;
    }

    @Override
    public void setValue(double value) {
        this.value = value;
    }
}
