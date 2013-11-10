/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cyberiantiger.terragen.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.cyberiantiger.minecraft.Coord;
import org.cyberiantiger.terragen.Selection;
import org.cyberiantiger.terragen.TerraGen;
import org.cyberiantiger.terragen.object.ObjectTemplate;

/**
 *
 * @author antony
 */
public class Update implements Command {

    public boolean execute(TerraGen gen, CommandSender sender, String... args) {
                if (args.length != 1 && args.length != 4) {
            return false;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage("Failed: No selection");
            return true;
        }

        Player p = (Player) sender;

        Selection s = gen.getSelection(p);

        if (!s.isValid()) {
            sender.sendMessage("Failed: Invalid selection");
            return true;
        }

        String name = args[0];

        Coord selectionSize = s.getSize();

        Coord offset = new Coord(-selectionSize.getX() / 2, 0, -selectionSize.getZ() / 2);

        if (args.length == 4) {
            try {
                int xoffset = Integer.parseInt(args[1]);
                int yoffset = Integer.parseInt(args[2]);
                int zoffset = Integer.parseInt(args[3]);
                offset = offset.translate(xoffset, yoffset, zoffset);
            } catch (NumberFormatException nfe) {
                return false;
            }
        }

        ObjectTemplate template = gen.getTemplate(name, true);

        template.create(s, offset);

        p.sendMessage("Template " + name + " created or updated from selection.");

        return true;
    }

    public String getUsage() {
        return "/tg update <name> [<xoffset> <yoffset> <zoffset>]";
    }

}
