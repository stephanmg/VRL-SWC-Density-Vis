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
		long startTime = System.currentTimeMillis();
		File[] stackFiles = folder.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return    name.endsWith(".swc")
				       || name.endsWith(".xml")
				       || name.endsWith(".asc");
			}
		});
		
		/// Note the consensus geometry should not be included in the stack
		/// input folder of SWC/XML/ASC files, since otherwise doubly counted
		ArrayList<File> files = new ArrayList<File>(Arrays.asList(stackFiles));
		if (consensus != null) { files.add(consensus); }
		
		/// if no files are selected, abort execution and print error msg
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
			
			/// instantiate the strategy and the visualizer
			DensityVisualizable visualizer = factory.getDensityVisualizer(fileType); 
			DensityComputationContext densityComputationContext = new DensityComputationContext();
			densityComputationContext.setDensityComputationStrategy(new DensityComputationStrategyFactoryProducer().getDefaultAbstractDensityComputationStrategyFactory().getDefaultComputationStrategy(fileType)); 
			visualizer.setContext(densityComputationContext);
			
			/// TODO: This could be doen with a factory method to give 
			/// the required visualizer, e.g. XML/ASC/SWC.
			DensityVisualizable xmlDensityVisualizer = new XMLDensityVisualizer(XMLDensityUtil.getDefaultDiameterImpl());
			xmlDensityVisualizer.setContext(new DensityComputationContext(new TreeDensityComputationStrategyXML(width, height, depth)));
			xmlDensityVisualizer.setFiles(files);
			log.info("Files for density visualization: " + files);
			
			/// TODO: Add parameter to GUI for scaling the visual output,
			/// e.g. scale density and the geometry with a value: scaling.
			xmlDensityVisualizer.prepare(Color.yellow, 1, compartment);

			/// decorate with isosurfaces if user wishes to get isosurfaces
			if (bIsoSurfaces) {
				log.info("Isosurface density visualizer decorator used.");
				xmlDensityVisualizer = new IsosurfaceDensityVisualizerDecorator(xmlDensityVisualizer, mAverage, mDeviation);
				/// TODO: bring Marching Cubes into the ISosurfacDensityVisualizerDecorator
			} else {
				log.info("No isosurface density visualizer decorator used.");
			}
			
			/// Note: We cache the geometry, this produced in rare cases
			/// multiple parent branches. This should be fixed now.
			/// parse all files and calculate density
			xmlDensityVisualizer.parseStack(); 
			Density density = xmlDensityVisualizer.computeDensity(); 
			long estimatedTime = System.currentTimeMillis() - startTime;
			System.err.println("Time has passed: " + estimatedTime);
			
			/// Note for dim and center this is the DIameterImpl! and thus not slow...
			
			/// get dim and center in physiological units
			Vector3f dim = (Vector3f) xmlDensityVisualizer.getDimension();
			Vector3f center = (Vector3f) xmlDensityVisualizer.getCenter(); 
			log.info("DIMENSION (of subset of geometry): " + dim);
			log.info("CENTER (of subset of geometry): " + center);
			
	 		/// TODO: 0.01 is visual scale for output on Canvas -> pass scaling parameter -> set to 1 also!
			/// ScaleDensityToJava scales only the density, need also to scale the bounding box geometry (cube)
			/// Best idea to scale it here?!
			VTriangleArray vta = new Cube(
				new Vector3d(center.x*0.01, center.y*0.01, center.z*0.01), 
				new Vector3d(dim.x*0.01, dim.y*0.01, dim.z*0.01))
				.toCSG().toVTriangleArray();
			
			return new Object[]{new DensityResult(density, vta), stackFiles};
		}
		return new Object[]{new DensityResult(null, null), stackFiles};
	}
}
