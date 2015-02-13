/// package's name
package edu.gcsc.vrl.swcdensityvis.importer;

/**
 *
 * @author stephan
 */
public class SWCDensityVisualizer implements DensityVisualizable {
	private DensityComputationContext context = new DensityComputationContext(new DefaultDensityComputation());
	
	@Override
	public void parse() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void parseStack() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void getDimension() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void getBoundingBox() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void computeDensity() {
		context.executeDensityComputation();
	}

	@Override
	public void getDensity() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void getLineGraphGeometry() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void setContext(DensityComputationContext densityComputationContext) {
		context = densityComputationContext;
	}

}
