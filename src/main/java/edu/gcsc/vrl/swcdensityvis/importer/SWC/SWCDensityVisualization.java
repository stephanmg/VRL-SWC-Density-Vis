/// package's name
package edu.gcsc.vrl.swcdensityvis.importer.SWC;

/// imports
import edu.gcsc.vrl.densityvis.VisUtil;
import eu.mihosoft.vrl.annotation.ComponentInfo;
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
import static javax.media.j3d.GeometryArray.COLOR_4;
import static javax.media.j3d.GeometryArray.COORDINATES;
import javax.media.j3d.LineArray;
import javax.media.j3d.Shape3D;
import javax.vecmath.Color3f;
import javax.vecmath.Color4f;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

/**
 * @brief Density visualization for SWC files
 * @author stephanmg <stephan@syntaktischer-zucker.de>
 */
@ComponentInfo(name="SWCDensityVisualization", category="Neuro/SWC-Density-Vis")
public class SWCDensityVisualization implements java.io.Serializable {
  private static final long serialVersionUID=1L;

  @OutputInfo(style="shaped3darraycustom")
  public Shape3DArray visualizeDensity(
    @ParamGroupInfo(group="Visualization|false|no description") 
    @ParamInfo(name="Density") edu.gcsc.vrl.densityvis.DensityResult density,
    @ParamGroupInfo(group="Visualization|false|no description") 
    @ParamInfo(name="Min Density", style="slider", options="min=1;max=100;") int percentage,
    
    @ParamGroupInfo(group="Visualization") 
    @ParamInfo(name="Density Color 0", style="color-chooser", options="value=java.awt.Color.blue") Color dColorZero,
    @ParamGroupInfo(group="Visualization") 
    @ParamInfo(name="Density Color 1", style="color-chooser", options="value=java.awt.Color.red") Color dColorOne,
    @ParamGroupInfo(group="Visualization") 
    @ParamInfo(name="Density Transparency", style="default", options="value=true") boolean dTransparency,
    
    
    @ParamGroupInfo(group="Geometry|false|no description") 
    @ParamInfo(name="Line-graph Geometry") 
    @SuppressWarnings("rawtypes") HashMap cells,
    
    @ParamGroupInfo(group="Geometry|false|no description") 
    @ParamInfo(name="Line-graph Geometry Color", style="color-chooser", options="value=java.awt.Color.magenta") Color gColor,

    @ParamGroupInfo(group="Geometry|false|no description")
    @ParamInfo(name="Line-graph Geometry Transparency", style="slider", options="min=0;max=100;") int gTransparency,

    @ParamGroupInfo(group="Geometry|false|no description") 
    @ParamInfo(name="Bounding Box Color", style="color-chooser", options="value=java.awt.Color.green") Color mColor,
    
    @ParamGroupInfo(group="Geometry|false|no description") 
    @ParamInfo(name="Bounding Box Transparency", style="slider", options="") int mTransparency
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
	
    @SuppressWarnings("unchecked")
    Iterator<Map.Entry<String, ArrayList<SWCCompartmentInformation>>> it = cells.entrySet().iterator();
    while (it.hasNext()) {
	  Map.Entry<String, ArrayList<SWCCompartmentInformation>> entry = it.next();
	  ArrayList<SWCCompartmentInformation> points = entry.getValue();
	  for (SWCCompartmentInformation info : points) {
		  LineArray la = new LineArray(2, COORDINATES|COLOR_4);
		  la.setColor(0, color2Color4f(gColor, gTransparency));
		  la.setColor(1, color2Color4f(gColor, gTransparency));
		  int from = info.getConnectivity().getFirst();
		  int to = info.getConnectivity().getSecond() -1; 
		  System.err.println("from: " + from);
		  System.err.println("to: " + to);
		  System.err.println("");
		  if (from == -1 || to < 0) continue;
		  Vector3f vector_old = (points.get(from).getCoordinates());
		  Vector3f vector2_old = (points.get(to).getCoordinates());
		  Vector3f vector = new Vector3f(vector_old);
		  Vector3f vector2 = new Vector3f(vector2_old);
		  System.err.println("vector: " + vector);
		  System.err.println("vector2: " + vector2);
		  System.err.println("");
		  /// TODO: Use the same scaling factor as in the DensityVisualization
		  vector.scale(0.01f);
		  vector2.scale(0.01f);
		  Point3f point = new Point3f(vector);
		  Point3f point2 = new Point3f(vector2);
		  System.err.println("point: " + point);
		  System.err.println("point2: " + point2);
		  System.err.println("");
		  la.setCoordinates(0, new Point3f[]{point, point2});
		  Shape3D myShape = new Shape3D(la);
		  result.add(myShape);
		 }
		  
  }
    return result;
  }
  /**
 * Converts color from AWT to Java3D.
 *
 * @param c color to convert
 * @return color as Java3D color
 */
	private static Color3f color2Color3f(Color c) {
		return new Color3f(c.getRed() / 255f,
			c.getGreen() / 255f,
			c.getBlue() / 255f);
	}

	/**
	 * @brief
	 * @param c
	 * @param w percentage 0 to 100 (0 means no transparency, 100 means full transparecny)
	 * @return 
	 */
	private static Color4f color2Color4f(Color c, int w) {
		return new Color4f(c.getRed() / 255f,
			c.getGreen() / 255f,
			c.getBlue() / 255f, 
			(1 - w/100f));
	}
}

