package edu.gcsc.vrl.swcdensityvis.types;

import eu.mihosoft.vrl.annotation.TypeInfo;
import eu.mihosoft.vrl.lang.VLangUtils;
import eu.mihosoft.vrl.reflection.LayoutType;
import eu.mihosoft.vrl.reflection.TypeRepresentationBase;
import eu.mihosoft.vrl.visual.VBoxLayout;
import eu.mihosoft.vrl.visual.VTextField;
import groovy.lang.Script;
import static java.awt.Component.LEFT_ALIGNMENT;
import javax.swing.Box;

@TypeInfo(type = Matrix.class, input = true, output = true, style = "default")
public class MatrixType extends TypeRepresentationBase {

	private static final long serialVersionUID = 1L;
	private Matrix matrix;
	private int rows = 3;
	private int cols = 3;

	public MatrixType() {
		setHideConnector(true);
		matrix = new Matrix();
	}

	public void init() {
		eu.mihosoft.vrl.system.VMessage.info("MatrixType", "init was called");
		VBoxLayout layout = new VBoxLayout(this, VBoxLayout.Y_AXIS);
		setLayout(layout);
		setLayoutType(LayoutType.STATIC);

		nameLabel.setAlignmentX(LEFT_ALIGNMENT);
		nameLabel.setText("Matrix:");
		add(nameLabel);

		VTextField one = new VTextField("one");
		VTextField two = new VTextField("two");
		VTextField three = new VTextField("three");
		VTextField four = new VTextField("four");
		VTextField five = new VTextField("five");
		VTextField six = new VTextField("six");
		VTextField seven = new VTextField("seven");
		VTextField eight = new VTextField("eight");
		VTextField nine = new VTextField("nine");

		Box row1 = Box.createHorizontalBox();
		row1.setAlignmentX(LEFT_ALIGNMENT);
		row1.add(one);
		row1.add(two);
		row1.add(three);
		

		Box row2 = Box.createHorizontalBox();
		row2.setAlignmentX(LEFT_ALIGNMENT);
		row2.add(four);
		row2.add(five);
		row2.add(six);

		Box row3 = Box.createHorizontalBox();
		row3.setAlignmentX(LEFT_ALIGNMENT);
		row3.add(seven);
		row3.add(eight);
		row3.add(nine);

		add(row1);
		add(row2);
		add(row3);
	}

	@Override
	public void addedToMethodRepresentation() {
		super.addedToMethodRepresentation();
		init();
	}

	@Override
	public Object getViewValue() {
		if (matrix != null) {
			return matrix;
		} else {
			return new Matrix();
		}

	}

	/**
	 * @brief evaluates script options on request
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

		}
	}

	private void setRowCount(int rows) {
		this.rows = rows;
	}

	private void setColCount(int cols) {
		this.cols = cols;
	}

	private int getColCount() {
		return this.cols;
	}

	private int getRowCount() {
		return this.rows;
	}

	/**
	 * @todo implement
	 * @return
	 */
	@Override
	public String getValueAsCode() {
		return "\""
			+ VLangUtils.addEscapesToCode(getValue().toString()) + "\"";
	}
}
