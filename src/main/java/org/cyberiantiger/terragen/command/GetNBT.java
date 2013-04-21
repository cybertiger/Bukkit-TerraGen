/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cyberiantiger.terragen.command;

import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.cyberiantiger.minecraft.nbt.CompoundTag;
import org.cyberiantiger.terragen.Selection;
import org.cyberiantiger.terragen.SelectionIterator;
import org.cyberiantiger.terragen.TerraGen;

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
