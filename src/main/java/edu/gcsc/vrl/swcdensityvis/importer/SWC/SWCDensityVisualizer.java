/// package's name
package edu.gcsc.vrl.swcdensityvis.importer.SWC;

/// imports
import edu.gcsc.vrl.densityvis.Density;
import edu.gcsc.vrl.swcdensityvis.data.Compartment;
import edu.gcsc.vrl.swcdensityvis.importer.DefaultDensityVisualizer;
import edu.gcsc.vrl.swcdensityvis.importer.DensityComputationContext;
import edu.gcsc.vrl.swcdensityvis.importer.DensityData;
import edu.gcsc.vrl.swcdensityvis.importer.DensityVisualizable;
import eu.mihosoft.vrl.v3d.Shape3DArray;
import java.awt.Color;
import java.io.File;
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

	@Override
	public Object[] getDimension() {
		return defaultDensityVisualizer.getDimension();
	}

	@Override
	public Object getCenter() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void setFiles(ArrayList<File> files) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void prepare(Color color, double scale, Compartment compartment) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

}
