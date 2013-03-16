/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cyberiantiger.terragen.command;

import org.bukkit.command.CommandSender;
import org.cyberiantiger.terragen.TerraGen;
import org.cyberiantiger.terragen.object.BlockInfo;
import org.cyberiantiger.terragen.object.ObjectTemplate;

/**
 *
 * @author antony
 */
public class Remove implements Command {

    public boolean execute(TerraGen gen, CommandSender sender, String... args) {
        if (args.length != 2) {
            return false;
        }

        ObjectTemplate template = gen.getTemplate(args[0]);

        BlockInfo block = BlockInfo.valueOf(args[1]);

        if (template == null) {
            sender.sendMessage("No template: " + args[0]);
            return true;
        }

        if (block == null) {
            sender.sendMessage("Unknown block: " + block);
            return true;
        }

        int count = template.remove(block);

        sender.sendMessage("Removed " + count + " blocks from " + args[0]);

        return true;
    }

    public String getUsage() {
        return "/tg remove <template> <block id>";
    }
}
