/// package's name
package edu.gcsc.vrl.swcdensityvis;

/// imports
import edu.gcsc.vrl.jfreechart.*;
import edu.gcsc.vrl.densityvis.*;
import eu.mihosoft.vrl.annotation.ComponentInfo;
import eu.mihosoft.vrl.annotation.OutputInfo;
import eu.mihosoft.vrl.annotation.ParamInfo;
import eu.mihosoft.vrl.math.Trajectory;
import java.util.Collection;

@ComponentInfo(name = "ComputeSWCDistance", category = "Neuro/SWC-Density-Vis")
public class ComputeSWCDistance implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	// add your code here
	@OutputInfo(name = "Trajectory")
	public Trajectory computeTraj(
		@ParamInfo(name = "Density", typeName = "Density of the image (stack)", style = "default", options = "") DensityResult dens,
		@ParamInfo(name = "Min Density", typeName = "Minimum density for visualization", style = "slider", options = "value=20;min=10;max=100;") int percentage) {

		Iterable<Distance> distances
			= DistanceUtil.computeDistance(
				dens.getDensity(), GeometryUtil.vTriangleArray2PointArray(dens.getGeometry()), percentage);

		Trajectory t = new Trajectory();
		t.setLabel("Distances");
		t.setxAxisLabel("Voxel");
		t.setyAxisLabel("Bounding distance");

		double i = 0;
		for (Distance d : distances) {
			t.add(i, d.getDistance());
			i++;
		}

		return t;
	}

	// add your code here
	@OutputInfo(name = "Data")
	public HistogramData computeHist(
		@ParamInfo(name = "Density", style = "default", options = "") DensityResult dens,
		@ParamInfo(name = "Min Density", style = "slider", options = "value=20;min=10;max=100;") int percentage) {

		Collection<Distance> distances
			= DistanceUtil.computeDistance(
				dens.getDensity(), GeometryUtil.vTriangleArray2PointArray(dens.getGeometry()), percentage);

		return VisUtil.distance2Histogram(distances, 30);
	}
}
