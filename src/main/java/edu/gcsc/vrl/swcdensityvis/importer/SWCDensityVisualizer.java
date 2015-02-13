/// package's name
package edu.gcsc.vrl.swcdensityvis.importer;

import edu.gcsc.vrl.densityvis.DensityResult;
import eu.mihosoft.vrl.v3d.Shape3DArray;

/**
 *
 * @author stephan
 */
public class SWCDensityVisualizer implements DensityVisualizable {
	private final DefaultDensityVisualizer defaultDensityVisualizer = new DefaultDensityVisualizer();

	@Override
	public void parse() {
		defaultDensityVisualizer.parse();
	}

	@Override
	public void parseStack() {
		defaultDensityVisualizer.parseStack();
	}

	@Override
	public void getDimension() {
		defaultDensityVisualizer.getDensity();
	}

	@Override
	public void getBoundingBox() {
		defaultDensityVisualizer.getBoundingBox();
	}

	@Override
	public DensityResult computeDensity() {
		return defaultDensityVisualizer.computeDensity();
	}

	@Override
	public void getDensity() {
		defaultDensityVisualizer.getDensity();
	}

	@Override
	public Shape3DArray getLineGraphGeometry() {
		return defaultDensityVisualizer.getLineGraphGeometry();
	}

	@Override
	public void setContext(DensityComputationContext densityComputationContext) {
		defaultDensityVisualizer.setContext(densityComputationContext);
	}
}
