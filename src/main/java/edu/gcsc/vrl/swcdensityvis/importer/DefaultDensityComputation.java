/// package's name
package edu.gcsc.vrl.swcdensityvis.importer;

/// imports
import edu.gcsc.vrl.densityvis.Density;
import edu.gcsc.vrl.densityvis.DensityResult;
import edu.gcsc.vrl.swcdensityvis.DensityUtil;
import edu.gcsc.vrl.swcdensityvis.importer.SWC.SWCCompartmentInformation;
import edu.gcsc.vrl.swcdensityvis.util.SWCUtility;
import eu.mihosoft.vrl.v3d.VTriangleArray;
import eu.mihosoft.vrl.v3d.jcsg.Cube;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

/**
 * @brief in case of increasing complexity, one can introduce the DensityComputationStrategyFactory as an abstract Factory, i . e. a Factory EdgeDensityComputationStrategy and TreeDensityComputationStrategy as well as a DefaultDensityComputationStrategy factory
 * @author stephan
 */
public class DefaultDensityComputation implements DensityComputationStrategy {

	@Override
	public Density computeDensity() {
		/// combine here DensityUtil and DensityImpl!
		int width = 10;
		int height = 10;
		int depth = 10;
		String choice = "axon";
		HashMap<String, ArrayList<SWCCompartmentInformation>> cells = new HashMap<String, ArrayList<SWCCompartmentInformation>>();
		Density density = DensityUtil.computeDensity(cells, width, height, depth, choice);
		double dim = Collections.max(Arrays.asList(SWCUtility.getDimensions(cells).x, SWCUtility.getDimensions(cells).y, SWCUtility.getDimensions(cells).z));
		return density;
	}
}
