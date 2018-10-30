/// package's name
package edu.gcsc.vrl.swcdensityvis.marching_cubes;

/// imports
import com.sun.j3d.utils.geometry.GeometryInfo;
import com.sun.j3d.utils.geometry.NormalGenerator;
import edu.gcsc.vrl.densityvis.VoxelSet;
import edu.gcsc.vrl.swcdensityvis.data.Edge;
import edu.gcsc.vrl.swcdensityvis.importer.DensityVisualizable;
import eu.mihosoft.vrl.reflection.Pair;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import javax.media.j3d.GeometryArray;
import javax.media.j3d.Shape3D;
import javax.media.j3d.TriangleArray;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

/**
 * @brief parallel marching cubes
 * @author stephanmg <stephan@syntaktischer-zucker.de>
 */
public class MarchingCubes {
	/// lerp
	private static float[] lerp(float[] vec1, float[] vec2, float alpha) {
		return new float[]{vec1[0] + (vec2[0] - vec1[0]) * alpha, vec1[1] + (vec2[1] - vec1[1]) * alpha, vec1[2] + (vec2[2] - vec1[2]) * alpha};
	}

	/// marching cubes
	private static void marchingCubesFloat(float[] values, int[] volDim, int volZFull, float[] voxDim, float isoLevel, int offset, CallbackMC callback) {
		ArrayList<Point3f> vertices = new ArrayList<Point3f>();
		// Actual position along edge weighted according to function values.
		float vertList[][] = new float[12][3];

		// Calculate maximal possible axis value (used in vertice normalization)
		float maxX = voxDim[0] * (volDim[0] - 1);
		float maxY = voxDim[1] * (volDim[1] - 1);
		float maxZ = voxDim[2] * (volZFull - 1);
		float maxAxisVal = Math.max(maxX, Math.max(maxY, maxZ));

		// Volume iteration
		for (int z = 0; z < volDim[2] - 1; z++) {
			for (int y = 0; y < volDim[1] - 1; y++) {
				for (int x = 0; x < volDim[0] - 1; x++) {
                   			// Indices pointing to cube vertices
					//              pyz  ___________________  pxyz
					//                  /|                 /|
					//                 / |                / |
					//                /  |               /  |
					//          pz   /___|______________/pxz|
					//              |    |              |   |
					//              |    |              |   |
					//              | py |______________|___| pxy
					//              |   /               |   /
					//              |  /                |  /
					//              | /                 | /
					//              |/__________________|/
					//             p                     px
					int p = x + (volDim[0] * y) + (volDim[0] * volDim[1] * (z + offset)),
						px = p + 1,
						py = p + volDim[0],
						pxy = py + 1,
						pz = p + volDim[0] * volDim[1],
						pxz = px + volDim[0] * volDim[1],
						pyz = py + volDim[0] * volDim[1],
						pxyz = pxy + volDim[0] * volDim[1];

					//				  X              Y                    Z
					float position[] = new float[]{x * voxDim[0], y * voxDim[1], (z + offset) * voxDim[2]};

					// Voxel intensities
					float value0 = values[p],
						value1 = values[px],
						value2 = values[py],
						value3 = values[pxy],
						value4 = values[pz],
						value5 = values[pxz],
						value6 = values[pyz],
						value7 = values[pxyz];

					// Voxel is active if its intensity is above isolevel
					int cubeindex = 0;
					if (value0 > isoLevel) {
						cubeindex |= 1;
					}
					if (value1 > isoLevel) {
						cubeindex |= 2;
					}
					if (value2 > isoLevel) {
						cubeindex |= 8;
					}
					if (value3 > isoLevel) {
						cubeindex |= 4;
					}
					if (value4 > isoLevel) {
						cubeindex |= 16;
					}
					if (value5 > isoLevel) {
						cubeindex |= 32;
					}
					if (value6 > isoLevel) {
						cubeindex |= 128;
					}
					if (value7 > isoLevel) {
						cubeindex |= 64;
					}

					// Fetch the triggered edges
					int bits = LookupTables.EDGE_TABLE[cubeindex];

					// If no edge is triggered... skip
					if (bits == 0) {
						continue;
					}

					// Interpolate the positions based od voxel intensities
					float mu = 0.5f;

					// bottom of the cube
					if ((bits & 1) != 0) {
						mu = (float) ((isoLevel - value0) / (value1 - value0));
						vertList[0] = lerp(position, new float[]{position[0] + voxDim[0], position[1], position[2]}, mu);
					}
					if ((bits & 2) != 0) {
						mu = (float) ((isoLevel - value1) / (value3 - value1));
						vertList[1] = lerp(new float[]{position[0] + voxDim[0], position[1], position[2]}, new float[]{position[0] + voxDim[0], position[1] + voxDim[1], position[2]}, mu);
					}
					if ((bits & 4) != 0) {
						mu = (float) ((isoLevel - value2) / (value3 - value2));
						vertList[2] = lerp(new float[]{position[0], position[1] + voxDim[1], position[2]}, new float[]{position[0] + voxDim[0], position[1] + voxDim[1], position[2]}, mu);
					}
					if ((bits & 8) != 0) {
						mu = (float) ((isoLevel - value0) / (value2 - value0));
						vertList[3] = lerp(position, new float[]{position[0], position[1] + voxDim[1], position[2]}, mu);
					}
					// top of the cube
					if ((bits & 16) != 0) {
						mu = (float) ((isoLevel - value4) / (value5 - value4));
						vertList[4] = lerp(new float[]{position[0], position[1], position[2] + voxDim[2]}, new float[]{position[0] + voxDim[0], position[1], position[2] + voxDim[2]}, mu);
					}
					if ((bits & 32) != 0) {
						mu = (float) ((isoLevel - value5) / (value7 - value5));
						vertList[5] = lerp(new float[]{position[0] + voxDim[0], position[1], position[2] + voxDim[2]}, new float[]{position[0] + voxDim[0], position[1] + voxDim[1], position[2] + voxDim[2]}, mu);
					}
					if ((bits & 64) != 0) {
						mu = (float) ((isoLevel - value6) / (value7 - value6));
						vertList[6] = lerp(new float[]{position[0], position[1] + voxDim[1], position[2] + voxDim[2]}, new float[]{position[0] + voxDim[0], position[1] + voxDim[1], position[2] + voxDim[2]}, mu);
					}
					if ((bits & 128) != 0) {
						mu = (float) ((isoLevel - value4) / (value6 - value4));
						vertList[7] = lerp(new float[]{position[0], position[1], position[2] + voxDim[2]}, new float[]{position[0], position[1] + voxDim[1], position[2] + voxDim[2]}, mu);
					}
					// vertical lines of the cube
					if ((bits & 256) != 0) {
						mu = (float) ((isoLevel - value0) / (value4 - value0));
						vertList[8] = lerp(position, new float[]{position[0], position[1], position[2] + voxDim[2]}, mu);
					}
					if ((bits & 512) != 0) {
						mu = (float) ((isoLevel - value1) / (value5 - value1));
						vertList[9] = lerp(new float[]{position[0] + voxDim[0], position[1], position[2]}, new float[]{position[0] + voxDim[0], position[1], position[2] + voxDim[2]}, mu);
					}
					if ((bits & 1024) != 0) {
						mu = (float) ((isoLevel - value3) / (value7 - value3));
						vertList[10] = lerp(new float[]{position[0] + voxDim[0], position[1] + voxDim[1], position[2]}, new float[]{position[0] + voxDim[0], position[1] + voxDim[1], position[2] + voxDim[2]}, mu);
					}
					if ((bits & 2048) != 0) {
						mu = (float) ((isoLevel - value2) / (value6 - value2));
						vertList[11] = lerp(new float[]{position[0], position[1] + voxDim[1], position[2]}, new float[]{position[0], position[1] + voxDim[1], position[2] + voxDim[2]}, mu);
					}

					// construct triangles -- get correct vertices from triTable.
					int i = 0;
					// "Re-purpose cubeindex into an offset into triTable."
					cubeindex <<= 4;

					while (LookupTables.TRIANGLE_TABLE[cubeindex + i] != -1) {
						int index1 = LookupTables.TRIANGLE_TABLE[cubeindex + i];
						int index2 = LookupTables.TRIANGLE_TABLE[cubeindex + i + 1];
						int index3 = LookupTables.TRIANGLE_TABLE[cubeindex + i + 2];

						// Add triangles vertices normalized with the maximal possible value
						vertices.add(new Point3f(vertList[index3][0] / maxAxisVal - 0.5f, vertList[index3][1] / maxAxisVal - 0.5f, vertList[index3][2] / maxAxisVal - 0.5f));
						vertices.add(new Point3f(vertList[index2][0] / maxAxisVal - 0.5f, vertList[index2][1] / maxAxisVal - 0.5f, vertList[index2][2] / maxAxisVal - 0.5f));
						vertices.add(new Point3f(vertList[index1][0] / maxAxisVal - 0.5f, vertList[index1][1] / maxAxisVal - 0.5f, vertList[index1][2] / maxAxisVal - 0.5f));

						i += 3;
					}
				}
			}
		}
		callback.setVertices(vertices);
		callback.run();
	}
	
	/**
	 * @brief callback for marching cubes
	 */
	abstract class CallbackMC implements Runnable {
		private ArrayList<Point3f> vertices;

		@SuppressWarnings("AssignmentToCollectionOrArrayFieldFromParameter")
		void setVertices(ArrayList<Point3f> vertices) {
			this.vertices = vertices;
		}

		@SuppressWarnings("ReturnOfCollectionOrArrayField")
		ArrayList<Point3f> getVertices() {
			return this.vertices;
		}
	}

	/**
	 * @brief creates a scalar field for the marching cubes algorithm
	 * @param voxels
	 * @param visualizer
	 * Note: To ensure consistency one needs to iterate in the same way over
	 * the density data as the DensityComputationStrategies do, e.g. see the
	 * triple for loop in TreeDensityComputationStrategy in the XML package
	 * @return 
	 */
	private static float[] createScalarField(List<? extends VoxelSet> voxels, DensityVisualizable visualizer) {
		Vector3f center = (Vector3f) visualizer.getCenter();
		Vector3f dim = (Vector3f) visualizer.getDimension();
		int index = 0;
		final float[] scalarField = new float[voxels.size() * (int) dim.x / voxels.get(0).getWidth()];
		for (float x = center.x - dim.x; x < center.x + center.x; x += voxels.get(0).getWidth()) {
			for (float y = center.y - dim.y; y < center.y + center.y; y += voxels.get(0).getHeight()) {
				for (float z = center.z - dim.z; z < center.z + center.z; z += voxels.get(0).getDepth()) {
					scalarField[index] = (float) voxels.get(index).getValue();
					System.err.println("Density value: " + (float) voxels.get(index).getValue());
					System.err.println("Voxels coordinates: " +  voxels.get(index).getX() + ", " + voxels.get(index).getY() + ", " + voxels.get(index).getZ());
					index++;
				}
			}
		}
		System.err.println("scalarField length: " + scalarField.length);
		return scalarField;
	}


	/**
	 * @brief the marching cubes for our density voxels
	 * @param voxels
	 * @param visualizer
	 * @param scale
	 * @param offset
	 * @return 
	 */
	@SuppressWarnings("CallToPrintStackTrace")
	public Shape3D testMC2(List<? extends VoxelSet> voxels, DensityVisualizable visualizer, float scale) {
		/// dimension of density data
		Vector3f dim = (Vector3f) visualizer.getDimension();
		Vector3f center = (Vector3f) visualizer.getCenter();
		float width = dim.x;
		float depth = dim.y;
		float height = dim.z;

		/// dimension of individual voxels (Note: Could be improved)
		float voxWidth = voxels.get(0).getWidth();
		float voxHeight = voxels.get(0).getHeight();
		float voxDepth = voxels.get(0).getDepth();

		/// size of scalar field
		final int sizeX = (int) (width / voxWidth);
		final int sizeY = (int) (depth / voxHeight);
		final int sizeZ = (int) (height / voxDepth);

		/// the scalar field for our voxel data
		final float[] scalarField = createScalarField(voxels, visualizer);
		final float[] voxDim = new float[]{voxWidth, voxHeight, voxDepth};
		
		/// the iso level which should be visualized
		final float isoLevel = 0.99f;
		
		/// the number of threads (Note: Includes hyperthreading)
		int nThreads = Runtime.getRuntime().availableProcessors();
		ArrayList<Thread> threads = new ArrayList<Thread>();

		/// the resulting array
		final ArrayList<ArrayList<Point3f>> results = new ArrayList<ArrayList<Point3f>>();

		/// distribute thread work depending on number of threads 
		int remainder = sizeX % nThreads;
		int segment = sizeX / nThreads;

		/// partition parallel work
		int zAxisOffset = 0;
		for (int i = 0; i < nThreads; i++) {
			/// distribute remainder among first (remainder) threads
			int segmentSize = (remainder-- > 0) ? segment + 1 : segment;

			/// padding needs to be added to correctly close the gaps between segments
			final int paddedSegmentSize = (i != nThreads - 1) ? segmentSize + 1 : segmentSize;

			/// finished callback
			final CallbackMC callback = new CallbackMC() {
				@Override
				public void run() {
					results.add(getVertices());
				}
			};

			final int finalZAxisOffset = zAxisOffset;

			/// create thread runnable
			Thread t = new Thread() {
				public void run() {
					MarchingCubes.marchingCubesFloat(scalarField, new int[]{sizeX, sizeY, paddedSegmentSize}, sizeZ, voxDim, isoLevel, finalZAxisOffset, callback);
				}
			};

			/// add thread to list and start
			threads.add(t);
			t.start();

			/// correct offsets for next iteration
			zAxisOffset += segmentSize;
		}

		/// join threads
		for (int i = 0; i < threads.size(); i++) {
			try {
				threads.get(i).join();
			} catch (InterruptedException e) {
				System.err.println("Threads could not be joined.");
				e.printStackTrace();
			}
		}

		/// debug information: total number of verts
		int totalVerts = 0;
		for (int i = 0; i < results.size(); i++) {
			totalVerts += results.get(i).size();
		}
		System.err.println("total vertices: " + totalVerts);
		System.err.println("center: " + center);

		/// fill linear 1d list with all vertices of surface
		ArrayList<Point3f> vertices = new ArrayList<Point3f>();
		for (int i = 0; i < results.size(); i++) {
			vertices.addAll(results.get(i));
		}
		
		/// origin / offset is (0, 0, 0) in ScaleDensityToJava3D... TODO shift!
		for (int i = 0; i < vertices.size(); i++) {
			vertices.get(i).x *= 100f*scale; // voxel dim times scale
			vertices.get(i).y *= 100f*scale;
			vertices.get(i).z *= 100f*scale;
		}

		
		Pair<Vector3f, Vector3f> bb = (Pair<Vector3f, Vector3f>)visualizer.getBoundingBox();
		Vector3f min_bb = bb.getSecond();
		min_bb.x *= 0.01;
		min_bb.y *= 0.01;
		min_bb.z *= 0.01;
		System.err.println("min_bb: " + min_bb);
		/// Note: we shift from min of vertices of the isocontours... however, we should use the pivot which seems to be (0, 0, 0)
		Vector3f min = getMin(vertices);
		float shift_x = (min_bb.x - min.x);
		float shift_y = (min_bb.y - min.y);
		float shift_z = (min_bb.z - min.z);
		System.err.println("min: " + min);
		
		/*
		for (int i = 0; i < vertices.size(); i++) {
			vertices.get(i).x -= shift_x;
			vertices.get(i).y -= shift_y;
			vertices.get(i).z -= shift_z;
		}*/

		/// create Java3D triangles with normal information and colors
		TriangleArray triArray = new TriangleArray(vertices.size(), TriangleArray.COORDINATES 
			| TriangleArray.NORMALS | TriangleArray.COLOR_3);
		triArray.setCoordinates(0, vertices.toArray(new Point3f[vertices.size()]));
		
		GeometryInfo geometryInfo = new GeometryInfo(triArray);
		NormalGenerator ng = new NormalGenerator();
		ng.generateNormals(geometryInfo);
		GeometryArray result = geometryInfo.getGeometryArray();
		/// TODO: assign colors depending on the the density value
		/// Note For illumination model, need to get gradient of scalarfield
		return new Shape3D(result);
	}
	

	/**
	 * @brief main
	 * @param args 
	 */
	public static void main(String... args) {
		new MarchingCubes().testMC();
	}
	
	/**
	 * @brief get minimum coordinate of a set of vertices
	 * @param vertices
	 * @return 
	 */
	public static Vector3f getMin(ArrayList<Point3f> vertices) {
		ArrayList<Float> temp_x = new ArrayList<Float>();
		ArrayList<Float> temp_y = new ArrayList<Float>();
		ArrayList<Float> temp_z = new ArrayList<Float>();
		for (Point3f p : vertices) {
			temp_x.add(p.x);
			temp_y.add(p.y);
			temp_z.add(p.z);
		}
		return new Vector3f(Collections.min(temp_x), Collections.min(temp_y), Collections.min(temp_z));
	}
	
	/**
	 * @brief get maximum coordinate of a set of vertices
	 * @param vertices
	 * @return 
	 */
	public static Vector3f getMax(ArrayList<Point3f> vertices) {
		ArrayList<Float> temp_x = new ArrayList<Float>();
		ArrayList<Float> temp_y = new ArrayList<Float>();
		ArrayList<Float> temp_z = new ArrayList<Float>();
		for (Point3f p : vertices) {
			temp_x.add(p.x);
			temp_y.add(p.y);
			temp_z.add(p.z);
		}
		return new Vector3f(Collections.max(temp_x), Collections.max(temp_y), Collections.max(temp_z));
	}
	
	/**
	 * @brief old test method. TODO: Can be discarded later
	 */
	@SuppressWarnings("CallToPrintStackTrace")
	public void testMC() {
		/// Let's say our bounding box is 100, 200, 300 then we have to calculate this into a "size".
		/// if our voxel size would be 100 then we would have 1, 2, 3 for the scalarfield and voxDim would be 100, 100, 100
		/// bounding of geometry
		float width = 1000;
		float depth = 1000;
		float height = 1000;

		float voxWidth = 100;
		float voxHeight = 100;
		float voxDepth = 100;

		final float[] scalarField = new float[10 * 10 * 10]; /// a scalar field with 10*10*10 points in 3D voxel dim describes how large one of these points is, if voxDim 1x1x1 then we have exactly 10*10*10 points, otherwise we reduce by a factor of voxdim, e.g. if voxDim is 2x2x2 we reduce by factor 8 
		//	float[] voxDim = new float[]{2.0f, 2.0f, 2.0f};
		final float[] voxDim = new float[]{100f, 100f, 100f};
		float isoLevel = 0.99f;
		int offset = 0;
		int index = 0;
		for (int x = 0; x < 1000; x += 1) {
			scalarField[index] = (float) Math.random();
			index++;
		}

		/*System.err.println("Lenght scalar field: " + scalarField.length);
		 int volZFull = 10;
		 ArrayList<Point3f> vertices = marchingCubesFloat(scalarField, new int[]{10, 10, 10}, volZFull, voxDim, isoLevel, offset);
		 System.err.println("num vertices: " + vertices.size());*/
		int nThreads = Runtime.getRuntime().availableProcessors();
		ArrayList<Thread> threads = new ArrayList<Thread>();
		final ArrayList<ArrayList<Point3f>> results = new ArrayList<ArrayList<Point3f>>();

		// Thread work distribution
		int remainder = 10 % nThreads;
		int segment = 10 / nThreads;

		int zAxisOffset = 0;
		for (int i = 0; i < nThreads; i++) {
			// Distribute remainder among first (remainder) threads
			int segmentSize = (remainder-- > 0) ? segment + 1 : segment;

			// Padding needs to be added to correctly close the gaps between segments
			final int paddedSegmentSize = (i != nThreads - 1) ? segmentSize + 1 : segmentSize;

			// Finished callback
			final CallbackMC callback = new CallbackMC() {
				@Override
				public void run() {
					results.add(getVertices());
				}
			};

			// Java...
			final int finalZAxisOffset = zAxisOffset;

			// Start the thread
			Thread t = new Thread() {
				public void run() {
					MarchingCubes.marchingCubesFloat(scalarField, new int[]{10, 10, paddedSegmentSize}, 10, voxDim, 0.99f, finalZAxisOffset, callback);
				}
			};

			threads.add(t);
			t.start();

			// Correct offsets for next iteration
			zAxisOffset += segmentSize;
		}

		// Join the threads
		for (int i = 0; i < threads.size(); i++) {
			try {
				threads.get(i).join();
			} catch (InterruptedException e) {
				System.err.println("Threads could not be joined.");
				e.printStackTrace();
			}
		}
		System.err.println("results.size: " + results.size());
		int totalVerts = 0;
		for (int i = 0; i < results.size(); i++) {
			totalVerts += results.get(i).size();
		}
		System.err.println("total vertices: " + totalVerts);
	}


	/**
	 * @brief old prepare method: TODO: can be discarded
	 * @param voxels
	 * @param visualizer
	 * @return 
	 */
	public static float[] prepare_data_for_MC(List<? extends VoxelSet> voxels, DensityVisualizable visualizer) {

		final float[] scalarField = new float[voxels.size()];
		for (int i = 0; i < voxels.size(); i++) {
			scalarField[i] = (float) voxels.get(i).getValue();
		}
		return scalarField;
	}
	/**
	 * @brief old test for MCs with threads: TODO can be discarded later
	 * @param voxels
	 * @param visualizer
	 * @return
	 */
	public Shape3D run_MC_with_threads(List<? extends VoxelSet> voxels, DensityVisualizable visualizer) {
		/// TODO: add parallel call to threads here from example code
		/// TODO: need to shift towards center of voxel densities
		///final float[] scalarField  = prepare_data_for_MC(voxels, visualizer);
		final float[] scalarField = createScalarField(voxels, visualizer);
		System.err.println("size of scalar field: " + scalarField.length);
		Vector3f dim = (Vector3f) visualizer.getDimension();
		int[] volDim = new int[]{10, 10, 10}; /// TODO: use bounding box of our geometry
		int volZFull = 10;
		float[] voxDim = new float[]{1, 1, 1}; /// TODO: use voxel dim of our geometry
		float isoLevel = 0.5f;
		int offset = 0;
		System.err.println("Running marching cubes now!");

		final ArrayList<Point3f> vertices = new ArrayList<Point3f>();
		final CallbackMC callback = new CallbackMC() {
			@Override
			public void run() {
				vertices.addAll(getVertices());
			}
		};

		marchingCubesFloat(scalarField, volDim, volZFull, voxDim, isoLevel, offset, callback);
		System.err.println("Number of triangles: " + vertices.size());
		TriangleArray triArray = new TriangleArray(vertices.size(), TriangleArray.COORDINATES | TriangleArray.NORMALS | TriangleArray.COLOR_3);
		/// TODO: set the colors (add parameter to run MC with threads from VRL-Studio GUI!)
		/// Depending on the isoLevel the color are ligher or darker maybe?
		triArray.setCoordinates(0, vertices.toArray(new Point3f[vertices.size()]));
		GeometryInfo geometryInfo = new GeometryInfo(triArray);
		NormalGenerator ng = new NormalGenerator();
		ng.generateNormals(geometryInfo);
		GeometryArray result = geometryInfo.getGeometryArray();
		/// Note For illumination model, we need to calculate gradient of the scalar field potentially..
		return new Shape3D(result);
	}
}
