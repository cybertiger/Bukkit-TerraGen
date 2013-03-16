/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cyberiantiger.terragen.generator;

/**
 *
 * @author antony
 */
public class Hydrology {

    private final int w;
    private final int h;
    private final double[][] heightMap;
    private final double[][] waterMap;
    private final double[][] velocityMapX;
    private final double[][] velocityMapY;
    private final double[][] normalMapX;
    private final double[][] normalMapY;
    private final double seaLevel;
    private final double gravity;
    private final double damping;

    public Hydrology(double[][] heightMap, int w, int h, double seaLevel, double gravity, double damping) {
        this.heightMap = heightMap;
        this.waterMap = new double[h][w];
        this.velocityMapX = new double[h][w];
        this.velocityMapY = new double[h][w];
        this.normalMapX = new double[h][w];
        this.normalMapY = new double[h][w];
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                double z = heightMap[i][j];
                if (z < seaLevel) {
                    waterMap[i][j] = seaLevel - z;
                }
            }
        }
        this.w = w;
        this.h = h;
        this.seaLevel = seaLevel;
        this.gravity = gravity;
        this.damping = damping;
    }

    private double getHeight(int x, int y) {
        // Pretend values outside height map are equal to nearest edge
        // For simplicity.
        if (x < 0) x = 0;
        else if (x >= w) x = w-1;
        if (y < 0) y = 0;
        else if (y >= h) y = h-1;
        return heightMap[x][y] + waterMap[x][y];
    }

    private void computeNormal(int x, int y) {
        double i = 0, j = 0; // normal vector;
        double m;

        double tl = getHeight(x-1,y+1);
        double tm = getHeight(x, y+1);
        double tr = getHeight(x+1,y+1);
        double ml = getHeight(x-1,y);
        double mr = getHeight(x+1,y);
        double bl = getHeight(x-1,y-1);
        double bm = getHeight(x, y-1);
        double br = getHeight(x+1, y-1);

        i += 2*ml+tm+bm-2*mr-tr-br;
        j += 2*bm+br+bl-2*tm-tl-tr;

        // Don't scale to unit vector, not required.
        // m = Math.sqrt(i * i + j * j + 64); // Z component is always 8
        // i /= m;
        // j /= m;
        normalMapX[x][y] = i;
        normalMapY[x][y] = j;
        // normalMapZ[x][y] = 8; // Always 8.
    }

    private void computeNormals() {
        for (int i = 0; i < w; i ++) {
            for (int j = 0; j < h; j++)  {
                computeNormal(i,j);
            }
        }
    }

    private void applyPrecipitation() {
        // very naive, assumes uniform precipitation over land, none over sea.
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                double z = heightMap[i][j];
                if (z >= seaLevel) {
                    waterMap[i][j] += 0.1D;
                }
            }
        }
    }

    private void step(double time) {
        // Store moved water and it's velocity to be added back at the
        // end of the step.
        double[][] waterVolume = new double[w][h];
        double[][] waterVelocityX = new double[w][h];
        double[][] waterVelocityY = new double[w][h];

        applyPrecipitation();
        computeNormals();

        // Calculate gradients effect on water velocity & update main array.
        // Also calculate displaced water and it's velocity in tmp array,
        // whilst removing any displaced water from main array.
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                double z = heightMap[i][j];
                if (z < seaLevel)
                    continue;

                // Normal.
                double ni = normalMapX[i][j];
                double nj = normalMapY[i][j];
                double nk = 8d;

                // Current velocity
                double vi = velocityMapX[i][j];
                double vj = velocityMapY[i][j];

                // Mass of water (also height).
                double m = waterMap[i][j];

                double s = - m * gravity * nk / (ni*ni+nj*nj+nk*nk);

                double vdi = vi * s * time;
                double vdj = vj * s * time;

                vi = (vi + vdi) * damping;
                vj = (vj + vdj) * damping;

                // Calculate volume of moved water.
                
            }
        }
    }
}
