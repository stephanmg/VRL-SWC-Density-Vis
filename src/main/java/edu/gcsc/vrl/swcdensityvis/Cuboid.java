/// package's name
package edu.gcsc.vrl.swcdensityvis;

/// imports
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @brief an (immutable) cuboid 
 * @author stephan
 */
@ToString @EqualsAndHashCode @AllArgsConstructor @Getter @Setter 
public class Cuboid {
	private final float x;
	private final float y;
	private final float z;
	private final float width;
	private final float height;
	private final float depth;
}
