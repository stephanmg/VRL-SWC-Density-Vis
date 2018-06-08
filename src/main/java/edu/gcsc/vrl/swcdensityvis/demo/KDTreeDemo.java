/// package's name
package edu.gcsc.vrl.swcdensityvis.demo;

/// imports
import edu.gcsc.vrl.swcdensityvis.importer.SWC.SWCCompartmentInformation;
import edu.gcsc.vrl.swcdensityvis.util.SWCUtility;
import edu.wlu.cs.levy.CG.KDTree;
import edu.wlu.cs.levy.CG.KeySizeException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.vecmath.Vector3f;
/**
 * @brief KD tree demo
 * @author stephanmg <stephan@syntaktischer-zucker.de>
 */
public class KDTreeDemo {
	public static void main(String... args) {
		HashMap<String, ArrayList<SWCCompartmentInformation>> cells = new HashMap<String, ArrayList<SWCCompartmentInformation>>(1);
		try {
		 	cells.put("dummy", SWCUtility.parse(new File("data/02a_pyramidal2aFI_original.swc")));
		
	 	} catch (IOException e) {
		 System.err.println("File not found: " + e);
	 	}
		 
		for (Map.Entry<String, ArrayList<SWCCompartmentInformation>> cell : cells.entrySet()) {
		HashMap<Vector3f, ArrayList<Vector3f>> incidents = SWCUtility.getIndicents(cell.getValue());
		for (Map.Entry<Vector3f, ArrayList<Vector3f>> inci : incidents.entrySet()) {
			if (inci.getValue().size() >= 3) {
				System.err.println("Size of edges:" + inci.getValue().size());
			}
			/*System.out.println("Compartment: " + inci.getKey());
			for (Vector3f vertex : inci.getValue()) {
				System.out.println("Vertex:" + vertex);
			}*/
		}
		KDTree<ArrayList<Vector3f>> tree = SWCUtility.buildKDTree(incidents);
		double[] elem = {2.14, 14.34, -0.15};
		try {
			tree.search(elem);
		double[] lo = {-100, -100,-100 };
		double[] hi = {200, 200, 200};
			List<ArrayList<Vector3f>> temps = tree.range(lo, hi);
			System.out.println("temps size:" + temps.size());
			for (ArrayList<Vector3f> temp : temps) {
				System.out.println("temp size: " + temp.size());
			}
		}catch (KeySizeException e) {
		}
			double [] A = {2, 5};
	double [] B = {1, 1};
	double [] C = {3, 9};
	double [] T = {1, 10};

	// make a KD-tree and add some nodes
	KDTree<String> kd = new KDTree<String>(2);
	try {
	    kd.insert(A, new String("A"));
	    kd.insert(B, new String("B"));
	    kd.insert(C, new String("C"));
	}
	catch (Exception e) {
	    System.err.println(e);
	}


	// look for node B
	try {
	    String n = kd.search(B);
	    System.err.println(n);

	}
	catch (Exception e) {
	    System.err.println(e);
	}

	try {

	    // find T's nearest neighbor, which should be C
	    String n = kd.nearest(T);
	    System.err.println(n);

	    // remove C from the tree
	    kd.delete(C);

	    // now T's nearest neighbor should be A
	    n = kd.nearest(T);
	    System.err.println(n);
	}
	catch (Exception e) {
	    System.err.println(e);
	}
		}
    }
}
