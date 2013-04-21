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
public class OctaveSource implements NoiseSource {
    private final NoiseSource source;
    private final int octaves;

    public OctaveSource(NoiseSource source, int octaves) {
        this.source = source;
        this.octaves = octaves;
    }

    public double noise(double x, double y, double z) {
        double m = 1D;
        int count = octaves;
        double ret = 0;
        while (count-- > 0) {
            ret += source.noise(x*m, y*m, z*m) / m;
            m *= 2;
        }
        return ret;
    }
}
