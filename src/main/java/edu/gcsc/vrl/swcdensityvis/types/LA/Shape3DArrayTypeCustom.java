package edu.gcsc.vrl.swcdensityvis.types.LA;

import eu.mihosoft.vrl.annotation.TypeInfo;
import eu.mihosoft.vrl.types.Shape3DArrayType;
import eu.mihosoft.vrl.types.UniverseCreator;
import eu.mihosoft.vrl.types.VCanvas3D;
import eu.mihosoft.vrl.v3d.Shape3DArray;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.media.j3d.LineArray;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Transform3D;
import javax.swing.AbstractAction;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.vecmath.Color3f;
import javax.vecmath.Point3f;

@TypeInfo(type = Shape3DArray.class, input = false, output = true, style = "shaped3darraycustom")
public class Shape3DArrayTypeCustom extends Shape3DArrayType {

	private final class ToggleButtonAction extends AbstractAction {

		private static final long serialVersionUID = 1L;
		boolean bToggleRotate = false;

		public ToggleButtonAction(String name, Integer mnemonic) {
			super(name);
			putValue(MNEMONIC_KEY, mnemonic);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (bToggleRotate == true) {
				bToggleRotate = false;
			} else {
				eu.mihosoft.vrl.system.VMessage.info("Menu item pressed", "Toggle rotate");
				bToggleRotate = true;
			}

			new Thread() {
				private final double rotStep = 10;
				private final double rotStepRad = rotStep * Math.PI / 180;
				private final double rotRadMax = 2 * Math.PI;
				private final double fps = 5;
				private double rotInitRad = 2 * Math.PI / 180;

				@Override
				public void run() {
					while (bToggleRotate) {
						rotInitRad += rotStepRad;
						rotInitRad %= rotRadMax;
						double vals[] = {1, 0, 0, 0,
							0, Math.cos(rotInitRad), Math.sin(rotInitRad), 0,
							0, -Math.sin(rotInitRad), Math.cos(rotInitRad), 0,
							10, 10, 10, 1};

						double vals2[] = {
							1, 0, 0, 0,
							0, Math.cos(rotInitRad), -Math.sin(rotInitRad), 0,
							0, Math.sin(rotInitRad), Math.cos(rotInitRad), 0,
							0, 0, 0, 1
						};
						//setOrientationFromValues(vals);
						Transform3D t3d = new Transform3D();
						t3d.set(vals2);
						getUniverseCreator().getRootGroup().setTransform(t3d);
						try {
							Thread.sleep((long) (1000 / fps));
						} catch (InterruptedException ex) {
							Logger.getLogger(Shape3DArrayTypeCustom.class.getName()).log(Level.SEVERE, null, ex);
						}

					}
				}
			}.start();

			System.err.println(Arrays.toString(getOrientationFromUniverse()));
		}
	}

	private Shape3D xAxis;
	private Shape3D yAxis;
	private Shape3D zAxis;

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
		Shape3DArray array = (Shape3DArray) getViewValue();

		if (array != null) {
			eu.mihosoft.vrl.system.VMessage.info("not null!", "");
			Shape3DArray shape = new Shape3DArray();

			LineArray axisXLines = new LineArray(2, LineArray.COORDINATES);
			axisXLines.setCoordinate(0, new Point3f(-1.0f, 0.0f, 0.0f));
			axisXLines.setCoordinate(1, new Point3f(1.0f, 0.0f, 0.0f));

			LineArray axisYLines = new LineArray(2, LineArray.COORDINATES | LineArray.COLOR_3);

			axisYLines.setCoordinate(0, new Point3f(0.0f, -1.0f, 0.0f));
			axisYLines.setCoordinate(1, new Point3f(0.0f, 1.0f, 0.0f));
			axisYLines.setColor(0, new Color3f(255, 255, 255));
			axisYLines.setColor(1, new Color3f(255, 255, 255));

			Point3f z1 = new Point3f(0.0f, 0.0f, -1.0f);
			Point3f z2 = new Point3f(0.0f, 0.0f, 1.0f);
			LineArray axisZLines = new LineArray(10, LineArray.COORDINATES | LineArray.COLOR_3);

			axisZLines.setCoordinate(0, z1);
			axisZLines.setCoordinate(1, z2);
			axisZLines.setCoordinate(2, z2);
			axisZLines.setCoordinate(3, new Point3f(0.1f, 0.1f, 0.9f));
			axisZLines.setCoordinate(4, z2);
			axisZLines.setCoordinate(5, new Point3f(-0.1f, 0.1f, 0.9f));
			axisZLines.setCoordinate(6, z2);
			axisZLines.setCoordinate(7, new Point3f(0.1f, -0.1f, 0.9f));
			axisZLines.setCoordinate(8, z2);
			axisZLines.setCoordinate(9, new Point3f(-0.1f, -0.1f, 0.9f));
			this.xAxis = new Shape3D(axisXLines);
			this.yAxis = new Shape3D(axisYLines);
			this.zAxis = new Shape3D(axisZLines);
			shape.add(xAxis);
			shape.add(yAxis);
			shape.add(zAxis);
			setViewValue(shape);
		}

		JPopupMenu menu = getCanvas().getMenu();
		if (menu != null) {
			ToggleButtonAction toggleBA = new ToggleButtonAction("Toggle rotate", KeyEvent.VK_R);
			JMenuItem rotate = new JMenuItem(toggleBA);
			menu.addSeparator();
			menu.add(rotate);
		}
	}
}