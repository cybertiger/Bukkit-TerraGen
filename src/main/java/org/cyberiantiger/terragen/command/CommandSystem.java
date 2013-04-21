/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cyberiantiger.terragen.command;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.command.CommandSender;
import org.cyberiantiger.terragen.TerraGen;

/**
 *
 * @author antony
 */
public class CommandSystem {

    public static final int MAX_LINE_LENGTH = 53;

    private final static Map<String, Command> commands = new HashMap<String,Command>();
    static {
        commands.put("clone", new Clone());
        commands.put("delete", new Delete());
        commands.put("export", new Export());
        commands.put("exportall", new ExportAll());
        commands.put("list", new List());
        commands.put("reload", new Reload());
        commands.put("remove", new Remove());
        commands.put("rename", new Rename());
        commands.put("replace", new Replace());
        commands.put("spawn", new Spawn());
        commands.put("spawnall", new SpawnAll());
        commands.put("tool", new Tool());
        commands.put("update", new Update());
        commands.put("info", new Info());
        commands.put("prop", new Prop());
        commands.put("clearprop", new Clear());
        commands.put("getnbt", new GetNBT());
    }

    public static boolean execute(TerraGen gen, CommandSender sender, String cmd, String... args) {
        Command command = commands.get(cmd);

        if(command == null)
            return false;

        if (!command.execute(gen, sender, args)) {
            sender.sendMessage(command.getUsage());
        }

        return true;
    }

}
