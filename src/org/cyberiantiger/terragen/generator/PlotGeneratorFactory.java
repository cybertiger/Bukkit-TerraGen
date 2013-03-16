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
public class PlotGeneratorFactory extends AbstractGeneratorFactory {

    /*
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
     */
    public ChunkGenerator create(TerraGen gen, String paramString) {
        Map<String,String> args = parseArgsMap(paramString);
        PlotGenerator ret = new PlotGenerator();
        if (args.containsKey("plot")) {
            ret.setPlot(BlockInfo.valueOf(args.get("plot"), ret.getPlot()));
        }
        if (args.containsKey("border")) {
            ret.setBorder(BlockInfo.valueOf(args.get("border"), ret.getBorder()));
        }
        if (args.containsKey("path")) {
            ret.setPath(BlockInfo.valueOf(args.get("path"), ret.getPath()));
        }
        if (args.containsKey("worldFloor")) {
            ret.setWorldFloor(BlockInfo.valueOf(args.get("worldFloor"), ret.getWorldFloor()));
        }
        if (args.containsKey("ground")) {
            ret.setGround(BlockInfo.valueOf(args.get("ground"), ret.getGround()));
        }
        if (args.containsKey("pathSize")) {
            try {
                ret.setPathSize(Integer.parseInt(args.get("pathSize")));
            } catch (NumberFormatException nfe) {
                gen.getLogger().info("pathSize should be a number");
            }
        }
        if (args.containsKey("borderSize")) {
            try {
                ret.setBorderSize(Integer.parseInt(args.get("borderSize")));
            } catch (NumberFormatException nfe) {
                gen.getLogger().info("borderSize should be a number");
            }
        }
        if (args.containsKey("plotSize")) {
            try {
                ret.setPlotSize(Integer.parseInt(args.get("plotSize")));
            } catch (NumberFormatException nfe) {
                gen.getLogger().info("plotSize should be a number");
            }
        }
        if (args.containsKey("plotHeight")) {
            try {
                ret.setPlotHeight(Integer.parseInt(args.get("plotHeight")));
            } catch (NumberFormatException nfe) {
                gen.getLogger().info("plotHeight should be a number");
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
