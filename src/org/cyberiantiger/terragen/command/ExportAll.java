/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cyberiantiger.terragen.command;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import org.bukkit.command.CommandSender;
import org.cyberiantiger.terragen.TerraGen;
import org.cyberiantiger.terragen.object.BO2Support;
import org.cyberiantiger.terragen.object.ObjectTemplate;

/**
 *
 * @author antony
 */
public class ExportAll implements Command {

    public boolean execute(TerraGen gen, CommandSender sender, String... args) {
        if (args.length != 0) {
            return false;
        }

        int success = 0;
        int failed = 0;

        for (String templateName : gen.getTemplateNames()) {
            File file = new File(gen.getTemplateDir(), templateName + BO2Support.EXTENSION);

            ObjectTemplate template = gen.getTemplate(templateName);

            if (template == null) {
                sender.sendMessage("No template named: " + templateName);
                return true;
            }

            try {
                BO2Support.exportFile(template, file);
                sender.sendMessage("Exported " + templateName + " as " + file.getName());
                success++;
            } catch (IOException ex) {
                sender.sendMessage("Failed: check console for error");
                gen.getLogger().log(Level.SEVERE, null, ex);
                failed++;
            }
        }

        sender.sendMessage("Exported " + success + " templates (" + failed + " failed)");

        return true;
    }

    public String getUsage() {
        return "/tg exportall";
    }
}
