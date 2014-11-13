/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.gcsc.vrl.swcdensityvis;

import edu.wlu.cs.levy.CG.KDTree;
import edu.wlu.cs.levy.CG.KeySizeException;
import eu.mihosoft.vrl.reflection.Pair;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import javax.vecmath.Vector3d;

 /**
   * @brief calculate sampling cuboid normals (note: as they dont change 
   * 		we can also call this once for our (invariant) sampling cuboid)
   */
  class SamplingCuboid {
	private static final SamplingCuboid instance = new SamplingCuboid();
	
	private SamplingCuboid() {
	}

  	public static SamplingCuboid getInstance() {
   		return SamplingCuboid.instance;
	}

	  /**      
	   * @brief calculates all normals of a cuboid
	   *              
	   *            p5 .... p6    
	   *         .  .      .
	   *     .      .   .  .
	   *  .         .      .
	   * p1 .... p2 .      .
	   * .       .  p7    .p8
	   * .  .    .     .
	   * p3 .... p4 . 
	   * 
	   * @param p1
	   * @param p2
	   * @param p3
	   * @param p4
	   * @param p5
	   * @param p6
	   * @return 
	   */
	  public static ArrayList<Vector3d> getSamplingCuboidNormals(Vector3d p1, Vector3d p2, Vector3d p3, Vector3d p4, Vector3d p5, Vector3d p6, Vector3d p7, Vector3d p8) {
		ArrayList<Vector3d> temp = new ArrayList<Vector3d>();
		temp.add(calculateNormal(p1, p2, p3, p4)); // front
		temp.add(calculateNormal(p5, p6, p7, p8)); // rear
		temp.add(calculateNormal(p3, p4, p7, p8)); // bottom
		temp.add(calculateNormal(p1, p2, p5, p6)); // top
		temp.add(calculateNormal(p1, p3, p5, p7)); // left
		temp.add(calculateNormal(p4, p8, p2, p6)); // right
		return temp;
	  }
		
	  /**
	   * @brief calculates the normal of a (rectangular) plane
	   * @param p1
	   * @param p2
	   * @param p3
	   * @param p4
	   * @return 
	   */
	  private static Vector3d calculateNormal(Vector3d p1, Vector3d p2, Vector3d p3, Vector3d p4) {
		Vector3d a = new Vector3d(p3);
		a.sub(p2);
		Vector3d b = new Vector3d(p1);
		b.sub(p2);
		Vector3d c = new Vector3d();
		c.cross(a, b);
		return c;
	  }
  }
	

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
					line = line.trim();
					String[] columns = line.split(" ");
					assert columns.length == SWCCompartmentInformation.COLUMNS_SIZE : "SWC not in standardized format, "
						+ "i. e. columns do not match the format specification.";
					SWCCompartmentInformation info = new SWCCompartmentInformation();
					info.setIndex(Integer.parseInt(columns[0]) - 1);
					info.setType(Integer.parseInt(columns[1]));
					info.setCoordinates(new Vector3d(Double.parseDouble(columns[2]),
						Double.parseDouble(columns[3]),
						Double.parseDouble(columns[4])));

					info.setThickness(Double.parseDouble(columns[5]));
					info.setConnectivity(new Pair<Integer, Integer>(Integer.parseInt(columns[0])-1 ,
						Integer.parseInt(columns[6])));
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
	 * @brief compute density
	 * @todo  implement 
	 * @param cells
	 */
	public static void computeDensity(HashMap<String, ArrayList<SWCCompartmentInformation>> cells) {
	  final Vector3d dims = SWCUtility.getDimensions(cells);
	  HashMap<String, ArrayList<Double>> sum_length_in_cubes;
	  Pair<Vector3d, Vector3d> boundingBox = SWCUtility.getBoundingBox(cells);
	  // in µm!
	  final double width = 5;
	  final double height = 5;
	  final double depth = 5;
	  
	  class PartialDensityComputer implements Callable<ArrayList<Double>> {
		/// lengthes in sampling cubes and actual cell
		private volatile ArrayList<Double> lengths;
		private volatile Map.Entry<String, ArrayList<SWCCompartmentInformation>> cell;
	
		/**
		 * @brieft def ctor
		 */
		public PartialDensityComputer(Map.Entry<String, ArrayList<SWCCompartmentInformation>> cell) {
		  this.cell = cell;
		}

 		 @Override 
		 public ArrayList<Double> call() {
		   KDTree<ArrayList<Vector3d>> tree = buildKDTree(getIndicents(cell.getValue()));
		   for (double x = 0; x < width; x+=width) {
			 for (double y = 0; y < height; y+=height) {
			   for (double z = 0; z < depth; z+=depth) {
				  /*
				   *              
				   *            p5 .... p6    
				   *         .  .      .
				   *     .      .   .  .
				   *  .         .      .
				   * p1 .... p2 .      .
				   * .       .  p7    .p8
				   * .  .    .     .
				   * p3 .... p4 . 
				   *
				   *
				   */
				 Vector3d p1 = new Vector3d(x, y+height, z);
				 Vector3d p2 = new Vector3d(x+width, y+height, z);
				 Vector3d p3 = new Vector3d(x, y, z);
				 Vector3d p4 = new Vector3d(x+width, y, z);
				 
				 Vector3d p5 = new Vector3d(x, y+height, z+depth);
				 Vector3d p6 = new Vector3d(x+width, y+height, z+depth);
				 Vector3d p7 = new Vector3d(x, y, z+depth);
				 Vector3d p8 = new Vector3d(x+width, y, z+depth);
				 
				 double[] upper = {x+width, y+height, z+depth};
				 double[] lower = {x, y, z};
				 
				 List<ArrayList<Vector3d>> temps = new ArrayList<ArrayList<Vector3d>>();
				 try {
				 	temps = tree.range(lower, upper);
				 } catch (KeySizeException e) {
				 	System.err.println("Keysize exception!");  
				 }
				 	
				 double length = 0.;
				 for (ArrayList<Vector3d> elem : temps) {
				   for (Vector3d elem2 : elem) {
					ArrayList<Vector3d> normals = SamplingCuboid.getSamplingCuboidNormals(p1, p2, p3, p4, p5, p6, p7, p8);
					/**
					 * @todo implement below
					 */
					
					//	EdgeSegmentWithinCube(x, y, z, width, height, depth, p1, p2, v1, v2, normals.get(0));
					//	EdgeSegmentWithinCube(x, y, z, width, height, depth, p1, p2, v1, v2, normals.get(0));
					//	EdgeSegmentWithinCube(x, y, z, width, height, depth, p1, p2, v1, v2, normals.get(0));
					//	EdgeSegmentWithinCube(x, y, z, width, height, depth, p1, p2, v1, v2, normals.get(0));
					//	EdgeSegmentWithinCube(x, y, z, width, height, depth, p1, p2, v1, v2, normals.get(0));
					//	EdgeSegmentWithinCube(x, y, z, width, height, depth, p1, p2, v1, v2, normals.get(0));
					//	EdgeSegmentWithinCube(x, y, z, width, height, depth, p1, p2, v1, v2, normals.get(0));
					//	EdgeSegmentWithinCube(x, y, z, width, height, depth, p1, p2, v1, v2, normals.get(0));
					double value = 0.; // EdgeSegmentWithinCuboid ...
					length+=value;
				   }
				 }
		 		lengths.add(length); // add sampling cube length
			   }
			 }
		   }
		   
		   /// ...
		   /// ...
		   /// ...
		   /** @todo implement
			* 
			*/
 			 // 0.: take n = #cpus = #threads SWC geometries off the HashMap
				// 1.: create n kd trees for the n geometries, attach to leaf (compartment nodes) all incidents (i. e. start or end of edge starting in compartment node/leaf).
					// 2.: run over sampling area with sampling cube width, height and depth
						// 3.: sampling cubes are easy to describe, generate them statically, to save the total dendritic length
							// 4.: determine with EdgeSegment below the amount of edge within the cube, sum up for the compartment in this sampling cube (so we would consider all edges here! (cf note above))
								// 5.: after all compartments have been calculated, gather all and generate voxels by VoxelImpl()
		   return lengths;
		 }
	 }
	  
	 int processors = Runtime.getRuntime().availableProcessors();
	 ExecutorService executor = Executors.newFixedThreadPool(processors);
	
	ArrayList<Callable<ArrayList<Double>>> callables = new ArrayList<Callable<ArrayList<Double>>>();
	for (Map.Entry<String, ArrayList<SWCCompartmentInformation>> cell :  cells.entrySet()) {
		Callable<ArrayList<Double>> c = new PartialDensityComputer(cell);
		callables.add(c);
	}
	
	try {
		List<Future<ArrayList<Double>>> results = executor.invokeAll(callables);
		ArrayList<ArrayList<Double>> subresults = new ArrayList<ArrayList<Double>>();
		ArrayList<Double> endresults = new ArrayList<Double>(); // sample cubes
		for (Future<ArrayList<Double>> res : results) {
		  subresults.add(res.get());
		}
		
		/** @todo below could also be done in parallel */
		int index;
		for (ArrayList<Double> subres : subresults) { // subres = one cell with n sampling cubes
		  index = 0; // first cube
		  for (Double d : subres) {
			endresults.add(index, endresults.get(index) + d); // accumulate in each cube
			index++; // next cube
		  }
		}
	} catch (ExecutionException e) {
	  e.printStackTrace();
	} catch (InterruptedException e) {
	  e.printStackTrace();
	}
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
	 * @param v1
	 * @param v2
	 * @param normal
	 * @return 
	 * 
	 * TODO: note better implement a function like RayBoxIntersection, or we use all faces of the cube and call RayPlaneIntersection...
	 */
	public static double EdgeSegmentWithinCube(double x, double y, double z, double width, double height, double depth, Vector3d p1, Vector3d p2, Vector3d v1, Vector3d v2, Vector3d normal) {
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
				/// @todo RayLineIntersection(p1, ray)
				Vector3d vOut = new Vector3d();
				boolean intersects = RayPlaneIntersection(vOut, 0.0, p1, v1, v2, normal, 0.0);
				if (intersects) {
					Vector3d temp = new Vector3d(vOut);
					temp.sub(p1);
					return temp.length();
				} else {
					return -1;
				}
				
			} else {
				Vector3d vOut = new Vector3d();
				boolean intersects = RayPlaneIntersection(vOut, 0.0, p1, v1, v2, normal, 0.0);
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
			boolean intersects = RayPlaneIntersection(vOut1, 0.0, p1, v1, v2, normal, 0.0);
			boolean intersects2 = RayPlaneIntersection(vOut2, 0.0, p2, v1, v2, normal, 0.0);
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
	/**
	 * @brief get all incident vertices
	 * @param cell
	 * @return 
	 */
	public static HashMap<Vector3d, ArrayList<Vector3d>> getIndicents(ArrayList<SWCCompartmentInformation> cell) {
		/// split into initializing phase (init keys of ArrayList<ArrayList<Vector3d>> to v0)
		/// and a populating phase (i. e. add to ArrayList the incident to V0) to get rid of O(n^2) for loop
		/// if necessary in the future (then we need to make the convention, that the arraylist is ordered
		/// in the same way as the compartments are appearing in the single swc file...
		/*
		// init phase
		ArrayList<ArrayList<Vector3d>> incidents2 = new ArrayList<ArrayList<Vector3d>>(cell.size());
		// populate phase
		for (SWCCompartmentInformation info : cell) {
		  incidents2.get(info.getConnectivity().getSecond()).add(info.getCoordinates());
		}
		*/
	  
		HashMap<Vector3d, ArrayList<Vector3d>> incidents = new HashMap<Vector3d, ArrayList<Vector3d>>(cell.size());
		for (SWCCompartmentInformation info : cell) {
			ArrayList<Vector3d> temp = new ArrayList<Vector3d>();
			Vector3d v0 = info.getCoordinates();
			for (SWCCompartmentInformation info2 : cell) {
				if (info.getIndex() == info2.getConnectivity().getSecond()) {
					temp.add(info2.getCoordinates());
				}
			}
			incidents.put(v0, temp);
		}
		return incidents;
	}

	/**
	 * @brief builds a kd tree for the cell
	 * @param cell
	 * @return the newly created kd tree
	 */
	public static KDTree<ArrayList<Vector3d>> buildKDTree(HashMap<Vector3d, ArrayList<Vector3d>> cell) {
		KDTree<ArrayList<Vector3d>> kd = new KDTree<ArrayList<Vector3d>>(3);
		try {
			for (Map.Entry<Vector3d, ArrayList<Vector3d>> entry : cell.entrySet()) {
		  	Vector3d vec = entry.getKey();
		  	double[] key = { vec.x, vec.y, vec.z };
		  	kd.insert(key, entry.getValue());
			}		
		} catch (Exception e) {
	    	System.err.println(e);
		}
		return kd;
	}
	
	public static void main(String... args) {
		try {
			ArrayList<SWCCompartmentInformation> info = parse(new File("/Users/stephan/Code/git/VRL-SWC-Density-Vis/data/02a_pyramidal2aFI.swc"));
			getIndicents(info);
			System.out.println("Incidents size: " + info.size());
		} catch (IOException ioe) {
			System.err.println("Error reading from file: " + ioe);
		}
	}
}
	