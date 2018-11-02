/// package's name
package edu.gcsc.vrl.swcdensityvis.types.common;

/// imports
import edu.gcsc.vrl.swcdensityvis.data.FileSelection;
import edu.gcsc.vrl.swcdensityvis.data.FolderInfo;
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
import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * @brief represent selected files
 * @author stephanmg <stephan@syntaktischer-zucker.de>
 */
@TypeInfo(type = FileSelection.class, input = true, output = false, style = "default")
public class FileSelectionType extends TypeRepresentationBase implements Serializable, LoadFolderObserver {

	private static final long serialVersionUID = 1L;
	protected JList sectionList = null;
	DefaultListModel sectionListModel = null;
	protected VTextField hocFileName = null;
	private FileSelection section = new FileSelection();
	private String hoc_tag = null;
	private int maxElementsList = 10;
	private final int preSelectedElementInList = 0;
	private boolean setListScrollable = true;
	private int maxCellWidth = 100;
	private final double percentage = 0.5;

	public void init() {
		// create a VBoxLayout and set it as layout
		VBoxLayout layout = new VBoxLayout(this, VBoxLayout.Y_AXIS);
		setLayout(layout);
		setLayoutType(LayoutType.STATIC);

		nameLabel.setAlignmentX(LEFT_ALIGNMENT);
		add(nameLabel);

		// elements are horizontally aligned
		Box horizBox = Box.createHorizontalBox();
		horizBox.setAlignmentX(LEFT_ALIGNMENT);

		hocFileName = new VTextField("-- no folder was selected --");
		hocFileName.setAlignmentX(Component.LEFT_ALIGNMENT);
		hocFileName.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
			}

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				// eu.mihosoft.vrl.system.VMessage.info("VTextField", "key was released");
				if (sectionList.getSelectedIndices() != null && hocFileName.getText() != null) {
					// construct selectedValuesList by hand since 
					// getSelectedValuesList() depends on 1.7 and may raise an exception
					/**
					 * @todo implement/
					 */

				}
			}
		});
		add(hocFileName);

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
				if (sectionList.getSelectedIndices() != null && hocFileName.getText() != null) {
					// construct selectedValuesList by hand since 
					// getSelectedValuesList() depends on 1.7 and may raise an exception
					List<String> selSubsets = new ArrayList<String>();
					for (int i : sectionList.getSelectedIndices()) {
						selSubsets.add((String) sectionList.getModel().getElementAt(i));
					}
					section = new FileSelection();
					section.set_names(selSubsets);
				}
			}
		});
		sectionList.setSelectedIndex(preSelectedElementInList);
		sectionList.setVisibleRowCount(maxElementsList);
		sectionList.setFixedCellWidth(maxCellWidth);
		if (setListScrollable) {
			JScrollPane pane = new JScrollPane(sectionList);
			pane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			pane.setAlignmentX(Component.LEFT_ALIGNMENT);
			add(pane);
		} else {
			add(sectionList);
		}
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
			return new FileSelection();
		}

	}

	/**
	 * @brief default ctor for section type
	 */
	public FileSelectionType() {
		// hide connector 
		setHideConnector(true);
		section = new FileSelection();
		//System.err.println("FileSelectionType ctor called!");

	}

	/**
	 * @brief inits and sets teh method representation
	 */
	@Override
	public void addedToMethodRepresentation() {
		super.addedToMethodRepresentation();
		init();

		if (hoc_tag == null) {
			System.err.println("hoc tag was null in addedToMethodRepresentation!");
		}

		if (hoc_tag != null) {
			int id = this.getParentMethod().getParentObject().getObjectID();
			Object o = ((VisualCanvas) getMainCanvas()).getInspector().getObject(id);
			int windowID = 0;
			System.err.println("registering an observer");
			LoadFolderObservable.getInstance().addObserver(this, hoc_tag, o, windowID);
		}

	}

	/**
	 * @brief what does this actually do?
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
		// read the hoc_tag
		if (getValueOptions() != null) {
			if (getValueOptions().contains("hoc_tag")) {
				property = script.getProperty("hoc_tag");
			}
		}
		
		if (property != null) {
			hoc_tag = (String) property;
		}

		if (hoc_tag == null) {
			getMainCanvas().getMessageBox().addMessage("Invalid ParamInfo option",
				"ParamInfo for hoc-subset-selection requires hoc_tag in options",
				getConnector(), MessageType.ERROR);
		}
		
		
		property = null;

		if (getValueOptions() != null) {
			if (getValueOptions().contains("visibleElements")) {
				property = script.getProperty("visibleElements");
				
				if (property != null) {
					this.maxElementsList = (Integer) property;
				}
			}
		}
		
		property = null;
		
		if (getValueOptions() != null) {
			if (getValueOptions().contains("scrollable")) {
				property = script.getProperty("scrollable");

				if (property != null) {
					String choice = (String) property;
					if ("auto".equalsIgnoreCase(choice)) {
						this.setListScrollable = true;
						this.maxElementsList = (int) percentage * this.section.get_names().size();
					} else if ("false".equalsIgnoreCase(choice)) {
						this.setListScrollable = false;
					} else if ("true".equalsIgnoreCase(choice)) {
						this.setListScrollable = true;
					} else {
						this.setListScrollable = false;
					}
						
				}
			}
		}
		
		if (getValueOptions() != null) {
			if (getValueOptions().contains("maxCellWidth")) {
				property = script.getProperty("maxCellWidth");
				
				if (property != null) {
					this.maxCellWidth = (Integer) property;
				}
			}
		}

		property = null;
		
	}

	/**
	 * @brief dispose resources on graphical user interface destruction
	 */
	@Override
	public void dispose() {
		if (hoc_tag != null) {
			LoadFolderObservable.getInstance().deleteObserver(this);
		}
	}

	/**
	 * @brief update the hoc file information object
	 * @param info file info for hoc
	 */
	@Override
	public void update(FolderInfo info) {
		adjustView(info);
	}

	/**
	 * @brief sets the value of the view
	 * @param o
	 */
	@Override
	public void setViewValue(Object o) {
		if (o instanceof FileSelection) {
			FileSelection sec = (FileSelection) o;
			section = sec;
		}
		if (hoc_tag != null) {
			int id = this.getParentMethod().getParentObject().getObjectID();
			Object obj = ((VisualCanvas) getMainCanvas()).getInspector().getObject(id);
			int windowID = 0;
			LoadFolderObservable.getInstance().notifyObserver(this, hoc_tag, obj, windowID);

		}
	}

	/**
	 * @brief adjust the view, upon change of layout or notified observers
	 * @param info file info for hoc
	 */
	@SuppressWarnings("unchecked")
	private void adjustView(FolderInfo info) {
		// adjust displayed subset list
		if (info != null) {
			if (!(info instanceof FolderInfo)) {
				throw new RuntimeException("FolderInfo was not "
					+ "found or constructed, i. e. instance "
					+ "is not FolderInfo");
			}

			// clear all elements
			sectionListModel.removeAllElements();
			ArrayList<String> sections = info.get_names_sections();
			hocFileName.setText("Number of files: " + sections.size());

			// add the elements to the subset list model
			for (String element : sections) {
				sectionListModel.addElement(element);
			}
		} else {
			sectionListModel.removeAllElements();
			hocFileName.setText("-- no folder was selected --");
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
