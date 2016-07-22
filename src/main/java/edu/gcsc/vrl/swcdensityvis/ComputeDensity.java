/// package's name
package edu.gcsc.vrl.swcdensityvis;

/// imports
import edu.gcsc.vrl.densityvis.Density;
import edu.gcsc.vrl.densityvis.DensityResult;
import edu.gcsc.vrl.swcdensityvis.importer.*;
import edu.gcsc.vrl.swcdensityvis.importer.XML.*;
import edu.gcsc.vrl.swcdensityvis.data.Compartment;
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
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import javax.vecmath.Vector3f;
import lombok.extern.log4j.Log4j;
import org.apache.commons.io.FilenameUtils;

/**
 * @brief ComputeDensity component
 * @author stephan <stephan@syntaktischer-zucker.de>
 */
@Log4j
@ComponentInfo(name = "ComputeDensity", category = "Neuro/SWC-Density-Vis")
public class ComputeDensity implements Serializable {
	/// sVUID
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
		@ParamInfo(name = "(Single) Consensus Geometry File", typeName = "Consensus geometry", style = "load-compartment-dialog", options="file_tag=\"geometry\"; endings=[\"swc\", \"xml\", \"asc\"]; description=\"SWC, XML or ASC files (.swc, .xml, .asc)\"") File consensus,
		
		@ParamGroupInfo(group = "Common options|true|Compute the density for the image (stack); Dimensions|true|Dimensions")
		@ParamInfo(name = "Width", typeName = "Width of sampling cube", style = "slider", options = "min=1;max=100") int width,
		@ParamGroupInfo(group = "Common options|true|Compute the density for the image (stack); Dimensions|true|Dimensions")
		@ParamInfo(name = "Height", typeName = "Height of sampling cube", style = "slider", options = "min=1;max=100") int height,
		@ParamGroupInfo(group = "Common options|true|Compute the density for the image (stack); Dimensions|true|Dimensions")
		@ParamInfo(name = "Depth", typeName = "Depth of sampling cube", style = "slider", options = "min=1;max=100") int depth,
		@ParamGroupInfo(group = "Advanced options|true|Compute the density for the image (stack); Compartment|true|Compartment")
		@ParamInfo(name = "Compartment Types", typeName = "Compartment Type", style="default", options = "file_tag=\"geometry\"")
		Compartment compartment,
			
		@ParamGroupInfo(group = "Advanced options|true|Compute the density for the image (stack); Isosurfaces|true|Isosurfaces")
		@ParamInfo(name = "Visible?", style="default", options="value=true") 
		boolean bIsoSurfaces,
		
		@ParamGroupInfo(group = "Advanced options|true|Compute the density for the image (stack); Isosurfaces|true|Isosurfaces")
		@ParamInfo(name = "Average [%]", style = "slider", options = "min=0;max=100;value=50") 
		int mAverage,
		
		@ParamInfo(name = "Deviation [%]", style = "slider", options = "min=0;max=100;value=1") 
		int mDeviation

	) {
		File[] stackFiles = folder.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return    name.endsWith(".swc")
				       || name.endsWith(".xml")
				       || name.endsWith(".asc");
			}
		});
		
		/// consider all files from stack and the consensus geometry on top
		ArrayList<File> files = new ArrayList<File>(Arrays.asList(stackFiles));
		if (consensus != null) { files.add(consensus); }
		
		/// if no files are selected, abort execution and print error
		if (files.isEmpty()) {
			eu.mihosoft.vrl.system.VMessage.error(
				"ComputeDensity", 
				"At least one input file must be specified "
				+ "(i. e. at least one geometry file in the "
				+ "stack folder or a consensus geometry should "
				+ "be available!)"
			);
		} else {
			/// get fileType from selected consensus geometry
			String fileType = FilenameUtils.getExtension(consensus.toString());
			DensityVisualizableFactory factory = new DensityVisualizableFactory();
			
			/// instantiate the strategy and the visualizer ...
			DensityVisualizable visualizer = factory.getDensityVisualizer(fileType); 
			DensityComputationContext densityComputationContext = new DensityComputationContext();
			densityComputationContext.setDensityComputationStrategy(new DensityComputationStrategyFactoryProducer().getDefaultAbstractDensityComputationStrategyFactory().getDefaultComputationStrategy(fileType)); 
			visualizer.setContext(densityComputationContext);
			
			/// for now manually!
			DensityVisualizable xmlDensityVisualizer = new XMLDensityVisualizer(XMLDensityUtil.getDefaultDiameterImpl());
			xmlDensityVisualizer.setContext(new DensityComputationContext(new TreeDensityComputationStrategyXML(width, height, depth)));
			xmlDensityVisualizer.setFiles(files);
			log.info("Files for density visualization: " + files);
			
			/**
			 * @note 
			 * don't scale the geometry in the first place for the
			 * density calculation - only scale in the DensityVisualization
			 * later for visualization of the geometry
			 * 
			 * @note
			 * if we exclude compartments here, then they are omitted 
			 * for the density calculation - this may not be what we want
			 * also the line-graph geometry (consensus geometry) must be
			 * visualized and we exclude these compartments too
			 */
			xmlDensityVisualizer.prepare(Color.yellow, 1, compartment);

			/// decorate with isosurfaces if user wishes
			if (bIsoSurfaces) {
				log.info("Isosurface density visualizer decorator used.");
				xmlDensityVisualizer = new IsosurfaceDensityVisualizerDecorator(xmlDensityVisualizer, mAverage, mDeviation);
			} else {
				log.info("No isosurface density visualizer decorator used.");
			}
			
			/**
			 * @note 
			 * if we calculate geometry in the ComputeDensity it will
			 * get cached (this is by our implementation as this is a very
			 * expensive task, but then this can lead to multiple parent
			 * branches in certain circumstances - provide a fix in future)
			 */
			xmlDensityVisualizer.parseStack(); /// parse all files
			Density density = xmlDensityVisualizer.computeDensity(); /// compute the density
			
			/// get dim and center
			Vector3f dim = (Vector3f) xmlDensityVisualizer.getDimension();
			Vector3f center = (Vector3f) xmlDensityVisualizer.getCenter(); 

			/// this is the bounding box of the line-graph geometry only,
			/// this bounding box is different from the bounding box of the
			/// density voxels (which we don't display - but can be guessed
			/// if the density is visualized!)
			log.info("DIMENSION (of subset of geometry): " + dim);
			log.info("CENTER (of subset of geometry): " + center);
			VTriangleArray vta = new Cube(
				new Vector3d(center.x*0.01, center.y*0.01, center.z*0.01), 
				new Vector3d(dim.x*0.01, dim.y*0.01, dim.z*0.01))
				.toCSG().toVTriangleArray();
			
			return new Object[]{new DensityResult(density, vta), stackFiles};
		}
		return new Object[]{new DensityResult(null, null), stackFiles};
	}
}
