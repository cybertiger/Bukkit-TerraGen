/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cyberiantiger.terragen.command;

import org.bukkit.command.CommandSender;
import org.cyberiantiger.terragen.TerraGen;

/**
 *
 * @author antony
 */
public class Reload implements Command {

    public boolean execute(TerraGen gen, CommandSender sender, String... args) {
        if (args.length > 0)
            return false;
        gen.reload();
        sender.sendMessage("Templates reloaded.");
        return true;
    }

    public String getUsage() {
        return "/tg reload";
    }

}
