/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cyberiantiger.terragen.unsafe;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.minecraft.server.v1_4_R1.NBTBase;
import net.minecraft.server.v1_4_R1.NBTTagByte;
import net.minecraft.server.v1_4_R1.NBTTagByteArray;
import net.minecraft.server.v1_4_R1.NBTTagCompound;
import net.minecraft.server.v1_4_R1.NBTTagDouble;
import net.minecraft.server.v1_4_R1.NBTTagFloat;
import net.minecraft.server.v1_4_R1.NBTTagInt;
import net.minecraft.server.v1_4_R1.NBTTagIntArray;
import net.minecraft.server.v1_4_R1.NBTTagList;
import net.minecraft.server.v1_4_R1.NBTTagLong;
import net.minecraft.server.v1_4_R1.NBTTagShort;
import net.minecraft.server.v1_4_R1.NBTTagString;
import net.minecraft.server.v1_4_R1.TileEntity;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_4_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_4_R1.block.CraftBrewingStand;
import org.bukkit.craftbukkit.v1_4_R1.block.CraftChest;
import org.bukkit.craftbukkit.v1_4_R1.block.CraftCreatureSpawner;
import org.bukkit.craftbukkit.v1_4_R1.block.CraftDispenser;
import org.bukkit.craftbukkit.v1_4_R1.block.CraftFurnace;
import org.bukkit.craftbukkit.v1_4_R1.block.CraftJukebox;
import org.bukkit.craftbukkit.v1_4_R1.block.CraftNoteBlock;
import org.bukkit.craftbukkit.v1_4_R1.block.CraftSign;
import org.bukkit.craftbukkit.v1_4_R1.block.CraftSkull;
import org.cyberiantiger.terragen.nbt.ByteArrayTag;
import org.cyberiantiger.terragen.nbt.ByteTag;
import org.cyberiantiger.terragen.nbt.CompoundTag;
import org.cyberiantiger.terragen.nbt.DoubleTag;
import org.cyberiantiger.terragen.nbt.FloatTag;
import org.cyberiantiger.terragen.nbt.IntArrayTag;
import org.cyberiantiger.terragen.nbt.IntTag;
import org.cyberiantiger.terragen.nbt.ListTag;
import org.cyberiantiger.terragen.nbt.LongTag;
import org.cyberiantiger.terragen.nbt.ShortTag;
import org.cyberiantiger.terragen.nbt.StringTag;
import org.cyberiantiger.terragen.nbt.Tag;
import org.cyberiantiger.terragen.nbt.TagType;

/**
 *
 * @author antony
 */
public final class NBTTools {

    private static final Map<Material, Field> TILE_ENTITY_FIELD = new EnumMap<Material, Field>(Material.class);

    static {
        try {
            Field f;
            f = CraftBrewingStand.class.getDeclaredField("brewingStand");
            f.setAccessible(true);
            TILE_ENTITY_FIELD.put(Material.BREWING_STAND, f);
            f = CraftChest.class.getDeclaredField("chest");
            f.setAccessible(true);
            TILE_ENTITY_FIELD.put(Material.CHEST, f);
            f = CraftCreatureSpawner.class.getDeclaredField("spawner");
            f.setAccessible(true);
            TILE_ENTITY_FIELD.put(Material.MOB_SPAWNER, f);
            f = CraftDispenser.class.getDeclaredField("dispenser");
            f.setAccessible(true);
            TILE_ENTITY_FIELD.put(Material.DISPENSER, f);
            f = CraftFurnace.class.getDeclaredField("furnace");
            f.setAccessible(true);
            TILE_ENTITY_FIELD.put(Material.FURNACE, f);
            f = CraftJukebox.class.getDeclaredField("jukebox");
            f.setAccessible(true);
            TILE_ENTITY_FIELD.put(Material.JUKEBOX, f);
            f = CraftNoteBlock.class.getDeclaredField("note");
            f.setAccessible(true);
            TILE_ENTITY_FIELD.put(Material.NOTE_BLOCK, f);
            f = CraftSign.class.getDeclaredField("sign");
            f.setAccessible(true);
            TILE_ENTITY_FIELD.put(Material.SIGN, f);
            TILE_ENTITY_FIELD.put(Material.SIGN_POST, f);
            f = CraftSkull.class.getDeclaredField("skull");
            f.setAccessible(true);
            TILE_ENTITY_FIELD.put(Material.SKULL, f);
        } catch (NoSuchFieldException ex) {
            Logger.getLogger(NBTTools.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(NBTTools.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static NBTTagCompound toNativeCompound(CompoundTag tag) {
        NBTTagCompound result = new NBTTagCompound(tag.getName());
        for (Tag t : tag.getValue().values()) {
            switch (t.getType()) {
                case BYTE:
                    result.setByte(t.getName(), ((ByteTag) t).getRawValue());
                    break;
                case BYTE_ARRAY:
                    result.setByteArray(t.getName(), ((ByteArrayTag) t).getValue());
                    break;
                case COMPOUND:
                    result.setCompound(t.getName(), toNativeCompound(((CompoundTag) t)));
                    break;
                case DOUBLE:
                    result.setDouble(t.getName(), ((DoubleTag) t).getRawValue());
                    break;
                case FLOAT:
                    result.setFloat(t.getName(), ((FloatTag) t).getRawValue());
                    break;
                case INT:
                    result.setInt(t.getName(), ((IntTag) t).getRawValue());
                    break;
                case INT_ARRAY:
                    result.setIntArray(t.getName(), ((IntArrayTag) t).getValue());
                    break;
                case LIST:
                    result.set(t.getName(), toNativeList((ListTag) t));
                case LONG:
                    result.setLong(t.getName(), ((LongTag) t).getValue());
                    break;
                case SHORT:
                    result.setShort(t.getName(), ((ShortTag) t).getValue());
                    break;
                case STRING:
                    result.setString(t.getName(), ((StringTag) t).getValue());
                    break;
            }
        }
        return result;
    }

    public static NBTTagList toNativeList(ListTag tag) {
        NBTTagList result = new NBTTagList(tag.getName());

        switch (tag.getType()) {
            case BYTE:
                for (ByteTag t : (ByteTag[]) tag.getValue()) {
                    result.add(new NBTTagByte(null, t.getRawValue()));
                }
                break;
            case BYTE_ARRAY:
                for (ByteArrayTag t : (ByteArrayTag[]) tag.getValue()) {
                    result.add(new NBTTagByteArray(null, ((ByteArrayTag) t).getValue()));
                }
                break;
            case COMPOUND:
                for (CompoundTag t : (CompoundTag[]) tag.getValue()) {
                    result.add(toNativeCompound(t));
                }
                break;
            case DOUBLE:
                for (DoubleTag t : (DoubleTag[]) tag.getValue()) {
                    result.add(new NBTTagDouble(null, t.getRawValue()));
                }
                break;
            case FLOAT:
                for (FloatTag t : (FloatTag[]) tag.getValue()) {
                    result.add(new NBTTagFloat(null, t.getRawValue()));
                }
                break;
            case INT:
                for (IntTag t : (IntTag[]) tag.getValue()) {
                    result.add(new NBTTagInt(null, t.getRawValue()));
                }
                break;
            case INT_ARRAY:
                for (IntArrayTag t : (IntArrayTag[]) tag.getValue()) {
                    result.add(new NBTTagIntArray(null, t.getValue()));
                }
                break;
            case LIST:
                for (ListTag t : (ListTag[]) tag.getValue()) {
                    result.add(toNativeList((ListTag) t));
                }
            case LONG:
                for (LongTag t : (LongTag[]) tag.getValue()) {
                    result.add(new NBTTagLong(null, t.getRawValue()));
                }
                break;
            case SHORT:
                for (ShortTag t : (ShortTag[]) tag.getValue()) {
                    result.add(new NBTTagShort(null, t.getRawValue()));
                }
                break;
            case STRING:
                for (StringTag t : (StringTag[]) tag.getValue()) {
                    result.add(new NBTTagString(null, t.getValue()));
                }
        }

        return result;
    }
    public static final Field COMPOUND_MAP_FIELD;

    static {
        Field f = null;
        try {
            f = NBTTagCompound.class.getDeclaredField("map");
            f.setAccessible(true);
        } catch (NoSuchFieldException ex) {
            Logger.getLogger(NBTTools.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(NBTTools.class.getName()).log(Level.SEVERE, null, ex);
        }
        COMPOUND_MAP_FIELD = f;
    }

    public static CompoundTag fromNativeCompound(NBTTagCompound tag) {
        if (COMPOUND_MAP_FIELD != null) {
            try {
                Map<String, Tag> result = new HashMap<String, Tag>();
                Map<String, NBTBase> map = (Map<String, NBTBase>) COMPOUND_MAP_FIELD.get(tag);
                for (NBTBase b : map.values()) {
                    switch (TagType.values()[b.getTypeId()]) {
                        case BYTE:
                            result.put(b.getName(), new ByteTag(b.getName(), ((NBTTagByte) b).data));
                            break;
                        case BYTE_ARRAY:
                            result.put(b.getName(), new ByteArrayTag(b.getName(), ((NBTTagByteArray) b).data));
                            break;
                        case COMPOUND:
                            result.put(b.getName(), fromNativeCompound((NBTTagCompound) b));
                            break;
                        case DOUBLE:
                            result.put(b.getName(), new DoubleTag(b.getName(), ((NBTTagDouble) b).data));
                            break;
                        case FLOAT:
                            result.put(b.getName(), new FloatTag(b.getName(), ((NBTTagFloat) b).data));
                            break;
                        case INT:
                            result.put(b.getName(), new IntTag(b.getName(), ((NBTTagInt) b).data));
                            break;
                        case INT_ARRAY:
                            result.put(b.getName(), new IntArrayTag(b.getName(), ((NBTTagIntArray) b).data));
                            break;
                        case LIST:
                            result.put(b.getName(), fromNativeList((NBTTagList) b));
                            break;
                        case LONG:
                            result.put(b.getName(), new LongTag(b.getName(), ((NBTTagLong) b).data));
                            break;
                        case SHORT:
                            result.put(b.getName(), new ShortTag(b.getName(), ((NBTTagShort) b).data));
                            break;
                        case STRING:
                            result.put(b.getName(), new StringTag(b.getName(), ((NBTTagString) b).data));
                            break;
                    }
                }
                return new CompoundTag(tag.getName(), result);
            } catch (IllegalArgumentException ex) {
                Logger.getLogger(NBTTools.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalAccessException ex) {
                Logger.getLogger(NBTTools.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;
    }
    public static final Field LIST_TYPE_FIELD;

    static {
        Field f = null;
        try {
            f = NBTTagList.class.getDeclaredField("type");
            f.setAccessible(true);
        } catch (NoSuchFieldException ex) {
            Logger.getLogger(NBTTools.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(NBTTools.class.getName()).log(Level.SEVERE, null, ex);
        }
        LIST_TYPE_FIELD = f;
    }

    public static ListTag fromNativeList(NBTTagList tag) {
        try {
            TagType type = TagType.values()[(Byte) LIST_TYPE_FIELD.get(tag)];
            Tag[] t = (Tag[]) Array.newInstance(type.getTagClass(), tag.size());
            switch (type) {
                case BYTE:
                    for (int i = 0; i < tag.size(); i++) {
                        t[i] = new ByteTag(null, ((NBTTagByte)tag.get(i)).data);
                    }
                    break;
                case BYTE_ARRAY:
                    for (int i = 0; i < tag.size(); i++) {
                        t[i] = new ByteArrayTag(null, ((NBTTagByteArray)tag.get(i)).data);
                    }
                    break;
                case COMPOUND:
                    for (int i = 0; i < tag.size(); i++) {
                        t[i] = fromNativeCompound((NBTTagCompound)tag.get(i));
                    }
                    break;
                case DOUBLE:
                    for (int i = 0; i < tag.size(); i++) {
                        t[i] = new DoubleTag(null, ((NBTTagDouble)tag.get(i)).data);
                    }
                    break;
                case FLOAT:
                    for (int i = 0; i < tag.size(); i++) {
                        t[i] = new FloatTag(null, ((NBTTagFloat)tag.get(i)).data);
                    }
                    break;
                case INT:
                    for (int i = 0; i < tag.size(); i++) {
                        t[i] = new IntTag(null, ((NBTTagInt)tag.get(i)).data);
                    }
                    break;
                case INT_ARRAY:
                    for (int i = 0; i < tag.size(); i++) {
                        t[i] = new IntArrayTag(null, ((NBTTagIntArray)tag.get(i)).data);
                    }
                    break;
                case LIST:
                    for (int i = 0; i < tag.size(); i++) {
                        t[i] = fromNativeList((NBTTagList)tag.get(i));
                    }
                    break;
                case LONG:
                    for (int i = 0; i < tag.size(); i++) {
                        t[i] = new LongTag(null, ((NBTTagLong)tag.get(i)).data);
                    }
                    break;
                case SHORT:
                    for (int i = 0; i < tag.size(); i++) {
                        t[i] = new ShortTag(null, ((NBTTagShort)tag.get(i)).data);
                    }
                    break;
                case STRING:
                    for (int i = 0; i < tag.size(); i++) {
                        t[i] = new StringTag(null, ((NBTTagString)tag.get(i)).data);
                    }
                    break;
            }
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(NBTTools.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(NBTTools.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static void writeTileEntity(Block block, CompoundTag tag) {
        try {
            Field f = TILE_ENTITY_FIELD.get(block.getType());
            if (f == null) {
                return;
            }
            TileEntity e = (TileEntity) f.get(block.getState());
            e.a(toNativeCompound(tag));
            e.update();
            ((CraftWorld)block.getWorld()).getHandle().notify(block.getX(), block.getY(), block.getZ());
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(NBTTools.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(NBTTools.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static CompoundTag readTileEntity(Block block) {
        try {
            Field f = TILE_ENTITY_FIELD.get(block.getType());
            if (f == null) {
                return null;
            }
            TileEntity e = (TileEntity) f.get(block.getState());
            NBTTagCompound tag = new NBTTagCompound();
            e.b(tag);
            return fromNativeCompound(tag);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(NBTTools.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(NBTTools.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
