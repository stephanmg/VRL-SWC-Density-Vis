/// package's name
package edu.gcsc.vrl.swcdensityvis;

/// imports
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
import javax.vecmath.Vector3f;

/**
 *
 * @author stephan
 * @brief utilites for SWC files
 */
public final class SWCUtility {

	public final static float DEFAULT_WIDTH = 10.f;
	public final static float DEFAULT_HEIGHT = 10.f;
	public final static float DEFAULT_DEPTH = 10.f;
	public final static String DEFAULT_SELECTION = "ALL";
	public final static float EPS = 1e-12f;

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
					info.setConnectivity(new Pair<Integer, Integer>(Integer.parseInt(columns[0]) - 1,
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
	 * @return hashmap of arraylist of information for each compartment in
	 * each file
	 * @throws IOException
	 */
	@SuppressWarnings("NestedAssignment")
	public static HashMap<String, ArrayList<SWCCompartmentInformation>> parseStack(File folder) throws IOException {
		HashMap<String, ArrayList<SWCCompartmentInformation>> compartments = new HashMap<String, ArrayList<SWCCompartmentInformation>>();
		File[] swcFiles = folder.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".swc");
			}
		});

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
	@SuppressWarnings("serial")
	public static Pair<Vector3f, Vector3f> getBoundingBox(final Map.Entry<String, ArrayList<SWCCompartmentInformation>> cell) {
		return getBoundingBox(new HashMap<String, ArrayList<SWCCompartmentInformation>>() {
			{
				put(cell.getKey(), cell.getValue());
			}
		}
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

		return new Pair<Vector3f, Vector3f>(
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
	 * @brief get dimensions for a named cell
	 * @param cell
	 * @return
	 */
	@SuppressWarnings("serial")
	public static Vector3f getDimensions(final Map.Entry<String, ArrayList<SWCCompartmentInformation>> cell) {
		return getDimensions(new HashMap<String, ArrayList<SWCCompartmentInformation>>() {
			{
				put(cell.getKey(), cell.getValue());
			}
		});

	}

	/**
	 * @brief computes the density in an alternative way, i. e. iterates
	 * over the edges
	 * @param cells
	 * @return
	 */
	public static HashMap<Integer, Float> computeDensityAlternative(HashMap<String, ArrayList<SWCCompartmentInformation>> cells) {
		final Vector3f dims = SWCUtility.getDimensions(cells);
		final Pair<Vector3f, Vector3f> bounding = SWCUtility.getBoundingBox(cells);
		System.out.println("Dimensions of all cells: " + dims);
		System.out.println("Bounding box Min: " + bounding.getSecond() + ", Max:" + bounding.getFirst());

		/// sampling cube in geometry dimensions (i. e. µm!)
		final float width = 10.f;
		final float height = 10.f;
		final float depth = 10.f;

		class PartialDensityComputer implements Callable<HashMap<Integer, Float>> {

			/// store lengthes in the cuboids and the cell itself
			private volatile HashMap<Integer, Float> lengths = new HashMap<Integer, Float>();
			private volatile Map.Entry<String, ArrayList<SWCCompartmentInformation>> cell;

			/**
			 * @brief def ctor
			 */
			public PartialDensityComputer(Map.Entry<String, ArrayList<SWCCompartmentInformation>> cell) {
				this.cell = cell;
			}

			@Override
			@SuppressWarnings("ReturnOfCollectionOrArrayField")
			public HashMap<Integer, Float> call() {
				/// preprocess, determine characteristic edge length (one can chose also something different)
				HashMap<Vector3f, ArrayList<Vector3f>> incidents = getIndicents(cell.getValue());
				for (Map.Entry<Vector3f, ArrayList<Vector3f>> entry : incidents.entrySet()) {
					for (Vector3f edge : entry.getValue()) {
						Pair<Vector3f, Vector3f> bounding_e = EdgeUtility.getBounding(new Edge<Vector3f>(entry.getKey(), edge));
						Cuboid bounding_ee = new Cuboid(bounding_e.getFirst().x, bounding_e.getFirst().y, bounding_e.getFirst().y, bounding_e.getSecond().x, bounding_e.getSecond().y, bounding_e.getSecond().z);
						Cuboid min = new Cuboid(bounding_e.getFirst().x, bounding_e.getFirst().y, bounding_e.getFirst().y, width, depth, height);
						Cuboid max = new Cuboid(bounding_e.getSecond().x, bounding_e.getSecond().y, bounding_e.getSecond().z, width, depth, height);
						Pair<int[], int[]> bounding_ss = CuboidUtility.getSampleCuboidBounding(bounding_ee, min, max, width, depth, height);

						for (int i = bounding_ss.getFirst()[0]; i < bounding_ss.getSecond()[0]; i++) {
							for (int j = bounding_ss.getFirst()[1]; j < bounding_ss.getSecond()[1]; j++) {
								for (int k = bounding_ss.getFirst()[2]; k < bounding_ss.getSecond()[2]; k++) {
									float x = bounding_e.getFirst().x;
									float y = bounding_e.getFirst().y;
									float z = bounding_e.getFirst().z;

									Integer real_index = i * j * k;
									float len = EdgeSegmentWithinCuboid(x, y, z, width, height, depth, edge, new ArrayList<Vector3f>(Arrays.asList(entry.getKey())));
									if (len != 0) {
										if (lengths.containsKey(real_index)) {
											lengths.put(real_index, lengths.get(real_index) + len);
										} else {
											lengths.put(real_index, len);
										}
									}
								}
							}
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
		for (Map.Entry<String, ArrayList<SWCCompartmentInformation>> cell : cells.entrySet()) {
			Callable<HashMap<Integer, Float>> c = new PartialDensityComputer(cell);
			callables.add(c);
		}

		HashMap<Integer, Float> vals = new HashMap<Integer, Float>();
		/*
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
		
		 /// total length
		 float total_length = 0;
		 for (Map.Entry<Integer, Float> entry : vals.entrySet()) {
		 total_length += entry.getValue() / cells.size();
		 }
		
		 /// densities
		 for (Map.Entry<Integer, Float> entry : vals.entrySet()) {
		 entry.setValue(entry.getValue() / total_length);
		 }
		
		 System.out.println("Total dendritic length [\\mu m]: " + total_length);
		
		 System.out.println("Non-zero cuboids: " + vals.size());
		 long timeSpentInMillisecondsSerial = System.currentTimeMillis() - millisecondsStartSerial;
		 System.out.println("Serial work [s]: " +timeSpentInMillisecondsSerial/1000.0);
	
		 } catch (ExecutionException e) {
		 System.err.println(e);
		 } catch (InterruptedException e) {
		 System.err.println(e);
		 }*/
		return vals;
	}

	/**
	 * @brief compute dendritic length in cuboid
	 * @return a hashmap repreesentint dendritic length in each sampling
	 * cuboid
	 * @param cells
	 */
	public static HashMap<Integer, Float> computeDensity(HashMap<String, ArrayList<SWCCompartmentInformation>> cells) {
		return computeDensity(cells, DEFAULT_WIDTH, DEFAULT_HEIGHT, DEFAULT_DEPTH, DEFAULT_SELECTION);
	}

	public static HashMap<Integer, Float> computeDensity(HashMap<String, ArrayList<SWCCompartmentInformation>> cells, String type) {
		return computeDensity(cells, DEFAULT_WIDTH, DEFAULT_HEIGHT, DEFAULT_DEPTH, type);
	}
	
	/**
	 * @brief scales and transform the geometry
	 * @param cells_
	 * @param new_origin
	 * @param new_dim
	 * @param scaling_factor
	 * @return a scaled version of the original geometry
	 */
	public static HashMap<String, ArrayList<SWCCompartmentInformation>> scaleAndTransformAndCopyGeometry(HashMap<String, ArrayList<SWCCompartmentInformation>> cells_, Vector3f new_origin, Vector3f new_dim, float scaling_factor) {
		@SuppressWarnings("unchecked")
		HashMap<String, ArrayList<SWCCompartmentInformation>> cells = (HashMap<String, ArrayList<SWCCompartmentInformation>>) cells_.clone();
		
		for (Map.Entry<String, ArrayList<SWCCompartmentInformation>> entry : cells.entrySet()) {
			for (SWCCompartmentInformation compartment : entry.getValue()) {
				Vector3f coords = new Vector3f(compartment.getCoordinates());

				if (Math.abs(scaling_factor) < EPS) {
					// auto-scale mode
					Vector3f old_dim = SWCUtility.getDimensions(cells);
					float real_scaling_factor = (Collections.max(Arrays.asList(new_dim.x, new_dim.y, new_dim.z)) / (Collections.max(Arrays.asList(old_dim.x, old_dim.x, old_dim.z))));
					coords.scale(real_scaling_factor);
				} else {
					// scale with suppplied scaling factor
					coords.scale(scaling_factor);
				}
				
				// transform to new origin
				Vector3f min = SWCUtility.getBoundingBox(cells).getSecond();
				Vector3f direction = new Vector3f(min);
				direction.sub(new_origin);
				coords.add(new_origin);
				compartment.setCoordinates(coords);
			}
		}
		return cells;
	}
	public static HashMap<String, ArrayList<SWCCompartmentInformation>> scaleAndTransformAndCopyGeometry(HashMap<String, ArrayList<SWCCompartmentInformation>> cells_, Vector3f new_origin, Vector3f new_dim) {
		return scaleAndTransformAndCopyGeometry(cells_, new_origin, new_dim, 0);
	}

	/**
	 * @brief compute dendritic length in cuboid
	 * @return a hashmap representing dendritic length in each sampling
	 * cuboid
	 * @param cells
	 * @param width_
	 * @param height_
	 * @param depth_
	 * @param type
	 */
	public static HashMap<Integer, Float> computeDensity(HashMap<String, ArrayList<SWCCompartmentInformation>> cells, float width_, float height_, float depth_, final String type) {
		/// get dimensions and bounding box and report
		final Vector3f dims = SWCUtility.getDimensions(cells);
		final Pair<Vector3f, Vector3f> bounding = SWCUtility.getBoundingBox(cells);
		System.out.println("Dimensions of all cells: " + dims);
		System.out.println("Bounding box Min: " + bounding.getSecond() + ", Max:" + bounding.getFirst());

		/// sampling cube in geometry dimensions (i. e. µm!)
		final float width = width_;
		final float height = height_;
		final float depth = depth_;

		/**
		 * @brief thread, e. g. callable, which computes for one cell
		 * the dendritic length in each cuboid
		 * @todo performance penalties: i. build kdtree and operations
		 * on kdtree (maybe we don't need the kdtree, this could
		 * probably the case if we have geometries with a few edges!)
		 * ii. intersection algorithms iii. hashmap as sparse data
		 * structure, maybe we should chose some structure from the
		 * linear algebra for java package, cf. below
		 * @see la4j package @ http://la4j.org/
		 * @see profiler @ https://profiler.netbeans.org/
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
			@SuppressWarnings("ReturnOfCollectionOrArrayField")
			public HashMap<Integer, Float> call() {
				/// preprocess, determine characteristic edge length (one can chose also something different)
				int size = 0;
				HashMap<Vector3f, ArrayList<Vector3f>> incidents = getIndicents(cell.getValue(), type);
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
				for (float x = bounding.getSecond().x; x < bounding.getFirst().x; x += width) {
					for (float y = bounding.getSecond().y; y < bounding.getFirst().y; y += height) {
						for (float z = bounding.getSecond().z; z < bounding.getFirst().z; z += depth) {
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
							double[] upper = {x + width + lambda_x, y + height + lambda_y, z + depth + lambda_z};
							double[] lower = {x - lambda_x, y - lambda_y, z - lambda_z};

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

							float length = 0.f;

							/// a list of all edges within the bigger sampling cube bounding box
							for (ArrayList<Vector3f> elem : temps) {
								/// starting vertex is the last in attached metadata ArrayList
								Vector3f starting_vertex = new Vector3f(elem.get(elem.size() - 1));

								/// determine the amount of edge in each sampling cube
								length += EdgeSegmentWithinCuboid(
									x, y, z, width, height, depth,
									starting_vertex, elem);
							}

							/// if length is not zero in this cube, add it to the hashmap with cube_index
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
		for (Map.Entry<String, ArrayList<SWCCompartmentInformation>> cell : cells.entrySet()) {
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
			System.out.println("Parallel work [s]: " + timeSpentInMillisecondsParallel / 1000.0);

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

			/// total length
			float total_length = 0;
			for (Map.Entry<Integer, Float> entry : vals.entrySet()) {
				total_length += entry.getValue() / cells.size();
			}

			/// densities
			for (Map.Entry<Integer, Float> entry : vals.entrySet()) {
				entry.setValue(entry.getValue() / total_length);
			}

			System.out.println("Total dendritic length [\\mu m]: " + total_length);

			System.out.println("Non-zero cuboids: " + vals.size());
			long timeSpentInMillisecondsSerial = System.currentTimeMillis() - millisecondsStartSerial;
			System.out.println("Serial work [s]: " + timeSpentInMillisecondsSerial / 1000.0);

		} catch (ExecutionException e) {
			System.err.println(e);
		} catch (InterruptedException e) {
			System.err.println(e);
		}
		return vals;
	}

	/**
	 * @brief @todo check if this is correct -> introduce JUnit Tests. case
	 * 1 is verified, case 2, 3 needs to be verified
	 * @param x
	 * @param y
	 * @param z
	 * @param width
	 * @param height
	 * @param depth
	 * @param p1
	 * @param end_vertices
	 * @return
	 */
	public static float EdgeSegmentWithinCuboid(float x, float y, float z, float width, float height, float depth, Vector3f p1, ArrayList<Vector3f> end_vertices) {
		float length = 0.f;
		for (Vector3f p2 : end_vertices) {
			/// Case 1: Both vertices inside cube (verified to work)
			if (((p1.x <= x + width && p1.x >= x)
				&& (p1.y <= y + height && p1.y >= y)
				&& (p1.z <= z + depth && p1.z >= z))
				&& ((p2.x <= x + width && p2.x >= x)
				&& (p2.y <= y + height && p2.y >= y)
				&& (p2.z <= z + depth && p2.z >= z))) {
				Vector3f temp = new Vector3f(p1);
				temp.sub(p2);
				length += temp.length();
				/*if (temp.length() > Math.sqrt(width*width +height*height + depth*depth)) {
				 System.err.println("erroneous segment!");
				 } else {
				 length += temp.length();
				 }*/
				/// Case 2: One vertex inside the cube (p1 in, p2 out) (dominates if the sampling cube is of the size of the largest edge length in the geometry
			} else if (((p2.x > x + width || p2.x < x)
				|| (p2.y > y + height || p2.y < y)
				|| (p2.z > z + depth || p2.z < z))
				&& (((p1.x <= x + width && p1.x >= x)
				&& (p1.y <= y + height && p1.y >= y)
				&& (p1.z <= z + depth && p1.z >= z)))) {
				Vector3f dir = new Vector3f(p2);
				dir.sub(p1);
				Pair<Boolean, Pair<Float, Float>> res = RayBoxIntersection(p1, dir, new Vector3f(x, y, z), new Vector3f(x + width, y + height, z + depth));
				if (res.getFirst()) {
					Vector3f x1 = new Vector3f(p1);
					Vector3f scaled1 = new Vector3f(dir);
					scaled1.scale(res.getSecond().getFirst());
					x1.add(scaled1);
					Vector3f segment = new Vector3f(x1);
					segment.sub(p1);
					length += segment.length();
					/*if (segment.length() > Math.sqrt(width*width +height*height + depth*depth)) {
					 System.err.println("erroneous segment!");
					 } else {
					 length += segment.length();
					 }*/
				}
				/// Case 2: One vertex inside cube (p1 out, p2 in)
			} else if (((p1.x > x + width || p1.x < x)
				|| (p1.y > y + height || p1.y < y)
				|| (p1.z > z + depth || p1.z < z))
				&& (((p2.x <= x + width && p2.x >= x)
				&& (p2.y <= y + height && p2.y >= y)
				&& (p2.z <= z + depth && p2.z >= z)))) {
				Vector3f dir = new Vector3f(p1);
				dir.sub(p2);
				Pair<Boolean, Pair<Float, Float>> res = RayBoxIntersection(p2, dir, new Vector3f(x, y, z), new Vector3f(x + width, y + height, z + depth));
				if (res.getFirst()) {
					Vector3f x1 = new Vector3f(p2);
					Vector3f scaled_dir = new Vector3f(dir);
					scaled_dir.scale(res.getSecond().getFirst());
					x1.add(scaled_dir);
					Vector3f segment = new Vector3f(x1);
					segment.sub(p2);
					length += segment.length();
					/*if (segment.length() > Math.sqrt(width*width +height*height + depth*depth)) {
					 System.err.println("Erroneous segment!");
					 } else {
					 length += segment.length();
					 }*/
				}
				/// Case 3: End vertex and start vertex outside of the sampling cube, i. e. p1 and p2 outside (dominates if the sampling cube is very small) 
			} else if (((p1.x > x + width || p1.x < x)
				|| (p1.y > y + height || p1.y < y)
				|| (p1.z > z + depth || p1.z < z))
				&& ((p2.x > x + width || p2.x < x)
				|| (p2.y > y + height || p2.y < y)
				|| (p2.z > z + depth || p2.z < z))) {
				Vector3f dir = new Vector3f(p2);
				dir.sub(p1);
				Pair<Boolean, Pair<Float, Float>> res = LineBoxIntersection(p1, p2, new Vector3f(x, y, z), new Vector3f(x + width, y + height, z + depth));
				if (res.getFirst()) {
					Vector3f x1 = new Vector3f(p2);
					Vector3f x2 = new Vector3f(p2);
					Vector3f scaled1 = new Vector3f(dir);
					Vector3f scaled2 = new Vector3f(dir);

					scaled1.scale(res.getSecond().getFirst());
					scaled2.scale(res.getSecond().getSecond());

					/// get intersection points
					x1.add(scaled1);
					x2.add(scaled2);

					/// determine segment
					Vector3f segment = new Vector3f(x2);
					segment.sub(x1);
					length += segment.length();
					/*if (segment.length() > Math.sqrt(width*width +height*height + depth*depth)) {
					 System.err.println("Erroneous segment!");
					 } else {
					 length += segment.length();
					 }*/
				}
			}
		}
		return length;
	}

	/**
	 * @brief cleans the choice string
	 * @param type
	 * @return
	 */
	public static String get_clean_choice(String type) {
		return type.replace(" ", "_").replace("(", "").replace(")", "").toUpperCase();
	}

	/**
	 * @brief get a selection of incidents
	 * @param cell
	 * @param type
	 * @return
	 */
	public static HashMap<Vector3f, ArrayList<Vector3f>> getIndicents(final ArrayList<SWCCompartmentInformation> cell, String type) {
		// get clean type string
		String type_clean = get_clean_choice(type);
		// get index
		int index = SWCCompartmentType.valueOf(type_clean).ordinal();

		if (DEFAULT_SELECTION.equals(type)) {
			return getIndicents(cell);
		} else {
			final HashMap<Vector3f, ArrayList<Vector3f>> incidents = new HashMap<Vector3f, ArrayList<Vector3f>>(cell.size());
			for (SWCCompartmentInformation info : cell) {
				ArrayList<Vector3f> temp = new ArrayList<Vector3f>();
				Vector3f v0 = info.getCoordinates();
				for (SWCCompartmentInformation info2 : cell) {
					if (info.getIndex() == info2.getConnectivity().getSecond() - 1) {
						if (index == info.getType()) {
							temp.add(info2.getCoordinates());
						}
					}
				}
				temp.add(v0); // starting vertex: this could be improved certainly
				incidents.put(v0, temp);
			}
			return incidents;
		}
	}

	/**
	 * @brief get all incident vertices
	 * @todo see implementation notes concerning speedup of computation
	 * @param cell
	 * @return
	 */
	public static HashMap<Vector3f, ArrayList<Vector3f>> getIndicents(final ArrayList<SWCCompartmentInformation> cell) {
		final HashMap<Vector3f, ArrayList<Vector3f>> incidents = new HashMap<Vector3f, ArrayList<Vector3f>>(cell.size());
		for (SWCCompartmentInformation info : cell) {
			ArrayList<Vector3f> temp = new ArrayList<Vector3f>();
			Vector3f v0 = info.getCoordinates();
			for (SWCCompartmentInformation info2 : cell) {
				if (info.getIndex() == info2.getConnectivity().getSecond() - 1) {
					temp.add(info2.getCoordinates());
				}
			}
			temp.add(v0); // starting vertex: this could be improved certainly
			incidents.put(v0, temp);
		}
		return incidents;
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
	}

	/**
	 * @brief builds a kd tree for the cell
	 * @param cell
	 * @return the newly created kd tree
	 */
	public static KDTree<ArrayList<Vector3f>> buildKDTree(final HashMap<Vector3f, ArrayList<Vector3f>> cell) {
		KDTree<ArrayList<Vector3f>> kd = new KDTree<ArrayList<Vector3f>>(3);
		try {
			for (Map.Entry<Vector3f, ArrayList<Vector3f>> entry : cell.entrySet()) {
				Vector3f vec = entry.getKey();
				double[] key = {vec.x, vec.y, vec.z};
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
	 * @brief computes intersection between ray and box
	 * @note however we can use SwappablePair implementation to swap pairs
	 * @param rayFrom
	 * @param rayDir
	 * @param boxMin
	 * @param boxMax
	 * @return
	 */
	public static Pair<Boolean, Pair<Float, Float>> RayBoxIntersection(Vector3f rayFrom, Vector3f rayDir, Vector3f boxMin, Vector3f boxMax) {

		float t1, t2;
		boolean bMinMaxSet = false;
		float eps = 1.0e-6f;
		float tMin = -1f, tMax = -1f;
		float tNearOut = 0, tFarOut = 0;

		if (Math.abs(rayDir.x) > eps) {
			t1 = (boxMin.x - rayFrom.x) / rayDir.x;
			t2 = (boxMax.x - rayFrom.x) / rayDir.x;
			if (t1 > t2) {
				float temp = t1;
				t1 = t2;
				t2 = temp;
			}
			tMin = t1;
			tMax = t2;
			bMinMaxSet = true;
		} else {
			if (rayFrom.x < boxMin.x) {
				return new Pair<Boolean, Pair<Float, Float>>(false, new Pair<Float, Float>(-1f, -1f));
			}
			if (rayFrom.x > boxMax.x) {
				return new Pair<Boolean, Pair<Float, Float>>(false, new Pair<Float, Float>(-1f, -1f));
			}
		}

		if (Math.abs(rayDir.y) > eps) {
			t1 = (boxMin.y - rayFrom.y) / rayDir.y;
			t2 = (boxMax.y - rayFrom.y) / rayDir.y;
			if (t1 > t2) {
				float temp = t1;
				t1 = t2;
				t2 = temp;
			}
			if (bMinMaxSet) {
				if ((t1 <= tMax) && (t2 >= tMin)) {
					tMin = Math.max(t1, tMin);
					tMax = Math.min(t2, tMax);
				} else {
					return new Pair<Boolean, Pair<Float, Float>>(false, new Pair<Float, Float>(-1f, -1f));
				}
			} else {
				tMin = t1;
				tMax = t2;
			}
			bMinMaxSet = true;
		} else {
			if (rayFrom.y < boxMin.y) {
				return new Pair<Boolean, Pair<Float, Float>>(false, new Pair<Float, Float>(-1f, -1f));
			}
			if (rayFrom.y > boxMax.y) {
				return new Pair<Boolean, Pair<Float, Float>>(false, new Pair<Float, Float>(-1f, -1f));
			}
		}

		if (Math.abs(rayDir.z) > eps) {
			t1 = (boxMin.z - rayFrom.z) / rayDir.z;
			t2 = (boxMax.z - rayFrom.z) / rayDir.z;
			if (t1 > t2) {
				float temp = t1;
				t1 = t2;
				t2 = temp;
			}
			if (bMinMaxSet) {
				if ((t1 <= tMax) && (t2 >= tMin)) {
					tMin = Math.max(t1, tMin);
					tMax = Math.min(t2, tMax);
				} else {
					return new Pair<Boolean, Pair<Float, Float>>(false, new Pair<Float, Float>(-1f, -1f));
				}
			} else {
				tMin = t1;
				tMax = t2;
			}
			bMinMaxSet = true;
		} else {
			if (rayFrom.z < boxMin.z) {
				return new Pair<Boolean, Pair<Float, Float>>(false, new Pair<Float, Float>(-1f, -1f));
			}
			if (rayFrom.z > boxMax.z) {
				return new Pair<Boolean, Pair<Float, Float>>(false, new Pair<Float, Float>(-1f, -1f));
			}
		}

		if (bMinMaxSet) {
			if (Math.abs(tMin) > Math.abs(tMax)) {
				float temp = tMin;
				tMin = tMax;
				tMax = temp;
			}
			tNearOut = tMin;
			tFarOut = tMax;
			return new Pair<Boolean, Pair<Float, Float>>(true, new Pair<Float, Float>(tNearOut, tFarOut));
		} else {
			if (BoxProbe(rayFrom, boxMin, boxMax)) {
				if (tNearOut != 0) {
					tNearOut = 0;
				}
				if (tFarOut != 0) {
					tFarOut = 0;
				}

				return new Pair<Boolean, Pair<Float, Float>>(true, new Pair<Float, Float>(tNearOut, tFarOut));
			} else {
				return new Pair<Boolean, Pair<Float, Float>>(false, new Pair<Float, Float>(0.f, 0.f));
			}
		}
	}

	/**
	 * @brief calculates the intersection between line and box
	 * @param v1
	 * @param v2
	 * @param boxMin
	 * @param boxMax
	 * @return
	 */
	public static Pair<Boolean, Pair<Float, Float>> LineBoxIntersection(Vector3f v1, Vector3f v2, Vector3f boxMin, Vector3f boxMax) {
		Vector3f dir = new Vector3f(v2);
		dir.sub(v1);
		Pair<Boolean, Pair<Float, Float>> res = RayBoxIntersection(v1, dir, boxMin, boxMax);
		if (res.getFirst()) {
			float tNear = res.getSecond().getFirst();
			float tFar = res.getSecond().getSecond();
			if ((tNear >= 0 && tNear <= 1.0) && (tFar >= 0 && tNear <= 1.0)) {
				return res;
			}
		}
		return new Pair<Boolean, Pair<Float, Float>>(false, new Pair<Float, Float>(0f, 0f));
	}

	/**
	 * @brief determine if the ray starting point is within the bounding box
	 * @param rayFrom
	 * @param boxMin
	 * @param boxMax
	 * @return
	 */
	public static boolean BoxProbe(Vector3f rayFrom, Vector3f boxMin, Vector3f boxMax) {
		if (rayFrom.x < boxMin.x || rayFrom.x > boxMax.x) {
			return false;
		}

		if (rayFrom.y < boxMin.y || rayFrom.y > boxMax.y) {
			return false;
		}

		if (rayFrom.z < boxMin.z || rayFrom.z > boxMax.z) {
			return false;
		}

		return true;
	}

	/**
	 * @brief main method
	 * @param args
	 */
	public static void main(String... args) {
		/*
		 HashMap<String, ArrayList<SWCCompartmentInformation>> cells = new HashMap<String, ArrayList<SWCCompartmentInformation>>();
		 try {
		 long millisecondsStart = System.currentTimeMillis();
		 for (int i = 0; i < 8; i++) {
		 cells.put("dummy" + i, SWCUtility.parse(new File("data/02a_pyramidal2aFI_original.swc")));
		 //cells.put("dummy" + i, SWCUtility.parse(new File("data/BC130711AB.swc")));
		 }
		
		 long timeSpentInMilliseconds = System.currentTimeMillis() - millisecondsStart;
		 System.out.println("Time for setup [s]: " +timeSpentInMilliseconds/1000.0);
			
		 HashMap<Integer, Float> res = SWCUtility.computeDensity(cells);
		 //	HashMap<Integer, Float> res = SWCUtility.computeDensityAlternative(cells);
			
		 millisecondsStart = System.currentTimeMillis();
		 for (Map.Entry<Integer, Float> e : res.entrySet()) {
		 System.out.println("Cuboid cell #" + e.getKey() + " with dendritic length of " + e.getValue());
		 }
			
		 timeSpentInMilliseconds = System.currentTimeMillis() - millisecondsStart;
		 System.out.println("Time for output [s]: " +timeSpentInMilliseconds/1000.0);
			
		 /// debug code here	
		 /*
		 int size_edges = 0;
		 for (Map.Entry<String, ArrayList<SWCCompartmentInformation>> cell : cells.entrySet()) {
		 HashMap<Vector3f, ArrayList<Vector3f>> incidents = SWCUtility.getIndicents(cell.getValue());
		 for (Map.Entry<Vector3f, ArrayList<Vector3f>> inci : incidents.entrySet()) {
		 //System.err.println("Size of edges:" + inci.getValue().size());
		 size_edges += inci.getValue().size() - 1;
		 }
		 System.out.println("Size of edges: " + size_edges);
		 /*System.out.println("Compartment: " + inci.getKey());
		 for (Vector3f vertex : inci.getValue()) {
		 System.out.println("Vertex:" + vertex);
		 }*/

		/*
		 } catch (IOException e) {
		 System.err.println("File not found: " + e);
		 }*/
	}
}
