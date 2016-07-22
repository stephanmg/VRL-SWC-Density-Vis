/// package's name
package edu.gcsc.vrl.swcdensityvis.data;

/// imports
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @brief a swappable pair for numbers only
 * @param <T> a number wrapper
 * @author stephanmg <stephan@syntaktischer-zucker.de>
 */
@Getter @Setter @AllArgsConstructor @EqualsAndHashCode @ToString 
public class SwappablePair<T extends java.lang.Number> {
	private T first;
	private T second;
}