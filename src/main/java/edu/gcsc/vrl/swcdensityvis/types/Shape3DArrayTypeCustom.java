package edu.gcsc.vrl.swcdensityvis.types;

import eu.mihosoft.vrl.annotation.TypeInfo;
import eu.mihosoft.vrl.types.Shape3DArrayType;
import eu.mihosoft.vrl.types.UniverseCreator;
import eu.mihosoft.vrl.types.VCanvas3D;
import eu.mihosoft.vrl.v3d.Shape3DArray;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import javax.swing.JButton;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

@TypeInfo(type = Shape3DArray.class, input = false, output = true, style = "shaped3darraycustom")
public class Shape3DArrayTypeCustom extends Shape3DArrayType {

	private static final long serialVersionUID = 1L;

	public Shape3DArrayTypeCustom() {
		super();
	}

	public Shape3DArrayTypeCustom(VCanvas3D canvas, UniverseCreator universeCreator) {
		super(canvas, universeCreator);

	}

	@Override
	protected void init3DView(final VCanvas3D canvas, UniverseCreator universeCreator) {
		super.init3DView(canvas, universeCreator);
		JPopupMenu menu = getCanvas().getMenu();
		if (menu != null) {
			JMenuItem rotate = new JMenuItem("Toggle rotate");
			rotate.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					eu.mihosoft.vrl.system.VMessage.info("Menu item pressed", "Toggle rotate");
					System.err.println(Arrays.toString(getOrientationFromUniverse()));
				}});
			menu.addSeparator();
			menu.add(rotate);
		}

	}
}
