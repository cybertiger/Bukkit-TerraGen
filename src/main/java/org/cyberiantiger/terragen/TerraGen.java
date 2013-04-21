/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cyberiantiger.terragen;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.cyberiantiger.minecraft.unsafe.CBShim;
import org.cyberiantiger.minecraft.unsafe.NBTTools;
import org.cyberiantiger.terragen.command.CommandSystem;
import org.cyberiantiger.terragen.generator.EvilGeneratorFactory;
import org.cyberiantiger.terragen.generator.FlatGeneratorFactory;
import org.cyberiantiger.terragen.generator.GeneratorFactory;
import org.cyberiantiger.terragen.generator.PlotGeneratorFactory;
import org.cyberiantiger.terragen.generator.VoidGeneratorFactory;
import org.cyberiantiger.terragen.object.BO2Support;
import org.cyberiantiger.terragen.object.ObjectTemplate;

/**
 *
 * @author antony
 */
public class TerraGen extends JavaPlugin implements Listener {

    public static final String TOOL_SELECTION = "tool.selection";
    public static final String TOOL_ID = "tool.id";
    public static final Material DEFAULT_TOOL_SELECTION = Material.STICK;
    public static final Material DEFAULT_TOOL_ID = Material.BONE;
    private NBTTools tools;

    @Override
    public void onEnable() {
        tools = CBShim.createShim(NBTTools.class, this);
        saveDefaultConfig();
        getServer().getPluginManager().registerEvents(this, this);
        File templateDir = getTemplateDir();

        if (!templateDir.isDirectory()) {
            if (!templateDir.mkdirs()) {
                getLogger().log(Level.WARNING, "Failed to create template directory: {0}", templateDir);
            }
        }

        reload();
    }

    public NBTTools getNBTTools() {
        return tools;
    }

    @Override
    public void onDisable() {
    }

    public ItemStack getSelectionTool() {
        return getConfig().getItemStack(TOOL_SELECTION);
    }

    public void setSelectionTool(ItemStack item) {
        setSelectionTool(item, true);
    }

    public void setSelectionTool(ItemStack item, boolean save) {
        getConfig().set(TOOL_SELECTION, item);
        if (save) {
            saveConfig();
        }
    }

    public ItemStack getIdTool() {
        return getConfig().getItemStack(TOOL_ID);
    }

    public void setIdTool(ItemStack item) {
        setIdTool(item, true);
    }

    public void setIdTool(ItemStack item, boolean save) {
        getConfig().set(TOOL_ID, item);
        if (save) {
            saveConfig();
        }
    }

    public boolean getAllowFlintAndSteelIgnite(Player p) {
        return p.hasPermission("terragen.fire.light");
    }

    public boolean getAllowFireballIgnite(World world) {
        return getConfig().getBoolean("fire.fireball")
                || getConfig().getBoolean("fire." + world.getName() + ".fireball");
    }

    public boolean getAllowLavaIgnite(World world) {
        return getConfig().getBoolean("fire.lava")
                || getConfig().getBoolean("fire." + world.getName() + ".lava");
    }

    public boolean getAllowLightningIgnite(World world) {
        return getConfig().getBoolean("fire.lightning")
                || getConfig().getBoolean("fire." + world.getName() + ".lightning");
    }

    public boolean getAllowSpreadIgnite(World world) {
        return getConfig().getBoolean("fire.spread")
                || getConfig().getBoolean("fire." + world.getName() + ".spread");
    }
    public static final Map<String, GeneratorFactory> generators = new HashMap<String, GeneratorFactory>();

    static {
        generators.put("plot", new PlotGeneratorFactory());
        generators.put("void", new VoidGeneratorFactory());
        generators.put("flat", new FlatGeneratorFactory());
        generators.put("evil", new EvilGeneratorFactory());
    }

    @Override
    public ChunkGenerator getDefaultWorldGenerator(String worldName, String id) {
        if (id == null || id.length() == 0) {
            return null;
        }
        int i = id.indexOf('?');
        String gen;
        String args;
        if (i == -1) {
            gen = id;
            args = "";
        } else {
            gen = id.substring(0, i);
            args = id.substring(i + 1);
        }

        GeneratorFactory factory = generators.get(gen);

        if (factory == null) {
            return null;
        }

        return factory.create(this, args);
    }
    private Map<Player, Selection> selectionMap = new HashMap<Player, Selection>();

    public Selection getSelection(Player p) {
        Selection s = selectionMap.get(p);
        if (s == null) {
            s = new Selection();
            selectionMap.put(p, s);
        }
        return s;
    }
    private volatile boolean disablePhysics = false;

    public <T> T disablePhysics(Callable<T> c) throws Exception {
        disablePhysics = true;
        try {
            return c.call();
        } finally {
            disablePhysics = false;
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onBlockPhysics(BlockPhysicsEvent e) {
        if (disablePhysics) {
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onBlockIgnite(BlockIgniteEvent e) {
        boolean allow = true;
        switch (e.getCause()) {
            case FIREBALL:
                allow = getAllowFireballIgnite(e.getBlock().getWorld());
                break;
            case FLINT_AND_STEEL:
                allow = getAllowFlintAndSteelIgnite(e.getPlayer());
                break;
            case LAVA:
                allow = getAllowLavaIgnite(e.getBlock().getWorld());
                break;
            case LIGHTNING:
                allow = getAllowLightningIgnite(e.getBlock().getWorld());
                break;
            case SPREAD:
                allow = getAllowSpreadIgnite(e.getBlock().getWorld());
                break;
        }
        if (!allow) {
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onClick(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if (getSelectionTool() != null && getSelectionTool().equals(p.getItemInHand()) && p.hasPermission("terragen.tool.selection")) {
            if (e.getAction() == Action.LEFT_CLICK_BLOCK) {
                Block b = e.getClickedBlock();
                Location l = b.getLocation();
                getSelection(p).setFrom(l);
                p.sendMessage("Selection area location from: " + l.getBlockX() + ", " + l.getBlockY() + ", " + l.getBlockZ());
                e.setCancelled(true);
            }
            if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                Block b = e.getClickedBlock();
                Location l = b.getLocation();
                getSelection(p).setTo(l);
                p.sendMessage("Selection area location to: " + l.getBlockX() + ", " + l.getBlockY() + ", " + l.getBlockZ());
                e.setCancelled(true);
            }
        }
        if (getIdTool() != null && getIdTool().equals(p.getItemInHand()) && p.hasPermission("terragen.tool.id")) {
            if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                Block b = e.getClickedBlock();
                StringBuilder id = new StringBuilder();
                id.append(b.getTypeId());
                byte data = b.getData();
                if (data != 0) {
                    id.append(':');
                    id.append(data);
                }
                p.sendMessage("Block id: " + id.toString());
                e.setCancelled(true);
            }
        }
    }

    private List<String> instances = new ArrayList<String>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String cmdName = command.getName();
        if ("tg".equals(cmdName)) {
            if (args.length == 0) {
                sender.sendMessage(command.getUsage());
                return true;
            }
            if (!sender.hasPermission("terragen.cmd." + args[0])) {
                sender.sendMessage("You do not have permission to do that.");
                return true;
            }
            cmdName = args[0];
            String[] shiftArgs = new String[args.length - 1];
            System.arraycopy(args, 1, shiftArgs, 0, args.length - 1);


            if (!CommandSystem.execute(this, sender, cmdName, shiftArgs)) {
                sender.sendMessage(command.getUsage());
            }

            return true;
        }
        return false;
    }

    public File getTemplateDir() {
        return new File(getDataFolder(), "template");
    }
    
    private Map<String, ObjectTemplate> templates = new HashMap<String, ObjectTemplate>();

    public Set<String> getTemplateNames() {
        Set<String> ret = new TreeSet<String>(new Comparator<String>() {

            public int compare(String o1, String o2) {
                return o1.compareToIgnoreCase(o2);
            }
        });
        ret.addAll(templates.keySet());
        return ret;
    }

    public ObjectTemplate getTemplate(String name) {
        return getTemplate(name, false);
    }

    public ObjectTemplate getTemplate(String name, boolean create) {
        ObjectTemplate ret = templates.get(name);
        if (create && ret == null) {
            ret = new ObjectTemplate();
            templates.put(name, ret);
        }
        return ret;
    }

    public ObjectTemplate setTemplate(String name, ObjectTemplate template) {
        return templates.put(name, template);
    }

    public ObjectTemplate removeTemplate(String name) {
        return templates.remove(name);
    }

    public void reload() {
        reloadConfig();
        templates.clear();
        File[] files = getTemplateDir().listFiles();
        for (File f : files) {
            String s = f.getName();
            if (s.endsWith(BO2Support.EXTENSION)) {
                String name = s.substring(0, s.length() - BO2Support.EXTENSION.length());
                ObjectTemplate template = getTemplate(name, true);
                try {
                    BO2Support.importFile(template, f);
                } catch (IOException ex) {
                    Logger.getLogger(TerraGen.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    private String[] motd = null;


}
