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
public class Delete implements Command {

    public boolean execute(TerraGen gen, CommandSender sender, String... args) {
        if (args.length != 1) {
            return false;
        }
        String template = args[0];

        ObjectTemplate t = gen.removeTemplate(template);

        if (t == null) {
            sender.sendMessage("No template named: " + template);
        } else {
            sender.sendMessage("Template deleted: " + template);
        }
        return true;
    }

    public String getUsage() {
        return "/tg delete <template>";
    }
}
