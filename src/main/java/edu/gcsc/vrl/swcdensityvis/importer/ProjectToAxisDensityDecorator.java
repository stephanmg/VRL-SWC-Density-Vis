package edu.gcsc.vrl.swcdensityvis.importer;

import edu.gcsc.vrl.densityvis.Density;
import edu.gcsc.vrl.densityvis.VoxelSet;
import eu.mihosoft.vrl.math.Trajectory;
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
	private	final HashMap<Integer, Double> values = new HashMap<Integer, Double>(); 
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
	 * Note: One could use reflection to shorten the if guard statements like:
	 	>> Method method = voxels.get(i).getClass().getMethod("get" + axis.toUpperCase());
		>> method.invoke(voxels.get(i));
	 * @return Density
	 */
	@Override
	public Density computeDensity() {
		Density density = super.impl.computeDensity();
		List<? extends VoxelSet> voxels = density.getVoxels();

		System.err.println("Number of voxels: " + voxels.size());
		
		for (int i = 0; i < voxels.size(); i++) {
			/// x axis
			if (axis.equalsIgnoreCase("x")) { 
				if (values.get(voxels.get(i).getX()) == null) {
					values.put(voxels.get(i).getX(), voxels.get(i).getValue());
				} else {
					values.put(voxels.get(i).getX(), values.get(voxels.get(i).getX()) + voxels.get(i).getValue());
				}
			}

			/// y axis
			if (axis.equalsIgnoreCase("y")) { 
				if (values.get(voxels.get(i).getY()) == null) {
					values.put(voxels.get(i).getY(), voxels.get(i).getValue());
				} else {
					values.put(voxels.get(i).getY(), values.get(voxels.get(i).getY()) + voxels.get(i).getValue());
				}
			}

			/// z axis
			if (axis.equalsIgnoreCase("z")) { 
				if (values.get(voxels.get(i).getZ()) == null) {
					values.put(voxels.get(i).getZ(), voxels.get(i).getValue());
				} else {
					values.put(voxels.get(i).getZ(), values.get(voxels.get(i).getZ()) + voxels.get(i).getValue());
				}
			}
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
		for (Map.Entry<Integer, Double> entry : values.entrySet()) {
			trajectory.add(entry.getKey(), entry.getValue());
			System.err.println("x: " + entry.getKey() + ", y:" + entry.getValue());
		}
		return trajectory;
	}
}
