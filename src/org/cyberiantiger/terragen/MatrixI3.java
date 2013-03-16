/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cyberiantiger.terragen;

/**
 *
 * @author antony
 */
public final class MatrixI3 {

    private final int[][] data;
    public static final MatrixI3 IDENTITY = new MatrixI3(new int[][]{{1, 0, 0}, {0, 1, 0}, {0, 0, 1}});
    public static final MatrixI3 ROTATE90 = new MatrixI3(new int[][]{{0, 0, -1}, {0, 1, 0}, {1, 0, 0}});
    public static final MatrixI3 ROTATE180 = ROTATE90.multiply(ROTATE90);
    public static final MatrixI3 ROTATE270 = ROTATE180.multiply(ROTATE90);

    public MatrixI3(int[][] data) {
        this.data = data;
    }

    public int[][] getData() {
        return data;
    }

    public Coord multiply(Coord coord) {
        int x = data[0][0] * coord.getX() + data[0][1] * coord.getY() + data[0][2] * coord.getZ();
        int y = data[1][0] * coord.getX() + data[1][1] * coord.getY() + data[1][2] * coord.getZ();
        int z = data[2][0] * coord.getX() + data[2][1] * coord.getY() + data[2][2] * coord.getZ();
        return new Coord(x, y, z);
    }

    public MatrixI3 multiply(MatrixI3 o) {
        // Naive implementation.
        int[][] result = new int[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                for (int k = 0; k < 3; k++) {
                    result[j][i] += data[j][k] * o.data[k][i];
                }
            }
        }
        return new MatrixI3(result);
    }

    @Override
    public String toString() {
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                out.append(data[i][j]);
                if (j != 2) out.append(' ');
            }
            out.append('\n');
        }
        return out.toString();
    }
}
