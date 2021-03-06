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
public class Rename implements Command {

    public boolean execute(TerraGen gen, CommandSender sender, String... args) {
        if (args.length != 2)
            return false;
        String from = args[0];
        String to = args[1];

        ObjectTemplate t = gen.removeTemplate(from);
        if (t == null) {
            sender.sendMessage("No templated named: " + from);
            return true;
        }
        gen.setTemplate(to, t);

        sender.sendMessage("Template " + from + " renamed to " + to);
        
        return true;
    }

    public String getUsage() {
        return "/tg rename <from> <to>";
    }

}
