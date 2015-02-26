/// package's name
package edu.gcsc.vrl.swcdensityvis.importer.XML;

/// imports
import edu.gcsc.vrl.densityvis.Density;
import edu.gcsc.vrl.swcdensityvis.data.Edge;
import edu.gcsc.vrl.swcdensityvis.importer.DensityData;
import edu.gcsc.vrl.swcdensityvis.importer.TreeDensityComputationStrategy;
import static edu.gcsc.vrl.swcdensityvis.util.SWCUtility.EdgeSegmentWithinCuboid;
import static edu.gcsc.vrl.swcdensityvis.util.SWCUtility.buildKDTree;
import edu.wlu.cs.levy.CG.KDTree;
import edu.wlu.cs.levy.CG.KeySizeException;
import eu.mihosoft.vrl.reflection.Pair;
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
 */
public final class TreeDensityComputationStrategyXML implements TreeDensityComputationStrategy {
	private HashMap<String, ArrayList<Edge<Vector3f>>> cells = new HashMap<String, ArrayList<Edge<Vector3f>>>();
	private final float width_ = 10;
	private final float depth_ = 10;
	private final float height_= 10;
	/**
	 * 
	 */
	public TreeDensityComputationStrategyXML() {
		
	}
	
	public TreeDensityComputationStrategyXML(HashMap<String, ArrayList<Edge<Vector3f>>> cells) {
		this.cells = cells;
	}

	/**
	 * 
	 * @return 
	 */
	private Pair<Vector3f, Vector3f> getBoundingBox() {
		ArrayList<Float> temp_x = new ArrayList<Float>();
		ArrayList<Float> temp_y = new ArrayList<Float>();
		ArrayList<Float> temp_z = new ArrayList<Float>();
		for (ArrayList<Edge<Vector3f>> entry : cells.values()) {
			for (Edge<Vector3f> edge : entry) {
				temp_x.add(edge.getFrom().x);
				temp_x.add(edge.getTo().x);
				temp_y.add(edge.getFrom().y);
				temp_y.add(edge.getTo().y);
				temp_z.add(edge.getFrom().z);
				temp_z.add(edge.getTo().z);
			}
		}

		return new Pair<Vector3f, Vector3f>(
			new Vector3f(Collections.max(temp_x), Collections.max(temp_y), Collections.max(temp_z)),
			new Vector3f(Collections.min(temp_x), Collections.min(temp_y), Collections.min(temp_z))
		);

	}

	/**
	 * 
	 * @return 
	 */
	@Override
	public Vector3f getDimension() {
		Pair<Vector3f, Vector3f> bounding = getBoundingBox();
			return new Vector3f(
			Math.abs(bounding.getFirst().x - bounding.getSecond().x),
			Math.abs(bounding.getFirst().y - bounding.getSecond().y),
			Math.abs(bounding.getFirst().z - bounding.getSecond().z)
		);
	}
	
	/**
	 * @todo this is not correct! extract the connectivity information from the XML file!
	 * @todo we overestimate and underestimate the real connections!
	 */
	private HashMap<Vector3f, ArrayList<Vector3f>> getIncidents(ArrayList<Edge<Vector3f>> cell) {
		final HashMap<Vector3f, ArrayList<Vector3f>> incidents = new HashMap<Vector3f, ArrayList<Vector3f>>();
		for (Edge<Vector3f> edge : cell) {
			incidents.put(edge.getFrom(), new ArrayList<Vector3f>(Arrays.asList(edge.getTo(), edge.getFrom())));
		}
		return incidents;
	}

	/**
	 * 
	 * @return 
	 */
	@Override
	public Density computeDensity() {
		/// get dimensions and bounding box and report
		final Vector3f dims = getDimension();
		final Pair<Vector3f, Vector3f> bounding = getBoundingBox();
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
			private volatile ArrayList<Edge<Vector3f>> cell;

			/// note: characteristic edge length (arithmetic mean for now, could use min, max instead too)
			private volatile float lambda_x = 0.f;
			private volatile float lambda_y = 0.f;
			private volatile float lambda_z = 0.f;

			/**
			 * @brief def ctor
			 */
			public PartialDensityComputer(ArrayList<Edge<Vector3f>> cell) {
				this.cell = cell;
			}

			@Override
			@SuppressWarnings("ReturnOfCollectionOrArrayField")
			public HashMap<Integer, Float> call() {
				/// preprocess, determine characteristic edge length (one can chose also something different)
				int size = 0;
				HashMap<Vector3f, ArrayList<Vector3f>> incidents = getIncidents(cell);
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
		for (Map.Entry<String, ArrayList<Edge<Vector3f>>> cell : cells.entrySet()) {
			Callable<HashMap<Integer, Float>> c = new PartialDensityComputer(cell.getValue());
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
		return new XMLDensityTreeImpl(vals, bounding, (int) width, (int) height, (int) depth);
	}

	/**
	 * 
	 * @param data 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void setDensityData(DensityData data) {
		this.cells = (HashMap<String, ArrayList<Edge<Vector3f>>>) data.getDensityData();
	}
	
	/**
	 * 
	 * @return 
	 */
	@Override
	public Object getCenter() {
		Pair<Vector3f, Vector3f> minMax = getBoundingBox();
		return new Vector3f( (minMax.getFirst().x + minMax.getSecond().x) / 2,
				     (minMax.getFirst().y + minMax.getSecond().y) / 2,
			             (minMax.getFirst().z + minMax.getSecond().z) / 2);
	}

}
