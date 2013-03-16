/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cyberiantiger.terragen.command;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.minecraft.server.v1_4_R1.NBTBase;
import net.minecraft.server.v1_4_R1.NBTTagCompound;
import net.minecraft.server.v1_4_R1.TileEntity;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_4_R1.block.CraftCreatureSpawner;
import org.bukkit.entity.Player;
import org.cyberiantiger.terragen.Selection;
import org.cyberiantiger.terragen.SelectionIterator;
import org.cyberiantiger.terragen.TerraGen;
import org.cyberiantiger.terragen.nbt.CompoundTag;

/**
 *
 * @author antony
 */
public class GetNBT implements Command {

    public boolean execute(final TerraGen gen, final CommandSender sender, String... args) {
        Selection sel = gen.getSelection((Player)sender);

        sel.iterate(new SelectionIterator(){

            public void block(int x, int y, int z, Block block) {
                CompoundTag t = gen.getNBTTools().readTileEntity(block);
                t.setString("Text2", "eki eki pftang");
                gen.getNBTTools().writeTileEntity(block, t);
            }
        });
        return true;
    }

    public String getUsage() {
        return "/tg getnbt";
    }

}
