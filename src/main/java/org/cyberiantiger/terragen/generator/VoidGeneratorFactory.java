/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cyberiantiger.terragen.generator;

import java.util.Map;
import org.bukkit.block.Biome;
import org.bukkit.generator.ChunkGenerator;
import org.cyberiantiger.terragen.TerraGen;
import org.cyberiantiger.terragen.object.BlockInfo;

/**
 *
 * @author antony
 */
public class VoidGeneratorFactory extends AbstractGeneratorFactory {

    public ChunkGenerator create(TerraGen gen, String paramString) {
        Map<String, String> args = parseArgsMap(paramString);
        gen.getLogger().info("Creating void generator with args: " + args);
        VoidGenerator ret = new VoidGenerator();
        if (args.containsKey("block")) {
            ret.setBlock(BlockInfo.valueOf(args.get("block"), ret.getBlock()));
        }
        if (args.containsKey("spawnHeight")) {
            try {
                ret.setSpawnHeight(Integer.parseInt(args.get("spawnHeight")));
            } catch (NumberFormatException nfe) {
                gen.getLogger().info("spawnHeight must be a number");
            }
        }
        if (args.containsKey("spawnSize")) {
            try {
                ret.setSpawnSize(Integer.parseInt(args.get("spawnSize")));
            } catch (NumberFormatException nfe) {
                gen.getLogger().info("spawnSize must be a number");
            }
        }
        if (args.containsKey("biome")) {
            try {
                ret.setBiome(Biome.valueOf(args.get("biome").toUpperCase()));
            } catch (IllegalArgumentException iae) {
                gen.getLogger().info("Unknown biome: " + args.get("biome"));
            }
        }
        return ret;
    }
}
