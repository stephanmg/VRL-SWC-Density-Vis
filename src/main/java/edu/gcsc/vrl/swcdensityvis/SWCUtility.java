/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.gcsc.vrl.swcdensityvis;

import eu.mihosoft.vrl.reflection.Pair;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.vecmath.Tuple3d;
import javax.vecmath.Vector3d;

/**
 *
 * @author stephan
 * @brief utilites for SWC files
 */
public final class SWCUtility {
	/**
	 * @brief private ctor since utility classs should be final and private
	 */
	private SWCUtility() {
		
	}

	/**
	 * @brief parses a swc file
	 * @param file 
	 * @return list of compartment information for this file
	 * @throws IOException
	 */
	@SuppressWarnings("NestedAssignment")
	public static ArrayList<SWCCompartmentInformation> parse(File file) throws IOException {
		ArrayList<SWCCompartmentInformation> temp = new ArrayList<SWCCompartmentInformation>();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(file));
			String line;
			while ((line = br.readLine()) != null) {
				if (!line.startsWith("#")) {
					String[] columns = line.split(" ");
					assert columns.length != SWCCompartmentInformation.COLUMNS_SIZE : "SWC not in standardized format, "
						+ "i. e. columns do not match the format specification.";
					SWCCompartmentInformation info = new SWCCompartmentInformation();
					info.setIndex(Integer.parseInt(columns[0]) - 1);
					info.setType(Integer.parseInt(columns[1]));
					info.setCoordinates(new Vector3d(Double.parseDouble(columns[2]),
						Double.parseDouble(columns[3]),
						Double.parseDouble(columns[4])));

					info.setThicknesses(Double.parseDouble(columns[5]));
					info.setConnectivity(new Pair<Integer, Integer>(Integer.parseInt(columns[6]),
						Integer.parseInt(columns[7])));
					temp.add(info);
				}
			}
		} finally {
			br.close();
		}
		return temp;
	}
	
	/**
	 * @brief parses a bunch of swc file
	 * @param folder 
	 * @return hashmap of arraylist of information for each compartment in each file
	 * @throws IOException
	 */
	@SuppressWarnings("NestedAssignment")
	public static HashMap<String, ArrayList<SWCCompartmentInformation>> parseStack(File folder) throws IOException { 
		HashMap<String, ArrayList<SWCCompartmentInformation>> compartments = null;
		File[] swcFiles = folder.listFiles(new FilenameFilter()
		{
    		@Override
 		 public boolean accept(File dir, String name) {
        	 return name.endsWith(".xml");
		}});
		
		for (File f : swcFiles) {
			compartments.put(f.getName(), parse(f));
		}
		
	return compartments;
	}
	
	
	/**
	 * @brief get bounding box for an anonymous cell
	 * @param cell
	 * @return pair min max coordinates 3d
	 */
	public static Pair<Vector3d, Vector3d> getBoundingBox(final ArrayList<SWCCompartmentInformation> cell) {
		return getBoundingBox(	new HashMap<String, ArrayList<SWCCompartmentInformation>>()
						{{ put("Anonymous cell", cell); }}
					);
	}
	
	/**
	 * @brief get bounding box for a bunch of cells
	 * @param cells input cells 
	 * @return pair min max coordinates 3d
	 */
	public static Pair<Vector3d, Vector3d> getBoundingBox(HashMap<String, ArrayList<SWCCompartmentInformation>> cells) {
		ArrayList<Double> temp_x = null;
		ArrayList<Double> temp_y = null;
		ArrayList<Double> temp_z = null;
		
		for (Map.Entry<String, ArrayList<SWCCompartmentInformation>> entry : cells.entrySet()) {
			for (SWCCompartmentInformation info : entry.getValue()) {
				temp_x.add(info.getCoordinates().x);
				temp_y.add(info.getCoordinates().y);
				temp_z.add(info.getCoordinates().z);
			}
		}
		
		return new Pair<Vector3d, Vector3d>
			(
			new Vector3d(Collections.min(temp_x), Collections.min(temp_y), Collections.min(temp_z)), 
			new Vector3d(Collections.max(temp_x), Collections.max(temp_y), Collections.max(temp_z))
			);

	}
	
	/**
	 * @brief get dimensions (width, height and depth) for the cells 
	 * @param cells
	 * @return the dimensions as a vector
	 */
	public static Vector3d getDimensions(HashMap<String, ArrayList<SWCCompartmentInformation>> cells) {
		Pair<Vector3d, Vector3d> bounding = getBoundingBox(cells);
		return new Vector3d(
			bounding.getFirst().x - bounding.getSecond().x,
			bounding.getFirst().y - bounding.getSecond().y,
			bounding.getFirst().z - bounding.getSecond().z	
		);
	}
	
	/**
	 * @brief determines amount of an edge within an sampling cube
	 * @param x
	 * @param y
	 * @param z
	 * @param width
	 * @param height
	 * @param depth
	 * @param p1
	 * @param p2
	 * @return 
	 * 
	 * TODO: note better implement a function like RayBoxIntersection, or we use all faces of the cube and call RayPlaneIntersection...
	 */
	public static double EdgeSegmentWithinCube(double x, double y, double z, double width, double height, double depth, Vector3d p1, Vector3d p2) {
		/// Case 1: Both vertices inside cube
		if ( ((p1.x <= x+width || p1.x >= x) &&
		      (p1.y <= y+height || p1.y >= y) && 
		      (p1.z <= z+depth || p1.z >= z)) &&
		     ((p2.x <= x+width || p2.x >= x) &&
		      (p2.y <= y+height || p2.y >= y) && 
		      (p2.z <= z+depth || p2.z >= z)) ) {
			Vector3d temp = new Vector3d(p1);
			temp.sub(p2);
			return temp.length();
		/// Case 2: One vertex inside cube 
		} else if ( ((p1.x <= x+width || p1.x >= x) &&
		      (p1.y <= y+height || p1.y >= y) && 
		      (p1.z <= z+depth || p1.z >= z)) || 
		     ((p2.x <= x+width || p2.x >= x) &&
		      (p2.y <= y+height || p2.y >= y) && 
		      (p2.z <= z+depth || p2.z >= z)) ) {
			if (((p1.x <= x+width || p1.x >= x) &&
		      		(p1.y <= y+height || p1.y >= y) && 
		      		(p1.z <= z+depth || p1.z >= z))) {
				/// if p1 inside, construct line starting from p1
				/// todo RayLineIntersection(p1, ray)
				Vector3d vOut = new Vector3d();
				boolean intersects = RayPlaneIntersection(vOut, 0.0, p1, new Vector3d(), new Vector3d(), new Vector3d(), 0.0);
				if (intersects) {
					Vector3d temp = new Vector3d(vOut);
					temp.sub(p1);
					return temp.length();
				} else {
					return -1;
				}
				
			} else {
				Vector3d vOut = new Vector3d();
					boolean intersects = RayPlaneIntersection(vOut, 0.0, p1, new Vector3d(), new Vector3d(), new Vector3d(), 0.0);
				if (intersects) {
					Vector3d temp = new Vector3d(vOut);
					temp.sub(p1);
					return temp.length();
				} else {
					return -1;
				}
			}
		/// Case 3: Both vertices outside the cube
		} else {
			Vector3d vOut1 = new Vector3d();
			Vector3d vOut2 = new Vector3d();
			boolean intersects = RayPlaneIntersection(vOut1, 0.0, p1, new Vector3d(), new Vector3d(), new Vector3d(), 0.0);
			boolean intersects2 = RayPlaneIntersection(vOut2, 0.0, p1, new Vector3d(), new Vector3d(), new Vector3d(), 0.0);
			if (intersects && intersects2) {
				Vector3d temp = new Vector3d(vOut1);
				temp.sub(vOut2);
				return temp.length();
			} else {
				return -1;
			}
		}
	}

	
	/**
	 * @brief determines the first intersection point of a ray with a plane
	 * @param vOut
	 * @param tOut
	 * @param rayFrom
	 * @param rayDir
	 * @param p
	 * @param normal
	 * @param tol
	 * @return 
	 */
	public static boolean RayPlaneIntersection(Vector3d vOut, double tOut, Vector3d rayFrom, Vector3d rayDir, Vector3d p, Vector3d normal, double tol) {
		double denom = rayDir.dot(normal);
		if (Math.abs(denom) < tol) {
			return false;
		} else {
			Vector3d v = new Vector3d(p);
			v.sub(rayFrom);
			tOut = v.dot(normal) / denom;
			v.scale(tOut, rayDir);
			vOut.add(rayFrom, v);
			return true;
		}
	}
	
	/**
	 * @brief determines line square intersection
	 * @param S1
	 * @param S2
	 * @param S3
	 * @param R1
	 * @param R2
	 * @param tol
	 * @return 
	 */
	public static boolean LineSquareIntersection(Vector3d S1, Vector3d S2, Vector3d S3, Vector3d R1, Vector3d R2, double tol) {
	Vector3d dS21 = new Vector3d(S2);
	Vector3d dS31 = new Vector3d(S3);
	Vector3d n = new Vector3d(); 
	Vector3d dR = new Vector3d(R1);

	dS21.sub(S1);
	dS31.sub(S1);
	n.cross(dS21, dS31);
	dR.sub(R2);
	
	double ndotdR = n.dot(dR);

        if (Math.abs(ndotdR) < tol) { 
            return false; /// indicate no intersection
        }
	
	Vector3d R1subS1 = new Vector3d(R1);
	R1subS1.sub(S1);
	
        double t = -n.dot(R1subS1) / ndotdR;
        Vector3d M = new Vector3d(R1);
	Vector3d dRscaled = new Vector3d(dR);
	dRscaled.scale(t);
	R1.add(dRscaled);

        Vector3d dMS1 = new Vector3d(M);
	dMS1.sub(S1);
	
        double u = dMS1.dot(dS21);
        double v = dMS1.dot(dS31);

        return (u >= 0.0f && u <= dS21.dot(dS21)
             && v >= 0.0f && v <= dS31.dot(dS31));
	}
}
	
	