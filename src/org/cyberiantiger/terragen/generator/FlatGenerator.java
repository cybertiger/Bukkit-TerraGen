/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cyberiantiger.terragen.generator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.generator.ChunkGenerator;
import org.cyberiantiger.terragen.object.BlockInfo;

/**
 *
 * @author antony
 */
public class FlatGenerator extends ChunkGenerator {

    private List<Layer> layers = new ArrayList<Layer>();
    private Biome biome = Biome.PLAINS;

    public FlatGenerator() {
        layers.add(new Layer(BlockInfo.valueOf("bedrock"), 1));
        layers.add(new Layer(BlockInfo.valueOf("dirt"), 4));
        layers.add(new Layer(BlockInfo.valueOf("grass"), 1));
    }

    public List<Layer> getLayers() {
        return layers;
    }

    public void setLayers(List<Layer> layers) {
        this.layers = layers;
    }

    public Biome getBiome() {
        return biome;
    }

    public void setBiome(Biome biome) {
        this.biome = biome;
    }

    @Override
    public byte[][] generateBlockSections(World world, Random random, int x, int z, BiomeGrid biomes) {
        byte[][] ret = new byte[world.getMaxHeight()>>4][];

        int yOffset = 0;
        Iterator<Layer> itr = layers.iterator();
        if (itr.hasNext()) {
            Layer layer = itr.next();
            int layerOffset = 0;
            while (yOffset < world.getMaxHeight()) {
                if (layer.getBlock().getId() == Material.AIR.getId()) {
                    yOffset += layer.getCount();
                    layerOffset += layer.getCount();
                }

                if (layerOffset >= layer.getCount()) {
                    if (!itr.hasNext())
                        break;
                    layer = itr.next();
                    layerOffset = 0;
                    continue;
                }

                int blockOffset = yOffset & 0xf;

                byte[] block = ret[yOffset>>4];
                if (block == null) {
                    block = ret[yOffset>>4] = new byte[4096];
                }

                int left = 16-blockOffset;
                int todo = Math.min(left, layer.getCount());

                arraySet(block, blockOffset<<8, todo<<8, (byte) layer.getBlock().getId());

                yOffset += todo;
                layerOffset += todo;
            }
        }

        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                biomes.setBiome(i, j, biome);
            }
        }

        return ret;
    }

    @Override
    public Location getFixedSpawnLocation(World world, Random random) {
        int y = 0;
        for (Layer l : layers) {
            y += l.getCount();
        }
        return new Location(world, 0, y+1, 0);
    }


    private void arraySet(byte[] b, int offset, int len, byte val) {
        Arrays.fill(b,offset,offset+len,val);
    }

    public static class Layer {

        private final BlockInfo block;
        private final int count;

        public Layer(BlockInfo block, int count) {
            this.block = block;
            this.count = count;
        }

        public BlockInfo getBlock() {
            return block;
        }

        public int getCount() {
            return count;
        }
    }
}
