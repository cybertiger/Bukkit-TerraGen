/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cyberiantiger.terragen.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.cyberiantiger.terragen.TerraGen;

/**
 *
 * @author antony
 */
public class Tool implements Command {

    public boolean execute(TerraGen gen, CommandSender sender, String... args) {
        if (args.length != 1 && args.length != 2)
            return false;
        String tool = args[0];

        if ("selection".equals(tool)) {
            if (args.length == 2) {
                if ("set".equals(args[1])) {
                    if (! (sender instanceof Player)) {
                        sender.sendMessage("Player only.");
                        return false;
                    }
                    Player p = (Player) sender;
                    ItemStack wand = p.getItemInHand();
                    gen.setSelectionTool(wand);
                    p.sendMessage("Selection tool set to " + wand.toString());
                } else if ("clear".equals(args[1])) {
                    gen.setSelectionTool(null);
                    sender.sendMessage("Selection tool cleared.");
                } else {
                    return false;
                }
            } else {
                sender.sendMessage("Selection tool: " + gen.getSelectionTool());
            }
        } else if ("id".equals(tool)) {
            if (args.length == 2) {
                if ("set".equals(args[1])) {
                    if (! (sender instanceof Player)) {
                        sender.sendMessage("Player only.");
                        return false;
                    }
                    Player p = (Player) sender;
                    ItemStack wand = p.getItemInHand();
                    gen.setIdTool(p.getItemInHand());
                    p.sendMessage("Id tool set to " + wand.toString());
                } else if ("clear".equals(args[1])) {
                    gen.setIdTool(null);
                    sender.sendMessage("Id tool cleared.");
                } else {
                    return false;
                }
            } else {
                sender.sendMessage("ID tool: " + gen.getIdTool());
            }
        } else {
            return false;
        }
        return true;
    }

    public String getUsage() {
        return "/tg tool <selection|id> [<clear|set>]";
    }

}
