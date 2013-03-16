/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cyberiantiger.terragen.object;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.Callable;
import org.bukkit.Rotation;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.cyberiantiger.terragen.Coord;
import org.cyberiantiger.terragen.Selection;
import org.cyberiantiger.terragen.SelectionIterator;
import org.cyberiantiger.terragen.TerraGen;

/**
 *
 * @author antony
 */
public class ObjectTemplate implements Cloneable {
    // TODO Entities and TileEntities.

    private Map<String, String> props = new TreeMap<String, String>();
    private Map<Coord, BlockInfo> blocks = new TreeMap<Coord, BlockInfo>();
    private int minX;
    private int maxX;
    private int minY;
    private int maxY;
    private int minZ;
    private int maxZ;

    public ObjectTemplate() {
        clear();
    }

    @Override
    public Object clone() {
        try {
            ObjectTemplate ret = (ObjectTemplate) super.clone();
            ret.props = new HashMap<String, String>(props);
            ret.blocks = new HashMap<Coord, BlockInfo>(blocks);
            return ret;
        } catch (CloneNotSupportedException ex) {
            return null;
        }
    }

    public void apply(World world, Coord coord, Rotation rotation) {

    }

    /**
     * Apply this template at the given coord in the given world.
     *
     * Physics is turned off, however block update events still screw things up.
     * 
     * @param world
     * @param coord
     */
    public void apply(World world, Coord coord) {
        apply(world, coord, Orientation.NORTH);
    }

    /**
     * Apply this template at the given coord in the given world.
     *
     * Physics is turned off, however block update events still screw things up.
     *
     * @param world
     * @param coord
     * @param o
     */
    public void apply(World world, Coord coord, Orientation o) {
        apply(world, coord, o, null, false);
    }

    /**
     * Apply this template at the given coord in the given world.
     *
     * If gen is supplied we disable physics events as well as spawning blocks
     * with physics turned off.
     * 
     * @param world
     * @param coord
     * @param o
     * @param gen
     * @param physics
     */
    public void apply(final World world, final Coord coord, final Orientation o, TerraGen gen, final boolean physics) {
        if (!physics && gen != null) {
            try {
                gen.disablePhysics(new Callable<Void>() {
                    public Void call() throws Exception {
                        apply(world, coord, o, null, false);
                        return null;
                    }
                });
            } catch (Exception e) {
                gen.getLogger().severe(e.getMessage());
            }
        }
        for (Map.Entry<Coord, BlockInfo> e : blocks.entrySet()) {
            Coord offset = o.apply(e.getKey());
            BlockInfo info = o.apply(e.getValue());
            int y = coord.getY() + offset.getY();
            Coord location = coord.translate(offset);

            // Don't spawn outside world height.
            if (location.getY() < 0 || location.getY() > world.getMaxHeight()) {
                continue;
            }

            Block block = world.getBlockAt(location.getX(), location.getY(), location.getZ());
            if (block != null) {
                block.setTypeIdAndData(info.getId(), info.getData(), physics);
                // Bug workaround, sometimes you don't get the data you're looking for.
                if (block.getData() != info.getData()) {
                    System.out.println("Working around block data that won't stick");
                    block.setData(info.getData());
                }
                if (info.getTag() != null) {

                }
            }
        }
    }

    public Map<String, String> getProperties() {
        return props;
    }

    public void clear() {
        props.clear();
        blocks.clear();
        minX = Integer.MAX_VALUE;
        minY = Integer.MAX_VALUE;
        minZ = Integer.MAX_VALUE;
        maxX = Integer.MIN_VALUE;
        maxY = Integer.MIN_VALUE;
        maxZ = Integer.MIN_VALUE;
    }

    public void create(final Selection selection, final Coord offset) {
        clear();
        selection.iterate(new SelectionIterator() {

            public void block(int x, int y, int z, Block block) {
                if (block.isEmpty()) {
                    return;
                }
                blocks.put(new Coord(x + offset.getX(), y + offset.getY(), z + offset.getZ()), new BlockInfo(block.getTypeId(), block.getData()));
            }
        });
    }

    public int getMaxX() {
        return maxX;
    }

    public int getMaxY() {
        return maxY;
    }

    public int getMaxZ() {
        return maxZ;
    }

    public int getMinX() {
        return minX;
    }

    public int getMinY() {
        return minY;
    }

    public int getMinZ() {
        return minZ;
    }

    public void addBlock(Coord coord, BlockInfo block) {
        int x = coord.getX();
        int y = coord.getY();
        int z = coord.getZ();
        if (x < minX) {
            minX = x;
        }
        if (x > maxX) {
            maxX = x;
        }
        if (y < minY) {
            minY = y;
        }
        if (y > maxY) {
            maxY = y;
        }
        if (z < minZ) {
            minZ = z;
        }
        if (z > maxZ) {
            maxZ = z;
        }
        blocks.put(coord, block);
    }

    public Map<Coord, BlockInfo> getBlocks() {
        return blocks;
    }

    public Coord getSize() {
        if (blocks.isEmpty()) {
            return Coord.ZERO;
        }

        return new Coord(maxX - minX, maxY - minY, maxZ - minZ);
    }

    public Coord getOffset() {
        return new Coord(-minX, -minY, -minZ);
    }

    public int remove(BlockInfo block) {
        int count = 0;
        Iterator<Map.Entry<Coord,BlockInfo>> i = blocks.entrySet().iterator();
        while (i.hasNext()) {
            Map.Entry<Coord,BlockInfo> e = i.next();
            if (e.getValue().equals(block)) {
                count++;
                i.remove();
            }
        }
        updateSize();
        return count;
    }

    public int replace(BlockInfo from, BlockInfo to) {
        int count = 0;
        for (Map.Entry<Coord, BlockInfo> e : blocks.entrySet()) {
            if (e.getValue().equals(from)) {
                count++;
                e.setValue(to);
            }
        }
        return count;
    }

    protected void updateSize() {
        minX = Integer.MAX_VALUE;
        minY = Integer.MAX_VALUE;
        minZ = Integer.MAX_VALUE;
        maxX = Integer.MIN_VALUE;
        maxY = Integer.MIN_VALUE;
        maxZ = Integer.MIN_VALUE;
        for (Coord coord : blocks.keySet()) {
            int x = coord.getX();
            int y = coord.getY();
            int z = coord.getZ();
            if (x < minX) {
                minX = x;
            }
            if (x > maxX) {
                maxX = x;
            }
            if (y < minY) {
                minY = y;
            }
            if (y > maxY) {
                maxY = y;
            }
            if (z < minZ) {
                minZ = z;
            }
            if (z > maxZ) {
                maxZ = z;
            }
        }
    }
}
