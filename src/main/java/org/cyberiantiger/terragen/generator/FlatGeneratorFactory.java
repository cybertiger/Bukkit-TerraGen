/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cyberiantiger.terragen.generator;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.block.Biome;
import org.cyberiantiger.terragen.TerraGen;
import org.cyberiantiger.terragen.generator.FlatGenerator.Layer;
import org.cyberiantiger.terragen.object.BlockInfo;

/**
 *
 * @author antony
 */
public class FlatGeneratorFactory extends AbstractGeneratorFactory {

    public FlatGenerator create(TerraGen gen, String paramString) {
        FlatGenerator ret = new FlatGenerator();
        List<Arg> args = parseArgsList(paramString);

        List<Layer> layers = new ArrayList<Layer>();

        Biome b = null;

        for (Arg a : args) {
            if ("boime".equals(a.getKey())) {
                try {
                    b = Biome.valueOf(a.getKey().toUpperCase());
                } catch (IllegalArgumentException iae) {
                    gen.getLogger().info("Unknown biome: " + a.getKey());
                }
                continue;
            }
            BlockInfo block = BlockInfo.valueOf(a.getKey());
            if (block == null) {
                gen.getLogger().info("Unknown block: " + a.getKey());
                continue;
            }
            try {
                int count = Integer.parseInt(a.getValue());
                layers.add(new Layer(block, count));
            } catch (NumberFormatException nfe) {
                gen.getLogger().info("Not a number: " + a.getValue());
            }
        }

        if (!layers.isEmpty()) {
            ret.setLayers(layers);
        }

        if (b != null) {
            ret.setBiome(b);
        }

        return ret;
    }
}
