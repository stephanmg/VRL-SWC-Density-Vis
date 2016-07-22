/// package's name
package edu.gcsc.vrl.swcdensityvis.util;

/// imports
import edu.gcsc.vrl.swcdensityvis.importer.SWC.SWCCompartmentInformation;
import edu.gcsc.vrl.swcdensityvis.ext.quickhull3d.Point3d;
import edu.gcsc.vrl.swcdensityvis.ext.quickhull3d.QuickHull3D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.vecmath.Vector3f;

/**
 * @brief computational utilities for density vis
 * @author stephanmg <stephan@syntaktischer-zucker.de>
 */
public final class CompUtility {
	private final static Vector3f ORIGO_CARTESIAN_COORDINATES_SYSTEM = new Vector3f(0, 0, 0);
	private final static Vector3f UNIT_VECTOR_X_DIRECTION = new Vector3f(1, 0, 0);
	private final static Vector3f UNIT_VECTOR_Y_DIRECTION = new Vector3f(0, 1, 0);
	private final static Vector3f UNIT_VECTOR_Z_DIRECTION = new Vector3f(0, 0, 1);

	/**
	 * @brief utility pattern
	 */
	private CompUtility() {
	}

	/**
	 * @brief gets all convex hulls in 3d for all files (cells)
	 * @param cells
	 * @return
	 */
	public static HashMap<String, QuickHull3D> hull(HashMap<String, ArrayList<SWCCompartmentInformation>> cells) {
		HashMap<String, ArrayList<Point3d>> points = new HashMap<String, ArrayList<Point3d>>();
		HashMap<String, QuickHull3D> hulls = new HashMap<String, QuickHull3D>();
		Iterator<Map.Entry<String, ArrayList<SWCCompartmentInformation>>> it = cells.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, ArrayList<SWCCompartmentInformation>> entry = it.next();
			ArrayList<SWCCompartmentInformation> compartments = entry.getValue();
			String key = entry.getKey();
			for (SWCCompartmentInformation point : compartments) {
				points.get(key).add(new Point3d(point.getCoordinates().x, point.getCoordinates().y, point.getCoordinates().z));
			}
		}
		Iterator<Map.Entry<String, ArrayList<Point3d>>> it2 = points.entrySet().iterator();
		while (it2.hasNext()) {
			QuickHull3D hull = new QuickHull3D();
			Map.Entry<String, ArrayList<Point3d>> entry = it2.next();
			String key = entry.getKey();
			Point3d[] values = (Point3d[]) entry.getValue().toArray();
			hull.build(values);
			hulls.put(key, hull);
		}
		return hulls;
	}

	/**
	 * @brief orthogonally projects a point q to the plane defined by n and
	 * p (in 3d space)
	 * @see for more complex projections, i. e. oblique, one could also use
	 * a projection matrix as in the orthogonal projections one could also
	 * use it: http://mathworld.wolfram.com/ProjectionMatrix.html
	 * @param q - initial point
	 * @param n - normal of the plane (can be non-normalized)
	 * @param p - point within the plane
	 * @return
	 * @see
	 */
	public static Vector3f projectToPlane(Vector3f q, Vector3f n, Vector3f p) {
		n.normalize();
		Vector3f d = new Vector3f(q);
		d.sub(p);
		n.scale(n.dot(d));
		Vector3f qPrime = new Vector3f(q);
		qPrime.sub(n);
		return qPrime;
	}

	/**
	 * @brief projects to xy-plane
	 * @param q
	 * @return
	 */
	public static Vector3f projectToXYPlane(Vector3f q) {
		return projectToPlane(q, UNIT_VECTOR_Z_DIRECTION, ORIGO_CARTESIAN_COORDINATES_SYSTEM);
	}

	/**
	 * @brief projects to xz-plane
	 * @param q
	 * @return
	 */
	public static Vector3f projectToXZPlane(Vector3f q) {
		return projectToPlane(q, UNIT_VECTOR_Y_DIRECTION, ORIGO_CARTESIAN_COORDINATES_SYSTEM);
	}

	/**
	 * @brief proejcts to yz-plane
	 * @param q
	 * @return
	 */
	public static Vector3f projectToYZPlane(Vector3f q) {
		return projectToPlane(q, UNIT_VECTOR_X_DIRECTION, ORIGO_CARTESIAN_COORDINATES_SYSTEM);
	}

	/**
	 * @brief the uniform shrinkage of a quickhull
	 * @param qhull
	 * @param factor
	 * @todo
	 */
	public static QuickHull3D shrink_uniform(QuickHull3D qhull, double factor) {
		/// 1. get quickhull
		/// 2. calculate center out of vertices
		/// 3. scale coordinates by factor (with respect to the densities in the 100 % qhull!)
		/// 4. transform back to calculated center
		return new QuickHull3D();
	}

}
