/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cyberiantiger.terragen.generator;

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
public class VoidGenerator extends ChunkGenerator {

    private int spawnSize = 3;
    private int spawnHeight = 64;
    private BlockInfo block = new BlockInfo(1);
    private Biome biome = Biome.PLAINS;

    public BlockInfo getBlock() {
        return block;
    }

    public void setBlock(BlockInfo block) {
        this.block = block;
    }

    public int getSpawnHeight() {
        return spawnHeight;
    }

    public void setSpawnHeight(int spawnHeight) {
        this.spawnHeight = spawnHeight;
    }

    public int getSpawnSize() {
        return spawnSize;
    }

    public void setSpawnSize(int spawnSize) {
        this.spawnSize = spawnSize;
    }

    public Biome getBiome() {
        return biome;
    }

    public void setBiome(Biome biome) {
        this.biome = biome;
    }

    @Override
    public byte[][] generateBlockSections(World world, Random random, int x, int z, BiomeGrid biomes) {
        int maxHeight = world.getMaxHeight();
        byte[][] chunk = new byte[maxHeight >> 4][];

        int xPos = x * 16;
        int zPos = z * 16;

        if (Math.abs(xPos + 8) < spawnSize + 8
                && Math.abs(zPos + 8) < spawnSize + 8) {
            int maxValue = (int) ((spawnSize + 0.5f) * (spawnSize + 0.5f));
            for (int i = 0; i < 16; i++) {
                for (int j = 0; j < 16; j++) {
                    int xVal = xPos + j;
                    int zVal = zPos + i;
                    if (xVal * xVal + zVal * zVal < maxValue) {
                        setBlock(chunk, j, spawnHeight, i, (byte) block.getId());
                    }
                }
            }
        }
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                biomes.setBiome(i, j, biome);
            }
        }
        return chunk;
    }

    @Override
    public Location getFixedSpawnLocation(World world, Random random) {
        return new Location(world, 0, spawnHeight + 1, 0);
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
