/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cyberiantiger.terragen.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.command.CommandSender;
import org.cyberiantiger.terragen.TerraGen;
import org.cyberiantiger.terragen.object.ObjectTemplate;

/**
 *
 * @author antony
 */
public class Info implements Command {

    public boolean execute(TerraGen gen, CommandSender sender, String... args) {
        if (args.length != 1) {
            return false;
        }

        ObjectTemplate ot = gen.getTemplate(args[0]);
        if (ot == null) {
            sender.sendMessage("Template " + args[0] + " not found.");
            return true;
        }

        java.util.List<String> out = new ArrayList<String>();

        out.add("Template: " + args[0]);
        out.add("Size: " + ot.getSize());
        out.add("Offset: " + ot.getOffset());

        for (Map.Entry<String,String> e : ot.getProperties().entrySet()) {
            out.add(e.getKey() + " = " + e.getValue());
        }

        sender.sendMessage(out.toArray(new String[out.size()]));

        return true;
    }

    public String getUsage() {
        return "/info <template>";
    }

}
