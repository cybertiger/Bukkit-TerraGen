/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cyberiantiger.terragen.command;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.cyberiantiger.minecraft.Coord;
import org.cyberiantiger.terragen.TerraGen;
import org.cyberiantiger.terragen.object.ObjectTemplate;
import org.cyberiantiger.terragen.object.Orientation;

/**
 *
 * @author antony
 */
public class Spawn implements Command {

    public boolean execute(TerraGen gen, CommandSender sender, String... args) {
        if (args.length != 1 && args.length != 2 && args.length != 5) {
            return false;
        }
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can spawn templates, sorry.");
            return true;
        }

        String template = args[0];

        ObjectTemplate ot = gen.getTemplate(template);

        if (ot == null) {
            sender.sendMessage("Template " + template + " not found.");
            return true;
        }

        Player p = (Player) sender;

        Block b = p.getTargetBlock(null, 200);

        if (b == null) {
            sender.sendMessage("Look at the block where you wish to spawn the object.");
            return true;
        }

        Location l = b.getLocation();
        World w = b.getWorld();

        Coord offset = new Coord(l.getBlockX(), l.getBlockY() + 1, l.getBlockZ());

        Orientation o = Orientation.NORTH;

        if (args.length == 2) {
            try {
                o = Orientation.valueOf(args[1].toUpperCase());
            } catch (IllegalArgumentException iae) {
                sender.sendMessage("Not a valid orientation: " + args[1] + " must be north/south/east/west");
                return true;
            }
        }

        if (args.length == 5) {
            try {
                int xoffset = Integer.parseInt(args[2]);
                int yoffset = Integer.parseInt(args[3]);
                int zoffset = Integer.parseInt(args[4]);
                offset = offset.translate(xoffset, yoffset, zoffset);
            } catch (NumberFormatException nfe) {
                return false;
            }
        }
        ot.apply(w, offset, o, gen, false);

        sender.sendMessage("Template " + template + " spawned.");

        return true;

    }

    public String getUsage() {
        return "/tg spawn <template> [<orientation> [<xoffset> <yoffset> <zoffset>]]";
    }
}
