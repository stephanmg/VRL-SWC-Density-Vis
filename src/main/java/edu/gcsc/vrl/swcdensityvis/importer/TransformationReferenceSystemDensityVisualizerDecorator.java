/// package's name
package edu.gcsc.vrl.swcdensityvis.importer;

/// imports
import edu.gcsc.vrl.densityvis.Density;
import edu.gcsc.vrl.swcdensityvis.data.Compartment;
import eu.mihosoft.vrl.v3d.Shape3DArray;
import java.awt.Color;
import java.io.File;
import java.util.ArrayList;


/**
 * @brief transform to reference system density visualizer decorator
 * @author stephanmg <stephan@syntaktischer-zucker.de>
 */
public class TransformationReferenceSystemDensityVisualizerDecorator extends DensityVisualizerDecorator {
	public TransformationReferenceSystemDensityVisualizerDecorator(DensityVisualizable impl) {
		super(impl);
	}

	@Override
	public void setFiles(ArrayList<File> files) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void parse() {
		throw new UnsupportedOperationException("Not supported yet."); 
	}

	@Override
	public void parseStack() {
		throw new UnsupportedOperationException("Not supported yet."); 
	}

	@Override
	public Shape3DArray calculateGeometry() {
		throw new UnsupportedOperationException("Not supported yet."); 
	}

	@Override
	public void setContext(DensityComputationContext context) {
		throw new UnsupportedOperationException("Not supported yet."); 
	}

	@Override
	public void prepare(Color color, double scale, Compartment compartment) {
		throw new UnsupportedOperationException("Not supported yet."); 
	}

	@Override
	public Density computeDensity() {
		throw new UnsupportedOperationException("Not supported yet."); 
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
}
