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
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import javax.vecmath.Tuple3f;
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
	 * @brief compute dendritic length in cuboid
	 * @return a hashmap representing dendritic length in each sampling cuboid
	 * @param cells
	 */
	public static HashMap<Integer, Float> computeDensity(HashMap<String, ArrayList<SWCCompartmentInformation>> cells) {
	  /// get dimensions and bounding box and report
	  final Vector3f dims = SWCUtility.getDimensions(cells);
	  final Pair<Vector3f, Vector3f> bounding = SWCUtility.getBoundingBox(cells);
	  System.out.println("Dimensions of all cells: " + dims);
	  System.out.println("Bounding box Min: " + bounding.getSecond()  + ", Max:" + bounding.getFirst());
	  
	  /// sampling cube in geometry dimensions (i. e. µm!)
	  final float width = 1.f;
	  final float height = 1.f; 
	  final float depth = 1.f; 
	
	  /**
	   * @brief thread, e. g. callable, which computes for one cell the dendritic length in each cuboid
	   *
	   * @todo RayPlaneIntersection is errorneous, this needs to be fixed immediately before resolving
	   *       potential speed bottlenecks (see the next todos) or the cube construction may be (!!) errorneous.
	   * @todo probably the cuboids dont need to be created explicit, thus performance should increase
	   * @todo revise the intersection algorithms in general for speed bottlenecks and correcteness:
	   *        the edge length detection still seems to be at a point errorneous...
	   * @todo revise the buildKDtree (* most sever speed trap)
	   * 
	   * @todo KD TREE is not necessary, we could just go over the EDGES directly... if we have very few edges
	   *       but how do we get edges which do not have one vertex at least in the cuboid then?! 
	   *       (e. g. Case 3 ideas added already below)
	   * @todo probably we should use sparse data structure instead of the HashMap approach (see below)
	   * 
	   * @see la4j package @ http://la4j.org/
	   */
	  class PartialDensityComputer implements Callable<HashMap<Integer, Float>> {
		/// store lengthes in the cuboids and the cell itself
		private volatile HashMap<Integer, Float> lengths = new HashMap<Integer, Float>();
		private volatile Map.Entry<String, ArrayList<SWCCompartmentInformation>> cell;
		
		/// note: characteristic edge length (arithmetic mean for now, could use min, max instead too)
		private volatile float lambda_x = 0.f;
		private volatile float lambda_y = 0.f;
		private volatile float lambda_z = 0.f;
	
		/**
		 * @brief def ctor
		 */
		public PartialDensityComputer(Map.Entry<String, ArrayList<SWCCompartmentInformation>> cell) {
		  this.cell = cell;
		}

 		 @Override 
		 public HashMap<Integer, Float> call() {
		   /// preprocess, determine characteristic edge length
		   int size = 0;
		   HashMap<Vector3f, ArrayList<Vector3f>> incidents = getIndicents(cell.getValue());
		   //ArrayList<Float> e_lengths = new ArrayList<Float>(incidents.size());
		   for (Map.Entry<Vector3f, ArrayList<Vector3f>> entry : incidents.entrySet()) {
			   ArrayList<Vector3f> vecs = entry.getValue();
			   size += vecs.size();
			   for (Vector3f vec : vecs) {
				Vector3f temp = new Vector3f(entry.getKey());
				temp.sub(vec);
				lambda_x += Math.abs(temp.x);
				lambda_y += Math.abs(temp.y);
				lambda_z += Math.abs(temp.z);
			   }
		   }
		   lambda_x /= (size - incidents.size());
		   lambda_y /= (size - incidents.size());
		   lambda_z /= (size - incidents.size());
		   
		   System.out.println("Characteristic edge length for x-coordinate [\\mu m]: " + lambda_x);
		   System.out.println("Characteristic edge length for y-coordinate [\\mu m]: " + lambda_y);
		   System.out.println("Characteristic edge length for z-coordinate [\\mu m]: " + lambda_z);
		   
		   /// increate performance. if bounding box already larger as characteristic edge length
		   /// then it is safe to set the lambda offset to zero
		   if (width  > 2*lambda_x) { lambda_x = 0; }
		   if (height > 2*lambda_y) { lambda_y = 0; }
		   if (depth  > 2*lambda_z) { lambda_z = 0; }
		   
		   /// create a kd tree for the geometry, attach to leaf all compartment nodes
	           /// each lead node gets attached the vertices which are connected to the
	           /// leaf node with and edge (getIncidents)
		   KDTree<ArrayList<Vector3f>> tree = buildKDTree(incidents);
		   
		   /// iterate with the width, height, depth over the bounding box of the cells
		   /// note, that the cuboids get created explicit, which may not be necessary
		   /// note: it could be more efficient to iterate instead over the BoundingBoxes of each Edge
		   ///       then we need an indexing scheme for inserting into a hashmap since the number of
		   ///       boxes can be very large, i. e. > 10e9 boxes need to be the same for every geometry!
		   int cube_index = 0;
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
				 
				 /// we search within the charachteristic length boundaries, then we consider
				 /// later only width and height and length boxes for intersections or we
				 /// may use Gillians approach 
				
				 /// note: optimization is pari with non-optimization for boundary for now
				 /*float lambda_x_min = lambda, lambda_y_min = lambda, lambda_z_min = lambda;
				 float lambda_x_max = lambda, lambda_y_max = lambda, lambda_z_max = lambda;
				 
				 if (lambda < (x - bounding.getSecond().x)) lambda_x_min = 0;
				 if (lambda < (y - bounding.getSecond().y)) lambda_y_min = 0;
				 if (lambda < (z - bounding.getSecond().z)) lambda_z_min = 0;
				 
				 if (lambda < (bounding.getSecond().x - x)) lambda_x_max = 0;
				 if (lambda < (bounding.getSecond().y - y)) lambda_y_max = 0;
				 if (lambda < (bounding.getSecond().z - z)) lambda_z_max = 0;
				 
				 double[] upper = {x+width+lambda_x_max, y+height+lambda_y_max, z+depth+lambda_z_max};
				 double[] lower = {x-lambda_x_min, y-lambda_y_min, z-lambda_z_min};*/

				 double[] upper = {x+width+lambda_x, y+height+lambda_y, z+depth+lambda_z};
				 double[] lower = {x-lambda_x, y-lambda_y, z-lambda_z};
				 
				 ///double[] upper = {x+width+lambda, y+height+lambda, z+depth+lambda};
				 ///double[] lower = {x-lambda, y-lambda, z-lambda};
				 
				 List<ArrayList<Vector3f>> temps = new ArrayList<ArrayList<Vector3f>>();
				 /// speed bottleneck is here the kdtree obvious
				 /// note: that up to 1,000,000,000 iterations it's quite fine
				 /// and done within 13 seconds (when using #procs geometries)
				 /// but if we go to 1,000,000,000,000 i. e. sampling cube
				 /// sizes of 0.001 in geometry units it gets slow...
				 try {
				 	temps = tree.range(lower, upper);
				 } catch (KeySizeException e) {
				 	System.err.println("Keysize exception: " + e);  
				 }
				 
				// a list of nodes which are in the range lower to upper
				 float length = 0.f;
				 
				// sampling cube normals stay the same within one cuboid and also the plane describing vertices inside the plane
				 for (ArrayList<Vector3f> elem : temps) { 
				ArrayList<Vector3f> cube_points = new ArrayList<Vector3f>(Arrays.asList(p1, p5, p3, p2, p7, p8));
				ArrayList<Vector3f> normals = SamplingCuboid.getSamplingCuboidNormals(p1, p2, p3, p4, p5, p6, p7, p8); 
				// starting vertex (last vertex in list ...)
				  Vector3f starting_vertex = new Vector3f(elem.get(elem.size()-1));
				   
				/// each node in range has attached the incident edges we calculated 
				/// previously (we need however the starting vertex somehow, see above)
				/// for now the starting vertex, aka the compartment, is the first 
				/// vertex in the attached meta-data arraylist for each compartment 
				/// we have inserted in the kd tree before
				 //  for (Vector3f elem2 : elem) { 
					float val = 0.f;
					/// determine the amount of edge in the sampling cube for each edge
					val += EdgeSegmentWithinCuboid(x, y, z, width, height, depth, starting_vertex, elem, cube_points, normals);
					// val += EdgeSegmentWithinCuboid(x, y, z, width, height, depth, starting_vertex, elem2, p1, p2, normals.get(0)); // p1, p2 vertices in plane and normal: front 
					//val += EdgeSegmentWithinCuboid(x, y, z, width, height, depth, starting_vertex, elem2, p5, p6, normals.get(1)); // p1, p2 vertices in plane and normal: rear
				//	val += EdgeSegmentWithinCuboid(x, y, z, width, height, depth, starting_vertex, elem2, p3, p4, normals.get(2)); // p1, p2 vertices in plane and normal: bottom
				//	val += EdgeSegmentWithinCuboid(x, y, z, width, height, depth, starting_vertex, elem2, p1, p2, normals.get(3)); // p1, p2 vertices in plane and normal: top
				//	val += EdgeSegmentWithinCuboid(x, y, z, width, height, depth, starting_vertex, elem2, p3, p7, normals.get(4)); // p1, p2 vertices in plane and normal: left 
				//	val += EdgeSegmentWithinCuboid(x, y, z, width, height, depth, starting_vertex, elem2, p4, p8, normals.get(5)); // p1, p2 vertices in plane and normal: right 
					length+=val;
				 ///  }
				 }
				 // add summed length to cuboids, if length is not null, to hashmap.
				 // this is sparse then
				 if (length != 0) {
			 		lengths.put(cube_index, length);
				 }
				 cube_index++;
			   }
			 }
		   }
		   return lengths;
		 }
	 }
 
	// take number of available processors and create a fixed thread pool,
	// the executor executes then at most the number of available processors
	// threads to calculate the partial density (Callable PartialDensityComputer)
	int processors = Runtime.getRuntime().availableProcessors();
	ExecutorService executor = Executors.newFixedThreadPool(processors);
	System.out.println("Number of processors: " + processors);
	
	ArrayList<Callable<HashMap<Integer, Float>>> callables = new ArrayList<Callable<HashMap<Integer, Float>>>();
	for (Map.Entry<String, ArrayList<SWCCompartmentInformation>> cell :  cells.entrySet()) {
		Callable<HashMap<Integer, Float>> c = new PartialDensityComputer(cell);
		callables.add(c);
	}
	
	HashMap<Integer, Float> vals = new HashMap<Integer, Float>();
	try {
		/// perform parallel work
		long millisecondsStartParallel = System.currentTimeMillis();
		List<Future<HashMap<Integer, Float>>> results = executor.invokeAll(callables);
		ArrayList<HashMap<Integer, Float>> subresults = new ArrayList<HashMap<Integer, Float>>();
		for (Future<HashMap<Integer, Float>> res : results) {
		  subresults.add(res.get());
		}
		executor.shutdown();
		long timeSpentInMillisecondsParallel = System.currentTimeMillis() - millisecondsStartParallel;
		System.out.println("Parallel work [s]: " +timeSpentInMillisecondsParallel/1000.0);
			
		long millisecondsStartSerial = System.currentTimeMillis();
		/// serial summation. note, the below could also be done in parallel somehow
		for (HashMap<Integer, Float> result : subresults) {
			for (Map.Entry<Integer, Float> map_entry : result.entrySet()) {
				if (vals.containsKey(map_entry.getKey())) {
					vals.put(map_entry.getKey(), map_entry.getValue() + vals.get(map_entry.getKey()));
				} else {
					vals.put(map_entry.getKey(), map_entry.getValue()); 
				}
			}
		}
		
		double total_length = 0;
		for (Map.Entry<Integer, Float> entry : vals.entrySet()) {
			total_length += entry.getValue() / cells.size();
		}
		System.out.println("Total dendritic length [\\mu m]: " + total_length);
		
		System.out.println("Non-zero cuboids: " + vals.size());
		long timeSpentInMillisecondsSerial = System.currentTimeMillis() - millisecondsStartSerial;
		System.out.println("Serial work [s]: " +timeSpentInMillisecondsSerial/1000.0);
	
	} catch (ExecutionException e) {
	  e.printStackTrace();
	} catch (InterruptedException e) {
	  e.printStackTrace();
	}
	/// return values 
	return vals;
}
	 
	/**
	 * @brief  
	 * @todo check if this is correct
	 * @param x
	 * @param y
	 * @param z
	 * @param width
	 * @param height
	 * @param depth
	 * @param p1
	 * @param end_vertices
	 * @param cube_points
	 * @param cube_normals
	 * @return 
	 */
	public static float EdgeSegmentWithinCuboid(float x, float y, float z, float width, float height, float depth, Vector3f p1, ArrayList<Vector3f> end_vertices, ArrayList<Vector3f> cube_points, ArrayList<Vector3f> cube_normals) {
		float length = 0.f;
	for (Vector3f p2 : end_vertices) {
		/// Case 1: Both vertices inside cube
		if ( ((p1.x <= x+width && p1.x >= x) &&
		      (p1.y <= y+height && p1.y >= y) && 
		      (p1.z <= z+depth && p1.z >= z)) &&
		     ((p2.x <= x+width && p2.x >= x) &&
		      (p2.y <= y+height && p2.y >= y) && 
		      (p2.z <= z+depth && p2.z >= z)) ) {
			//System.err.println("if");
			Vector3f temp = new Vector3f(p1);
			temp.sub(p2);
			length += temp.length();
			/**
			 * @todo implement here
			 */
		/// Case 2: One vertex inside cube or one vertex outside the cube or all vertices outside cube
			/// this method is seriously wrong (therefore we immensely overestimate this in addition
			/// to the existing errorneous calculations)
		 
		/// Case 2: End vertex outside of current sampling cube
		} else if ( ((p2.x >= x+width || p2.x <= x) &&
		      (p2.y >= y+height || p2.y <= y) && 
		      (p2.z >= z+depth || p2.z <= z)) &&
			 (! ((p1.x <= x+width && p1.x >= x) &&
		      (p1.y <= y+height && p1.y >= y) && 
		      (p1.z <= z+depth && p1.z >= z))) ) {
			Vector3f dir = new Vector3f(p2);
			dir.sub(p1);
			for (int i = 0; i < 6; i++) {
				Pair<Boolean, Vector3f> intersects = RayPlaneIntersection(p1, dir, cube_points.get(i), cube_normals.get(i), 1.0e-12f);
				if (intersects.getFirst()) {
					Vector3f temp = new Vector3f(intersects.getSecond());
					temp.sub(p1);
					length += temp.length();
					/**
					 * @todo we need LinePlaneIntersection here ... and cubes are wrongly defined above (cf getCubeNormals and cube vertices ...
					 */
					//return 0;
				}
			}
		/// Case 3: End vertex and start vertex outside of the sampling cube
		} else {
			return 0;
		}
		/// note however: 
		/// using characteristic length := max { length(Edge) } \forall Edge in Edges
		/// Case 3: does not happen -> we can also use Gillian's approach, as the
		/// boxes here get slightly larger then the user supplies in the end ...
		/// Case 3 needs to be handled separately, since we need the "middle segment" between 
		/// the intersecting points of the planes (cf above)
		/**
		 * @todo implement here
		 */
	}	
	return length;
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
	 * @todo: note better implement a function like RayBoxIntersection, or we use all faces of the cube and call RayPlaneIntersection... revise for speedup of computation
	 */
	public static float EdgeSegmentWithinCuboid(float x, float y, float z, float width, float height, float depth, Vector3f p1, Vector3f p2, Vector3f v1, Vector3f v2, Vector3f normal) {
		/// Case 1: Both vertices inside cube
		if ( ((p1.x <= x+width && p1.x >= x) &&
		      (p1.y <= y+height && p1.y >= y) && 
		      (p1.z <= z+depth && p1.z >= z)) &&
		     ((p2.x <= x+width && p2.x >= x) &&
		      (p2.y <= y+height && p2.y >= y) && 
		      (p2.z <= z+depth && p2.z >= z)) ) {
			//System.err.println("if");
			Vector3f temp = new Vector3f(p1);
			temp.sub(p2);
			return temp.length();
		/// Case 2: One vertex inside cube 
		} else if ( ((p1.x <= x+width && p1.x >= x) &&
		      (p1.y <= y+height && p1.y >= y) && 
		      (p1.z <= z+depth && p1.z >= z)) || 
		     ((p2.x <= x+width && p2.x >= x) &&
		      (p2.y <= y+height && p2.y >= y) && 
		      (p2.z <= z+depth && p2.z >= z)) ) {
			//System.err.println("else if");
			if (((p1.x <= x+width && p1.x >= x) &&
		      		(p1.y <= y+height && p1.y >= y) && 
		      		(p1.z <= z+depth && p1.z >= z))) {
				/// if p1 inside, construct line starting from p1
				Vector3f vOut = new Vector3f();
				Pair<Boolean, Vector3f> intersects = RayPlaneIntersection(p1, v1, v2, normal, 1.0e-6f);
				if (intersects.getFirst()) {
					Vector3f temp = new Vector3f(vOut);
					temp.sub(p1);
					//return temp.length();
					return 0;
				} else {
					return 0;
				}
				
			} else {
				//System.err.println("else");
				Vector3f vOut = new Vector3f();
				Pair<Boolean, Vector3f> intersects = RayPlaneIntersection(p2, v1, v2, normal, 1.0e-6f);
				if (intersects.getFirst()) {
					Vector3f temp = new Vector3f(vOut);
					temp.sub(p1);
					//return temp.length();
					return 0;
				} else {
					return 0;
				}
			}
		/// Case 3: Both vertices outside the cube (can not happen, but must be handled!)
		} else {
			System.err.println("else branch (in the implementation this cannot happen)!");
			Vector3f vOut1 = new Vector3f();
			Vector3f vOut2 = new Vector3f();
			Pair<Boolean, Vector3f> intersects = RayPlaneIntersection(p1, v1, v2, normal, 1.0e-6f);
			Pair<Boolean, Vector3f> intersects2 = RayPlaneIntersection(p2, v1, v2, normal, 1.0e-6f);
			if (intersects.getFirst() && intersects2.getFirst()) {
				Vector3f temp = new Vector3f(vOut1);
				temp.sub(vOut2);
				return temp.length();
			} else {
				return 0;
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
	public static Pair<Boolean, Vector3f> RayPlaneIntersection(Vector3f rayFrom, Vector3f rayDir, Vector3f p, Vector3f normal, float tol) {
		float denom = rayDir.dot(normal);
		if (Math.abs(denom) < tol) {
			return new Pair<Boolean, Vector3f>(false, new Vector3f());
		} else {
			Vector3f v = new Vector3f(p);
			v.sub(rayFrom);
			Float tOut = v.dot(normal) / denom;
			v.scale(tOut, rayDir);
			Vector3f vOut = new Vector3f();
			vOut.add(rayFrom, v);
			/// this actually is line plane intersection?! here is something wrong ...
			if (tOut <= 1. + tol && tOut >= -tol) {
				return new Pair<Boolean, Vector3f>(true, vOut);
			} else {
				return new Pair<Boolean, Vector3f>(false, vOut);
			}	
		}
	}
	
	/**
	 * @brief get all incident vertices
	 * @todo see implementation notes concerning speedup of computation
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
				if (info.getIndex() == info2.getConnectivity().getSecond()-1) {
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
	
	/**
	 * @brief main method
	 * @param args 
	 */
	public static void main(String... args) {
	   	HashMap<String, ArrayList<SWCCompartmentInformation>> cells = new HashMap<String, ArrayList<SWCCompartmentInformation>>();
		try {
			long millisecondsStart = System.currentTimeMillis();
			for (int i = 0; i < 8; i++) {
			 	cells.put("dummy" + i, SWCUtility.parse(new File("data/02a_pyramidal2aFI_original.swc")));
			}

		/*	for (Map.Entry<String, ArrayList<SWCCompartmentInformation>> cell : cells.entrySet()) {
		HashMap<Vector3f, ArrayList<Vector3f>> incidents = SWCUtility.getIndicents(cell.getValue());
		for (Map.Entry<Vector3f, ArrayList<Vector3f>> inci : incidents.entrySet()) {
			if (inci.getValue().size() >= 3) {
				System.err.println("Size of edges:" + inci.getValue().size());
			}
		}
			}*/
			/*System.out.println("Compartment: " + inci.getKey());
			for (Vector3f vertex : inci.getValue()) {
				System.out.println("Vertex:" + vertex);
			}*/

			
			long timeSpentInMilliseconds = System.currentTimeMillis() - millisecondsStart;
			System.out.println("Time for setup [s]: " +timeSpentInMilliseconds/1000.0);
			
			HashMap<Integer, Float> res = SWCUtility.computeDensity(cells);
			
			millisecondsStart = System.currentTimeMillis();
			for (Map.Entry<Integer, Float> e : res.entrySet()) {
				System.out.println("Cuboid cell #" + e.getKey() + " with dendritic length of " + e.getValue());
			}
			
			
			timeSpentInMilliseconds = System.currentTimeMillis() - millisecondsStart;
			System.out.println("Time for output [s]: " +timeSpentInMilliseconds/1000.0);
		
	 	} catch (IOException e) {
		 System.err.println("File not found: " + e);
		}
	}
}
	