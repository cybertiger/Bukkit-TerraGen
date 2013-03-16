/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cyberiantiger.terragen.object;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bukkit.Material;
import org.cyberiantiger.terragen.nbt.Tag;

/**
 *
 * @author antony
 */
public class BlockInfo {

    private final int id;
    private final byte data;
    private final Tag tag;

    public BlockInfo(int id) {
        this(id, (byte)0);
    }

    public BlockInfo(int id, byte data) {
        this(id, data, null);
    }

    public BlockInfo(int id, byte data, Tag tag) {
        this.id = id;
        this.data = data;
        this.tag = tag;
    }

    public byte getData() {
        return data;
    }

    public int getId() {
        return id;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final BlockInfo other = (BlockInfo) obj;
        if (this.id != other.id) {
            return false;
        }
        if (this.data != other.data) {
            return false;
        }
        return true;
    }

    public Tag getTag() {
        return tag;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 71 * hash + this.id;
        hash = 71 * hash + this.data;
        return hash;
    }
    
    private static final Pattern ID_PATTERN = Pattern.compile("(\\d+)(:(\\d+))?");

    public static BlockInfo valueOf(String s) {
        return valueOf(s, null);
    }
    
    public static BlockInfo valueOf(String s, BlockInfo def) {
        Material m = Material.matchMaterial(s);
        if (m != null) {
            return new BlockInfo(m.getId());
        }
        Matcher mm = ID_PATTERN.matcher(s);
        if (mm.matches()) {
            int id = Integer.valueOf(mm.group(1));
            byte data = 0;
            if (mm.group(3) != null) {
                data = Byte.valueOf(mm.group(3));
            }
            return new BlockInfo(id, data);
        }

        return def;
    }

    @Override
    public String toString() {
        return id + ":" + data;
    }
}
