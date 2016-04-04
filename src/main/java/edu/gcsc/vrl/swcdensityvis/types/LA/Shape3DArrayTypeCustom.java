/// package's name
package edu.gcsc.vrl.swcdensityvis.types.LA;

/// imports
import eu.mihosoft.vrl.annotation.TypeInfo;
import eu.mihosoft.vrl.dialogs.FileDialogManager;
import eu.mihosoft.vrl.io.ImageSaver;
import eu.mihosoft.vrl.media.VideoCreator;
import eu.mihosoft.vrl.types.Shape3DArrayType;
import eu.mihosoft.vrl.types.UniverseCreator;
import eu.mihosoft.vrl.types.VCanvas3D;
import eu.mihosoft.vrl.v3d.Shape3DArray;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.media.j3d.LineArray;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Transform3D;
import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.filechooser.FileFilter;
import javax.vecmath.Color3f;
import javax.vecmath.Point3f;

/**
 * @brief a custom Shape3DArray type with some useful enhancements
 * @author stephanmg <stephan@syntaktischer-zucker.de>
 */
@TypeInfo(type = Shape3DArray.class, input = false, output = true, style = "shaped3darraycustom")
public class Shape3DArrayTypeCustom extends Shape3DArrayType {
	/**
	 * @brief image file filter for saving density visualization
	 */
	class ImageFilter extends FileFilter {
		/**
		 * @brief accepts jpg, png, gif and bmp (tif not supported OOTB)
		 * @param file
		 * @return 
		 */
		@Override
		public boolean accept(File file) {
			return file.getName().toLowerCase().endsWith(".jpeg")
				|| file.getName().toLowerCase().endsWith(".jpg")
				|| file.getName().toLowerCase().endsWith(".png")
				|| file.getName().toLowerCase().endsWith(".gif")
				|| file.getName().toLowerCase().endsWith(".bmp") || file.isDirectory();
		}

		@Override
		public String getDescription() {
			return "Image files (jpg, png, gif, bmp)";
		}
	}

	/**
	 * @brief a class implementing the action of creating an animation This
	 * class outputs an animation out of rotated views of the images
	 * @todo needs parameterization! e.g. allow user to specify rotational parameter and movie parameters
	 */
	private final class CreateAnimation extends AbstractAction {

		private static final long serialVersionUID = 1L;
		private final Rotator rotator = new Rotator();
		private boolean bToggleRotate = false;
		private int imageNumber = 0;
		private File folder = null;
		private final float spf = 1;

		/**
		 * @brief ctor
		 * @param name
		 * @param mnemonic
		 */
		CreateAnimation(String name, Integer mnemonic) {
			super(name);
			putValue(MNEMONIC_KEY, mnemonic);
		}

		/**
		 * @brief rotates image with a given rotator and creates video
		 * @param e
		 */
		@Override
		public void actionPerformed(ActionEvent e) {

			if (bToggleRotate == true) {
				bToggleRotate = false;
			} else {
				eu.mihosoft.vrl.system.VMessage.info("Menu item pressed", "Toggle rotate");
				/// TODO: idea use a custom dialog, with video settings etc...
				final JFileChooser fc = new JFileChooser();
				fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				fc.showOpenDialog(getMainCanvas());
				fc.setVisible(true);
				folder = fc.getSelectedFile();
				bToggleRotate = true;
			}

			new Thread() {
				@Override
				public void run() {
					while (bToggleRotate) {
						rotator.rotate();
						/// BufferedImage img = getOffscreenCanvas().doRender(getCanvas().getWidth(), getCanvas().getHeight());
						BufferedImage img = getOffscreenCanvas().doRender(4096, 4096);
						try {
							ImageIO.write(img, "png", new File(folder + File.separator + "animation_" + imageNumber + ".png"));
							imageNumber++;
						} catch (IOException ioe) {
							eu.mihosoft.vrl.system.VMessage.error("ImageIO write error", "The file " + folder + File.separator + "animation_" + imageNumber + ".png" + " could not be written!");
							System.err.println(ioe);
						}
					}
				}
			}.start();

			new Thread() {
				@Override
				public void run() {
					if (!bToggleRotate) {
						/// images saved, create a video
						VideoCreator videoCreator = new VideoCreator();
						try {
							videoCreator.convert(folder, new File(folder + File.separator + "animation.mov"), spf);
						} catch (IOException ex) {
							eu.mihosoft.vrl.system.VMessage.error("Video creation error", "The following video could not be created: 'animation.mov' out of the images in folder " + folder);
							System.err.println(ex);
						}
					}
				}
			}.start();
		}
	}

	/**
	 * @brief image saver dialog
	 */
	private final class SaveImageDialog {
		public void showDialog(Component parent, Object o) {
			FileDialogManager dialogManager = new FileDialogManager();
			dialogManager.saveFile(parent, o, new ImageSaver(), new ImageFilter());
		}
	}

	/**
	 * @brief a class implementing the action of saving the rendered imaged
	 * blurred
	 * @todo introduce possibility to specify a user given matrix for
	 * blurring
	 */
	private final class SaveButtonAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		/**
		 * @brief ctor
		 * @param name
		 * @param mnemonic
		 */
		public SaveButtonAction(String name, Integer mnemonic) {
			super(name);
			putValue(MNEMONIC_KEY, mnemonic);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			BufferedImage img = getOffscreenCanvas().doRender(getCanvas().getWidth(), getCanvas().getHeight());

			float[] matrix = {
				1 / 16f, 1 / 8f, 1 / 16f,
				1 / 8f, 1 / 4f, 1 / 8f,
				1 / 16f, 1 / 8f, 1 / 16f,};

			int radius = 10;
			int size = radius * 2 + 1;
			float[] data = new float[size * size];

			float sigma = radius / 3.0f;
			float twoSigmaSquare = 2.0f * sigma * sigma;
			float sigmaRoot = (float) Math.sqrt(twoSigmaSquare * Math.PI);
			float total = 0.0f;

			int index = 0;
			for (int y = -radius; y <= radius; y++) {
				for (int x = -radius; x <= radius; x++) {
					float distance = x * x + y * y;
					data[index] = (float) Math.exp(-distance / twoSigmaSquare) / sigmaRoot;
					total += data[index];
					System.out.printf("%.3f\t", data[index]);
					index++;
				}
			}

			BufferedImageOp op = new ConvolveOp(new Kernel(size, size, data));
			BufferedImage blurred = null;
			for (int i = 0; i < 1; i++) {
				blurred = op.filter(img, null);
				img = blurred;
			}

			getOffscreenCanvas().imageUpdate(blurred, 0, 0, 0, 1000, 1000);

			new SaveImageDialog().showDialog(getMainCanvas(), blurred);

		}
	}

	/**
	 * @brief rotates the Shape3DArray view (in the VCanvas)
	 */
	class Rotator {

		private double rotStep = 10;
		private double rotStepRad = rotStep * Math.PI / 180;
		private double rotRadMax = 2 * Math.PI;
		private double fps = 5;
		private double rotInitRad = 2 * Math.PI / 180;
		private boolean bToggleRotate = true;

		/**
		 * @brief ctor
		 */
		Rotator() {

		}

		public void rotate() {
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

	/**
	 * @brief a class implementing the action for toggling rotating the view
	 */
	private final class ToggleButtonAction extends AbstractAction {

		private static final long serialVersionUID = 1L;
		boolean bToggleRotate = false;

		/**
		 * @brief ctor
		 * @param name
		 * @param mnemonic
		 */
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

	/**
	 * @brief ctor
	 */
	public Shape3DArrayTypeCustom() {
		super();
	}

	/**
	 * @brief ctor
	 * @param canvas
	 * @param universeCreator
	 */
	public Shape3DArrayTypeCustom(VCanvas3D canvas, UniverseCreator universeCreator) {
		super(canvas, universeCreator);
	}

	/**
	 * @brief init the 3D view of the Shape3DArray This creates a scale bar
	 * and adds the additional popup menu items
	 * @param canvas
	 * @param universeCreator
	 */
	@Override
	protected void init3DView(final VCanvas3D canvas, UniverseCreator universeCreator) {
		super.init3DView(canvas, universeCreator);
		Shape3DArray array = (Shape3DArray) getViewValue();

		/// scale bar
		if (array != null) {
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

		/// popup menu
		JPopupMenu menu = getCanvas().getMenu();
		if (menu != null) {
			ToggleButtonAction toggleBA = new ToggleButtonAction("Toggle rotate", KeyEvent.VK_R);
			SaveButtonAction saveBA = new SaveButtonAction("Save As blurred", KeyEvent.VK_B);
			JMenuItem rotate = new JMenuItem(toggleBA);
			menu.addSeparator();
			menu.add(rotate);
			menu.addSeparator();
			JMenuItem save = new JMenuItem(saveBA);
			menu.add(save);
			CreateAnimation ca = new CreateAnimation("Toggle animation", KeyEvent.VK_A);
			menu.addSeparator();
			JMenuItem animation = new JMenuItem(ca);
			menu.add(animation);
		}
	}
}
