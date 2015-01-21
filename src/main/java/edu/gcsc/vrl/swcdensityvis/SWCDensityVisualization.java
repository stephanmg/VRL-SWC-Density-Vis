package edu.gcsc.vrl.swcdensityvis;

import edu.gcsc.vrl.densityvis.VisUtil;
import edu.gcsc.vrl.densityvis.*;
import eu.mihosoft.vrl.annotation.ComponentInfo;
import eu.mihosoft.vrl.annotation.MethodInfo;
import eu.mihosoft.vrl.annotation.OutputInfo;
import eu.mihosoft.vrl.annotation.ParamGroupInfo;
import eu.mihosoft.vrl.annotation.ParamInfo;
import eu.mihosoft.vrl.v3d.Shape3DArray;
import eu.mihosoft.vrl.v3d.VGeometry3D;
import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import static javax.media.j3d.GeometryArray.COORDINATES;
import javax.media.j3d.LineArray;
import javax.media.j3d.Shape3D;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

/**
 *
 * @author stephan
 */
@ComponentInfo(name="SWCDensityVisualization", category="Neuro/SWC-Density-Vis")
public class SWCDensityVisualization implements java.io.Serializable {
  private static final long serialVersionUID=1L;

  @OutputInfo(style="vcanvas3d")
  @MethodInfo(
		valueName = " ",
		valueTypeName = " "
	)
  public Shape3DArray visualizeDensity(

    @ParamInfo(name="Density") DensityResult density,
    @ParamInfo(name="Geometry") HashMap cells,
    @ParamGroupInfo(group="Visualization|false|no description") 
    @ParamInfo(name="Min Density", style="slider", options="value=10;min=1;max=100;") int percentage,
    @ParamGroupInfo(group="Visualization") 
    @ParamInfo(name="Membrane Color", style="color-chooser", options="value=java.awt.Color.green") Color mColor,
    
    @ParamGroupInfo(group="Visualization") 
    @ParamInfo(name="Membrane Transparency", style="slider", options="") int mTransparency,
    @ParamGroupInfo(group="Visualization") 
    @ParamInfo(name="Density Color 1  ", style="color-chooser", options="value=java.awt.Color.blue") Color dColorZero,
    @ParamGroupInfo(group="Visualization") 
    @ParamInfo(name="Density Color 2  ", style="color-chooser", options="value=java.awt.Color.blue") Color dColorOne,
    @ParamGroupInfo(group="Visualization") 
    @ParamInfo(name="Density Transparency", style="default", options="value=true") boolean dTransparency
    ) {

    Shape3DArray result = new Shape3DArray();
    
    VGeometry3D geom3d = new VGeometry3D(
      density.getGeometry(),
      new Color( mColor.getRed(), mColor.getGreen(), mColor.getBlue(), 255 - mTransparency),
      null,1F,false, false, mTransparency > 0);

    int transparencyVal = 254;

    if (dTransparency) {
      transparencyVal = 0;
    }

    Color dColorZero_real = new Color(dColorZero.getRed(), dColorZero.getGreen(), dColorZero.getBlue(), transparencyVal);
    Color dColorOne_real = new Color(dColorOne.getRed(), dColorOne.getGreen(), dColorOne.getBlue(), 254);

    result.addAll(VisUtil.density2Java3D(
      density.getDensity(), density.getGeometry(), percentage, dColorZero_real, dColorOne_real, true, true));
    result.addAll( geom3d.generateShape3DArray() );
	
    /// scaling factor needs to be the same as in the density visualization!!! 
    /** @todo */
    @SuppressWarnings("unchecked")
    Iterator<Map.Entry<String, ArrayList<SWCCompartmentInformation>>> it = cells.entrySet().iterator();
    while (it.hasNext()) {
	  Map.Entry<String, ArrayList<SWCCompartmentInformation>> entry = it.next();
	  ArrayList<SWCCompartmentInformation> points = entry.getValue();
	  for (SWCCompartmentInformation info : points) {
		  LineArray la = new LineArray(2, COORDINATES);
			  Vector3f vector = new Vector3f(info.getCoordinates());
			  vector.scale(0.01f);
			  Vector3f vector2 = new Vector3f(0, 0, 0);
			  Point3f point = new Point3f(vector);
			  Point3f point2 = new Point3f(vector2);
			  la.setCoordinates(0, new Point3f[]{point, point2});
			  Shape3D myShape = new Shape3D(la);
			  result.add(myShape);
 	 }
  }
    return result;
  }
}

