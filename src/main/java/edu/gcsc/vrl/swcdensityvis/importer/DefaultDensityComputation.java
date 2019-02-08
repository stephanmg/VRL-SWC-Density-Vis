package edu.gcsc.vrl.swcdensityvis.importer;

import edu.gcsc.vrl.densityvis.Density;
import edu.gcsc.vrl.swcdensityvis.importer.SWC.DensityUtil;
import edu.gcsc.vrl.swcdensityvis.importer.SWC.SWCCompartmentInformation;
import edu.gcsc.vrl.swcdensityvis.util.SWCUtility;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

/**
 * @brief in case of increasing complexity, one can introduce the DensityComputationStrategyFactory as an abstract Factory, i . e. a Factory EdgeDensityComputationStrategy and TreeDensityComputationStrategy as well as a DefaultDensityComputationStrategy factory
 * @author stephan <stephan@syntaktischer-zucker.de>
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

	@Override
	public void setDensityData(DensityData data) {
		throw new UnsupportedOperationException("Not supported yet."); 
	}

	@Override
	public Object getDimension() {
		throw new UnsupportedOperationException("Not supported yet."); 
	}

	@Override
	public Object getCenter() {
		throw new UnsupportedOperationException("Not supported yet."); 
	}
	
	@Override
	public Object getBoundingBox() {
		throw new UnsupportedOperationException("Not supported yet."); 
	}
}
