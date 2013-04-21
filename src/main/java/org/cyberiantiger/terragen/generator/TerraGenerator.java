/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cyberiantiger.terragen.generator;

import java.util.Random;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.generator.ChunkGenerator;

/**
 *
 * @author antony
 */
public class TerraGenerator extends ChunkGenerator {

    private static final int CONTINENT_OCTAVES = 5;
    private static final double CONTINENT_SIZE = 128;
    private static final double SHELF_MIN = -0.1;
    private static final double CONTINENT_MIN = 0.1;
    private static final double CONTINENT_INNER_MIN = 0.13; // for beach & marsh.
    
    private static final int SALT_MARSH_OCTAVES = 2;
    private static final double SALT_MARSH_SIZE = 8;
    private static final double SALT_MARSH_MIN = 0;

    private static final int TERRAIN_OCTAVES = 6;
    private static final double TERRAIN_SIZE = 64;
    private static final double TERRAIN_MOUNTAIN_MIN = 0.5;

    private boolean init = false;
    private long seed = 0L;
    NoiseSource continent;
    double continentY;
    NoiseSource continent_boundary_biome;
    NoiseSource coastalMountain;
    double coastalMountainY;
    NoiseSource saltMarsh;
    double saltMarshY;

    public TerraGenerator() {
    }

    private synchronized void init(World world) {
        if (!init || world.getSeed() != seed) {
            this.seed = world.getSeed();
            Random r = new Random(seed);

            // continent generator.
            {
                NoiseSource s1 = new PerlinNoise(new Random(r.nextLong()));
                OctaveSource s2 = new OctaveSource(s1, CONTINENT_OCTAVES);
                NoiseModifier s3 = new NoiseModifier(s2);
                s3.setWavelength(CONTINENT_SIZE);
                this.continent = s3;
                continentY = r.nextDouble() * CONTINENT_SIZE;
            }
            // mountain biome generator
            {
                NoiseSource s1 = new PerlinNoise(new Random(r.nextLong()));
                AbsSource s2 = new AbsSource(s1);
                OctaveSource s3 = new OctaveSource(s2, TERRAIN_OCTAVES);
                NoiseModifier s4 = new NoiseModifier(s3);
                s4.setWavelength(TERRAIN_SIZE);
                s4.setAmplitude(-1);
                s4.setTranslate(1);
                this.coastalMountain = s4;
                coastalMountainY = r.nextDouble() * TERRAIN_SIZE;
            }
            // salt marsh biome generator
            {
                NoiseSource s1 = new PerlinNoise(new Random(r.nextLong()));
                OctaveSource s2 = new OctaveSource(s1, SALT_MARSH_OCTAVES);
                NoiseModifier s3 = new NoiseModifier(s2);
                s3.setWavelength(SALT_MARSH_SIZE);
                saltMarsh = s3;
                saltMarshY = r.nextDouble() * SALT_MARSH_SIZE;
            }
        }
    }

    @Override
    public byte[][] generateBlockSections(World world, Random random, int x, int z, BiomeGrid biomes) {
        init(world);
        x = x * 16;
        z = z * 16;
        // Override default biome.
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                biomes.setBiome(i, j, Biome.PLAINS);
            }
        }
        int maxHeight = world.getMaxHeight();
        byte[][] chunk = new byte[maxHeight >> 4][];

        for (int a = 0; a < 16; a++) {
            for (int b = 0; b < 16; b++) {
                double xd = x + a;
                double zd = z + b;
                setBlock(chunk, a, 0, b, (byte) Material.BEDROCK.getId());
                double contentVal = continent.noise(xd, continentY, zd);
                if (contentVal > CONTINENT_INNER_MIN) {
                    double coastalMountainVal = coastalMountain.noise(xd, coastalMountainY, zd);
                    if (coastalMountainVal > TERRAIN_MOUNTAIN_MIN) {
                        setBlock(chunk, a, 1, b, (byte) Material.STONE.getId());
                    } else {
                        setBlock(chunk, a, 1, b, (byte) Material.GRASS.getId());
                    }
                } else if (contentVal > CONTINENT_MIN) {
                    double saltMarshVal = saltMarsh.noise(xd, saltMarshY, zd);
                    if (saltMarshVal > SALT_MARSH_MIN) {
                        setBlock(chunk, a, 1, b, (byte) Material.MELON_BLOCK.getId());
                        continue;
                    }
                    if (contentVal <= CONTINENT_INNER_MIN) {
                        setBlock(chunk, a, 1, b, (byte) Material.SAND.getId());
                    } else {
                        setBlock(chunk, a, 1, b, (byte) Material.GRASS.getId());
                    }

                } else if (contentVal > SHELF_MIN) {
                    setBlock(chunk, a, 1, b, (byte) Material.LAPIS_BLOCK.getId());
                } else {
                    setBlock(chunk, a, 1, b, (byte) Material.WATER.getId());
                }
            }
        }
        /*
        for (int i = 0; i<chunk.length; i++) {
        int y0 = i<<4;
        byte[] chunkBlock = new byte[4096];
        for (int j = 0; j < 4096; j++) {
        int yOff = j>>8;
        int zOff = (j>>4) & 0xf;
        int xOff = j & 0xf;
        double xd = x+xOff;
        double yd = y0+yOff;
        double zd = z+zOff;

        xd *= scale;
        yd *= scale;
        zd *= scale;

        double val = noise.noise(xd,yd,zd);
        //System.out.println("Got noise: "+val+" for " + xd + "," + yd + "," + zd);
        if (val > 0.0) {
        chunkBlock[j] = (byte) Material.AIR.getId();
        } else {
        chunkBlock[j] = (byte) Material.STONE.getId();
        }
        }
        chunk[i] = chunkBlock;
        }
         */
        return chunk;
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
