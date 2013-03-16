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
public class Prop implements Command {

    public boolean execute(TerraGen gen, CommandSender sender, String... args) {
        if (args.length != 3) {
            return false;
        }
        ObjectTemplate t = gen.getTemplate(args[0]);
        if (t == null) {
            sender.sendMessage("Template " + args[0] + " does not exist");
            return true;
        }

        t.getProperties().put(args[1], args[2]);

        sender.sendMessage("Updated " + args[0] + " set " + args[1] + " = " + args[2]);

        return true;
    }

    public String getUsage() {
        return "/tg prop <template> <key> <value>";
    }

}
