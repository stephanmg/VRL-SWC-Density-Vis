/// package's name
package edu.gcsc.vrl.swcdensityvis.importer;

import edu.gcsc.vrl.densityvis.Density;
import edu.gcsc.vrl.swcdensityvis.data.Compartment;
import eu.mihosoft.vrl.v3d.Shape3DArray;
import java.awt.Color;
import java.io.File;
import java.util.ArrayList;

/**
 * @brief the default density visualizer
 * Note: A general implementation may not be possible
 * @author stephan <stephan@syntaktischer-zucker.de>
 */
public class DefaultDensityVisualizer implements DensityVisualizable {
	private DensityComputationContext context = new DensityComputationContext(new DefaultDensityComputation());

	@Override
	public void parse() {
		throw new UnsupportedOperationException("Not supported yet."); 
	}

	@Override
	public void parseStack() {
		throw new UnsupportedOperationException("Not supported yet."); 
	}

	@Override
	public Density computeDensity() {
		DefaultDensityData data = new DefaultDensityData();
		this.context.setDensityData(data);
		return context.executeDensityComputation();
	}

	@Override
	public Shape3DArray calculateGeometry() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void setContext(DensityComputationContext context) {
		this.context = context;
	}

	@Override
	public void setDensityData(DensityData data) {
		throw new UnsupportedOperationException("Not supported yet."); 
	}

	@Override
	public Object[] getDimension() {
		throw new UnsupportedOperationException("Not supported yet."); 
	}

	@Override
	public Object getCenter() {
		throw new UnsupportedOperationException("Not supported yet."); 
	}

	@Override
	public void setFiles(ArrayList<File> files) {
		throw new UnsupportedOperationException("Not supported yet."); 
	}

	@Override
	public void prepare(Color color, double scale, Compartment compartment) {
		throw new UnsupportedOperationException("Not supported yet."); 
	}
	
	@Override
	public Object getBoundingBox() {
		throw new UnsupportedOperationException("Not supported yet."); 
	}
}
