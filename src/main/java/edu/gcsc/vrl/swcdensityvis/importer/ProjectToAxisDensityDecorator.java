package edu.gcsc.vrl.swcdensityvis.importer;

import edu.gcsc.vrl.densityvis.Density;
import edu.gcsc.vrl.densityvis.VoxelSet;
import eu.mihosoft.vrl.math.Trajectory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @brief this decorator projects density data to an axis, e.g. x, y or z axis
 * The original density won't change however the projected data will be stored
 * @author stephanmg <stephan@syntaktischer-zucker.de>
 */

public class ProjectToAxisDensityDecorator extends DensityVisualizerDecorator {
	/// store trajectory
	private	final HashMap<Integer, Double> xyValues = new HashMap<Integer, Double>(); 
	private String axis = "";
	/**
	 * @brief ctor
	 * @param impl 
 	 * @see {@link DensityVisualizerDecorator}
	 */
	public ProjectToAxisDensityDecorator(DensityVisualizable impl) {
		super(impl);
	}

	/**
	 * @brief projects to a specified axis (e.g. x, y or z)
	 * @see DensityVisualizable#computeDensity() 
	 * @return Density
	 */
	@Override
	public Density computeDensity() {
		Density density = super.impl.computeDensity();
		List<? extends VoxelSet> voxels = density.getVoxels();
		HashMap<Integer, ArrayList<Double>> values = new HashMap<Integer, ArrayList<Double>>();
		
		for (int i = 0; i < voxels.size(); i++) {
			if (axis.equalsIgnoreCase("x")) { values.get(voxels.get(i).getX()).add(voxels.get(i).getValue()); }
			if (axis.equalsIgnoreCase("y")) { values.get(voxels.get(i).getY()).add(voxels.get(i).getValue()); }
			if (axis.equalsIgnoreCase("z")) { values.get(voxels.get(i).getY()).add(voxels.get(i).getValue()); }
		}

		xyValues.clear();
		
		for (Map.Entry<Integer, ArrayList<Double>> entry : values.entrySet()) {
			double sum = 0;
			for (Double d : entry.getValue()) {
				sum += d;
			}
			xyValues.put(entry.getKey(), sum);
		}
		return density;
	}

	/**
	 * @brief returns the projected values for plotting
	 * @return 
	 */
	public Trajectory getAxis(String axis) {
		this.axis = axis;
		computeDensity();
		Trajectory trajectory = new Trajectory(axis);
		for (Map.Entry<Integer, Double> entry : xyValues.entrySet()) {
			trajectory.add(entry.getKey(), entry.getValue());
		}
		return trajectory;
	}
}
