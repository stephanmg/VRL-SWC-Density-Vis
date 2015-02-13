/// package's name
package edu.gcsc.vrl.swcdensityvis.importer;

/**
 *
 * @author stephan
 */
public class XMLDensityVisualizer implements DensityVisualizable {
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
	public void computeDensity() {
		defaultDensityVisualizer.computeDensity();
	}

	@Override
	public void getDensity() {
		defaultDensityVisualizer.getDensity();
	}

	@Override
	public void getLineGraphGeometry() {
		defaultDensityVisualizer.getLineGraphGeometry();
	}

	@Override
	public void setContext(DensityComputationContext densityComputationContext) {
		defaultDensityVisualizer.setContext(densityComputationContext);
	}
}
