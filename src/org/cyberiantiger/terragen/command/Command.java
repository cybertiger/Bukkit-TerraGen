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
public interface Command {

    public boolean execute(TerraGen gen, CommandSender sender, String... args);
    public String getUsage();
}
