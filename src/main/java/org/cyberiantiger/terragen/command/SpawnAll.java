/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cyberiantiger.terragen.command;

import java.util.Iterator;
import java.util.Set;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.cyberiantiger.terragen.Coord;
import org.cyberiantiger.terragen.TerraGen;
import org.cyberiantiger.terragen.object.ObjectTemplate;
import org.cyberiantiger.terragen.object.Orientation;

/**
 *
 * @author antony
 */
public class SpawnAll implements Command {

    public boolean execute(TerraGen gen, CommandSender sender, String... args) {
        if (args.length != 0) {
            return false;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage("Player only.");
            return true;
        }

        Player p = (Player) sender;

        Block b = p.getTargetBlock(null, 200);

        if (b == null) {
            sender.sendMessage("Look at the block where you wish to spawn the object.");
            return true;
        }

        Set<String> templateNames = gen.getTemplateNames();

        int gridSize = (int) Math.ceil(Math.sqrt(templateNames.size()));

        String[][] name = new String[gridSize][gridSize];
        ObjectTemplate[][] grid = new ObjectTemplate[gridSize][gridSize];

        Iterator<String> itr = templateNames.iterator();
        int[] xWidth = new int[gridSize];
        int[] zWidth = new int[gridSize];

        for (int j = gridSize-1; j >= 0; j--) {
            for (int i = 0; i < gridSize; i++) {
                if (itr.hasNext()) {
                    String s = itr.next();
                    ObjectTemplate t = gen.getTemplate(s);
                    name[i][j] = s;
                    grid[i][j] = t;
                    Coord c = t.getSize();
                    if (c.getX() > xWidth[i]) {
                        xWidth[i] = c.getX();
                    }
                    if (c.getZ() > zWidth[j]) {
                        zWidth[j] = c.getY();
                    }
                }
            }
        }

        int totalX = 0;
        int totalZ = 0;

        for (int i = 0; i < gridSize; i++) {
            totalX += xWidth[i] + 1;
            totalZ += zWidth[i] + 1;
        }

        Coord base = new Coord(b.getLocation().getBlockX() - (totalX / 2), 0, b.getLocation().getBlockZ() - (totalZ / 2));
        World w = b.getWorld();

        int x = base.getX();

        for (int i = 0; i < gridSize; i++) {
            int z = base.getZ();
            for (int j = 0; j < gridSize; j++) {
                ObjectTemplate t = grid[i][j];
                if (t != null) {
                    Coord size = t.getSize();
                    Coord offset = t.getOffset();

                    int xSpawn = x + (xWidth[i] + offset.getX()) / 2;
                    int zSpawn = z + (zWidth[j] - offset.getZ()) / 2;

                    Block h = w.getHighestBlockAt(xSpawn, zSpawn);

                    t.apply(w, new Coord(xSpawn, h.getY() + offset.getY(), zSpawn), Orientation.NORTH, gen, false);

                    zSpawn += t.getMaxZ() + 1;

                    Block hh = w.getHighestBlockAt(xSpawn, zSpawn);

                    hh.setTypeId(Material.SIGN_POST.getId());
                    hh.setData((byte) 0);

                    BlockState state = hh.getState();
                    if (state instanceof Sign) {
                        Sign sign = (Sign) state;
                        String signText = name[i][j];
                        if (signText.length() > 64) {
                            signText = signText.substring(0, 61) + "...";
                        }
                        String[] text = new String[4];
                        for (int k = 0; k < 60; k += 15) {
                            if (signText.length() < k) {
                                sign.setLine(k / 15, "");
                            } else if (signText.length() < k + 15) {
                                sign.setLine(k / 15, signText.substring(k, signText.length()));
                            } else {
                                sign.setLine(k / 15, signText.substring(k, 15));
                            }
                        }
                        sign.update();
                    }
                }
                z += zWidth[j] + 1;
            }
            x += xWidth[i] + 1;
        }

        return true;
    }

    public String getUsage() {
        return "/tg spawnall";
    }
}
