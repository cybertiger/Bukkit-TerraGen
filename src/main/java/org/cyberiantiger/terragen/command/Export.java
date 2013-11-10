/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cyberiantiger.terragen.command;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.command.CommandSender;
import org.cyberiantiger.terragen.TerraGen;
import org.cyberiantiger.terragen.object.BO2Support;
import org.cyberiantiger.terragen.object.ObjectTemplate;

/**
 *
 * @author antony
 */
public class Export implements Command {

    public boolean execute(TerraGen gen, CommandSender sender, String... args) {
        if (args.length != 1) {
            return false;
        }

        String name = args[0];
        
        File file = new File(gen.getTemplateDir(), args[0] + BO2Support.EXTENSION);

        ObjectTemplate template = gen.getTemplate(name);

        if (template == null) {
            sender.sendMessage("No template named: " + name);
            return true;
        }

        try {
            BO2Support.exportFile(template, file);
            sender.sendMessage("Exported " + name + " as " + file.getName());
        } catch (IOException ex) {
            sender.sendMessage("Failed: check console for error");
            Logger.getLogger(Export.class.getName()).log(Level.SEVERE, null, ex);
        }

        return true;
    }

    public String getUsage() {
        return "/tg export <template>";
    }
}
