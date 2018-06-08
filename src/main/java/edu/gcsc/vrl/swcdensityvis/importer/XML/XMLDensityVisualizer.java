/// package's name
package edu.gcsc.vrl.swcdensityvis.importer.XML;

/// imports
import edu.gcsc.vrl.densityvis.Density;
import edu.gcsc.vrl.swcdensityvis.importer.DensityComputationContext;
import edu.gcsc.vrl.swcdensityvis.importer.DensityData;
import eu.mihosoft.vrl.reflection.Pair;
import eu.mihosoft.vrl.v3d.Shape3DArray;
import java.io.File;
import java.util.ArrayList;
import javax.vecmath.Vector3f;

/**
 * @brief the default XML density visualizer
 * @author stephanmg <stephan@syntaktischer-zucker.de>
 */
public class XMLDensityVisualizer extends XMLDensityVisualizerBase {
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
	@Override
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

	/**
	 * @brief calculates the bounding box of the geometry to visualize
	 * @return 
	 */
	public Pair<Vector3f, Vector3f> getBoundingBox() {
		Vector3f dim = (Vector3f) this.getDimension();
		Vector3f center = (Vector3f) this.getCenter();
		
		return new Pair<Vector3f, Vector3f>
			(
				new Vector3f(
					    center.x - dim.x / 2.0f, 
					    center.y - dim.y / 2.0f,
				            center.z - dim.z / 2.0f
			),
				new Vector3f(
					    center.x + dim.x / 2.0f, 
					    center.y + dim.y / 2.0f,
				            center.z + dim.z / 2.0f
			)
		);
	}

}
