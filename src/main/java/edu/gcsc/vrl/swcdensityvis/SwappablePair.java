/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.gcsc.vrl.swcdensityvis;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @brief a swappable pair for numbers only
 * @param <T> a number wrapper
 * @author stephan
 */
@Getter @Setter @AllArgsConstructor @EqualsAndHashCode @ToString 
public class SwappablePair<T extends java.lang.Number> {
	private T first;
	private T second;
}