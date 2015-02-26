/// package's name
package edu.gcsc.vrl.swcdensityvis.importer.XML;

/// imports
import edu.gcsc.vrl.densityvis.Density;
import edu.gcsc.vrl.swcdensityvis.importer.DensityComputationContext;
import edu.gcsc.vrl.swcdensityvis.importer.DensityData;
import eu.mihosoft.vrl.v3d.Shape3DArray;
import java.awt.Color;
import java.io.File;
import java.util.ArrayList;

/**
 *
 * @author stephan
 */
public class XMLDensityVisualizer extends XMLDensityVisualizerBase {

	/**
	 *
	 * @param color
	 * @param scalingFactor
	 */
	public void prepare(Color color, double scalingFactor) {
		impl.setLineGraphColor(color);
		impl.setScalingFactor(scalingFactor);
	}

	/**
	 *
	 * @param impl
	 */
	public XMLDensityVisualizer(XMLDensityVisualizerImplementable impl) {
		super(impl);
	}

	/**
	 *
	 */
	public XMLDensityVisualizer() {
		super(XMLDensityUtil.getDefaultImpl());
	}

	/**
	 *
	 */
	@Override
	public void parse() {
		this.impl.parse();
	}

	/**
	 *
	 */
	@Override
	public void parseStack() {
		this.impl.parseStack();
	}

	/**
	 * @return
	 */
	@Override
	public Object getDimension() {
		return this.impl.getDimension();
	}

	/**
	 *
	 * @return
	 */
	@Override
	public Shape3DArray calculateGeometry() {
		return this.impl.calculateGeometry();
	}

	/**
	 * @return
	 */
	@Override
	public Density computeDensity() {
		return this.impl.computeDensity();
	}

	/**
	 * @param context
	 */
	@Override
	public void setContext(DensityComputationContext context) {
		this.impl.setContext(context);
	}

	/**
	 *
	 * @param files
	 */
	public void setFiles(ArrayList<File> files) {
		this.impl.setFiles(files);
	}

	/**
	 * 
	 * @param data 
	 */
	@Override
	public void setDensityData(DensityData data) {
		this.impl.setDensityData(data);
	}

	/**
	 * 
	 * @return 
	 */
	@Override
	public Object getCenter() {
		return this.impl.getCenter();
	}

}
