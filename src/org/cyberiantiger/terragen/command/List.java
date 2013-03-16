/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cyberiantiger.terragen.command;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import org.bukkit.command.CommandSender;
import org.cyberiantiger.terragen.TerraGen;

/**
 *
 * @author antony
 */
public class List implements Command {

    public boolean execute(TerraGen gen, CommandSender sender, String... args) {
        if (args.length > 0) {
            return false;
        }

        Set<String> templates = gen.getTemplateNames();

        java.util.List<String> output = new ArrayList<String>();
        StringBuilder tmp = new StringBuilder();

        tmp.append("").append(templates.size()).append(" templates found.");
        output.add(tmp.toString());
        tmp.setLength(0);

        Iterator<String> i = templates.iterator();
        while (i.hasNext()) {
            String s = i.next();
            boolean last = !i.hasNext();
            if (tmp.length() == 0) {
                tmp.append(s);
                if (last) {
                    tmp.append('.');
                } else {
                    tmp.append(',');
                }
            } else {
                if (tmp.length() + 2 + s.length()
                        > CommandSystem.MAX_LINE_LENGTH) {
                    output.add(tmp.toString());
                    tmp.setLength(0);
                    tmp.append(s);
                    if (last) {
                        tmp.append('.');
                    } else {
                        tmp.append(',');
                    }
                } else {
                    tmp.append(' ');
                    tmp.append(s);
                    if (last) {
                        tmp.append('.');
                    } else {
                        tmp.append(',');
                    }
                }
            }

        }

        if (tmp.length() > 0) {
            output.add(tmp.toString());
        }

        // TODO: Paging.
        for (String s : output) {
            sender.sendMessage(s);
        }
        return true;
    }

    public String getUsage() {
        return "/tg list";


    }
}
