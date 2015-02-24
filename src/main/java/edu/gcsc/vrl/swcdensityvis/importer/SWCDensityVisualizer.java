/// package's name
package edu.gcsc.vrl.swcdensityvis.importer;

/// imports
import edu.gcsc.vrl.densityvis.Density;
import eu.mihosoft.vrl.v3d.Shape3DArray;
import java.util.ArrayList;

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
		defaultDensityVisualizer.getDimension();
	}

	@Override
	public void getBoundingBox() {
		defaultDensityVisualizer.getBoundingBox();
	}

	@Override
	public Density computeDensity() {
		return defaultDensityVisualizer.computeDensity();
	}

	@Override
	public Shape3DArray calculateGeometry() {
		return defaultDensityVisualizer.calculateGeometry();
	}

	@Override
	public void setContext(DensityComputationContext densityComputationContext) {
		defaultDensityVisualizer.setContext(densityComputationContext);
	}

	@Override
	public void setDensityData(DensityData data) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

}
