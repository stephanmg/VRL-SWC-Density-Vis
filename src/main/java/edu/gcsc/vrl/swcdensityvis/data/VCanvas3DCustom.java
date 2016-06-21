/// package's name
package edu.gcsc.vrl.swcdensityvis.data;

/// imports
import eu.mihosoft.vrl.reflection.TypeRepresentationBase;
import eu.mihosoft.vrl.types.VCanvas3D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * @brief enhanced VCanvas3D
 * @author stephanmg <stephan@syntaktischer-zucker.de>
 */
public class VCanvas3DCustom extends VCanvas3D implements MouseListener {
	/// sVUID
	private static final long serialVersionUID = 1L;

	/**
	 * @brief see {@link VCanvas3D} 
	 * @param typeRepresentation 
	 */
	public VCanvas3DCustom(TypeRepresentationBase typeRepresentation) {
		super(typeRepresentation);
	}

	/**
	 * @brief overrides the default mouseClicked event to allow focus
	 * @param evt
	 */
	@Override
	public void mouseClicked(MouseEvent evt) {
		/// set focusable and focus to allow usage of KeyListeners
		/// for the VCanvas3DCustom class derived from the VCanvas3D class
		/// which in turn was derived from the JCanvas3D class
		setFocusable(true);
		requestFocusInWindow(true);
		
		/// call super method to ensure correct functionality
		super.mouseClicked(evt);
	}
}
