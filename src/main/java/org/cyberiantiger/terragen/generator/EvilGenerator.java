/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cyberiantiger.terragen.generator;

import java.util.Random;
import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator;

/**
 *
 * @author antony
 */
public class EvilGenerator extends ChunkGenerator {

    private static final double NOISE_REDUCTION = 2;
    private static final double LAND = 0.6;
    private static final double ISLAND_HEIGHT = 32;
    private static final double LEVEL_HEIGHT = 32;
    private static final int LEVEL_COUNT = 5;
    private static final double ISLAND_SIZE = 128;
    private static final double ISLAND_FLOOR_VARIATION_WAVELENGTH = 128;
    private static final double ISLAND_FLOOR_VARIATION_AMPLITUDE = 32;
    private static final int ISLAND_FLOOR_OCTAVES = 7;
    private static final int ISLAND_SHAPE_OCTAVES = 2;
    private static final double ISLAND_SHARPNESS = 4; // higher = more sharp
    private static final double GLOWSTONE_PERCENT = 0.75;
    private static final double BIOME_SIZE = 64;
    private static final int BIOME_OCTAVES = 4;
    private static final double BIOME_NETHER_MIN = -0.3;
    private static final double BIOME_NETHER_MAX = 0.3;
    private World world;
    private NoiseSource n1;
    private NoiseSource n2;
    private NoiseSource n3;
    private double y1[] = new double[LEVEL_COUNT];
    private double y2[] = new double[LEVEL_COUNT];
    private double y3[] = new double[LEVEL_COUNT];

    @Override
    public byte[][] generateBlockSections(World world, Random r, int x, int z, BiomeGrid biomes) {
        init(world, r);
        int maxHeight = world.getMaxHeight();
        byte[][] ret = new byte[maxHeight >> 4][];
        byte[] block = ret[0] = new byte[4096];

        int xBase = x * 16;
        int zBase = z * 16;

        double[][][] levelNoise = new double[LEVEL_COUNT][16][16];
        double[][][] levelOffset = new double[LEVEL_COUNT][16][16];
        double[][][] biomeType = new double[LEVEL_COUNT][16][16];
        for (int i = 0; i < LEVEL_COUNT; i++) {
            for (int j = 0; j < 16; j++) {
                for (int k = 0; k < 16; k++) {
                    levelNoise[i][j][k] = n1.noise(xBase + k, y1[i], zBase + j);
                    levelOffset[i][j][k] = n2.noise(xBase + k, y2[i], zBase + j);
                    biomeType[i][j][k] = n3.noise(xBase + k, y3[i], zBase + j);
                }
            }
        }
        for (int i = 0; i < maxHeight; i++) {
            for (int j = 0; j < 16; j++) {
                for (int k = 0; k < 16; k++) {
                    boolean hasLand = false;
                    double blockChance = 0;
                    double levelHeight = Double.MAX_VALUE;
                    double biomeChance = 0d;
                    for (int l = 0; l < LEVEL_COUNT; l++) {
                        if (levelNoise[l][j][k] > LAND) {
                            double h = (1 + l) * LEVEL_HEIGHT + levelOffset[l][j][k];
                            if (h > i && h < levelHeight) {
                                blockChance = levelNoise[l][j][k];
                                biomeChance = biomeType[l][j][k];
                                levelHeight = h;
                                hasLand = true;
                            }
                        }
                    }
                    if (hasLand) {
                        blockChance = (blockChance - LAND) / (1 - LAND);
                        blockChance = Math.pow(blockChance, ISLAND_SHARPNESS);
                        blockChance -= NOISE_REDUCTION / ISLAND_HEIGHT;
                        double bChance = blockChance - (levelHeight - i) / ISLAND_HEIGHT;
                        int depth = (int) levelHeight - i;
                        if (bChance > 0) {
                            if (depth == 0) {
                                setBlock(ret, k, i, j, (byte) 110); // Mycellium
                            } else if (depth > ISLAND_HEIGHT * GLOWSTONE_PERCENT) {
                                setBlock(ret, k, i, j, (byte) 89);
                            } else {
                                setBlock(ret, k, i, j, (byte) 49);
                            }
                        }
                    }

                    /*
                    if (levelNoise[j][k] > 0.5 && i <= 32 + levelOffset[j][k]) {
                    double d = 2 * (levelNoise[j][k] - 0.5); // 0-1, 1 = middle.
                    d = Math.pow(d, 4);
                    d -= (32 + levelOffset[j][k] - i) / 32d;
                    if (d > 0) {
                    setBlock(ret, k, i, j, (byte) 49);
                    }
                    }
                     */
                }
            }
        }
        return ret;
    }

    private void init(World world, Random r) {
        r.setSeed(world.getSeed());
        if (world != this.world) {
            this.world = world;
            {
                PerlinNoise n1 = new PerlinNoise(r);
                AbsSource n2 = new AbsSource(n1);
                OctaveSource n3 = new OctaveSource(n2, ISLAND_SHAPE_OCTAVES);
                NoiseModifier n4 = new NoiseModifier(n3);
                n4.setAmplitude(-1);
                n4.setTranslate(1);
                n4.setWavelength(ISLAND_SIZE);
                this.n1 = n4;
            }
            {
                PerlinNoise n1 = new PerlinNoise(r);
                OctaveSource n2 = new OctaveSource(n1, ISLAND_FLOOR_OCTAVES);
                NoiseModifier n3 = new NoiseModifier(n2);
                n3.setAmplitude(ISLAND_FLOOR_VARIATION_AMPLITUDE);
                n3.setWavelength(ISLAND_FLOOR_VARIATION_WAVELENGTH);
                this.n2 = n3;
            }
            {
                PerlinNoise n1 = new PerlinNoise(r);
                OctaveSource n2 = new OctaveSource(n1, BIOME_OCTAVES);
                NoiseModifier n3 = new NoiseModifier(n2);
                n3.setWavelength(BIOME_SIZE);
                this.n3 = n3;
            }
            for (int i = 0; i < LEVEL_COUNT; i++) {
                y1[i] = r.nextDouble() * 256 * ISLAND_SIZE;
                y2[i] = r.nextDouble() * 256 * ISLAND_FLOOR_VARIATION_WAVELENGTH;
                y3[i] = r.nextDouble() * 256 * BIOME_SIZE;
            }
        }
    }

    private static void setBlock(byte[][] chunk, int x, int y, int z, byte id) {
        int blockId = y >> 4;
        byte[] block = chunk[blockId];
        if (block == null) {
            chunk[blockId] = block = new byte[4096];
        }
        block[((y & 0xf) << 8) + (z << 4) + x] = id;
    }
}
