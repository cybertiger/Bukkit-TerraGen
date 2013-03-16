/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cyberiantiger.terragen.generator;

import java.util.Arrays;
import java.util.Random;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.generator.ChunkGenerator;
import org.cyberiantiger.terragen.object.BlockInfo;

/**
 *
 * @author antony
 */
public class PlotGenerator extends ChunkGenerator {

    private BlockInfo plot = new BlockInfo(2); // Grass
    private BlockInfo border = new BlockInfo(98); // Stone brick
    private BlockInfo path = new BlockInfo(44, (byte) 5); // Stone brick slab.
    private BlockInfo worldFloor = new BlockInfo(7); // Bedrock
    private BlockInfo ground = new BlockInfo(3); // Dirt
    private Biome biome = Biome.PLAINS;
    private int pathSize = 4;
    private int borderSize = 1;
    private int plotSize = 32;
    private int plotHeight = 64;

    public Biome getBiome() {
        return biome;
    }

    public void setBiome(Biome biome) {
        this.biome = biome;
    }

    public BlockInfo getBorder() {
        return border;
    }

    public void setBorder(BlockInfo border) {
        this.border = border;
    }

    public int getBorderSize() {
        return borderSize;
    }

    public void setBorderSize(int borderSize) {
        this.borderSize = borderSize;
    }

    public BlockInfo getGround() {
        return ground;
    }

    public void setGround(BlockInfo ground) {
        this.ground = ground;
    }

    public BlockInfo getPath() {
        return path;
    }

    public void setPath(BlockInfo path) {
        this.path = path;
    }

    public int getPathSize() {
        return pathSize;
    }

    public void setPathSize(int pathSize) {
        this.pathSize = pathSize;
    }

    public BlockInfo getPlot() {
        return plot;
    }

    public void setPlot(BlockInfo plot) {
        this.plot = plot;
    }

    public int getPlotHeight() {
        return plotHeight;
    }

    public void setPlotHeight(int plotHeight) {
        this.plotHeight = plotHeight;
    }

    public int getPlotSize() {
        return plotSize;
    }

    public void setPlotSize(int plotSize) {
        this.plotSize = plotSize;
    }

    public BlockInfo getWorldFloor() {
        return worldFloor;
    }

    public void setWorldFloor(BlockInfo worldFloor) {
        this.worldFloor = worldFloor;
    }

    @Override
    public byte[][] generateBlockSections(World world, Random random, int x, int z, BiomeGrid biomes) {
        byte[][] ret = new byte[world.getMaxHeight() / 16][];

        int xBase = x * 16;
        int zBase = z * 16;

        int max = plotSize + pathSize + 2 * borderSize;

        for (int i = 0; i <= plotHeight >> 4; i++) {
            byte[] block = ret[i] = new byte[4096];

            if (i == plotHeight >> 4) {
                int blockHeight = plotHeight & 0xf;
                if (blockHeight != 0) {
                    if (i == 0) {
                        arraySet(block, 0, 256, (byte) worldFloor.getId());
                        if (blockHeight > 1) {
                            arraySet(block, 256, (blockHeight << 8) - 257, (byte) ground.getId());
                        }
                    } else {
                        arraySet(block, 0, (blockHeight << 8) - 1, (byte) ground.getId());
                    }
                }
                int offset = blockHeight * 256;
                for (int j = 0; j < 16; j++) {
                    for (int k = 0; k < 16; k++) {
                        int xOffset = (k + xBase) % max;
                        int zOffset = (j + zBase) % max;

                        if (xOffset < 0) {
                            xOffset += max;
                        }
                        if (zOffset < 0) {
                            zOffset += max;
                        }

                        block[offset + (j << 4) + k] =
                                (byte) getBlock(xOffset, zOffset).getId();
                        biomes.setBiome(k, j, biome);
                    }
                }
            } else {
                if (i == 0) {
                    arraySet(block, 0, 256, (byte) worldFloor.getId());
                    arraySet(block, 256, 4096 - 256, (byte) ground.getId());
                } else {
                    arraySet(block, 0, 4096, (byte) ground.getId());
                }
            }
        }
        return ret;
    }

    private BlockInfo getBlock(int xOffset, int zOffset) {
        int p = pathSize + 2 * borderSize;
        if (xOffset >= p) {
            if (zOffset >= p) {
                return plot;
            }
            if (zOffset < borderSize || zOffset >= (borderSize + pathSize)) {
                return border;
            } else {
                return path;
            }
        } else if (zOffset >= p) {
            if (xOffset < borderSize || xOffset >= (borderSize + pathSize)) {
                return border;
            } else {
                return path;
            }
        } else {
            if ((xOffset >= borderSize && xOffset < (borderSize + pathSize))
                    || (zOffset >= borderSize && zOffset < (borderSize + pathSize))) {
                return path;
            } else {
                return border;
            }
        }
    }

    @Override
    public Location getFixedSpawnLocation(World world, Random random) {
        return new Location(world, borderSize*2 + pathSize + plotSize/2, plotHeight + 1, borderSize*2 + pathSize + plotSize/2);
    }

    private void arraySet(byte[] b, int offset, int len, byte val) {
        Arrays.fill(b, offset, offset+len, val);
    }
}
