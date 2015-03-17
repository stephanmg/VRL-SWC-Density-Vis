/// package's name
package edu.gcsc.vrl.swcdensityvis.data;

/// imports
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Getter;
import lombok.Setter;

/**
 * @brief an Edge
 * @author stephanmg <stephan@syntaktischer-zucker.de>
 * @param <T> 
 */
@ToString @Getter @Setter @EqualsAndHashCode @AllArgsConstructor public class Edge<T> {
	private T from;
	private T to;
}
