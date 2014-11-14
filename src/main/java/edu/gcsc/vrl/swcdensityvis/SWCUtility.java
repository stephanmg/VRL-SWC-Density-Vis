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
import javax.vecmath.Vector3f;

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
	  public static ArrayList<Vector3f> getSamplingCuboidNormals(Vector3f p1, Vector3f p2, Vector3f p3, Vector3f p4, Vector3f p5, Vector3f p6, Vector3f p7, Vector3f p8) {
		ArrayList<Vector3f> temp = new ArrayList<Vector3f>();
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
	  private static Vector3f calculateNormal(Vector3f p1, Vector3f p2, Vector3f p3, Vector3f p4) {
		Vector3f a = new Vector3f(p3);
		a.sub(p2);
		Vector3f b = new Vector3f(p1);
		b.sub(p2);
		Vector3f c = new Vector3f();
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
					info.setCoordinates(new Vector3f(Float.parseFloat(columns[2]),
						Float.parseFloat(columns[3]),
						Float.parseFloat(columns[4])));

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
	public static Pair<Vector3f, Vector3f> getBoundingBox(final ArrayList<SWCCompartmentInformation> cell) {
		return getBoundingBox(	new HashMap<String, ArrayList<SWCCompartmentInformation>>()
						{{ put("Anonymous cell", cell); }}
					);
	}
	
	/**
	 * @brief get bounding box for a bunch of cells
	 * @param cells input cells 
	 * @return pair min max coordinates 3d
	 */
	public static Pair<Vector3f, Vector3f> getBoundingBox(HashMap<String, ArrayList<SWCCompartmentInformation>> cells) {
		ArrayList<Float> temp_x = new ArrayList<Float>();
		ArrayList<Float> temp_y = new ArrayList<Float>();
		ArrayList<Float> temp_z = new ArrayList<Float>();
		
		for (Map.Entry<String, ArrayList<SWCCompartmentInformation>> entry : cells.entrySet()) {
			for (SWCCompartmentInformation info : entry.getValue()) {
				temp_x.add(info.getCoordinates().x);
				temp_y.add(info.getCoordinates().y);
				temp_z.add(info.getCoordinates().z);
			}
		}
		
		return new Pair<Vector3f, Vector3f>
			(
			new Vector3f(Collections.max(temp_x), Collections.max(temp_y), Collections.max(temp_z)),
			new Vector3f(Collections.min(temp_x), Collections.min(temp_y), Collections.min(temp_z)) 
			);

	}
	
	/**
	 * @brief get dimensions (width, height and depth) for the cells 
	 * @param cells
	 * @return the dimensions as a vector
	 */
	public static Vector3f getDimensions(HashMap<String, ArrayList<SWCCompartmentInformation>> cells) {
		Pair<Vector3f, Vector3f> bounding = getBoundingBox(cells);
		return new Vector3f(
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
	  final Vector3f dims = SWCUtility.getDimensions(cells);
	  final Pair<Vector3f, Vector3f> bounding = SWCUtility.getBoundingBox(cells);
	  final int number_of_cells = cells.size();
	  System.out.println("dims: " + dims);
	  // in µm!
	  final float width = 1;
	  final float height = 1;
	  final float depth = 1;
	  
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
		   KDTree<ArrayList<Vector3f>> tree = buildKDTree(getIndicents(cell.getValue()));
		   
		   /** @todo test: should finish in one step -> and it does. */
		   /// we iterate over the bounding box and generate sampling cubes
		   /// the cubes must not necessarily be created, but are for simplicity 
		   /// reasons for now handy 
		   /** @todo use bounding box min values for x y z and iterate 
		    * to bounding box max values with width height and depth 
		    */
		   for (float x = bounding.getSecond().x; x < bounding.getFirst().x; x+=width) {
			 for (float y = bounding.getSecond().y; y < bounding.getFirst().y; y+=height) {
			   for (float z = bounding.getSecond().z; z < bounding.getFirst().z; z+=depth) {
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
				 Vector3f p1 = new Vector3f(x, y+height, z);
				 Vector3f p2 = new Vector3f(x+width, y+height, z);
				 Vector3f p3 = new Vector3f(x, y, z);
				 Vector3f p4 = new Vector3f(x+width, y, z);
				 
				 Vector3f p5 = new Vector3f(x, y+height, z+depth);
				 Vector3f p6 = new Vector3f(x+width, y+height, z+depth);
				 Vector3f p7 = new Vector3f(x, y, z+depth);
				 Vector3f p8 = new Vector3f(x+width, y, z+depth);
				 
				 double[] upper = {x+width, y+height, z+depth};
				 double[] lower = {x, y, z};
				 
				 List<ArrayList<Vector3f>> temps = new ArrayList<ArrayList<Vector3f>>();
				 try {
				 	temps = tree.range(lower, upper);
				 } catch (KeySizeException e) {
				 	System.err.println("Keysize exception!");  
				 }
				 	
				 //System.err.println("only one sampling cube");
				 float length = 0.f;
				 for (ArrayList<Vector3f> elem : temps) { // a list of nodes which are in the range lower to upper
				  Vector3f starting_vertex = new Vector3f(elem.get(0)); // starting vertex
			   	  //elem.remove(0); // starting vertex, this can certainly be improved -> this is wrong
				  /** @todo this is wrong, since the first 
				   * vertex is needed in the next iteration again!
				   */
				   
				/// each node in range has attached the incident edges we calculated 
				/// previously (we need however the starting vertex somehow, see above)
				/// for now the starting vertex, aka the compartment, is the first 
				/// vertex in the attached meta-data arraylist for each compartment 
				/// we have inserted in the kd tree before
				   for (Vector3f elem2 : elem) { 
					ArrayList<Vector3f> normals = SamplingCuboid.getSamplingCuboidNormals(p1, p2, p3, p4, p5, p6, p7, p8);
					float val = 0.f;
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

	  class PartialSumComputer implements Callable<Float> { 
		  private final ArrayList<Float> subres_of_cube;
		  @Override
		  public Float call() {
			  float sum = 0.f;
			  for (float d : subres_of_cube) {
				  sum+=d;
			  }
			  return sum;
		  }

		  public PartialSumComputer(ArrayList<Float> subres_of_cube) {
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
	public static float EdgeSegmentWithinCube(float x, float y, float z, float width, float height, float depth, Vector3f p1, Vector3f p2, Vector3f v1, Vector3f v2, Vector3f normal) {
		/// Case 1: Both vertices inside cube
		if ( ((p1.x <= x+width || p1.x >= x) &&
		      (p1.y <= y+height || p1.y >= y) && 
		      (p1.z <= z+depth || p1.z >= z)) &&
		     ((p2.x <= x+width || p2.x >= x) &&
		      (p2.y <= y+height || p2.y >= y) && 
		      (p2.z <= z+depth || p2.z >= z)) ) {
			Vector3f temp = new Vector3f(p1);
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
				Vector3f vOut = new Vector3f();
				boolean intersects = RayPlaneIntersection(vOut, 0.0f, p1, v1, v2, normal, 1.0e-6f);
				if (intersects) {
					Vector3f temp = new Vector3f(vOut);
					temp.sub(p1);
					return temp.length();
				} else {
					return -1;
				}
				
			} else {
				Vector3f vOut = new Vector3f();
				boolean intersects = RayPlaneIntersection(vOut, 0.0f, p1, v1, v2, normal, 1.0e-6f);
				if (intersects) {
					Vector3f temp = new Vector3f(vOut);
					temp.sub(p1);
					return temp.length();
				} else {
					return -1;
				}
			}
		/// Case 3: Both vertices outside the cube
		} else {
			Vector3f vOut1 = new Vector3f();
			Vector3f vOut2 = new Vector3f();
			boolean intersects = RayPlaneIntersection(vOut1, 0.0f, p1, v1, v2, normal, 1.0e-6f);
			boolean intersects2 = RayPlaneIntersection(vOut2, 0.0f, p2, v1, v2, normal, 1.0e-6f);
			if (intersects && intersects2) {
				Vector3f temp = new Vector3f(vOut1);
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
	public static boolean RayPlaneIntersection(Vector3f vOut, float tOut, Vector3f rayFrom, Vector3f rayDir, Vector3f p, Vector3f normal, float tol) {
		float denom = rayDir.dot(normal);
		if (Math.abs(denom) < tol) {
			return false;
		} else {
			Vector3f v = new Vector3f(p);
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
	public static boolean LineSquareIntersection(Vector3f S1, Vector3f S2, Vector3f S3, Vector3f R1, Vector3f R2, float tol) {
	Vector3f dS21 = new Vector3f(S2);
	Vector3f dS31 = new Vector3f(S3);
	Vector3f n = new Vector3f(); 
	Vector3f dR = new Vector3f(R1);

	dS21.sub(S1);
	dS31.sub(S1);
	n.cross(dS21, dS31);
	dR.sub(R2);
	
	float ndotdR = n.dot(dR);

        if (Math.abs(ndotdR) < tol) { 
            return false; /// indicate no intersection
        }
	
	Vector3f R1subS1 = new Vector3f(R1);
	R1subS1.sub(S1);
	
        float t = -n.dot(R1subS1) / ndotdR;
        Vector3f M = new Vector3f(R1);
	Vector3f dRscaled = new Vector3f(dR);
	dRscaled.scale(t);
	R1.add(dRscaled);

        Vector3f dMS1 = new Vector3f(M);
	dMS1.sub(S1);
	
        float u = dMS1.dot(dS21);
        float v = dMS1.dot(dS31);

        return (u >= 0.0f && u <= dS21.dot(dS21)
             && v >= 0.0f && v <= dS31.dot(dS31));
	}
	/**
	 * @brief get all incident vertices
	 * @param cell
	 * @return 
	 */
	public static HashMap<Vector3f, ArrayList<Vector3f>> getIndicents(ArrayList<SWCCompartmentInformation> cell) {
		/// split into initializing phase (init keys of ArrayList<ArrayList<Vector3f>> to v0)
		/// and a populating phase (i. e. add to ArrayList the incident to V0) to get rid of O(n^2) for loop
		/// if necessary in the future (then we need to make the convention, that the arraylist is ordered
		/// in the same way as the compartments are appearing in the single swc file...
		/*
		// init phase
		ArrayList<ArrayList<Vector3f>> incidents2 = new ArrayList<ArrayList<Vector3f>>(cell.size());
		// populate phase
		for (SWCCompartmentInformation info : cell) {
		  incidents2.get(info.getConnectivity().getSecond()).add(info.getCoordinates());
		}
		*/
	  
		HashMap<Vector3f, ArrayList<Vector3f>> incidents = new HashMap<Vector3f, ArrayList<Vector3f>>(cell.size());
		for (SWCCompartmentInformation info : cell) {
			ArrayList<Vector3f> temp = new ArrayList<Vector3f>();
			Vector3f v0 = info.getCoordinates();
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
	public static KDTree<ArrayList<Vector3f>> buildKDTree(HashMap<Vector3f, ArrayList<Vector3f>> cell) {
		KDTree<ArrayList<Vector3f>> kd = new KDTree<ArrayList<Vector3f>>(3);
		try {
		   for (Map.Entry<Vector3f, ArrayList<Vector3f>> entry : cell.entrySet()) {
		  	Vector3f vec = entry.getKey();
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
			/*for (float d : res) {
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
	