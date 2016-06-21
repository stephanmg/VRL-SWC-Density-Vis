/// package's name
package edu.gcsc.vrl.swcdensityvis.importer.XML;

/// imports
import edu.gcsc.vrl.densityvis.Density;
import edu.gcsc.vrl.swcdensityvis.data.Edge;
import edu.gcsc.vrl.swcdensityvis.importer.AbstractDensityComputationStrategyFactory;
import edu.gcsc.vrl.swcdensityvis.importer.DensityComputationContext;
import edu.gcsc.vrl.swcdensityvis.importer.DensityComputationStrategyFactoryProducer;
import edu.gcsc.vrl.swcdensityvis.importer.DensityData;
import edu.gcsc.vrl.swcdensityvis.data.Compartment;
import edu.gcsc.vrl.swcdensityvis.util.ColorUtil;
import eu.mihosoft.vrl.v3d.Shape3DArray;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import static javax.media.j3d.GeometryArray.COLOR_3;
import static javax.media.j3d.GeometryArray.COORDINATES;
import javax.media.j3d.LineArray;
import javax.media.j3d.Shape3D;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.input.sax.XMLReaders;

/**
 *
 * @author stephan
 */
public class XMLDensityVisualizerImpl implements XMLDensityVisualizerImplementable {

	private final AbstractDensityComputationStrategyFactory strategyFactory = new DensityComputationStrategyFactoryProducer().getDefaultAbstractDensityComputationStrategyFactory(); /// edge factory 

	private DensityComputationContext context = new DensityComputationContext(strategyFactory.getDefaultComputationStrategy("XML")); /// get xml implementation of that strategy

	private final SAXBuilder saxBuilder = new SAXBuilder(XMLReaders.NONVALIDATING);
	private final HashMap<String, HashMap<String, Contour<Vector3d>>> contours = new HashMap<String, HashMap<String, Contour<Vector3d>>>();
	private final HashMap<String, HashMap<String, Tree<Vector3d>>> trees = new HashMap<String, HashMap<String, Tree<Vector3d>>>();

	/// proxy members
	private Shape3DArray lineGraphGeometry;
	private Density density;
	private boolean isGeometryModified;
	private final ArrayList<File> inputFiles = new ArrayList<File>();
	private Color gColor = null;
	private double SF = 1;
	private Compartment compartment;

	/**
	 *
	 * @param scalingFactor
	 */
	@Override
	public void setScalingFactor(double scalingFactor) {
		SF = scalingFactor;
	}

	/**
	 *
	 * @param files
	 */
	@Override
	public void setFiles(ArrayList<File> files) {
		this.inputFiles.addAll(files);
	}

	/**
	 * @brief only the first file of the list is parsed and processed
	 * TODO: use ID to identify trees from structure
	 */
	@Override
	public void parse() {
		try {
			System.out.print("Building DOM structure...");
			Document document = saxBuilder.build(this.inputFiles.get(0));
			Element rootNode = document.getRootElement();
			System.out.println(" done!");
			System.out.println("root node: " + rootNode.toString());
			if (!rootNode.toString().equalsIgnoreCase("[Element: <mbf/>]")) {
				eu.mihosoft.vrl.system.VMessage.warning("ComputeDensity", "XML in wrong format, trying to auto-correct XML file now!");
				XMLFileUtil.fixXMLFile(this.inputFiles.get(0).getAbsolutePath());
			}

			System.out.println("Processing Contours...");
			this.contours.put(this.inputFiles.get(0).getName(), process_contours(rootNode));
			System.out.println(" done!");

			System.out.println("Processing Trees...");
			this.trees.put(this.inputFiles.get(0).getName(), process_trees(rootNode));
			System.out.println(" done!");

			this.inputFiles.remove(0);
			isGeometryModified = true;
			
			/// output trees
			for (Map.Entry<String, HashMap<String, Tree<Vector3d>>> entry : trees.entrySet()) {
				System.err.println("Input file: " + entry.getKey() + " has " + entry.getValue().size() + "trees");
			}
			
			/// output contours
			for (Map.Entry<String, HashMap<String, Contour<Vector3d>>> entry : contours.entrySet()) {
				System.err.println("Input file: " + entry.getKey() + " has " + entry.getValue().size() + "contours");
			}



			ArrayList<String> files = new ArrayList<String>();
			ArrayList<String> to_remove = new ArrayList<String>();
			
			for (Map.Entry<String, HashMap<String, Tree<Vector3d>>> entry : trees.entrySet()) {
				files.add(entry.getKey());
				System.err.println("entry key: " + entry.getKey());
				for (Map.Entry<String, Tree<Vector3d>> tree : entry.getValue().entrySet()) {
					System.err.println("tree key: " + tree.getKey().trim() + ".");
					for (String comp : compartment.get_names()) {
						System.err.println("comp: " + comp.trim() + ".");
						if (tree.getKey().trim().matches(comp.trim())) {
							System.err.println("*** TO BE REMOVED ***");
							to_remove.add(tree.getKey());
						} else {
							System.err.println("*** NOT TO BE REMOVED ***");
						}
					}
				}
			}
			
			for (Map.Entry<String, HashMap<String, Tree<Vector3d>>> entry : trees.entrySet()) {
				HashMap<String, Tree<Vector3d>> tree_ = entry.getValue();
				for (String to_rm : to_remove) {
					try {
						tree_.remove(to_rm);
						System.err.println("*** REMOVED *** (" + to_rm + ").");
					} catch (NoSuchElementException ex) {
						System.err.println("Element not present in all files. Are you sure this is a consensus geometry file? " + ex);
					}
				}
			}
			
			/// output trees
			for (Map.Entry<String, HashMap<String, Tree<Vector3d>>> entry : trees.entrySet()) {
				System.err.println("Input file: " + entry.getKey() + " has " + entry.getValue().size() + "trees");
			}
			
			/// output contours
			for (Map.Entry<String, HashMap<String, Contour<Vector3d>>> entry : contours.entrySet()) {
				System.err.println("Input file: " + entry.getKey() + " has " + entry.getValue().size() + "contours");
			}


			
		
		} catch (IOException io) {
			System.out.println(io.getMessage());
		} catch (JDOMException jdomex) {
			System.out.println(jdomex.getMessage());
		}
	}

	/**
	 * @brief process_branches
	 */
	private void process_branches(List<Element> branches, Tree<Vector3d> t, Vector3d point_before_branch) {
		ArrayList<Edge<Vector3d>> edges = new ArrayList<Edge<Vector3d>>();

		for (Element branch : branches) {
			List<Element> points = branch.getChildren("point");
			if (points.size() >= 1) {
				edges.add(new Edge<Vector3d>(
					point_before_branch,
					new Vector3d(SF * Double.parseDouble(points.get(0).getAttributeValue("x")),
						SF * Double.parseDouble(points.get(0).getAttributeValue("y")),
						SF * Double.parseDouble(points.get(0).getAttributeValue("z")))));
			}

			for (int i = 0; i < points.size() - 1; i++) {
				edges.add(new Edge<Vector3d>(
					new Vector3d(SF * Double.parseDouble(points.get(i).getAttributeValue("x")),
						SF * Double.parseDouble(points.get(i).getAttributeValue("y")),
						SF * Double.parseDouble(points.get(i).getAttributeValue("z"))),
					new Vector3d(SF * Double.parseDouble(points.get(i + 1).getAttributeValue("x")),
						SF * Double.parseDouble(points.get(i + 1).getAttributeValue("y")),
						SF * Double.parseDouble(points.get(i + 1).getAttributeValue("z")))));
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
				point_before_branch_new = new Vector3d(SF * Double.parseDouble(points.get(0).getAttributeValue("x")),
					SF * Double.parseDouble(points.get(0).getAttributeValue("y")),
					SF * Double.parseDouble(points.get(0).getAttributeValue("z")));
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
	private HashMap<String, Tree<Vector3d>> process_trees(Element rootNode) {
		ArrayList< Edge< Vector3d>> edges = new ArrayList<Edge<Vector3d>>();
		HashMap<String, Tree<Vector3d>> trees = new HashMap<String, Tree<Vector3d>>();

		for (Element node : rootNode.getChildren("tree")) {
			Tree<Vector3d> t = new Tree<Vector3d>();
			String name = node.getAttributeValue("type");
			t.setType(node.getAttributeValue("type"));
			List<Element> points = node.getChildren("point");
			String color = node.getAttributeValue("color");
			t.setColor(Color.decode(color));
			for (int i = 0; i < points.size() - 1; i++) {
				edges.add(new Edge<Vector3d>(
					new Vector3d(SF * Double.parseDouble(points.get(i).getAttributeValue("x")),
						SF * Double.parseDouble(points.get(i).getAttributeValue("y")),
						SF * Double.parseDouble(points.get(i).getAttributeValue("z"))),
					new Vector3d(SF * Double.parseDouble(points.get(i + 1).getAttributeValue("x")),
						SF * Double.parseDouble(points.get(i + 1).getAttributeValue("y")),
						SF * Double.parseDouble(points.get(i + 1).getAttributeValue("z")))));
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

			/*for (Edge<Vector3d> edge : trees.get(name).getEdges()) {
				System.err.println("from: " + edge.getFrom() + " to: " + edge.getTo());
			}*/
		}
		return trees;
	}

	/**
	 * @brief process contours
	 * @param rootNode
	 */
	private HashMap<String, Contour<Vector3d>> process_contours(Element rootNode) {
		HashMap<String, Contour<Vector3d>> contours = new HashMap<String, Contour<Vector3d>>();
		for (Element node : rootNode.getChildren("contour")) {
			Contour<Vector3d> c = new Contour<Vector3d>();
			String contourName = node.getAttributeValue("name");
			c.setName(node.getAttributeValue("name"));
			ArrayList<Vector3d> points = new ArrayList<Vector3d>();
			for (Element point : node.getChildren("point")) {
				points.add(new Vector3d(SF * Double.parseDouble(point.getAttributeValue("x")),
					SF * Double.parseDouble(point.getAttributeValue("y")),
					SF * Double.parseDouble(point.getAttributeValue("z"))));

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

	/**
	 * @brief delegate to strategy 
	 * @return
	 */
	@Override
	public Vector3f getDimension() {
		return (Vector3f) this.context.getDensityComputationStrategy().getDimension();
	}

	/**
	 * @todo this works only because we use in the  ComputeDensity component the default density diameter impl 
	 * 	 to calculate the density.
	 *        in visualizedensity component we just dont call this method, we just return the lineGraphGeometry!
	 *        which returns the line graph geometry without diameter info, i. e. no cylinders, not lines!
	 * @return 
	 */
	@Override
	public Density computeDensity() {
		if (density == null || isGeometryModified) {
			XMLDensityData data = new XMLDensityData(new HashMap<String, ArrayList<Edge<Vector3f>>>());
			this.context.setDensityData(data);
			this.density = context.executeDensityComputation();
			
			/// TODO: needs full implementation -> then bounding box calculation (e.g. getDimension()) works
			/// since this is calculated on the density data not the geometry data...
		}

		return this.density;
	}

	/**
	 * 
	 * @param color 
	 */
	@Override
	public void setLineGraphColor(Color color) {
		this.gColor = color;
	}

	/**
	 * 
	 * @return 
	 */
	@Override
	@SuppressWarnings("ReturnOfCollectionOrArrayField")
	public Shape3DArray calculateGeometry() {
		if (this.lineGraphGeometry == null || isGeometryModified) {
			this.lineGraphGeometry = new Shape3DArray();
			for (HashMap<String, Tree<Vector3d>> ts : trees.values()) {
				for (Tree<Vector3d> t : ts.values()) {
					for (Edge<Vector3d> e : t.getEdges()) {
						LineArray la = new LineArray(2, COORDINATES | COLOR_3);
						if (gColor != null) {
							la.setColor(0, ColorUtil.color2Color3f(gColor));
							la.setColor(1, ColorUtil.color2Color3f(gColor));
						} else {
							/// no color set, use color from file
							la.setColor(0, ColorUtil.color2Color3f(t.getColor()));
							la.setColor(1, ColorUtil.color2Color3f(t.getColor()));
						}
						la.setCoordinates(0, new Point3f[]{new Point3f(e.getFrom()), new Point3f(e.getTo())});
						this.lineGraphGeometry.add(new Shape3D(la));
						///System.err.println("***adding one shape3d!***");
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

	@Override
	public void setDensityData(DensityData data) {
		this.context.setDensityData(data);
	}

	@Override
	public Object getCenter() {
		return this.context.getDensityComputationStrategy().getCenter();
	}

	@Override
	public void setExcludedCompartments(Compartment compartment) {
		this.compartment = compartment;
	}
	
	@Override
	public void prepare(Color color, double scale, Compartment compartment) {
		this.setLineGraphColor(color);
		this.setScalingFactor(scale);
		this.setExcludedCompartments(compartment);
	}

}
