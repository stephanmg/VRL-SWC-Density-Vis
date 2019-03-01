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
import edu.gcsc.vrl.swcdensityvis.util.MemoryUtil;
import eu.mihosoft.vrl.reflection.Pair;
import eu.mihosoft.vrl.v3d.Shape3DArray;
import eu.mihosoft.vrl.v3d.VGeometry3D;
import eu.mihosoft.vrl.v3d.jcsg.Cylinder;
import eu.mihosoft.vrl.v3d.jcsg.Vector3d;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.media.j3d.Shape3D;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4d;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.input.sax.XMLReaders;

/**
 * @brief the diameter implementation
 * This implementation displays all geometries (sectiosn and contours) as
 * cylinders, calculates the density and bounding box.
 * @author stephanmg <stephan@syntaktischer-zucker.de>
 */
public class XMLDensityVisualizerDiameterImpl implements XMLDensityVisualizerImplementable {

	private final AbstractDensityComputationStrategyFactory strategyFactory = new DensityComputationStrategyFactoryProducer().getDefaultAbstractDensityComputationStrategyFactory(); /// edge factory 

	private DensityComputationContext context = new DensityComputationContext(strategyFactory.getDefaultComputationStrategy("XML")); /// get xml implementation of that strategy

	private final SAXBuilder saxBuilder = new SAXBuilder(XMLReaders.NONVALIDATING);
	private final ArrayList<Pair<String, HashMap<String, Contour<Vector4d>>>> contours = new ArrayList<Pair<String, HashMap<String, Contour<Vector4d>>>>();
	private final ArrayList<Pair<String, HashMap<String, Tree<Vector4d>>>> trees = new ArrayList<Pair<String, HashMap<String, Tree<Vector4d>>>>();

	/// proxy members
	private Shape3DArray lineGraphGeometry;
	private Density density;
	private boolean isGeometryModified;
	private final ArrayList<File> inputFiles = new ArrayList<File>();
	private Color gColor = Color.WHITE;
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
	 * @brief get the compartment types from a single geometry file The
	 * single geometry file may be termed "consensus" geometry file. The
	 * very first file in the internal used inputFiles list is considered as
	 * the "consensus" geometry file for determining the available
	 * compartment types in the geometries.
	 * @return
	 */
	public Set<String> get_compartments() {
		Set<String> compartments = new HashSet<String>();
		try {
			Document document = saxBuilder.build(this.inputFiles.get(0));
			Element rootNode = document.getRootElement();
			HashMap<String, Tree<Vector4d>> trees = process_trees(rootNode);
			for (Map.Entry<String, Tree<Vector4d>> tree : trees.entrySet()) {
				String str = tree.getKey();
				String type = str.substring(0, str.indexOf("#"));
				compartments.add(type.trim());
			}
			return compartments;
		} catch (JDOMException ex) {
			Logger.getLogger(XMLDensityVisualizerDiameterImpl.class.getName()).log(Level.SEVERE, null, ex);
		} catch (IOException ex) {
			Logger.getLogger(XMLDensityVisualizerDiameterImpl.class.getName()).log(Level.SEVERE, null, ex);
		}
		return compartments;
	}

	/**
	 * @brief parse all input files
	 */
	@Override
	public void parse() {
		for (File file : this.inputFiles) {
			try {
				System.out.print("Building DOM structure...");
				Document document = saxBuilder.build(file);
				Element rootNode = document.getRootElement();
				System.out.println(" done!");
				System.err.println("root node: " + rootNode.toString());
				if (!rootNode.toString().equalsIgnoreCase("[Element: <mbf/>]")) {
					eu.mihosoft.vrl.system.VMessage.warning("ComputeDensity", "XML in wrong format, trying to auto-correct XML file now!");
					XMLFileUtil.fixXMLFile(file.getAbsolutePath());
					document = saxBuilder.build(file);
					rootNode = document.getRootElement();
				}
				System.out.println("Processing Contours...");
				Pair<String, HashMap<String, Contour<Vector4d>>> contour_entry;
				contour_entry = new Pair<String, HashMap<String, Contour<Vector4d>>>(file.getName(), process_contours(rootNode));
				this.contours.add(contour_entry);
				System.out.println(" done!");

				System.out.println("Processing Trees...");
				Pair<String, HashMap<String, Tree<Vector4d>>> tree_entry;
				tree_entry = new Pair<String, HashMap<String, Tree<Vector4d>>>(file.getName(), process_trees(rootNode));
				this.trees.add(tree_entry);
				System.out.println(" done!");

				isGeometryModified = true;

				/// output trees
				for (Pair<String, HashMap<String, Tree<Vector4d>>> cell : trees) {
					System.err.println("Input file: " + cell.getFirst() + " has " + cell.getSecond().size() + "trees");
				}

				/// output contours
				for (Pair<String, HashMap<String, Contour<Vector4d>>> cell : contours) {
					System.err.println("Input file: " + cell.getFirst() + " has " + cell.getSecond().size() + "contours");
				}

				/// exclude a list of compartments
				ArrayList<String> to_remove = new ArrayList<String>();
				for (Pair<String, HashMap<String, Tree<Vector4d>>> cell : this.trees) {
					HashMap<String, Tree<Vector4d>> tree_ = cell.getSecond();
					for (Map.Entry<String, Tree<Vector4d>> tree : tree_.entrySet()) {
						for (String comp : compartment.get_names()) {
							if (tree.getKey().matches(comp + ".*")) {
								to_remove.add(tree.getKey());
								System.err.println("**** REMOVED ****");
							}
						}
					}
				}

				for (Pair<String, HashMap<String, Tree<Vector4d>>> cell : this.trees) {
					HashMap<String, Tree<Vector4d>> tree_ = cell.getSecond();
					for (String to_rm : to_remove) {
						try {
							tree_.remove(to_rm);
						} catch (NoSuchElementException ex) {
							System.err.println("Element not present in all files. Are you sure this is a consensus geometry file? " + ex);
						}
					}
				}

				/// output trees
				for (Pair<String, HashMap<String, Tree<Vector4d>>> cell : trees) {
					System.err.println("Input file: " + cell.getFirst() + " has " + cell.getSecond().size() + "trees");
				}

				/// output contours
				for (Pair<String, HashMap<String, Contour<Vector4d>>> cell : contours) {
					System.err.println("Input file: " + cell.getFirst() + " has " + cell.getSecond().size() + "contours");
				}
			} catch (IOException io) {
				System.out.println(io.getMessage());
			} catch (JDOMException jdomex) {
				System.out.println(jdomex.getMessage());
			}
		}
	}

	/**
	 * @brief process_branches
	 */
	private void process_branches(List<Element> branches, Tree<Vector4d> t, Vector4d point_before_branch) {
		ArrayList<Edge<Vector4d>> edges = new ArrayList<Edge<Vector4d>>();

		for (Element branch : branches) {
			List<Element> points = branch.getChildren("point");
			if (points.size() >= 1) {
				edges.add(new Edge<Vector4d>(
					point_before_branch,
					new Vector4d(SF * Double.parseDouble(points.get(0).getAttributeValue("x")),
						SF * Double.parseDouble(points.get(0).getAttributeValue("y")),
						SF * Double.parseDouble(points.get(0).getAttributeValue("z")),
						SF * Double.parseDouble(points.get(0).getAttributeValue("d")))));
			}

			for (int i = 0; i < points.size() - 1; i++) {
				edges.add(new Edge<Vector4d>(
					new Vector4d(SF * Double.parseDouble(points.get(i).getAttributeValue("x")),
						SF * Double.parseDouble(points.get(i).getAttributeValue("y")),
						SF * Double.parseDouble(points.get(i).getAttributeValue("z")),
						SF * Double.parseDouble(points.get(i).getAttributeValue("d"))),
					new Vector4d(SF * Double.parseDouble(points.get(i + 1).getAttributeValue("x")),
						SF * Double.parseDouble(points.get(i + 1).getAttributeValue("y")),
						SF * Double.parseDouble(points.get(i + 1).getAttributeValue("z")),
						SF * Double.parseDouble(points.get(i + 1).getAttributeValue("d")))));
			}

			// append edges
			ArrayList<Edge<Vector4d>> edges_new = new ArrayList<Edge<Vector4d>>();
			if (!(t.getEdges() == null)) {
				edges_new.addAll(t.getEdges());
			}
			if (!edges.isEmpty()) {
				edges_new.addAll(edges);
			}
			t.setEdges(edges_new);

			Vector4d point_before_branch_new;
			/// if no point before next branch, then we must connect that point with a point after the next branch
			if (points.isEmpty()) {
				point_before_branch_new = point_before_branch;
				/// if only one point is present, then this is the point we create an edge to the point in the next branch
			} else if (points.size() == 1) {
				point_before_branch_new = new Vector4d(SF * Double.parseDouble(points.get(0).getAttributeValue("x")),
					SF * Double.parseDouble(points.get(0).getAttributeValue("y")),
					SF * Double.parseDouble(points.get(0).getAttributeValue("z")),
					SF * Double.parseDouble(points.get(0).getAttributeValue("d")));
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
	 * @brief process one tree
	 * @param node
	 * @return
	 */
	private Tree<Vector4d> process_tree(Element node) {
		ArrayList< Edge< Vector4d>> edges = new ArrayList<Edge<Vector4d>>();
		Tree<Vector4d> t = new Tree<Vector4d>();
		String name = node.getAttributeValue("type");
		t.setType(name);
		String color = node.getAttributeValue("color");
		t.setColor(Color.decode(color));
		t.setType(node.getAttributeValue("type"));
		String leaf = node.getAttributeValue("leaf");
		t.setLeaf(leaf);
		List<Element> points = node.getChildren("point");
		for (int i = 0; i < points.size() - 1; i++) {
			edges.add(new Edge<Vector4d>(
				new Vector4d(SF * Double.parseDouble(points.get(i).getAttributeValue("x")),
					SF * Double.parseDouble(points.get(i).getAttributeValue("y")),
					SF * Double.parseDouble(points.get(i).getAttributeValue("z")),
					SF * Double.parseDouble(points.get(i).getAttributeValue("d"))),
				new Vector4d(SF * Double.parseDouble(points.get(i + 1).getAttributeValue("x")),
					SF * Double.parseDouble(points.get(i + 1).getAttributeValue("y")),
					SF * Double.parseDouble(points.get(i + 1).getAttributeValue("z")),
					SF * Double.parseDouble(points.get(i + 1).getAttributeValue("d")))));
		}

		// append edges
		ArrayList<Edge<Vector4d>> edges_new = new ArrayList<Edge<Vector4d>>();
		if (!(t.getEdges() == null)) {
			edges_new.addAll(t.getEdges());
		}
		if (!edges.isEmpty()) {
			edges_new.addAll(edges);
		}
		t.setEdges(edges_new);

		/// at least one point must be present before branching obvious
		Vector4d point_before_branch = edges.get(edges.size() - 1).getTo();
		if (!node.getChildren("branch").isEmpty()) {
			System.err.println("Tree has branches!");
			process_branches(node.getChildren("branch"), t, point_before_branch);
		} else {
			System.err.println("Tree has no branches!");
		}
		System.err.println("Name: " + name);

		/*for (Edge<Vector4d> edge : trees.get(name).getEdges()) {
		 System.err.println("from: " + edge.getFrom() + " to: " + edge.getTo());
		 }*/
		return t;
	}

	/**
	 * @brief process trees
	 * Note: Trees are *not* allowed to be nested
	 */
	private HashMap<String, Tree<Vector4d>> process_trees(Element rootNode) {
		HashMap<String, Tree<Vector4d>> trees_ = new HashMap<String, Tree<Vector4d>>();

		int index = 0;
		for (Element node : rootNode.getChildren("tree")) {
			Tree<Vector4d> entry = process_tree(node);
			trees_.put(node.getAttributeValue("type") + " #" + index, entry);
			index++;
		}

		for (Map.Entry<String, Tree<Vector4d>> entry : trees_.entrySet()) {
			System.err.println("name of tree: " + entry.getKey());
		}

		return trees_;
	}

	/**
	 * @brief process contours
	 * Note: Contours are *not* allowed to be nested
	 * @param rootNode
	 */
	private HashMap<String, Contour<Vector4d>> process_contours(Element rootNode) {
		HashMap<String, Contour<Vector4d>> contours_ = new HashMap<String, Contour<Vector4d>>();
		int index = 0;
		for (Element node : rootNode.getChildren("contour")) {
			Contour<Vector4d> c = new Contour<Vector4d>();
			String color = node.getAttributeValue("color");
			c.setColor(Color.decode(color));
			String contourName = node.getAttributeValue("name");
			c.setName(contourName);
			String shape = node.getAttributeValue("shape");
			c.setName(shape);
			String closed = node.getAttributeValue("closed");
			c.setClosed(Boolean.valueOf(closed));

			ArrayList<Vector4d> points = new ArrayList<Vector4d>();
			for (Element point : node.getChildren("point")) {
				points.add(new Vector4d(SF * Double.parseDouble(point.getAttributeValue("x")),
					SF * Double.parseDouble(point.getAttributeValue("y")),
					SF * Double.parseDouble(point.getAttributeValue("z")),
					SF * Double.parseDouble(point.getAttributeValue("d"))));

			}
			c.setPoints(points);
			contours_.put(contourName + " # " + index, c);
			index++;

			for (Vector4d vec : points) {
				System.out.println(vec);
			}
		}
		return contours_;
	}

	/**
	 * @brief all files of the list are parsed and processed
	 */
	@Override
	public void parseStack() {
		parse();
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
	 * Note that this works only on the trees, the contours aren't processed
	 * for the density computation
	 * @return
	 */
	@Override
	public Density computeDensity() {
		if (density == null || isGeometryModified) {
			ArrayList<HashMap<String, ArrayList<Edge<Vector3f>>>> final_data_real = new ArrayList<HashMap<String, ArrayList<Edge<Vector3f>>>>();
			for (Pair<String, HashMap<String, Tree<Vector4d>>> cell : trees) {
				HashMap<String, ArrayList<Edge<Vector3f>>> local_data = new HashMap<String, ArrayList<Edge<Vector3f>>>();
				ArrayList<Edge<Vector3f>> final_data = new ArrayList<Edge<Vector3f>>();
				for (Map.Entry<String, Tree<Vector4d>> tree : cell.getSecond().entrySet()) {
					System.err.println("Number of edges (computeDensity): " + tree.getValue().getEdges().size());
					ArrayList<Edge<Vector3f>> points = new ArrayList<Edge<Vector3f>>();
					for (Edge<Vector4d> vec : tree.getValue().getEdges()) {
						Vector4d from = vec.getFrom();
						Vector4d to = vec.getTo();
						points.add(new Edge<Vector3f>(
							new Vector3f((float) from.x, (float) from.y, (float) from.z),
							new Vector3f((float) to.x, (float) to.y, (float) to.z)));
					}
					final_data.addAll(points);
					/// local_data.put(tree.getKey(), points);
				}
				local_data.put(cell.getFirst(), final_data);
				final_data_real.add(local_data);
			}
			for (Pair<String, HashMap<String, Tree<Vector4d>>> cell : trees) {
				System.err.println("cell name/file name: " + cell.getFirst());
			}

			XMLDensityData data = new XMLDensityData(final_data_real);
			this.context.setDensityData(data);
			System.err.println("Data empty?" + data.isEmpty());
			if (!data.isEmpty()) {
				this.density = context.executeDensityComputation();
			} else {
				eu.mihosoft.vrl.system.VMessage.info("No density data to visualize", "Check your input data (i.e. subset selection of compartments!");
			}
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
	 * @brief get's the bounding box
	 * @return 
	 */
	@Override
	public Object getBoundingBox() {
		return this.context.getDensityComputationStrategy().getBoundingBox();
	}

	/**
	 * Note: How can we speed this up? Cylinders have to be created in the given case
	 * @return
	 */
	@Override
	@SuppressWarnings("ReturnOfCollectionOrArrayField")
	public Shape3DArray calculateGeometry() {
		if (this.lineGraphGeometry == null || isGeometryModified) {
			this.lineGraphGeometry = new Shape3DArray();
			/// visualize trees
			for (Pair<String, HashMap<String, Tree<Vector4d>>> cell : trees) {
				System.err.println("Processing trees of cell: " + cell.getFirst());
				HashMap<String, Tree<Vector4d>> cell_trees = cell.getSecond();
				for (Tree<Vector4d> t : cell_trees.values()) {
					gColor = t.getColor();
					System.err.println("Edges: " + t.getEdges().size());
					for (Edge<Vector4d> e : t.getEdges()) {
						MemoryUtil.printHeapMemoryUsage();
						Cylinder cyl = new Cylinder(new Vector3d(e.getFrom().x, e.getFrom().y, e.getFrom().z), new Vector3d(e.getTo().x, e.getTo().y, e.getTo().z), e.getFrom().w, e.getTo().w, 1);

						this.lineGraphGeometry.addAll(new VGeometry3D(cyl.toCSG().toVTriangleArray(), new Color(gColor.getRed(), gColor.getGreen(), gColor.getBlue()), null, 1F, false, false, false).generateShape3DArray());
						MemoryUtil.printHeapMemoryUsage();
					}

					System.err.println("next tree!");
				}
			}

			System.err.println("trees done!");

			/// Process contours (Note: Could use edges instead of points)
			for (Pair<String, HashMap<String, Contour<Vector4d>>> cell : contours) {
				System.err.println("Processing contours of cell: " + cell.getFirst());
				HashMap<String, Contour<Vector4d>> cell_contours = cell.getSecond();
				for (Contour<Vector4d> con : cell_contours.values()) {
					gColor = con.getColor();
					ArrayList<Vector4d> points = con.getPoints();
					MemoryUtil.printHeapMemoryUsage();
					for (int i = 0; i < points.size() - 1; i++) {
						/// Note: Building many cylinders and 
						/// converting to them to VGeometry3D is
						/// consuming heavily memory... this is 
						/// not recommended. One could just switch 
						/// to the schematic representation here, e.g. 
						/// lines not cylinders as in XMLDensityVisImpl?
						Cylinder cyl = new Cylinder(new Vector3d(points.get(i).x, points.get(i).y, points.get(i).z), new Vector3d(points.get(i + 1).x, points.get(i + 1).y, points.get(i + 1).z), points.get(i).w, points.get(i + 1).w, 1);
						this.lineGraphGeometry.addAll(new VGeometry3D(cyl.toCSG().toVTriangleArray(), new Color(gColor.getRed(), gColor.getGreen(), gColor.getBlue()), null, 1F, false, false, false).generateShape3DArray());

					}
					MemoryUtil.printHeapMemoryUsage();

				}

			}

			System.err.println("contours done!");

		}

		System.err.println("contours done!");

		isGeometryModified = false;
		return this.lineGraphGeometry;
	}

	/**
	 *
	 * @param densityComputationContext
	 */
	@Override
	public void setContext(DensityComputationContext densityComputationContext) {
		context = densityComputationContext;
	}

	/**
	 *
	 * @param data
	 */
	@Override
	public void setDensityData(DensityData data) {
		this.context.setDensityData(data);
	}

	/**
	 *
	 * @return
	 */
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
