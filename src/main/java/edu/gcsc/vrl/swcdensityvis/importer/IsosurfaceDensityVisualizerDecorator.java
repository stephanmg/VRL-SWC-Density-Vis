/// package's name
package edu.gcsc.vrl.swcdensityvis.importer;

/// imports
import edu.gcsc.vrl.densityvis.Density;
import edu.gcsc.vrl.densityvis.VoxelSet;
import edu.gcsc.vrl.swcdensityvis.data.Compartment;
import eu.mihosoft.vrl.v3d.Shape3DArray;
import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @brief isosurface DensityVisualizer decorator
 * @author stephanmg <stephan@syntaktischer-zucker.de>
 */
public class IsosurfaceDensityVisualizerDecorator extends DensityVisualizerDecorator {
	/// isosurface definitions
	private int average;
	private int deviation;
	
	/**
	 * @brief 
	 * @param impl
 	 * @see {@link DensityVisualizerDecorator}
	 */
	public IsosurfaceDensityVisualizerDecorator(DensityVisualizable impl) {
		super(impl);
	}

	/**
	 * @brief 
	 * @param impl
	 * @param average
	 */
	public IsosurfaceDensityVisualizerDecorator(DensityVisualizable impl, int average) {
		super(impl);
		this.average = average;
	}
	
	/**
	 * @brief 
	 * @param impl
	 * @param average
	 * @param deviation
	 */
	public IsosurfaceDensityVisualizerDecorator(DensityVisualizable impl, int average, int deviation) {
		super(impl);
		this.average = average;
		this.deviation = deviation;
	}
	
	/**
	 * @see DensityVisualizable#parse()
	 */
	@Override
	public void parse() {
		super.getImpl().parse();
	}

	/**
	 * @see DensityVisualizable#parseStack()
	 */
	@Override
	public void parseStack() {
		super.getImpl().parseStack();
	}

	/**
	 * @see DensityVisualizable#calculateGeometry() 
	 * @return 
	 */
	@Override
	public Shape3DArray calculateGeometry() {
		return super.getImpl().calculateGeometry();
	}

	/**
	 * @see DensityVisualizable#setContext(edu.gcsc.vrl.swcdensityvis.importer.DensityComputationContext) 
	 * @param context 
	 */
	@Override
	public void setContext(DensityComputationContext context) {
		super.getImpl().setContext(context);
	}

	/**
	 * @brief excludes all voxels whose density value is below threshold
	 * The member isoSurfaces specifies a threshold for the density value
	 * of the voxels, if the voxel's density value is below this threshold
	 * the corresponding voxel will be removed from the voxel set and is
	 * therefore omitted in the density visualization
	 * @see DensityVisualizable#computeDensity() 
	 * @return Density
	 */
	@Override
	public Density computeDensity() {
		/// density
		Density density = super.getImpl().computeDensity();
		List<? extends VoxelSet> voxels = density.getVoxels();

		/// save indices of voxels matching threshold criterion
		for (int i = 0; i < voxels.size()-1; i++) {
			VoxelSet vs = voxels.get(i);
			Double voxelVal = vs.getValue();
			Double lowerBnd = (double) average-deviation;
			Double upperBnd = (double) average+deviation;
			
			if ( ! (Double.compare(voxelVal, lowerBnd) >= 0) && (Double.compare(voxelVal, upperBnd) <= 0) ) {
				density.getVoxels().remove(vs);
			}
		}
		
		return density;
	}

	/**
	 * @see DensityVisualizable#setDensityData(edu.gcsc.vrl.swcdensityvis.importer.DensityData) 
	 * @param data 
	 */
	@Override
	public void setDensityData(DensityData data) {
		super.getImpl().setDensityData(data);
	}

	/**
	 * @see DensityVisualizable#getDimension() 
	 * @return 
	 */
	@Override
	public Object getDimension() {
		return super.getImpl().getDimension();
	}

	/**
	 * @see DensityVisualizable#getCenter() 
	 * @return 
	 */
	@Override
	public Object getCenter() {
		return super.getImpl().getCenter();
	}

	/**
	 * @see DensityVisualizable#setFiles(java.util.ArrayList) 
	 * @param files 
	 */
	@Override
	public void setFiles(ArrayList<File> files) {
		super.getImpl().setFiles(files);
	}

	/**
	 * @see DensityVisualizable#prepare(java.awt.Color, double, edu.gcsc.vrl.swcdensityvis.data.Compartment) 
	 * @param color
	 * @param scale
	 * @param compartment 
	 */
	@Override
	public void prepare(Color color, double scale, Compartment compartment) {
		super.getImpl().prepare(color, scale, compartment);
	}
}
