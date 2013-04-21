/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cyberiantiger.terragen.generator;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import org.cyberiantiger.terragen.generator.PerlinNoise;
import java.util.Random;
import org.cyberiantiger.terragen.MatrixI3;

/**
 *
 * @author antony
 */
public class Main {
    public static void main(String... args) throws Exception {
        System.out.println(MatrixI3.IDENTITY);
        System.out.println(MatrixI3.ROTATE90);
        System.out.println(MatrixI3.ROTATE180);
        System.out.println(MatrixI3.ROTATE270);
        BeanInfo info = Introspector.getBeanInfo(PlotGenerator.class);
        for (PropertyDescriptor d : info.getPropertyDescriptors())
            System.out.println(d.getName());
        System.out.println(-1 % 10);
        System.out.println("".split(":").length);
        Random r = new Random();
        PerlinNoise noise = new PerlinNoise(new Random(0));
        double x = r.nextDouble();
        double y = r.nextDouble();
        double z = r.nextDouble();

        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;

        int highCount = 0;

        long time = System.currentTimeMillis();
        for (int i = 0; i < 65536; i++) {
            double val = noise.noise(r.nextDouble() * 16,r.nextDouble()*16,r.nextDouble()*16);
            if (val < min) min = val;
            if (val > max) max = val;
            if (val >= 0) {
                highCount++;
            }
        }

        time = System.currentTimeMillis() - time;
        System.out.println("Took " + time + "ms");
        System.out.println("Min: " + min + " Max:" + max);
        System.out.println("High count: " + highCount);
    }

}
