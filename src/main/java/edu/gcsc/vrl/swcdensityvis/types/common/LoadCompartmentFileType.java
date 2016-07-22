/// package's name
package edu.gcsc.vrl.swcdensityvis.types.common;

/// imports
import eu.mihosoft.vrl.annotation.TypeInfo;
import eu.mihosoft.vrl.dialogs.FileDialogManager;
import eu.mihosoft.vrl.io.VFileFilter;
import eu.mihosoft.vrl.lang.VLangUtils;
import eu.mihosoft.vrl.reflection.LayoutType;
import eu.mihosoft.vrl.reflection.TypeRepresentationBase;
import eu.mihosoft.vrl.reflection.VisualCanvas;
import eu.mihosoft.vrl.visual.MessageType;
import eu.mihosoft.vrl.visual.VBoxLayout;
import eu.mihosoft.vrl.visual.VTextField;
import groovy.lang.Script;
import static java.awt.Component.LEFT_ALIGNMENT;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JTextField;

/**
 * @brief ad Compartment load folder dialog
 * @author stephanmg <stephan@syntaktischer-zucker.de>
 */
@TypeInfo(type = File.class, input = true, output = false, style = "load-compartment-dialog")
public class LoadCompartmentFileType extends TypeRepresentationBase {

	private static final long serialVersionUID = 1L;

	/// the text field to display
	private VTextField input;
	/// filter to restrict to allowed files
	private VFileFilter fileFilter = new VFileFilter();
	/// the current file_tag
	private String file_tag = null;

	/**
	 * @brief ctor
	 */
	@Override
	public void addedToMethodRepresentation() {
		/// add to method representation
		super.addedToMethodRepresentation();

		// register at observable
		notifyLoadCompartmentFileObservable();
	}

	public LoadCompartmentFileType() {
		// create a Box and set it as layout
		VBoxLayout layout = new VBoxLayout(this, VBoxLayout.Y_AXIS);
		setLayout(layout);
		setLayoutType(LayoutType.STATIC);

		// set the name label
		nameLabel.setText("File Name:");
		nameLabel.setAlignmentX(0.0f);
		add(nameLabel);

		// elements are horizontally aligned
		Box horizBox = Box.createHorizontalBox();
		horizBox.setAlignmentX(LEFT_ALIGNMENT);
		add(horizBox);

		// create input field
		input = new VTextField(this, "");
		input.setHorizontalAlignment(JTextField.RIGHT);
		int height = (int) this.input.getMinimumSize().getHeight();
		input.setSize(new Dimension(120, height));
		input.setMinimumSize(new Dimension(120, height));
		input.setMaximumSize(new Dimension(120, height));
		input.setPreferredSize(new Dimension(120, height));
		input.setEditable(true);
		input.setAlignmentY(0.5f);
		input.setAlignmentX(0.0f);
		input.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				setViewValue(new File(input.getText()));
			}
		});
		horizBox.add(input);

		// hide connector, since no external data allowed
		setHideConnector(true);

		// create a file manager
		final FileDialogManager fileManager = new FileDialogManager();

		// create a load button
		JButton button = new JButton("...");
		button.setMaximumSize(new Dimension(50, button.getMinimumSize().height));
		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				File directory = null;
				if (getViewValueWithoutValidation() != null) {
					directory = new File(getViewValueWithoutValidation().toString());
					if (!directory.isDirectory()) {
						directory = directory.getParentFile();
					}
				}

				File file = fileManager.getLoadFile(getMainCanvas(),
					directory, fileFilter, false);

				if (file != null) {
					setViewValue(file);
				}
			}
		});

		horizBox.add(button);
	}

	@Override
	public void setViewValue(Object o) {
		if (o instanceof File) {
			input.setText(((File) o).getAbsolutePath());
			input.setCaretPosition(input.getText().length());
			input.setToolTipText(input.getText());
			input.setHorizontalAlignment(JTextField.RIGHT);
		}
		//  Here we inform the Singleton, that the file has been scheduled
		notifyLoadCompartmentFileObservable();
	}

	@Override
	public Object getViewValue() {
		return new File(input.getText());
	}

	@Override
	public void emptyView() {
		input.setText("");
	}

	@Override
	@SuppressWarnings("unchecked")
	protected void evaluationRequest(Script script) {
		super.evaluationRequest(script);

		Object property = null;

		if (getValueOptions() != null) {

			if (getValueOptions().contains("file_tag")) {
				property = script.getProperty("file_tag");
			}

			if (property != null) {
				file_tag = (String) property;
			}
		}

		if (file_tag == null) {
			getMainCanvas().getMessageBox().addMessage("Invalid ParamInfo option",
				"ParamInfo for file-subset-selection requires file_tag in options",
				getConnector(), MessageType.ERROR);
		} else {
			getMainCanvas().getMessageBox().addMessage("ParamInfo specified correctly", "file_tag was: " + file_tag, MessageType.INFO);
		}
		
		property = null;

		if (getValueOptions() != null) {
			if (getValueOptions().contains("endings")) {
				property = script.getProperty("endings");
			}

			if (property != null) {
				ArrayList<String> endings = (ArrayList<String>) property;
				fileFilter.setAcceptedEndings(endings);
				fileFilter.setDescription("Geometry files (*." + endings + ")");
			}
		}
		

	}

	protected void notifyLoadCompartmentFileObservable() {
		File file = new File(input.getText());
		int id = this.getParentMethod().getParentObject().getObjectID();
		Object o = ((VisualCanvas) getMainCanvas()).getInspector().getObject(id);
		int windowID = 0;

		//  Here we inform the Singleton, that the file has been scheduled
		if (!file.getAbsolutePath().isEmpty() && file.isFile()) {
			String msg = LoadCompartmentObservable.getInstance().setSelectedFile(file, file_tag, o, windowID);
			if (!msg.isEmpty() && !getMainCanvas().isLoadingSession()) {
				getMainCanvas().getMessageBox().addMessage("Invalid geometry file",
					msg, getConnector(), MessageType.ERROR);
			}

		} else {
			LoadCompartmentObservable.getInstance().setInvalidFile(file_tag, o, windowID);
			if (!input.getText().isEmpty() && !getMainCanvas().isLoadingSession()) {
				getMainCanvas().getMessageBox().addMessage("Invalid geometry file",
					"Specified filename invalid: " + file.toString(),
					getConnector(), MessageType.ERROR);
			}
		}
	}

	/**
	 * @brief for code generation
	 * @return
	 */
	@Override
	public String getValueAsCode() {
		return "\""
			+ VLangUtils.addEscapesToCode(getValue().toString()) + "\"";
	}
}

