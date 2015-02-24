/// package's name
package edu.gcsc.vrl.swcdensityvis.importer.XML;

/// imports
import edu.gcsc.vrl.densityvis.Density;
import edu.gcsc.vrl.densityvis.VoxelSet;
import edu.gcsc.vrl.swcdensityvis.data.Cuboid;
import edu.gcsc.vrl.swcdensityvis.data.Edge;
import edu.gcsc.vrl.swcdensityvis.importer.DensityData;
import edu.gcsc.vrl.swcdensityvis.importer.EdgeDensityComputationStrategy;
import edu.gcsc.vrl.swcdensityvis.importer.SWC.SWCCompartmentInformation;
import edu.gcsc.vrl.swcdensityvis.util.CuboidUtility;
import edu.gcsc.vrl.swcdensityvis.util.EdgeUtility;
import edu.gcsc.vrl.swcdensityvis.util.SWCUtility;
import static edu.gcsc.vrl.swcdensityvis.util.SWCUtility.DEFAULT_DEPTH;
import static edu.gcsc.vrl.swcdensityvis.util.SWCUtility.DEFAULT_HEIGHT;
import static edu.gcsc.vrl.swcdensityvis.util.SWCUtility.DEFAULT_SELECTION;
import static edu.gcsc.vrl.swcdensityvis.util.SWCUtility.DEFAULT_WIDTH;
import static edu.gcsc.vrl.swcdensityvis.util.SWCUtility.EdgeSegmentWithinCuboid;
import static edu.gcsc.vrl.swcdensityvis.util.SWCUtility.computeDensity;
import static edu.gcsc.vrl.swcdensityvis.util.SWCUtility.getBoundingBox;
import static edu.gcsc.vrl.swcdensityvis.util.SWCUtility.getIndicents;
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
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

/**
 * @brief implement the strategy here ... 
 * @author stephan
 */
public class EdgeDensityComputationStrategyXML implements EdgeDensityComputationStrategy {
	private HashMap<String, ArrayList<Edge<Vector3f>>> cells = new HashMap<String, ArrayList<Edge<Vector3f>>>();

	/**
	 * 
	 */
	public EdgeDensityComputationStrategyXML() {
		
	}
	
	/**
	 * @param cells
	 * @todo: idea to get the cells in it, we need to proceed as follows:
	 * 			1. parse the files by parseStack() and we use densityVisualizer.parseStack()
	 * 		        2. we can return the parsed data by getCells()
	 *                      3. the strategy get's the cells
	 */
	public EdgeDensityComputationStrategyXML(HashMap<String, ArrayList<Edge<Vector3f>>> cells) {
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
	private Vector3f getDimensions() {
		Pair<Vector3f, Vector3f> bounding = getBoundingBox();
			return new Vector3f(
			Math.abs(bounding.getFirst().x - bounding.getSecond().x),
			Math.abs(bounding.getFirst().y - bounding.getSecond().y),
			Math.abs(bounding.getFirst().z - bounding.getSecond().z)
		);
	}
	
	/**
	 * 
	 * @return 
	 */
	@Override
	public Density computeDensity() {
		final Pair<Vector3f, Vector3f> bounding = getBoundingBox();
		System.out.println("Dimensions of all cells: " + getDimensions());
		System.out.println("Bounding box Min: " + bounding.getSecond() + ", Max:" + bounding.getFirst());

		/// sampling cube in geometry dimensions (i. e. µm!)
		final float width = 10.f;
		final float height = 10.f;
		final float depth = 10.f;

		class PartialDensityComputer implements Callable<HashMap<Integer, Float>> {

			/// store lengthes in the cuboids and the cell itself
			private volatile HashMap<Integer, Float> lengths = new HashMap<Integer, Float>();
			private volatile ArrayList<Edge<Vector3f>> cell;

			/**
			 * @brief def ctor
			 */
			public PartialDensityComputer(ArrayList<Edge<Vector3f>> cell) {
				this.cell = cell;
			}

			@Override
			@SuppressWarnings("ReturnOfCollectionOrArrayField")
			public HashMap<Integer, Float> call() {
				for (Edge<Vector3f> edge : this.cell) {
						Pair<Vector3f, Vector3f> bounding_e = EdgeUtility.getBounding(new Edge<Vector3f>(edge.getFrom(), edge.getTo()));
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
									System.err.println("real_index: " + real_index);
									float len = EdgeSegmentWithinCuboid(x, y, z, width, height, depth, edge.getFrom(), new ArrayList<Vector3f>(Arrays.asList(edge.getTo())));
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
		return new XMLDensityImpl(vals, bounding, (int) width, (int) height, (int) depth);
	}

	@Override
	@SuppressWarnings("unchecked")
	public void setDensityData(DensityData data) {
		this.cells = (HashMap<String, ArrayList<Edge<Vector3f>>>) data.getDensityData();
	}
}
