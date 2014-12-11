/// package's name
package edu.gcsc.vrl.swcdensityvis;

/// imports
import edu.gcsc.vrl.densityvis.Density;
import edu.gcsc.vrl.densityvis.DensityResult;
import eu.mihosoft.vrl.annotation.ComponentInfo;
import eu.mihosoft.vrl.annotation.OutputInfo;
import eu.mihosoft.vrl.annotation.ParamInfo;
import eu.mihosoft.vrl.v3d.VTriangleArray;
import eu.mihosoft.vrl.v3d.jcsg.Cube;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import javax.vecmath.Vector3f;

/**
 * @brief computes the density
 */
@ComponentInfo(name="ComputeSWCDensity", category="Neuro/SWC-Density-Vis")
public class ComputeSWCDensity implements java.io.Serializable {
  private static final long serialVersionUID=1L;
  @OutputInfo(
    name="Density"
  )
  public DensityResult compute(
    @ParamInfo(
      name="Input folder",
      style="load-folder-dialog",
      options="endings=[\"swc\"]; description=\"SWC files (.swc)\"") File folder,
    @ParamInfo(name="width", style="default", options="value=10;min=5") int width,
    @ParamInfo(name="height", style="default", options="value=10;min=5") int height,
    @ParamInfo(name="depth", style="default", options="value=10;min=5") int depth,
    @ParamInfo(name="selection", style="selection", options="value=[\"all\", \"undefined\", \"axon\", \"(basal) dendrite\", \"apical dendrite\", \"fork point\", \"end point\", \"custom\"]") String choice
  ) {
	  HashMap<String, ArrayList<SWCCompartmentInformation>> cells = new HashMap<String, ArrayList<SWCCompartmentInformation>>();
	  try {
		  File[] swcFiles = folder.listFiles(new FilenameFilter()
		{
    		@Override
 		 public boolean accept(File dir, String name) {
        	 return name.endsWith(".swc");
		}});
		  
		  for (File f : swcFiles) {
			eu.mihosoft.vrl.system.VMessage.info("Parsing SWC file", f.toString());
		  	cells.put(f.getName(), SWCUtility.parse(f));
		  }
		  eu.mihosoft.vrl.system.VMessage.info("Computing density", "Total number of files for density computation: " + swcFiles.length);
		  
		  /**
		   * @todo this could be improved... -> since we dont use it below (DensityUtil must use it also, so w ecan make the selection!)
		   */
		  if ("ALL".equals(choice)) {
		  	SWCUtility.computeDensity(cells);
		  } else {
			SWCUtility.computeDensity(cells, choice);
		  }
  	} 
	  catch (IOException e) {
		   eu.mihosoft.vrl.system.VMessage.exception("File not found", e.toString());
	  }
	  
      /**
       * @todo here we need to scale the geometry to the range [0.1 to 100] for back and front plane clipping
       */
      SWCUtility.scaleAndTransformAndCopyGeometry(cells, new Vector3f(0.1f, 0.1f, 0.1f), new Vector3f(100.0f-0.1f, 100.0f-0.1f, 100.0f-0.1f));
      Density density = DensityUtil.computeDensity(cells, width, height, depth); /// density must respect new rescaled geometry...
      Vector3f dim = SWCUtility.getDimensions(cells);
      double backplane = 100.0;
      double frontplane = 0.1;
      double scaling_factor = (backplane - frontplane) / (Collections.max(Arrays.asList(dim.x, dim.y, dim.z)));
      System.err.println("scaling_factor: " + scaling_factor);
      System.err.println("least coordinates: " + SWCUtility.getBoundingBox(cells).getSecond());
 //     VTriangleArray vta = new Cube(dim.x*scaling_factor,dim.y*scaling_factor,dim.z*scaling_factor).toCSG().toVTriangleArray();
      VTriangleArray vta = new Cube(100.0, 100.0, 100.0).toCSG().toVTriangleArray();
      return new DensityResult(density, vta);
  }
}
