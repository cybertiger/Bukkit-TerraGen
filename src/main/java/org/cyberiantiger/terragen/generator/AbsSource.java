/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cyberiantiger.terragen.generator;

import org.cyberiantiger.terragen.generator.NoiseSource;

/**
 *
 * @author antony
 */
public class AbsSource implements NoiseSource {
    private final NoiseSource source;

    public AbsSource(NoiseSource source) {
        this.source = source;
    }

    public double noise(double x, double y, double z) {
        return Math.abs(source.noise(x, y, z));
    }
}
