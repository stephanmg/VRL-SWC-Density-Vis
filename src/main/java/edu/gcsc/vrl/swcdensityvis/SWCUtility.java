/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.gcsc.vrl.swcdensityvis;

import edu.wlu.cs.levy.CG.KDTree;
import edu.wlu.cs.levy.CG.KeyDuplicateException;
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
		ArrayList<Double> temp_x = new ArrayList<Double>();
		ArrayList<Double> temp_y = new ArrayList<Double>();
		ArrayList<Double> temp_z = new ArrayList<Double>();
		
		for (Map.Entry<String, ArrayList<SWCCompartmentInformation>> entry : cells.entrySet()) {
			for (SWCCompartmentInformation info : entry.getValue()) {
				temp_x.add(info.getCoordinates().x);
				temp_y.add(info.getCoordinates().y);
				temp_z.add(info.getCoordinates().z);
			}
		}
		
		return new Pair<Vector3d, Vector3d>
			(
			new Vector3d(Collections.max(temp_x), Collections.max(temp_y), Collections.max(temp_z)),
			new Vector3d(Collections.min(temp_x), Collections.min(temp_y), Collections.min(temp_z)) 
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
			Math.abs(bounding.getFirst().x - bounding.getSecond().x),
			Math.abs(bounding.getFirst().y - bounding.getSecond().y),
			Math.abs(bounding.getFirst().z - bounding.getSecond().z)	
		);
	}
	
	/**
	 * @brief compute density
	 * @todo  implement 
	 * @param cells
	 */
	public static ArrayList<Double> computeDensity(HashMap<String, ArrayList<SWCCompartmentInformation>> cells) {
	  final Vector3d dims = SWCUtility.getDimensions(cells);
	  final Pair<Vector3d, Vector3d> bounding = SWCUtility.getBoundingBox(cells);
	  final int number_of_cells = cells.size();
	  System.out.println("dims: " + dims);
	  // in µm!
	  final double width = 1;
	  final double height = 1;
	  final double depth = 1;
	  
	  class PartialDensityComputer implements Callable<ArrayList<Double>> {
		/// lengthes in sampling cubes and actual cell
		private volatile ArrayList<Double> lengths = new ArrayList<Double>();
		private volatile Map.Entry<String, ArrayList<SWCCompartmentInformation>> cell;
	
		/**
		 * @brieft def ctor
		 */
		public PartialDensityComputer(Map.Entry<String, ArrayList<SWCCompartmentInformation>> cell) {
		  this.cell = cell;
		}

 		 @Override 
		 public ArrayList<Double> call() {
		   /// create a kd tree for the geometry, attach to leaf all compartment nodes
	           /// each lead node gets attached the vertices which are connected to the
	           /// leaf node with and edge (getIncidents)
		   KDTree<ArrayList<Vector3d>> tree = buildKDTree(getIndicents(cell.getValue()));
		   
		   /** @todo test: should finish in one step -> and it does. */
		   /// we iterate over the bounding box and generate sampling cubes
		   /// the cubes must not necessarily be created, but are for simplicity 
		   /// reasons for now handy 
		   /** @todo use bounding box min values for x y z and iterate 
		    * to bounding box max values with width height and depth 
		    */
		   for (double x = bounding.getSecond().x; x < bounding.getFirst().x; x+=width) {
			 for (double y = bounding.getSecond().y; y < bounding.getFirst().y; y+=height) {
			   for (double z = bounding.getSecond().z; z < bounding.getFirst().z; z+=depth) {
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
				 	
				 //System.err.println("only one sampling cube");
				 double length = 0.;
				 for (ArrayList<Vector3d> elem : temps) { // a list of nodes which are in the range lower to upper
				  Vector3d starting_vertex = new Vector3d(elem.get(0)); // starting vertex
			   	  //elem.remove(0); // starting vertex, this can certainly be improved -> this is wrong
				  /** @todo this is wrong, since the first 
				   * vertex is needed in the next iteration again!
				   */
				   
				/// each node in range has attached the incident edges we calculated 
				/// previously (we need however the starting vertex somehow, see above)
				/// for now the starting vertex, aka the compartment, is the first 
				/// vertex in the attached meta-data arraylist for each compartment 
				/// we have inserted in the kd tree before
				   for (Vector3d elem2 : elem) { 
					ArrayList<Vector3d> normals = SamplingCuboid.getSamplingCuboidNormals(p1, p2, p3, p4, p5, p6, p7, p8);
					double val = 0.;
					/// determine the amount of edge in the sampling cube for each edge
					val += EdgeSegmentWithinCube(x, y, z, width, height, depth, starting_vertex, elem2, p1, p2, normals.get(0)); // p1, p2 vertices in plane and normal: front 
					val += EdgeSegmentWithinCube(x, y, z, width, height, depth, starting_vertex, elem2, p5, p6, normals.get(1)); // p1, p2 vertices in plane and normal: rear
					val += EdgeSegmentWithinCube(x, y, z, width, height, depth, starting_vertex, elem2, p3, p4, normals.get(2)); // p1, p2 vertices in plane and normal: bottom
					val += EdgeSegmentWithinCube(x, y, z, width, height, depth, starting_vertex, elem2, p1, p2, normals.get(3)); // p1, p2 vertices in plane and normal: top
					val += EdgeSegmentWithinCube(x, y, z, width, height, depth, starting_vertex, elem2, p3, p7, normals.get(4)); // p1, p2 vertices in plane and normal: left 
					val += EdgeSegmentWithinCube(x, y, z, width, height, depth, starting_vertex, elem2, p4, p8, normals.get(5)); // p1, p2 vertices in plane and normal: left 
					length+=val;
				   }
				 }
		 		//lengths.add(length); // add summed length of each sampling cube for the geometry
			   }
			 }
		   }
		   lengths.add(0.0);
		   return lengths;
		 }
	 }
	 
 
	// take number of available processors and create a fixed thread pool,
	// the executor executes then at most the number of available processors
	// threads to calculate the partial density (Callable PartialDensityComputer)
	int processors = Runtime.getRuntime().availableProcessors();
	ExecutorService executor = Executors.newFixedThreadPool(processors);
	System.out.println("Number of processors: " + processors);
	
	ArrayList<Callable<ArrayList<Double>>> callables = new ArrayList<Callable<ArrayList<Double>>>();
	for (Map.Entry<String, ArrayList<SWCCompartmentInformation>> cell :  cells.entrySet()) {
		Callable<ArrayList<Double>> c = new PartialDensityComputer(cell);
		callables.add(c);
	}
	
	System.out.println(callables.size());
	ArrayList<Double> endresults = new ArrayList<Double>();
	
	
	
		long millisecondsStart = 0;
	try {
		long millisecondsStart2 = System.currentTimeMillis();
		List<Future<ArrayList<Double>>> results = executor.invokeAll(callables);
		ArrayList<ArrayList<Double>> subresults = new ArrayList<ArrayList<Double>>();
		for (Future<ArrayList<Double>> res : results) {
		  subresults.add(res.get());
		}
		
				executor.shutdown();
		
		
			long timeSpentInMilliseconds2 = System.currentTimeMillis() - millisecondsStart2;
			System.out.println("Parallel work: " +timeSpentInMilliseconds2/1000.0);
			
		millisecondsStart= System.currentTimeMillis();
// prefill (since exact cube number is not known until here)
		/*for (Double d : subresults.get(0)) {
		  endresults.add(0.0);
		}*/
		

		/** @todo below could also be done in parallel and average dendritic length*/
		/// here we collect from all geometries the length in each cube
		/// then we add it to endresults, note however we should average
		/// the total dendritic length in each sampling cube...
	        /** @todo note that this is also very slow because not parallel! -> should be done also in the above threads or below in new threads callables ...*/
		/*int index;
		for (ArrayList<Double> subres : subresults) { // subres = one cell with n sampling cubes
		  index = 0; // first cube
		  for (Double d : subres) {
			endresults.add(index, endresults.get(index) + d); // accumulate in each cube
			index++; // next cube -> this is wrong still, next cube must be incremented out of this for loop
		  }
		}*/
		/**
		 * @todo densities should be computed, get total dendritic length in each cube
		 *       then average by number of cells, then we have an average for each cube
		 * 	 depending on the visualization then, we need to create voxels with a
		 *       dependent-color
		 */

	  class PartialSumComputer implements Callable<Double> { 
		  private final ArrayList<Double> subres_of_cube;
		  @Override
		  public Double call() {
			  double sum = 0.;
			  for (double d : subres_of_cube) {
				  sum+=d;
			  }
			  return sum;
		  }

		  public PartialSumComputer(ArrayList<Double> subres_of_cube) {
			  this.subres_of_cube = subres_of_cube;
		  }
		  
	  }
		
	} catch (ExecutionException e) {
	  e.printStackTrace();
	} catch (InterruptedException e) {
	  e.printStackTrace();
	}

			long timeSpentInMilliseconds = System.currentTimeMillis() - millisecondsStart;
			System.out.println("Time for postprocess: " +timeSpentInMilliseconds/1000.0);
	return endresults;
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
				boolean intersects = RayPlaneIntersection(vOut, 0.0, p1, v1, v2, normal, 1e-6);
				if (intersects) {
					Vector3d temp = new Vector3d(vOut);
					temp.sub(p1);
					return temp.length();
				} else {
					return -1;
				}
				
			} else {
				Vector3d vOut = new Vector3d();
				boolean intersects = RayPlaneIntersection(vOut, 0.0, p1, v1, v2, normal, 1e-6);
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
			boolean intersects = RayPlaneIntersection(vOut1, 0.0, p1, v1, v2, normal, 1e-6);
			boolean intersects2 = RayPlaneIntersection(vOut2, 0.0, p2, v1, v2, normal, 1e-6);
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
			temp.add(v0); // starting vertex: this could be improved certainly
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
		} catch (KeyDuplicateException e) {
	    	System.err.println(e);
		} catch (KeySizeException e) {
	    	System.err.println(e);
		}
		return kd;
	}
	
	public static void main(String... args) {
	  /*
	  try {
	  computeDensity(parseStack(new File("/path/to/folder")));
	  } catch (IOException e) {
		
	  }*/
	  	HashMap<String, ArrayList<SWCCompartmentInformation>> cells = new HashMap<String, ArrayList<SWCCompartmentInformation>>(1);
		try {
			long millisecondsStart = System.currentTimeMillis();
			/// 256 test files (@10x10x10 resolution of sampling cube) used and cube dimensions of 5x5x5 tested (@8 files), is efficient, but the serial collect is slow (see above) -> the call() function in the parallel code
			/// should also be revisited for efficiency reasons!
			for (int i = 0; i < 8; i ++) {
			 	cells.put("dummy" + i, SWCUtility.parse(new File("data/02a_pyramidal2aFI.swc")));
			}
			long timeSpentInMilliseconds = System.currentTimeMillis() - millisecondsStart;
			System.out.println("Time for setup: " +timeSpentInMilliseconds/1000.0);
			
			ArrayList<Double> res = SWCUtility.computeDensity(cells);
			/*for (double d : res) {
				System.out.println("d: " + d);
			}*/
		
	 	} catch (IOException e) {
		 System.err.println("File not found: " + e);
		}
	 	
	  
	/*	try {
			ArrayList<SWCCompartmentInformation> info = parse(new File("/Users/stephan/Code/git/VRL-SWC-Density-Vis/data/02a_pyramidal2aFI.swc"));
			getIndicents(info);
			System.out.println("Incidents size: " + info.size());
		} catch (IOException ioe) {
			System.err.println("Error reading from file: " + ioe);
		}*/
	}
}
	