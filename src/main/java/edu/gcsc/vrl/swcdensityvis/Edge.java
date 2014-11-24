/// package's name
package edu.gcsc.vrl.swcdensityvis;

/// imports
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author stephan
 * @param <T>
 */
@ToString @Getter @Setter @EqualsAndHashCode @AllArgsConstructor public class Edge<T> {
	private T from;
	private T to;
}
