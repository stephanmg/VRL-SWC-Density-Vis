package edu.gcsc.vrl.swcdensityvis;

import edu.gcsc.vrl.swcdensityvis.ext.quickhull3d.QuickHull3D;
import java.util.ArrayList;
import java.util.HashMap;
import javax.vecmath.Point3d;


/**
 *
 * @author stephan
 */
public final class CompUtility {
	private CompUtility() {
	}
	
	private static HashMap<String, QuickHull3D> hull(HashMap<String, ArrayList<SWCCompartmentInformation>> cells) {
		HashMap<String, ArrayList<Point3d>> points = new HashMap<String, ArrayList<Point3d>>();
		return new HashMap<String, QuickHull3D>();
	}

	/*
	public static QuickHull3D main (String[] args)
	 {
           // x y z coordinates of 6 points
	   Point3d[] points = new Point3d[]
	      { new Point3d (0.0,  0.0,  0.0),
		new Point3d (1.0,  0.5,  0.0),
		new Point3d (2.0,  0.0,  0.0),
		new Point3d (0.5,  0.5,  0.5),
		new Point3d (0.0,  0.0,  2.0),
		new Point3d (0.1,  0.2,  0.3),
		new Point3d (0.0,  2.0,  0.0),
	      };

	   QuickHull3D hull = new QuickHull3D();
	   hull.build (points);

	   System.out.println ("Vertices:");
	   Point3d[] vertices = hull.getVertices();
	   for (int i=0; i<vertices.length; i++)
	    { Point3d pnt = vertices[i];
	      System.out.println (pnt.x + " " + pnt.y + " " + pnt.z);
	    }

	   System.out.println ("Faces:");
	   int[][] faceIndices = hull.getFaces();
	   for (int i=0; i<vertices.length; i++)
	    { for (int k=0; k<faceIndices[i].length; k++)
	       { System.out.print (faceIndices[i][k] + " ");
	       }
	      System.out.println ("");
	    }
	 }*/
}