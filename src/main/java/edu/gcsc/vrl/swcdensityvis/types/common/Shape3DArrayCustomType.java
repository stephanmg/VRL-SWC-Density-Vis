/// package's name
package edu.gcsc.vrl.swcdensityvis.types.common;

/// imports
import edu.gcsc.vrl.swcdensityvis.data.VCanvas3DCustom;
import edu.gcsc.vrl.swcdensityvis.data.Shape3DArrayCustom;
import edu.gcsc.vrl.swcdensityvis.types.LA.DenseMatrix;
import eu.mihosoft.vrl.annotation.TypeInfo;
import eu.mihosoft.vrl.dialogs.SaveImageDialog;
import eu.mihosoft.vrl.media.VideoCreator;
import eu.mihosoft.vrl.reflection.CustomParamData;
import eu.mihosoft.vrl.reflection.TypeRepresentationBase;
import eu.mihosoft.vrl.types.UniverseCreator;
import eu.mihosoft.vrl.types.VCanvas3D;
import eu.mihosoft.vrl.types.VOffscreenCanvas3D;
import eu.mihosoft.vrl.types.VUniverseCreator;
import eu.mihosoft.vrl.visual.VBoxLayout;
import eu.mihosoft.vrl.visual.VContainer;
import eu.mihosoft.vrl.visual.VGraphicsUtil;
import groovy.lang.Script;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
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
import javax.media.j3d.Appearance;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.ColoringAttributes;
import javax.media.j3d.Font3D;
import javax.media.j3d.FontExtrusion;
import javax.media.j3d.Group;
import javax.media.j3d.LineArray;
import javax.media.j3d.OrderedGroup;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Switch;
import javax.media.j3d.Text3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransparencyAttributes;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.vecmath.Color3f;
import javax.vecmath.Point3f;

/**
 * @brief TypeRepresentation for Shape3DArrayCustom
 * @see {@link Shape3DArrayType}
 *
 * @author stephan <stephan@syntaktischer-zucker.de>
 */
@TypeInfo(type = Shape3DArrayCustom.class, input = false, output = true, style = "shaped3darraycustomtype")
public final class Shape3DArrayCustomType extends TypeRepresentationBase {

	/**
	 * @brief this keylistener responds to keyboard input Note: the
	 * VCanvas3D respectively VCanvas3DCustom focuses the canvas on a
	 * mouseclick on the canvas, only then the key listener can respond to
	 * keyboard input from users
	 */
	private final class KeyboardAction implements KeyListener {
		@Override
		public void keyTyped(KeyEvent e) {
		}

		/**
		 * @brief translate the camera view LEFT, RIGHT => x direction
		 * UP, DOWN => y direction UP+SHIFT, DOWN+Shift => z direction
		 * @param e
		 */
		@Override
		public void keyPressed(KeyEvent e) {
			int keyCode = e.getKeyCode();
			double vals[];
			Transform3D t3d;
			switch (keyCode) {
				case KeyEvent.VK_RIGHT:
					t3d = new Transform3D();
					vals = getOrientationFromUniverse();
					vals[3] += translationIncrementFactor;
					t3d.set(vals);
					getUniverseCreator().getRootGroup().setTransform(t3d);
					break;
				case KeyEvent.VK_LEFT:
					t3d = new Transform3D();
					vals = getOrientationFromUniverse();
					vals[3] -= translationIncrementFactor;
					t3d.set(vals);
					getUniverseCreator().getRootGroup().setTransform(t3d);
					break;
				case KeyEvent.VK_UP:
					t3d = new Transform3D();
					vals = getOrientationFromUniverse();
					if (!e.isShiftDown()) {
						vals[7] += translationIncrementFactor;
					} else {
						vals[11] += translationIncrementFactor;
					}
					t3d.set(vals);
					getUniverseCreator().getRootGroup().setTransform(t3d);
					break;
				case KeyEvent.VK_DOWN:
					t3d = new Transform3D();
					vals = getOrientationFromUniverse();
					if (!e.isShiftDown()) {
						vals[7] -= translationIncrementFactor;
					} else {
						vals[11] -= translationIncrementFactor;
					}
					t3d.set(vals);
					getUniverseCreator().getRootGroup().setTransform(t3d);
					break;
				default:
					break;
			}
		}

		@Override
		public void keyReleased(KeyEvent e) {
		}
	}

	/**
	 * @brief a class implementing the action for toggling rotating the view
	 */
	private final class ToggleButtonAction extends AbstractAction {
		/// sVUID
		private static final long serialVersionUID = 1L;

		/// members
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
			/// System.err.println("Blur kernel: " + blurKernel);
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
							Logger.getLogger(Shape3DArrayTypeCustomOld.class.getName()).log(Level.SEVERE, null, ex);
						}

					}
				}
			}.start();

			System.err.println(Arrays.toString(getOrientationFromUniverse()));
		}
	}

	/**
	 * @brief a class implementing the action of saving the rendered imaged
	 * blurred
	 * @todo introduce possibility to specify a user given matrix for
	 * blurring
	 */
	private final class SaveButtonAction extends AbstractAction {
		/// sVUID
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
	private class Rotator {

		private double rotStep = 10;
		private double rotStepRad = rotStep * Math.PI / 180;
		private double rotRadMax = 2 * Math.PI;
		private double fps = 5;
		private double rotInitRad = 2 * Math.PI / 180;
		private boolean bToggleRotate = true;

		/**
		 * @brief ctor
		 */
		public Rotator() {

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
				Logger.getLogger(Shape3DArrayTypeCustomOld.class.getName()).log(Level.SEVERE, null, ex);
			}

		}
	}

	/**
	 * @brief a class implementing the action of creating an animation This
	 * class outputs an animation out of rotated views of the images
	 * @todo needs parameterization! e.g. allow user to specify rotational
	 * parameter and movie parameters
	 */
	private final class CreateAnimation extends AbstractAction {
		/// sVUID
		private static final long serialVersionUID = 1L;

		/// members
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

	/// sVUID
	private static final long serialVersionUID = -4516600302355830671L;

	/// members
	private BranchGroup shapeGroups[] = new BranchGroup[2];
	private BranchGroup shapeParents[] = new BranchGroup[2];
	private Switch switchGroup;
	private java.util.BitSet visibleNodes;
	private UniverseCreator universeCreator;
	private VCanvas3DCustom canvas;
	private boolean doEmpty = true;
	private VContainer container;
	private Dimension previousVCanvas3DSize;
	protected Dimension minimumVCanvas3DSize;
	public static String ORIENTATION_KEY = "orientation";
	private double translationIncrementFactor = 1;
	private DenseMatrix blurKernel;

	/**
	 * Defines whether to force branch group in favour of ordered group.
	 */
	private boolean forceBranchGroup = false;

	/**
	 * Constructor.
	 *
	 * @param canvas the 3D canvas
	 * @param universeCreator the universe creator
	 */
	public Shape3DArrayCustomType(VCanvas3DCustom canvas, UniverseCreator universeCreator) {
		init();
		init3DView(canvas, universeCreator);
	}

	/**
	 * Constructor.
	 */
	public Shape3DArrayCustomType() {
		init();
		if (!VGraphicsUtil.NO_3D) {
			/// TODO can be removed (next two lines)
			VCanvas3D c = null;
			c = new VCanvas3D(this);
			init3DView(new VCanvas3DCustom(this), new VUniverseCreator());
			/// The UniverseCreator gives us the Mouse Zoom Behaviour,
			/// Rotation and Translation Behaviour (by a hidden Mouse Listener)
		} else {
			add(new JLabel("Java3D support disabled!"));
		}
	}

	/**
	 * Initializes this type representation.
	 */
	protected void init() {
		setUpdateLayoutOnValueChange(false);
		VBoxLayout layout = new VBoxLayout(this, VBoxLayout.X_AXIS);
		setLayout(layout);

		nameLabel.setText("Shape3D Array:");
		nameLabel.setAlignmentY(0.5f);
		this.add(nameLabel);

		setHideConnector(true);
	}

	/**
	 * Initializes the 3D view of this type representation.
	 *
	 * @param canvas the 3D canvas
	 * @param universeCreator the universe creator
	 */
	protected void init3DView(final VCanvas3DCustom canvas, UniverseCreator universeCreator) {
		dispose3D(); // very important to prevent memory leaks of derived classes!

		switchGroup = new Switch(Switch.CHILD_MASK);

		if (container != null) {
			this.remove(container);
		}

		this.canvas = canvas;
		this.universeCreator = universeCreator;

		//canvas = new VCanvas3D(this);
		canvas.setOpaque(false);
		canvas.setMinimumSize(new Dimension(160, 120));
		canvas.setPreferredSize(new Dimension(160, 120));
		canvas.setSize(new Dimension(160, 120));
		setValueOptions("width=160;height=120;blurValue=0.7F;"
			+ "renderOptimization=false;realtimeOptimization=false;"
			+ "doEmpty=true");

		minimumVCanvas3DSize = canvas.getMinimumSize();

//            canvas.setRenderOptimizationEnabled(true);
//            canvas.setRealTimeRenderOptimization(true);
//            canvas.setBlurValue(0.8f);
		switchGroup.setCapability(Switch.ALLOW_SWITCH_WRITE);
		switchGroup.setCapability(Switch.ENABLE_PICK_REPORTING);
		switchGroup.setCapability(Switch.ALLOW_CHILDREN_EXTEND);
		switchGroup.setCapability(Switch.ALLOW_CHILDREN_READ);
		switchGroup.setCapability(Switch.ALLOW_CHILDREN_WRITE);
		switchGroup.setCapability(BranchGroup.ALLOW_DETACH);

		for (int i = 0; i < shapeGroups.length; i++) {
			shapeGroups[i] = new BranchGroup();

			shapeGroups[i].setCapability(BranchGroup.ENABLE_PICK_REPORTING);
			shapeGroups[i].setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
			shapeGroups[i].setCapability(BranchGroup.ALLOW_CHILDREN_READ);
			shapeGroups[i].setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
			shapeGroups[i].setCapability(BranchGroup.ALLOW_DETACH);

			switchGroup.addChild(shapeGroups[i]);
		}

		visibleNodes
			= new java.util.BitSet(switchGroup.numChildren());

		container = new VContainer();

		container.add(canvas);

		this.add(container);

		this.setInputComponent(container);

		universeCreator.init(canvas);

		BranchGroup switchParentGroup = new BranchGroup();

		switchParentGroup.setCapability(BranchGroup.ENABLE_PICK_REPORTING);
		switchParentGroup.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
		switchParentGroup.setCapability(BranchGroup.ALLOW_CHILDREN_READ);
		switchParentGroup.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
		switchParentGroup.setCapability(BranchGroup.ALLOW_DETACH);

		switchParentGroup.addChild(switchGroup);

		universeCreator.getRootGroup().addChild(switchParentGroup);

		JMenuItem item = new JMenuItem("Reset View");

		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Transform3D t3d = new Transform3D();
				getUniverseCreator().getRootGroup().setTransform(t3d);
				getCanvas().contentChanged();
			}
		});

		canvas.getMenu().add(item);

		item = new JMenuItem("Save as Image");

		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				int w = 4096;
				int h = (int) (((double) canvas.getHeight() / (double) canvas.getWidth()) * w);

				BufferedImage img = getOffscreenCanvas().doRender(w, h);

				SaveImageDialog.showDialog(getMainCanvas(), img);
			}
		});

		canvas.getMenu().add(item);

		canvas.getMenu().addSeparator();

		item = new JMenuItem("Increase Zoom Speed");

		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				getUniverseCreator().setZoomFactor(
					getUniverseCreator().getZoomFactor() + 0.5);
			}
		});

		canvas.getMenu().add(item);

		item = new JMenuItem("Decrease Zoom Speed");

		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				getUniverseCreator().setZoomFactor(
					getUniverseCreator().getZoomFactor() - 0.5);
			}
		});

		canvas.getMenu().add(item);

		canvas.getMenu().addSeparator();

		item = new JMenuItem("Increase Translation Increment");

		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				translationIncrementFactor += 1;
			}
		});

		canvas.getMenu().add(item);

		item = new JMenuItem("Decrease Translation Increment");

		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (translationIncrementFactor > 0) {
					translationIncrementFactor -= 1;
				}
			}
		});

		canvas.getMenu().add(item);

		ToggleButtonAction toggleBA = new ToggleButtonAction("Toggle rotational view", KeyEvent.VK_R);
		SaveButtonAction saveBA = new SaveButtonAction("Save As blurred Image", KeyEvent.VK_B);
		JMenuItem rotate = new JMenuItem(toggleBA);
		canvas.getMenu().addSeparator();
		canvas.getMenu().add(rotate);
		canvas.getMenu().addSeparator();
		JMenuItem save = new JMenuItem(saveBA);
		canvas.getMenu().add(save);
		CreateAnimation ca = new CreateAnimation("Toggle animation", KeyEvent.VK_A);
		canvas.getMenu().addSeparator();
		JMenuItem animation = new JMenuItem(ca);
		canvas.getMenu().add(animation);

		canvas.addKeyListener(new KeyboardAction());

		Action doNothing = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				System.err.println("FOOO!");
			}
		};

		canvas.getInputMap().put(KeyStroke.getKeyStroke("SPACE"),
			"doSomething");
		canvas.getActionMap().put("doSomething",
			doNothing);
	}

	@Override
	synchronized public void setViewValue(Object o) {

		clearView();

		if (!VGraphicsUtil.NO_3D) {

			/// TODO: place coordinate system more nice
			/// TODO: add scale bar  more nice
			/// TODO: use getUniverseCreator().getRootGroup().getBounds() 
			/// to get bounds (there the scalebar and coord system can be placed!);
			final Shape3DArrayCustom shapes = (Shape3DArrayCustom) o;

			this.blurKernel = shapes.get_blurring_kernel();

			if (shapes.isCoordinateSystemVisible()) {
				/// x-axis
				LineArray axisXLines = new LineArray(2, LineArray.COORDINATES | LineArray.COLOR_3);
				axisXLines.setCoordinate(0, new Point3f(-1.0f, 0.0f, 0.0f));
				axisXLines.setCoordinate(1, new Point3f(1.0f, 0.0f, 0.0f));
				axisXLines.setColor(0, new Color3f(0, 0, 255));
				axisXLines.setColor(1, new Color3f(0, 0, 255));

				/// y-axis
				LineArray axisYLines = new LineArray(2, LineArray.COORDINATES | LineArray.COLOR_3);
				axisYLines.setCoordinate(0, new Point3f(0.0f, -1.0f, 0.0f));
				axisYLines.setCoordinate(1, new Point3f(0.0f, 1.0f, 0.0f));
				axisYLines.setColor(0, new Color3f(0, 255, 0));
				axisYLines.setColor(1, new Color3f(0, 255, 0));

				/// z-axis
				Point3f z1 = new Point3f(0.0f, 0.0f, -1.0f);
				Point3f z2 = new Point3f(0.0f, 0.0f, 1.0f);
				LineArray axisZLines = new LineArray(2, LineArray.COORDINATES | LineArray.COLOR_3);
				axisZLines.setCoordinate(0, z1);
				axisZLines.setCoordinate(1, z2);
				axisZLines.setColor(0, new Color3f(255, 255, 0));
				axisZLines.setColor(1, new Color3f(255, 255, 0));

				Shape3D xAxis = new Shape3D(axisXLines);
				Shape3D yAxis = new Shape3D(axisYLines);
				Shape3D zAxis = new Shape3D(axisZLines);
				shapes.add(xAxis);
				shapes.add(yAxis);
				shapes.add(zAxis);

				Text3D xLabel = new Text3D(new Font3D(new Font("Garamond", java.awt.Font.PLAIN, 1), new FontExtrusion()), "X", new Point3f(-1.0f, 0.0f, 0.0f));
				Text3D yLabel = new Text3D(new Font3D(new Font("Garamond", java.awt.Font.PLAIN, 1), new FontExtrusion()), "Y",
					new Point3f(0.0f, -1.0f, 0.0f));
				Text3D zLabel = new Text3D(new Font3D(new Font("Garamond", java.awt.Font.PLAIN, 1), new FontExtrusion()), "Z",
					new Point3f(0.0f, 0.0f, -1.0f));

				Shape3D text3dShape3d1 = new Shape3D(xLabel);
				Shape3D text3dShape3d2 = new Shape3D(yLabel);
				Shape3D text3dShape3d3 = new Shape3D(zLabel);

				Appearance appearance1 = new Appearance();
				Appearance appearance2 = new Appearance();
				Appearance appearance3 = new Appearance();

				ColoringAttributes ca1 = new ColoringAttributes();
				ca1.setColor(new Color3f(0, 0, 255));
				appearance1.setColoringAttributes(ca1);

				ColoringAttributes ca2 = new ColoringAttributes();
				ca2.setColor(new Color3f(0, 255, 0));
				appearance2.setColoringAttributes(ca2);

				ColoringAttributes ca3 = new ColoringAttributes();
				ca3.setColor(new Color3f(255, 255, 0));
				appearance3.setColoringAttributes(ca3);

				TransparencyAttributes myTA1 = new TransparencyAttributes();
				myTA1.setTransparency(0.3f);
				myTA1.setTransparencyMode(TransparencyAttributes.NICEST);
				appearance1.setTransparencyAttributes(myTA1);

				TransparencyAttributes myTA2 = new TransparencyAttributes();
				myTA2.setTransparency(0.3f);
				myTA2.setTransparencyMode(TransparencyAttributes.NICEST);
				appearance2.setTransparencyAttributes(myTA2);

				TransparencyAttributes myTA3 = new TransparencyAttributes();
				myTA3.setTransparency(0.3f);
				myTA3.setTransparencyMode(TransparencyAttributes.NICEST);
				appearance3.setTransparencyAttributes(myTA3);

				text3dShape3d1.setAppearance(appearance1);
				text3dShape3d2.setAppearance(appearance2);
				text3dShape3d3.setAppearance(appearance3);
				shapes.add(text3dShape3d1);
				shapes.add(text3dShape3d2);
				shapes.add(text3dShape3d3);

			}

			if (shapes.isScalebarVisible()) {
				/// scale bar line
				Point3f s1 = new Point3f(0.0f, 0.0f, -10.0f);
				Point3f s2 = new Point3f(0.0f, 0.0f, -9.0f);
				LineArray scaleBarLine = new LineArray(2, LineArray.COORDINATES | LineArray.COLOR_3);
				scaleBarLine.setCoordinate(0, s1);
				scaleBarLine.setCoordinate(1, s2);
				scaleBarLine.setColor(0, new Color3f(255, 0, 255));

				Shape3D scaleBar = new Shape3D(scaleBarLine);
				shapes.add(scaleBar);
				Text3D scaleLabel = new Text3D(new Font3D(new Font("Garamond", java.awt.Font.PLAIN, 1), new FontExtrusion()), "10µm",
					new Point3f(0.0f, 0.0f, -10.0f));

				Shape3D text3dShape3d4 = new Shape3D(scaleLabel);

				Appearance appearance4 = new Appearance();

				ColoringAttributes ca4 = new ColoringAttributes();
				ca4.setColor(new Color3f(255, 0, 255));
				appearance4.setColoringAttributes(ca4);

				TransparencyAttributes myTA4 = new TransparencyAttributes();
				myTA4.setTransparency(0.3f);
				myTA4.setTransparencyMode(TransparencyAttributes.NICEST);
				appearance4.setTransparencyAttributes(myTA4);
				text3dShape3d4.setAppearance(appearance4);
				shapes.add(text3dShape3d4);

			}
			
			/*
			Appearance appearanceBlue = new Appearance();
			ColoringAttributes coloringAttributesBlue = new ColoringAttributes();
			coloringAttributesBlue.setColor(new Color3f(Color.blue));
			appearanceBlue.setColoringAttributes(coloringAttributesBlue);

			Cone cone = new Cone(0.2f, 0.8f, appearanceBlue);
			Transform3D transform3dCone = new Transform3D();
			transform3dCone.setTranslation(new Vector3f(-0.2f, 0, 0));
			//Um 45 Grad an der x-Achse rotieren => Spitze geht auf User zu.
			transform3dCone.rotX(Math.PI / 4);
			TransformGroup transformGroupCone = new TransformGroup(transform3dCone);
			transformGroupCone.addChild(cone);
			*/

			if (shapeParents[0] != null) {
				shapeParents[1] = new BranchGroup();
				shapeParents[1].setCapability(BranchGroup.ENABLE_PICK_REPORTING);
				shapeParents[1].setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
				shapeParents[1].setCapability(BranchGroup.ALLOW_CHILDREN_READ);
				shapeParents[1].setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
				shapeParents[1].setCapability(BranchGroup.ALLOW_DETACH);

				Group childGroup = null;

				if (!shapes.isEmpty() && !isForceBranchGroup()) {
					Appearance app = shapes.get(0).getAppearance();
					if (app != null && app.getTransparencyAttributes() != null) {
						TransparencyAttributes tA = app.getTransparencyAttributes();
						if (tA.getTransparency() > 0) {
							childGroup = new OrderedGroup();
						}
					}
				}
				if (childGroup == null) {
					childGroup = new BranchGroup();
				}

				for (Shape3D s : shapes) {
					childGroup.addChild(s);
				}

				shapeParents[1].addChild(childGroup);

				shapeGroups[1].addChild(shapeParents[1]);
				visibleNodes.set(1, true);
				visibleNodes.set(0, false);
				switchGroup.setChildMask(visibleNodes);

				shapeParents[0].detach();
				shapeParents[0] = null;
			} else {
				shapeParents[0] = new BranchGroup();
				shapeParents[0].setCapability(BranchGroup.ENABLE_PICK_REPORTING);
				shapeParents[0].setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
				shapeParents[0].setCapability(BranchGroup.ALLOW_CHILDREN_READ);
				shapeParents[0].setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
				shapeParents[0].setCapability(BranchGroup.ALLOW_DETACH);

				Group childGroup = null;

				if (!shapes.isEmpty() && !isForceBranchGroup()) {
					Appearance app = shapes.get(0).getAppearance();
					if (app != null && app.getTransparencyAttributes() != null) {
						TransparencyAttributes tA = app.getTransparencyAttributes();
						if (tA.getTransparency() > 0) {
							childGroup = new OrderedGroup();
						}
					}
				}
				if (childGroup == null) {
					childGroup = new BranchGroup();
				}

				for (Shape3D s : shapes) {
					childGroup.addChild(s);
				}

				shapeParents[0].addChild(childGroup);

				shapeGroups[0].addChild(shapeParents[0]);
				visibleNodes.set(0, true);
				visibleNodes.set(1, false);
				switchGroup.setChildMask(visibleNodes);

				if (shapeParents[1] != null) {
					shapeParents[1].detach();
					shapeParents[1] = null;
				}
			}
		}
	}

	@Override
	public void emptyView() {
		clearView();
	}

	private void clearView() {

		if (!VGraphicsUtil.NO_3D) {
			if (isDoEmpty()) {
				if (visibleNodes != null) {
					visibleNodes.set(0, false);
					visibleNodes.set(1, false);
					switchGroup.setChildMask(visibleNodes);

					for (int i = 0; i < shapeParents.length; i++) {
						if (shapeParents[i] != null) {
							shapeParents[i].detach();
							shapeParents[i] = null;
						}
					}
					if (getCanvas() != null) {
						getCanvas().contentChanged();
						/// getCanvas().postRenderTask();
					}
				}
			}
		}
	}

	/**
	 * Defines the Vcanvas3D size by evaluating a groovy script.
	 *
	 * @param script the script to evaluate
	 */
	private void setVCanvas3DSizeFromValueOptions(Script script) {

		if (VGraphicsUtil.NO_3D) {
			return;
		}

		Integer w = null;
		Integer h = null;
		Object property = null;

		if (getValueOptions() != null) {

			if (getValueOptions().contains("width")) {
				property = script.getProperty("width");
			}

			if (property != null) {
				w = (Integer) property;
			}

			property = null;

			if (getValueOptions().contains("height")) {
				property = script.getProperty("height");
			}

			if (property != null) {
				h = (Integer) property;
			}

			property = null;

			if (getValueOptions().contains("forceBranchGroup")) {
				property = script.getProperty("forceBranchGroup");
			}

			if (property != null) {
				setForceBranchGroup((Boolean) property);
			}
		}

		if (w != null && h != null && getCanvas() != null) {
			// TODO find out why offset is 5
			getCanvas().setPreferredSize(new Dimension(w - 5, h));
			getCanvas().setMinimumSize(minimumVCanvas3DSize);
			getCanvas().setSize(new Dimension(w - 5, h));
		}

		System.out.println(getValueOptions());
	}

	/**
	 * Defines render options by evaluating a groovy script.
	 *
	 * @param script the script to evaluate
	 */
	private void setRenderOptionsFromValueOptions(Script script) {

		Object property = null;
		Boolean enableRenderOptimization = true;
		Boolean enableRealtimeOptimization = false;
		Float blurValue = 0.7f;

		if (getValueOptions() != null) {

			if (getValueOptions().contains("renderOptimization")) {
				property = script.getProperty("renderOptimization");
			}

			if (property != null) {
				enableRenderOptimization = (Boolean) property;
				canvas.setRenderOptimizationEnabled(
					enableRenderOptimization);
			}

			property = null;

			if (getValueOptions().contains("realtimeOptimization")) {
				property = script.getProperty("realtimeOptimization");
			}

			if (property != null) {
				enableRealtimeOptimization = (Boolean) property;
				canvas.setRealTimeRenderOptimization(
					enableRealtimeOptimization);
			}

			property = null;

			if (getValueOptions().contains("blurValue")) {
				property = script.getProperty("blurValue");
			}

			if (property != null) {
				blurValue = (Float) property;
				canvas.setBlurValue(blurValue);
			}

			property = null;

			if (getValueOptions().contains("doEmpty")) {
				property = script.getProperty("doEmpty");
			}

			if (property != null) {
				doEmpty = (Boolean) property;
			}
		}
	}

	@Override
	protected void evaluationRequest(Script script) {

		if (VGraphicsUtil.NO_3D) {
			return;
		}

		setVCanvas3DSizeFromValueOptions(script);
		setRenderOptionsFromValueOptions(script);
	}

	@Override
	public CustomParamData getCustomData() {

		if (VGraphicsUtil.NO_3D) {
			return new CustomParamData();
		}

		CustomParamData result = super.getCustomData();

		Transform3D t3d = new Transform3D();
		getUniverseCreator().getRootGroup().getTransform(t3d);
		double[] values = new double[16];
		t3d.get(values);

		result.put(ORIENTATION_KEY, values);

		return result;
	}

	public double[] getOrientationFromCustomData() {

		if (VGraphicsUtil.NO_3D) {
			return null;
		}

		double[] values = (double[]) super.getCustomData().get(ORIENTATION_KEY);
		return values;
	}

	public double[] getOrientationFromUniverse() {
		if (VGraphicsUtil.NO_3D) {
			return new double[0];
		}
		Transform3D t3d = new Transform3D();
		getUniverseCreator().getRootGroup().getTransform(t3d);
		double[] values = new double[16];
		t3d.get(values);
		return values;
	}

	@Override
	public void evaluateCustomParamData() {

		if (VGraphicsUtil.NO_3D) {
			return;
		}

		Transform3D t3d = new Transform3D();
		double[] values = getOrientationFromCustomData();
		if (values != null) {
			t3d.set(values);
			getUniverseCreator().getRootGroup().setTransform(t3d);
		}
	}

	public void setOrientationFromValues(double[] values) {

		if (VGraphicsUtil.NO_3D) {
			return;
		}

		Transform3D t3d = new Transform3D();
		if (values != null) {
			t3d.set(values);
			getUniverseCreator().getRootGroup().setTransform(t3d);
		}
	}

	/**
	 * Returns the canvas used for rendering
	 *
	 * @return the canvas used for rendering
	 */
	public VCanvas3D getCanvas() {
		return canvas;
	}

	/**
	 * Returns the canvas used for offscreen rendering
	 *
	 * @return the canvas used for offscreen rendering
	 */
	public VOffscreenCanvas3D getOffscreenCanvas() {
		return universeCreator.getOffscreenCanvas();
	}

	/**
	 * Indicates whether to empty view of the type representation.
	 *
	 * @return <code>true</code> if the view will be emptied;
	 * <code>false</code> otherwise
	 */
	public boolean isDoEmpty() {
		return doEmpty;
	}

	/**
	 * Returns the universe creator.
	 *
	 * @return the universe creator
	 */
	public UniverseCreator getUniverseCreator() {
		return universeCreator;
	}

	/**
	 * Disposes 3D resources.
	 */
	private void dispose3D() {
		if (!VGraphicsUtil.NO_3D) {
			clearView();
			value = null;

			if (getCanvas() != null) {
				getCanvas().getOffscreenCanvas3D().stopRenderer();
				getUniverseCreator().getUniverse().cleanup();
				universeCreator.dispose();
			}

			canvas = null;
			universeCreator = null;
		}
	}

	@Override
	public void dispose() {
		dispose3D();
		super.dispose();
	}

	@Override
	public void enterFullScreenMode(Dimension size) {
		super.enterFullScreenMode(size);
		previousVCanvas3DSize = canvas.getSize();
		container.setPreferredSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE));
		container.setMinimumSize(null);
		container.setMaximumSize(null);

		canvas.setPreferredSize(null);
		canvas.setMinimumSize(minimumVCanvas3DSize);
		canvas.setMaximumSize(null);

		revalidate();
	}

	@Override
	public void leaveFullScreenMode() {
		super.leaveFullScreenMode();
		container.setPreferredSize(null);
		container.setMinimumSize(null);
		container.setMaximumSize(null);

		canvas.setSize(previousVCanvas3DSize);
		canvas.setPreferredSize(previousVCanvas3DSize);
		canvas.setMinimumSize(minimumVCanvas3DSize);

		canvas.contentChanged();

		revalidate();
	}

	@Override
	public JComponent customViewComponent() {

//        final BufferedImage img = plotPane.getImage();
//
//        JPanel panel = new JPanel() {
//            @Override
//            public void paintComponent(Graphics g) {
//                g.drawImage(img, 0, 0, 640, 480,  null);
//            }
//        };
//
//        return panel;
		return null;
	}
//    protected void setMinimumVCanvas3DSize(Dimension canvas3DSize) {
//
//        canvas.setPreferredSize(canvas3DSize);
//        canvas.setMinimumSize(canvas3DSize);
//        minimumVCanvas3DSize = canvas3DSize;
//
//        setValueOptions("width=" + canvas3DSize.width + ";"
//                + "height=" + canvas3DSize.height);
//    }

	@Override
	public boolean noSerialization() {
		// we cannot serialize shape3d objects
		return true;
	}

	/**
	 * Indicates whether to force branch group in favour of ordered group.
	 *
	 * @return the state
	 */
	public boolean isForceBranchGroup() {
		return forceBranchGroup;
	}

	/**
	 * Defines whether to force branch group in favour of ordered group.
	 *
	 * @param forceBranchGroup the state to set
	 */
	public void setForceBranchGroup(boolean forceBranchGroup) {
		this.forceBranchGroup = forceBranchGroup;
	}
}
