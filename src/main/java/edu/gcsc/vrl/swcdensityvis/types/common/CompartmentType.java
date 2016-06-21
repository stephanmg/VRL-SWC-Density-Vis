/// package's name
package edu.gcsc.vrl.swcdensityvis.types.common;

/// imports
import edu.gcsc.vrl.swcdensityvis.data.Compartment;
import edu.gcsc.vrl.swcdensityvis.data.CompartmentInfo;
import eu.mihosoft.vrl.annotation.TypeInfo;
import eu.mihosoft.vrl.lang.VLangUtils;
import eu.mihosoft.vrl.reflection.LayoutType;
import eu.mihosoft.vrl.reflection.TypeRepresentationBase;
import eu.mihosoft.vrl.reflection.VisualCanvas;
import eu.mihosoft.vrl.visual.MessageType;
import eu.mihosoft.vrl.visual.VBoxLayout;
import eu.mihosoft.vrl.visual.VTextField;
import groovy.lang.Script;
import java.awt.Component;
import static java.awt.Component.LEFT_ALIGNMENT;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * @brief visualize the compartments within a geometry file as a selectable list
 * @author stephan
 */
@TypeInfo(type = Compartment.class, input = true, output = false, style = "default")
public class CompartmentType extends TypeRepresentationBase implements Serializable, LoadCompartmentObserver {

	private static final long serialVersionUID = 1L;
	protected JList sectionList = null;
	DefaultListModel sectionListModel = null;
	protected VTextField geometryFileName = null;
	private Compartment section = new Compartment();
	private String file_tag = null;

	public void init() {
		eu.mihosoft.vrl.system.VMessage.info("CompartmentType", "init was called");
		// create a VBoxLayout and set it as layout
		VBoxLayout layout = new VBoxLayout(this, VBoxLayout.Y_AXIS);
		setLayout(layout);
		setLayoutType(LayoutType.STATIC);

		nameLabel.setAlignmentX(LEFT_ALIGNMENT);
		add(nameLabel);

		// elements are horizontally aligned
		Box horizBox = Box.createHorizontalBox();
		horizBox.setAlignmentX(LEFT_ALIGNMENT);

		geometryFileName = new VTextField("-- no geometry file selected --");
		geometryFileName.setAlignmentX(Component.LEFT_ALIGNMENT);
		geometryFileName.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
			}

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				// eu.mihosoft.vrl.system.VMessage.info("VTextField", "key was released");
				if (sectionList.getSelectedIndices() != null && geometryFileName.getText() != null) {
					// construct selectedValuesList by hand since 
					// getSelectedValuesList() depends on 1.7 and may raise an exception
					/**
					 * @todo implement/
					 */

				}
			}
		});
		add(geometryFileName);

		// subset selection list
		sectionListModel = new DefaultListModel();
		sectionList = new JList(sectionListModel);
		sectionList.setSelectionModel(new DefaultListSelectionModel() {
			private static final long serialVersionUID = 1L;
			boolean gestureStarted = false;

			@Override
			public void setSelectionInterval(int index0, int index1) {
				if (!gestureStarted) {
					if (isSelectedIndex(index0)) {
						super.removeSelectionInterval(index0, index1);
					} else {
						super.addSelectionInterval(index0, index1);
					}
				}
				gestureStarted = true;
			}

			@Override
			public void setValueIsAdjusting(boolean isAdjusting) {
				if (isAdjusting == false) {
					gestureStarted = false;
				}
			}
		});
		sectionList.setAlignmentX(Component.LEFT_ALIGNMENT);
		sectionList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (sectionList.getSelectedIndices() != null && geometryFileName.getText() != null) {
					// construct selectedValuesList by hand since 
					// getSelectedValuesList() depends on 1.7 and may raise an exception
					List<String> selSubsets = new ArrayList<String>();
					for (int i : sectionList.getSelectedIndices()) {
						selSubsets.add((String) sectionList.getModel().getElementAt(i));
					}
					section = new Compartment();
					section.set_names(selSubsets);
				}
			}
		});
		add(sectionList);
	}

	/**
	 * @brief gets the view value
	 * @return
	 */
	@Override
	public Object getViewValue() {
		if (section != null) {
			return section;
		} else {
			return new Compartment();
		}

	}

	/**
	 * @brief default ctor for section type
	 */
	public CompartmentType() {
		// hide connector 
		setHideConnector(true);
		section = new Compartment();
		//System.err.println("CompartmentType ctor called!");

	}

	/**
	 * @brief inits and sets the method representation
	 */
	@Override
	public void addedToMethodRepresentation() {
		super.addedToMethodRepresentation();
		init();

		if (file_tag == null) {
			System.err.println("file_tag was null in addedToMethodRepresentation!");
		}

		if (file_tag != null) {
			/// CompartmentType uses global observers, as we want to load the corresponding
			/// compartment file once (i.e. we don't want to use multiple LoadCompartmentFileType 
			/// dialogs in various other components which gives the the names of the compartments
			/// in the file, since the compartment are the same in the file for all components)
			System.err.println("registering an (global) observer");
			LoadCompartmentObservable.getInstance().addObserver(this, file_tag);
		}

	}

	/**
	 * @brief evalulates the workflow contract
	 */
	@Override
	protected void evaluateContract() {
		if (isValidValue()) {
			super.evaluateContract();
		}
	}

	/**
	 * @brief evaluates the script Requests evaluation of the value options
	 * that are usually specified in
	 * {@link eu.mihosoft.vrl.annotation.ParamInfo}.
	 */
	@Override
	protected void evaluationRequest(Script script) {
		super.evaluationRequest(script);

		Object property = null;
		// read the file_tag
		if (getValueOptions() != null) {
			if (getValueOptions().contains("file_tag")) {
				property = script.getProperty("file_tag");
			}
		}
		if (property != null) {
			file_tag = (String) property;
		}

		if (file_tag == null) {
			getMainCanvas().getMessageBox().addMessage("Invalid ParamInfo option",
				"ParamInfo for geometry-subset-selection requires file_tag in options",
				getConnector(), MessageType.ERROR);
		}
	}

	/**
	 * @brief dispose resources on graphical user interface destruction
	 */
	@Override
	public void dispose() {
		if (file_tag != null) {
			LoadCompartmentObservable.getInstance().deleteObserver(this);
		}
	}

	/**
	 * @brief update the geometry file information object
	 * @param info file info for geometry
	 */
	@Override
	public void update(CompartmentInfo info) {
		adjustView(info);
	}

	/**
	 * @brief sets the value of the view
	 * @param o
	 */
	@Override
	public void setViewValue(Object o) {
		if (o instanceof Compartment) {
			Compartment sec = (Compartment) o;
			section = sec;
		}
		
		if (file_tag != null) {
			int id = this.getParentMethod().getParentObject().getObjectID();
			Object obj = ((VisualCanvas) getMainCanvas()).getInspector().getObject(id);
			int windowID = 0;
			LoadCompartmentObservable.getInstance().notifyObserver(this, file_tag, obj, windowID);
		}
	}

	/**
	 * @brief adjust the view, upon change of layout or notified observers
	 * @param info file info for geometry
	 */
	@SuppressWarnings("unchecked")
	private void adjustView(CompartmentInfo info) {
		// adjust displayed subset list
		if (info != null) {
			if (!(info instanceof CompartmentInfo)) {
				throw new RuntimeException("CompartmentInfo was not "
					+ "found or constructed, i. e. instance "
					+ "is not CompartmentInfo");
			}

			// clear all elements
			sectionListModel.removeAllElements();
			Set<String> compartments = info.get_names_compartments();
			///geometryFileName.setText("Number of compartments: " + compartments.size());
			remove(geometryFileName);

			// add the elements to the subset list model
			for (String element : compartments) {
				sectionListModel.addElement(element);
			}
		} else {
			sectionListModel.removeAllElements();
			geometryFileName.setText("-- no geometry file selected --");
		}
	}
	
	/**
	 * @brief necessary for code generation
	 * @return the escaped code
	 */
	@Override
	public String getValueAsCode() {
		return "\""
			+ VLangUtils.addEscapesToCode(getValue().toString()) + "\"";
	}
}
