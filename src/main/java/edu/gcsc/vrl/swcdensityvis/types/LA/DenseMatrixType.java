/// package's name
package edu.gcsc.vrl.swcdensityvis.types.LA;

/// imports
import eu.mihosoft.vrl.annotation.TypeInfo;
import eu.mihosoft.vrl.lang.VLangUtils;
import eu.mihosoft.vrl.reflection.LayoutType;
import eu.mihosoft.vrl.reflection.TypeRepresentationBase;
import eu.mihosoft.vrl.system.VMessage;
import eu.mihosoft.vrl.visual.MessageBox;
import eu.mihosoft.vrl.visual.MessageType;
import eu.mihosoft.vrl.visual.VBoxLayout;
import eu.mihosoft.vrl.visual.VTextField;
import groovy.lang.Script;
import static java.awt.Component.LEFT_ALIGNMENT;
import java.util.ArrayList;
import javax.swing.Box;

/**
 * @brief dense matrix type for VRL (default to 3x3 for now)
 * @author stephan grein
 */
@TypeInfo(type = DenseMatrix.class, input = true, output = true, style = "default")
public class DenseMatrixType extends TypeRepresentationBase {
	/// private final members
	private static final long serialVersionUID = 1L;
	private final ArrayList<VTextField> textfields = new ArrayList<VTextField>();
	private final int columns = 5;
	private final String emptyValue = "0";
	private final String defaultValue = ""; 
	
	/// private user-supplied members
	private final String defaultValueName = "DenseMatrix";
	private DenseMatrix matrix;
	private int rows = 3;
	private int cols = 3;
	private String[] values; /// provided values

	/**
	 * @brief default ctor and hide ctor in visual representation
	 * @param rows
	 * @param cols
	 */
	public DenseMatrixType(int rows, int cols) {
		setHideConnector(false);
		this.rows = rows;	
		this.cols = cols;
		matrix = new DenseMatrix(this.rows, this.cols);
	}
	
	/**
	 * @brief default ctor
	 */
	public DenseMatrixType() {
		setHideConnector(false);
		matrix = new DenseMatrix(rows, cols);
	}

	/**
	 * @brief init
	 */
	public void init() {
		VBoxLayout layout = new VBoxLayout(this, VBoxLayout.Y_AXIS);
		setLayout(layout);
		setLayoutType(LayoutType.STATIC);

		nameLabel.setAlignmentX(LEFT_ALIGNMENT);
		nameLabel.setText(getValueName().isEmpty() ? getDefaultValueName() : getValueName());
		add(nameLabel);

		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				textfields.add(new VTextField(getDefaultValue()));
			}
		}

		boolean bAutoScroll = true;
		
		if (values == null) {
			for (VTextField tf : textfields) {
				if (bAutoScroll) {
					tf.setAutoscrolls(true);
				}
				tf.setColumns(columns);
				tf.setText(getDefaultValue());
			}
		} else {
			if (values.length == rows*cols) {
				for (int i = 0; i < values.length; i++) {
					if (bAutoScroll) {
						this.textfields.get(i).setAutoscrolls(true);
					}	
					this.textfields.get(i).setColumns(columns);
					this.textfields.get(i).setText(values[i]);
				}
			} else {
				for (VTextField tf : textfields) {
					if (bAutoScroll) {
						tf.setAutoscrolls(true);
					}
					tf.setColumns(columns);
					tf.setText(getDefaultValue());
				}	
			}
		}

		int index = 0;
		for (int i = 0; i < rows; i++) {
			Box row = Box.createHorizontalBox();
			row.setAlignmentX(LEFT_ALIGNMENT);
			for (int j = 0; j < cols; j++) {
				row.add(textfields.get(index));
				index++;
			}
			this.add(row);
		}
	}

	/**
	 * @brief add this representation (when created on Canvas!)
	 */
	@Override
	public void addedToMethodRepresentation() {
		super.addedToMethodRepresentation();
		init();
	}
	
	/**
	 * @brief get the view's value, i. e. a matrix which has been populated elsewhere
	 * @return 
	 */
	@Override
	public Object getViewValue() {
		DenseMatrix m = null;
		try {
			m = new DenseMatrix(getRowCount(), getColCount());
			int index = 0;
			for (int i = 0; i < rows; i++) {
				for (int j = 0; j < cols; j++) {
					try {
						m.set(i, j, Double.parseDouble(this.textfields.get(index).getText()));
						System.err.println("Value: " + this.textfields.get(index).getText());
						index++;
					} catch (NumberFormatException e) {
						MessageBox box = getMainCanvas().getMessageBox();
						box.addUniqueMessage("DenseMatrixType", "Empty input detected.",
						getConnector(), MessageType.WARNING_SINGLE);
						System.err.println(e);
					}
				}
			}
		} catch (Exception e) {
			VMessage.info("DenseMatrixType", "Could not get the view's values! Error: " + e);
		}
		return m;
	}

	/**
	 * @brief evaluates script options on request (i. e. the @ParamInfo options),
	 * 	  this seems to occur just before the object will be created on the canvas
	 * @param script
	 */
	@Override
	protected void evaluationRequest(Script script) {
		Object property = null;
		if (getValueOptions() != null) {

			if (getValueOptions().contains("rows")) {
				property = script.getProperty("rows");
			}

			if (property != null) {
				setRowCount((Integer) property);
			}

			if (getValueOptions().contains("cols")) {
				property = script.getProperty("cols");
			}

			if (property != null) {
				setColCount((Integer) property);
			}
			
			if (getValueOptions().contains("values")) {
				property = script.getProperty("values");
				String values = (String) property;
				System.err.println("values: " + values);
				String[] vals = values.split(",");
				this.values = vals;
			}
		}
	}

	
	/**
	 * @brief evaluates the contract (if you press the invoke button for instance) 
	 */
	@Override
	protected void evaluateContract() {
		if (isValidValue()) {
			MessageBox box = getMainCanvas().getMessageBox();
			if ( (getColCount() < 0) || (getRowCount() < 0) ) {
				box.addUniqueMessage("DenseMatrixType",
				"Col and Row count must be greater than zero!",
				getConnector(), MessageType.WARNING_SINGLE);
			} else {
				int index = 0;
				for (int i = 0; i < rows; i++) {
					for (int j = 0; j < cols; j++) {
						this.matrix.set(i, j, Double.parseDouble(this.textfields.get(index).getText()));
						index++;
					}
				}
				super.evaluateContract();
			}
		}
	}
	
	/**
	 * @brief set view's value
	 * @param o
	 * @todo implement
	 */
	@Override
	public void setViewValue(Object o) {
		if (o instanceof DenseMatrix) {
			DenseMatrix dm = (DenseMatrix) o;
			dm.resize(rows, columns);
			if (values == null) {
				this.matrix = dm;
				int index = 0;
				for (int i = 0; i < rows; i++) {
					for (int j = 0; j < cols; j++) {
						this.textfields.get(index).setText("" + this.matrix.get(i, j));
						index++;
					}
				}
			}
		}
	}
	
	/**
	 * @brief empties the view: when the method is invoked, the view is emptied,
	 *        then the set and get view methods are called
	 */
	@Override
	public void emptyView() {
		for (VTextField tf : this.textfields) {
			tf.setText(getEmptyValue());
		}
	}
	
	/**
	 * @brief set number of rows
	 * @param rows
	 */
	private void setRowCount(int rows) {
		this.rows = rows;
	}

	/**
	 * @brief set number of cols
	 * @param cols
	 */
	private void setColCount(int cols) {
		this.cols = cols;
	}
	
	/**
	 * @brief get number of rows
	 * @return 
	 */
	private int getRowCount() {
		return this.rows;
	}

	/**
	 * @brief get number of cols
	 * @return 
	 */
	private int getColCount() {
		return this.cols;
	}
	
	/**
	 * @brief get the default value for textfields
	 * @return 
	 */
	private String getDefaultValue() {
		return this.defaultValue;
	}
	
	/**
	 * @brief get the name within the @ParamInfo and @OutputInfo annotation
	 * @return 
	 */
	private String getDefaultValueName() {
		return this.defaultValueName;
	}
	
	/**
	 * @brief returns the value if the view get's emptied
	 * @return 
	 */
	private String getEmptyValue() {
		return this.emptyValue;
	}


	/**
	 * @return
	 */
	@Override
	public String getValueAsCode() {
		return "\""
			+ VLangUtils.addEscapesToCode(getValue().toString()) + "\"";
	}

}
