/// package's name
package edu.gcsc.vrl.swcdensityvis.importer.XML;

/// imports
import edu.gcsc.vrl.densityvis.Density;
import edu.gcsc.vrl.swcdensityvis.data.Edge;
import edu.gcsc.vrl.swcdensityvis.importer.AbstractDensityComputationStrategyFactory;
import edu.gcsc.vrl.swcdensityvis.importer.DensityComputationContext;
import edu.gcsc.vrl.swcdensityvis.importer.DensityComputationStrategyFactoryProducer;
import edu.gcsc.vrl.swcdensityvis.importer.DensityVisualizable;
import eu.mihosoft.vrl.v3d.Shape3DArray;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import static javax.media.j3d.GeometryArray.COLOR_4;
import static javax.media.j3d.GeometryArray.COORDINATES;
import javax.media.j3d.LineArray;
import javax.media.j3d.Shape3D;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3d;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.input.sax.XMLReaders;

/**
 *
 * @author stephan
 */
public class XMLDensityVisualizerImpl implements DensityVisualizable {

	private final AbstractDensityComputationStrategyFactory strategyFactory = new DensityComputationStrategyFactoryProducer().getDefaultAbstractDensityComputationStrategyFactory(); /// edge factory 

	private DensityComputationContext context = new DensityComputationContext(strategyFactory.getDefaultComputationStrategy("XML")); /// get xml implementation of that strategy

	private final SAXBuilder saxBuilder = new SAXBuilder(XMLReaders.NONVALIDATING);
	private HashMap<String, HashMap<String, Contour>> contours;
	private HashMap<String, HashMap<String, Tree>> trees;

	/// proxy members
	private Shape3DArray lineGraphGeometry;
	private Density density;
	private boolean isGeometryModified;
	private ArrayList<File> inputFiles;

	/**
	 * 
	 * @param files 
	 */
	public void setFiles(ArrayList<File> files) {
		this.inputFiles.addAll(files);
	}
	
	/**
	 * @brief only the first file of the list is parsed and processed
	 */
	@Override
	public void parse() {
		try {
			System.out.print("Building DOM structure...");
			Document document = saxBuilder.build(this.inputFiles.get(0));
			Element rootNode = document.getRootElement();
			System.out.println(" done!");
			System.out.println("root node: " + rootNode.toString());

			System.out.println("Processing Contours...");
			this.contours.put(this.inputFiles.get(0).getName(), process_contours(rootNode));
			System.out.println(" done!");

			System.out.println("Processing Trees...");
			this.trees.put(this.inputFiles.get(0).getName(), process_trees(rootNode));
			System.out.println(" done!");

			this.inputFiles.remove(0);
			isGeometryModified = true;

		} catch (IOException io) {
			System.out.println(io.getMessage());
		} catch (JDOMException jdomex) {
			System.out.println(jdomex.getMessage());
		}
	}

	/**
	 * @brief process_branches
	 */
	private void process_branches(List<Element> branches, Tree t, Vector3d point_before_branch) {
		ArrayList<Edge<Vector3d>> edges = new ArrayList<Edge<Vector3d>>();

		for (Element branch : branches) {
			List<Element> points = branch.getChildren("point");
			if (points.size() >= 1) {
				edges.add(new Edge<Vector3d>(
					point_before_branch,
					new Vector3d(Double.parseDouble(points.get(0).getAttributeValue("x")),
						Double.parseDouble(points.get(0).getAttributeValue("y")),
						Double.parseDouble(points.get(0).getAttributeValue("z")))));
			}

			for (int i = 0; i < points.size() - 1; i++) {
				edges.add(new Edge<Vector3d>(
					new Vector3d(Double.parseDouble(points.get(i).getAttributeValue("x")),
						Double.parseDouble(points.get(i).getAttributeValue("y")),
						Double.parseDouble(points.get(i).getAttributeValue("z"))),
					new Vector3d(Double.parseDouble(points.get(i + 1).getAttributeValue("x")),
						Double.parseDouble(points.get(i + 1).getAttributeValue("y")),
						Double.parseDouble(points.get(i + 1).getAttributeValue("z")))));
			}

			// append edges
			ArrayList<Edge<Vector3d>> edges_new = new ArrayList<Edge<Vector3d>>();
			if (!(t.getEdges() == null)) {
				edges_new.addAll(t.getEdges());
			}
			if (!edges.isEmpty()) {
				edges_new.addAll(edges);
			}
			t.setEdges(edges_new);

			Vector3d point_before_branch_new;
			/// if no point before next branch, then we must connect that point with a point after the next branch
			if (points.isEmpty()) {
				point_before_branch_new = point_before_branch;
				/// if only one point is present, then this is the point we create an edge to the point in the next branch
			} else if (points.size() == 1) {
				point_before_branch_new = new Vector3d(Double.parseDouble(points.get(0).getAttributeValue("x")),
					Double.parseDouble(points.get(0).getAttributeValue("y")),
					Double.parseDouble(points.get(0).getAttributeValue("z")));
			} else {
				/// else the point which defines an edge with the next branch's point is the last point from the current branch
				point_before_branch_new = edges.get(edges.size() - 1).getTo();
			}

			if (!branch.getChildren("branch").isEmpty()) {
				System.err.println("Branch does contain a branch!");
				process_branches(branch.getChildren("branch"), t, point_before_branch_new);
			} else {
				System.err.println("Branch does not contain a branch!");
			}
		}
	}

	/**
	 * @brief process tress
	 */
	private HashMap<String, Tree> process_trees(Element rootNode) {
		ArrayList< Edge< Vector3d>> edges = new ArrayList<Edge<Vector3d>>();
		HashMap<String, Tree> trees = new HashMap<String, Tree>();

		for (Element node : rootNode.getChildren("tree")) {
			Tree t = new Tree();
			String name = node.getAttributeValue("type");
			t.setType(node.getAttributeValue("type"));
			List<Element> points = node.getChildren("point");
			for (int i = 0; i < points.size() - 1; i++) {
				edges.add(new Edge<Vector3d>(
					new Vector3d(Double.parseDouble(points.get(i).getAttributeValue("x")),
						Double.parseDouble(points.get(i).getAttributeValue("y")),
						Double.parseDouble(points.get(i).getAttributeValue("z"))),
					new Vector3d(Double.parseDouble(points.get(i + 1).getAttributeValue("x")),
						Double.parseDouble(points.get(i + 1).getAttributeValue("y")),
						Double.parseDouble(points.get(i + 1).getAttributeValue("z")))));
			}

			// append edges
			ArrayList<Edge<Vector3d>> edges_new = new ArrayList<Edge<Vector3d>>();
			if (!(t.getEdges() == null)) {
				edges_new.addAll(t.getEdges());
			}
			if (!edges.isEmpty()) {
				edges_new.addAll(edges);
			}
			t.setEdges(edges_new);

			/// at least one point must be present before branching obvious
			Vector3d point_before_branch = edges.get(edges.size() - 1).getTo();
			if (!node.getChildren("branch").isEmpty()) {
				System.err.println("Tree has branches!");
				process_branches(node.getChildren("branch"), t, point_before_branch);
			} else {
				System.err.println("Tree has no branches!");
			}
			trees.put(name, t);

			for (Edge<Vector3d> edge : trees.get(name).getEdges()) {
				System.err.println("from: " + edge.getFrom() + " to: " + edge.getTo());
			}
		}
		return trees;
	}

	/**
	 * @brief process contours
	 * @param rootNode
	 */
	private HashMap<String, Contour> process_contours(Element rootNode) {
		HashMap<String, Contour> contours = new HashMap<String, Contour>();
		for (Element node : rootNode.getChildren("contour")) {
			Contour c = new Contour();
			String contourName = node.getAttributeValue("name");
			c.setName(node.getAttributeValue("name"));
			ArrayList<Vector3d> points = new ArrayList<Vector3d>();
			for (Element point : node.getChildren("point")) {
				points.add(new Vector3d(Double.parseDouble(point.getAttributeValue("x")),
					Double.parseDouble(point.getAttributeValue("y")),
					Double.parseDouble(point.getAttributeValue("z"))));

			}
			c.setPoints(points);
			contours.put(contourName, c);
			for (Vector3d vec : points) {
				System.out.println(vec);
			}
		}
		return contours;
	}

	/**
	 * @brief all files of the list are parsed and processed
	 */
	@Override
	public void parseStack() {
		while (!inputFiles.isEmpty()) {
			parse();
		}
	}

	@Override
	public void getDimension() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void getBoundingBox() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public Density computeDensity() {
		if (density == null || isGeometryModified) {
			this.density = context.executeDensityComputation();
		}

		return this.density;
	}

	@Override
	@SuppressWarnings("ReturnOfCollectionOrArrayField")
	public Shape3DArray calculateGeometry() {
		if (this.lineGraphGeometry == null || isGeometryModified) {
			this.lineGraphGeometry = new Shape3DArray();
			for (HashMap<String, Tree> ts : trees.values()) {
				for (Tree t : ts.values()) {
					for (Edge<Vector3d> e : t.getEdges()) {
						LineArray la = new LineArray(2, COORDINATES | COLOR_4);
						la.setCoordinates(0, new Point3f[]{new Point3f(e.getFrom()), new Point3f(e.getTo())});
						this.lineGraphGeometry.add(new Shape3D(la));
					}
				}
			}
		}
		isGeometryModified = false;
		return this.lineGraphGeometry;
	}

	@Override
	public void setContext(DensityComputationContext densityComputationContext) {
		context = densityComputationContext;
	}

}
