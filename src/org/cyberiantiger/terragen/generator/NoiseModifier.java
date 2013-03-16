/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cyberiantiger.terragen.generator;

/**
 *
 * @author antony
 */
public class NoiseModifier implements NoiseSource {
    private final NoiseSource source;

    private double frequency = 1D;
    private double amplitude = 1D;
    private double translate = 0D;

    public NoiseModifier(NoiseSource source) {
        this.source = source;
    }

    public double noise(double x, double y, double z) {
        return translate + source.noise(x * frequency, y * frequency, z * frequency) * amplitude;
    }

    public double getTranslate() {
        return translate;
    }

    public void setTranslate(double translate) {
        this.translate = translate;
    }

    public double getAmplitude() {
        return amplitude;
    }

    public void setAmplitude(double amplitude) {
        this.amplitude = amplitude;
    }

    public double getFrequency() {
        return frequency;
    }

    public void setFrequency(double frequency) {
        this.frequency = frequency;
    }

    public double getWavelength() {
        return 1/frequency;
    }

    public void setWavelength(double wavelength) {
        this.frequency = 1/wavelength;
    }
}
