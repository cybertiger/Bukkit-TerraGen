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
public class Replace implements Command {

    public boolean execute(TerraGen gen, CommandSender sender, String... args) {
        if (args.length != 3)
            return false;

        ObjectTemplate template = gen.getTemplate(args[0]);
        BlockInfo from = BlockInfo.valueOf(args[1]);
        BlockInfo to = BlockInfo.valueOf(args[2]);

        if (template == null) {
            sender.sendMessage("No template: " + args[0]);
            return true;
        }

        if (from == null) {
            sender.sendMessage("Unknown block type: " + args[1]);
            return true;
        }

        if (to == null) {
            sender.sendMessage("Unknwon block type: " + args[2]);
        }

        int replaced = template.replace(from, to);

        sender.sendMessage("Replaced " + replaced + " blocks in " + args[0]);
        
        return true;
    }

    public String getUsage() {
        return "/tg replace <template> <block from> <block to>";
    }


}
