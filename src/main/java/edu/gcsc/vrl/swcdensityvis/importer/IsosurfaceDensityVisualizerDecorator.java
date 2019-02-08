package edu.gcsc.vrl.swcdensityvis.importer;

import edu.gcsc.vrl.densityvis.Density;
import edu.gcsc.vrl.densityvis.VoxelSet;
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
		Density density = super.impl.computeDensity();
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
}
