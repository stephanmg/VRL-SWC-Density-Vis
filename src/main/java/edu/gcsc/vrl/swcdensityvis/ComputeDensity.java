/// package's name
package edu.gcsc.vrl.swcdensityvis;

/// imports
import edu.gcsc.vrl.densityvis.Density;
import edu.gcsc.vrl.densityvis.DensityResult;
import edu.gcsc.vrl.swcdensityvis.importer.DensityComputationContext;
import edu.gcsc.vrl.swcdensityvis.importer.DensityComputationStrategyFactoryProducer;
import edu.gcsc.vrl.swcdensityvis.importer.DensityVisualizable;
import edu.gcsc.vrl.swcdensityvis.importer.DensityVisualizableFactory;
import edu.gcsc.vrl.swcdensityvis.importer.XML.TreeDensityComputationStrategyXML;
import edu.gcsc.vrl.swcdensityvis.importer.XML.XMLDensityUtil;
import edu.gcsc.vrl.swcdensityvis.importer.XML.XMLDensityVisualizer;
import eu.mihosoft.vrl.annotation.ComponentInfo;
import eu.mihosoft.vrl.annotation.MethodInfo;
import eu.mihosoft.vrl.annotation.OutputInfo;
import eu.mihosoft.vrl.annotation.ParamGroupInfo;
import eu.mihosoft.vrl.annotation.ParamInfo;
import eu.mihosoft.vrl.v3d.VTriangleArray;
import eu.mihosoft.vrl.v3d.jcsg.Cube;
import eu.mihosoft.vrl.v3d.jcsg.Vector3d;
import java.awt.Color;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import javax.vecmath.Vector3f;

/**
 * @brief computes the density
 */
@ComponentInfo(name = "ComputeDensity", category = "Neuro/SWC-Density-Vis")
public class ComputeDensity implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	@MethodInfo(valueStyle = "multi-out", interactive = false)
	@OutputInfo(
		style = "multi-out",
		elemNames = {"Density", "Geometry"},
		elemTypes = {DensityResult.class, File[].class}
	)
	public Object[] compute(
		//[\"swc\"]; 
		@ParamGroupInfo(group = "Common options|true|Compute the density for the image (stack); Folder|true|Input folder")
		@ParamInfo(name = "(Stack) Input folder", typeName = "Location of SWC files", style = "load-folder-dialog", options = "endings=[\"swc\", \"xml\", \"asc\"]; description=\"SWC, XML or ASC files (.swc, .xml, .asc)\"") File folder,
		@ParamInfo(name = "(Single) Consensus Geometry File", typeName = "Consensus geometry", style = "load-dialog", options="endings=[\"swc\", \"xml\", \"asc\"]; description=\"SWC, XML or ASC files (.swc, .xml, .asc)\"") File consensus,
		@ParamInfo(name = "File type", typeName = "Filetype", style = "selection", options = "value=[\"SWC\", \"XML\", \"ASC\"]") String selection,
		@ParamGroupInfo(group = "Common options|true|Compute the density for the image (stack); Dimensions|true|Dimensions")
		@ParamInfo(name = "Width", typeName = "Width of sampling cube", style = "slider", options = "min=1;max=100") int width,
		@ParamGroupInfo(group = "Common options|true|Compute the density for the image (stack); Dimensions|true|Dimensions")
		@ParamInfo(name = "Height", typeName = "Height of sampling cube", style = "slider", options = "min=1;max=100") int height,
		@ParamGroupInfo(group = "Common options|true|Compute the density for the image (stack); Dimensions|true|Dimensions")
		@ParamInfo(name = "Depth", typeName = "Depth of sampling cube", style = "slider", options = "min=1;max=100") int depth,
		@ParamGroupInfo(group = "Advanced options|true|Compute the density for the image (stack); Compartment|true|Compartment")
		@ParamInfo(name = "Type", typeName = "Compartment", style = "selection", options = "value=[\"all\", \"undefined\", \"axon\", \"(basal) dendrite\", \"apical dendrite\", \"fork point\", \"end point\", \"custom\"]") String choice
	) {
		File[] swcFiles = null;
		swcFiles = folder.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".swc")
					|| name.endsWith(".xml")
					|| name.endsWith(".asc");
			}
		});
		
		ArrayList<File> files = new ArrayList<File>(Arrays.asList(swcFiles));
		if (consensus != null) {
			files.add(consensus);
		}
		
		if (files.isEmpty()) {
			eu.mihosoft.vrl.system.VMessage.error("ComputeDensity", "At least one input file must be specified (i. e. at least one geometry file in the stack folder or a consensus geometry!)");
		} else {
		

		DensityVisualizableFactory factory = new DensityVisualizableFactory();
		DensityVisualizable visualizer = factory.getDensityVisualizer(selection);
		DensityComputationContext densityComputationContext = new DensityComputationContext();
		densityComputationContext.setDensityComputationStrategy(new DensityComputationStrategyFactoryProducer().getDefaultAbstractDensityComputationStrategyFactory().getDefaultComputationStrategy(selection));
		visualizer.setContext(densityComputationContext);

		XMLDensityVisualizer xmlDensityVisualizer;
		xmlDensityVisualizer = new XMLDensityVisualizer(XMLDensityUtil.getDefaultDiameterImpl());
		
		xmlDensityVisualizer.setContext(new DensityComputationContext(new TreeDensityComputationStrategyXML()));
		/**
		 * @todo setFiles could also be moved in the interface
		 */
		/**
		 * @todo the consensus file should be the geometry, but should not be considered in the density vis of the stack
		 */
		
		xmlDensityVisualizer.setFiles(files);
		/// don't scale the geometry in the first place! only afterwards teh density vis and the geometry (line-graph) is rescaled for java3d vis!
		xmlDensityVisualizer.prepare(Color.yellow, 1);

		/**
		 * @todo if we calculate geometry in the ComputeDensity it will
		 * get cached somehow! (this is by our implementation as this is
		 * expensive, but then leads here to multiple parent branch in
		 * java3d!)
		 */
		/// parse the files and compute density
		xmlDensityVisualizer.parseStack();
		Density density = xmlDensityVisualizer.computeDensity();
		
		/// get dim and center
		Vector3f dim = (Vector3f) xmlDensityVisualizer.getDimension();
		Vector3f center = (Vector3f) xmlDensityVisualizer.getCenter(); /* @todo this seems to be not correct */

		/// density must respect new rescaled geometry and therefore fit in cuboid
		/* @todo the vta is way to big, since the geometry/density get's rescaled with the VisUtil.
		 we could however rescale the cube too, or we just omit the cube for rendering,
		 since it isn't necessary in fact...
		 */
		
		/// bounding box of line-graph geometry only!
		VTriangleArray vta = new Cube(new Vector3d(center.x*0.01, center.y*0.01, center.z*0.01), new Vector3d(dim.x*0.01, dim.y*0.01, dim.z*0.01)).toCSG().toVTriangleArray();
		return new Object[]{new DensityResult(density, vta), swcFiles};
		}
		return new Object[]{new DensityResult(null, null), swcFiles};
	}
}
