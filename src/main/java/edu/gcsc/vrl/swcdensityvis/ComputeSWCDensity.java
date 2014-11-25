package edu.gcsc.vrl.swcdensityvis;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author stephan
 */

import edu.gcsc.vrl.densityvis.Density;
import edu.gcsc.vrl.densityvis.DensityResult;
import static edu.gcsc.vrl.swcdensityvis.SWCUtility.parse;
import eu.mihosoft.vrl.annotation.ComponentInfo;
import eu.mihosoft.vrl.annotation.OutputInfo;
import eu.mihosoft.vrl.annotation.ParamInfo;
import eu.mihosoft.vrl.v3d.VTriangleArray;
import eu.mihosoft.vrl.v3d.jcsg.Cube;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import javax.vecmath.Vector3f;

@ComponentInfo(name="ComputeSWCDensity", category="Neuro/SWC-Density-Vis")
public class ComputeSWCDensity implements java.io.Serializable {
  private static final long serialVersionUID=1L;

  // add your code here

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
    @ParamInfo(name="depth", style="default", options="value=10;min=5") int depth
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
		  SWCUtility.computeDensity(cells);
  	} 
	  catch (IOException e) {
		   eu.mihosoft.vrl.system.VMessage.exception("File not found", e.toString());
	  }
	  
      Density density = DensityUtil.computeDensity(cells, width, height, depth);
      Vector3f dim = SWCUtility.getDimensions(cells);
      VTriangleArray vta = new Cube(dim.x,dim.y,dim.z).toCSG().toVTriangleArray();
      return new DensityResult(density, vta);
  }
}
