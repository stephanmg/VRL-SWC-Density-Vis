package edu.gcsc.vrl.swcdensityvis;

import edu.gcsc.vrl.swcdensityvis.ext.quickhull3d.Point3d;
import edu.gcsc.vrl.swcdensityvis.ext.quickhull3d.QuickHull3D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @brief computational utilities for density vis
 * @author stephan
 */
public final class CompUtility {

	/**
	 * @brief private ctor
	 */
	private CompUtility() {
	}

	/**
	 * @brief gets all convex hulls in 3d for all files (cells)
	 * @param cells
	 * @return
	 */
	private static HashMap<String, QuickHull3D> hull(HashMap<String, ArrayList<SWCCompartmentInformation>> cells) {
		HashMap<String, ArrayList<Point3d>> points = new HashMap<String, ArrayList<Point3d>>();
		HashMap<String, QuickHull3D> hulls = new HashMap<String, QuickHull3D>();
		@SuppressWarnings("unchecked")
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
}
