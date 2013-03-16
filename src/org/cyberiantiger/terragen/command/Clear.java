/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cyberiantiger.terragen.command;

import org.bukkit.command.CommandSender;
import org.cyberiantiger.terragen.TerraGen;
import org.cyberiantiger.terragen.object.ObjectTemplate;

/**
 *
 * @author antony
 */
public class Clear implements Command {

    public boolean execute(TerraGen gen, CommandSender sender, String... args) {
        if (args.length != 2) {
            return false;
        }

        ObjectTemplate t = gen.getTemplate(args[0]);
        if (t == null) {
            sender.sendMessage("Template " + args[0] + " not found.");
            return true;
        }

        if (t.getProperties().remove(args[1]) != null) {
            sender.sendMessage("Updated " + args[0] + " cleared " + args[1]);
        } else {
            sender.sendMessage("Property " + args[1] + " not found in " + args[0]);
        }
        return true;
    }

    public String getUsage() {
        return "/tg clear <template> <prop>";
    }
}
