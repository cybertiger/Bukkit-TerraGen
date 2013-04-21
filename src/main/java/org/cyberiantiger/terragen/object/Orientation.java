/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cyberiantiger.terragen.object;

import org.cyberiantiger.terragen.Coord;
import org.cyberiantiger.terragen.MatrixI3;

/**
 *
 * @author antony
 */
public enum Orientation {

    NORTH(MatrixI3.IDENTITY, (byte) 0),
    EAST(MatrixI3.ROTATE90, (byte) 1),
    SOUTH(MatrixI3.ROTATE180, (byte) 2),
    WEST(MatrixI3.ROTATE270, (byte) 3);
    private final byte offset;
    private final MatrixI3 m;

    private Orientation(MatrixI3 m, byte offset) {
        this.m = m;
        this.offset = offset;
    }

    public Coord apply(Coord c) {
        return m.multiply(c);
    }

    public BlockInfo apply(BlockInfo b) {
        if (offset == 0) {
            return b;
        }
        // This is an unoptimised mess.
        // Only slightly better than an optimised mess.
        switch (b.getId()) {
            case 17: // Log
                // Direction encoded in 3rd and 4th bit, 0 = up/down, 4 = n/s, 8 = e/w, 12 = broken.
                if (m == MatrixI3.ROTATE180) {
                    return b;
                } else {
                    byte data = b.getData();
                    data = (byte) ((data & 3) | ((data << 1) & 8) | ((data >> 1) & 4));
                    return new BlockInfo(b.getId(), data);
                }
            case 23: // Dispenser
            case 54: // Chest
            case 61: // Furnace
            case 62: // Burning furnace
            case 68: // Wall sign
            case 130: // Enderchest.
                // 2 north
                // 3 south
                // 4 west
                // 5 east
                // preserve 4th bit.
                switch (offset) {
                    case 1:
                        switch (b.getData() & 0x7) {
                            case 2:
                                return new BlockInfo(b.getId(), (byte) (5 | (b.getData() & 8)));
                            case 3:
                                return new BlockInfo(b.getId(), (byte) (4 | (b.getData() & 8)));
                            case 4:
                                return new BlockInfo(b.getId(), (byte) (2 | (b.getData() & 8)));
                            case 5:
                                return new BlockInfo(b.getId(), (byte) (3 | (b.getData() & 8)));
                        }
                        break;
                    case 2:
                        switch (b.getData() & 0x7) {
                            case 2:
                                return new BlockInfo(b.getId(), (byte) (3 | (b.getData() & 8)));
                            case 3:
                                return new BlockInfo(b.getId(), (byte) (2 | (b.getData() & 8)));
                            case 4:
                                return new BlockInfo(b.getId(), (byte) (5 | (b.getData() & 8)));
                            case 5:
                                return new BlockInfo(b.getId(), (byte) (4 | (b.getData() & 8)));
                        }
                        break;
                    case 3:
                        switch (b.getData() & 0x7) {
                            case 2:
                                return new BlockInfo(b.getId(), (byte) (4 | (b.getData() & 8)));
                            case 3:
                                return new BlockInfo(b.getId(), (byte) (5 | (b.getData() & 8)));
                            case 4:
                                return new BlockInfo(b.getId(), (byte) (3 | (b.getData() & 8)));
                            case 5:
                                return new BlockInfo(b.getId(), (byte) (2 | (b.getData() & 8)));
                        }
                        break;
                }
                // Default: fucked data values
                return b;
            case 26: // Beds
                // preserve top bit2
                // 0 - south
                // 1 - west
                // 2 - north
                // 3 - east
                return new BlockInfo(b.getId(), (byte) (((b.getData() + offset) & 0x3) | (b.getData() & 0xC)));
            case 66: // Normal rail.
                // 0x6 south & east.
                // 0x7 south & west.
                // 0x8 north & west.
                // 0x9 north & east.
                if (b.getData() >= 6 && b.getData() <= 9) {
                    return new BlockInfo(b.getId(), (byte) (((b.getData() - 6 + offset) & 0x3) + 6));
                }
            /* FALL THROUGH */
            case 27: // Powered Rail
            case 28: // Detector Rail
                // 0x0 North-South
                // 0x1 East-West
                // 0x2 ascending east.
                // 0x3 ascending west.
                // 0x4 ascending north.
                // 0x5 ascending south.
                // Powered rails: bit 4 = power.
                switch (offset) {
                    case 1:
                        switch (b.getData() & 7) {
                            case 0:
                                return new BlockInfo(b.getId(), (byte) (1 | (b.getData() & 8)));
                            case 1:
                                return new BlockInfo(b.getId(), (byte) (b.getData() & 8));
                            case 2:
                                return new BlockInfo(b.getId(), (byte) (5 | (b.getData() & 8)));
                            case 3:
                                return new BlockInfo(b.getId(), (byte) (4 | (b.getData() & 8)));
                            case 4:
                                return new BlockInfo(b.getId(), (byte) (2 | (b.getData() & 8)));
                            case 5:
                                return new BlockInfo(b.getId(), (byte) (3 | (b.getData() & 8)));
                        }
                        break;
                    case 2:
                        switch (b.getData() & 7) {
                            case 2:
                                return new BlockInfo(b.getId(), (byte) (3 | (b.getData() & 8)));
                            case 3:
                                return new BlockInfo(b.getId(), (byte) (2 | (b.getData() & 8)));
                            case 4:
                                return new BlockInfo(b.getId(), (byte) (5 | (b.getData() & 8)));
                            case 5:
                                return new BlockInfo(b.getId(), (byte) (4 | (b.getData() & 8)));
                        }
                        break;
                    case 3:
                        switch (b.getData() & 0x7) {
                            case 0:
                                return new BlockInfo(b.getId(), (byte) (1 | (b.getData() & 8)));
                            case 1:
                                return new BlockInfo(b.getId(), (byte) (b.getData() & 8));
                            case 2:
                                return new BlockInfo(b.getId(), (byte) (4 | (b.getData() & 8)));
                            case 3:
                                return new BlockInfo(b.getId(), (byte) (5 | (b.getData() & 8)));
                            case 4:
                                return new BlockInfo(b.getId(), (byte) (3 | (b.getData() & 8)));
                            case 5:
                                return new BlockInfo(b.getId(), (byte) (2 | (b.getData() & 8)));
                        }
                        break;
                }
                return b;
            case 29: // Sticky piston
            case 33: // Piston
            case 34: // Piston extension
                // Bit 4 = Powered or sticky extension flag.
                // 0 - down
                // 1 - up
                // 2 - north
                // 3 - south
                // 4 - west
                // 5 - east.
                switch (offset) {
                    case 1:
                        switch (b.getData() & 7) {
                            case 2:
                                return new BlockInfo(b.getId(), (byte) ((b.getData() & 8) | 5));
                            case 3:
                                return new BlockInfo(b.getId(), (byte) ((b.getData() & 8) | 4));
                            case 4:
                                return new BlockInfo(b.getId(), (byte) ((b.getData() & 8) | 2));
                            case 5:
                                return new BlockInfo(b.getId(), (byte) ((b.getData() & 8) | 3));
                        }
                        break;
                    case 2:
                        switch (b.getData() & 7) {
                            case 2:
                                return new BlockInfo(b.getId(), (byte) ((b.getData() & 8) | 3));
                            case 3:
                                return new BlockInfo(b.getId(), (byte) ((b.getData() & 8) | 2));
                            case 4:
                                return new BlockInfo(b.getId(), (byte) ((b.getData() & 8) | 5));
                            case 5:
                                return new BlockInfo(b.getId(), (byte) ((b.getData() & 8) | 4));
                        }
                        break;
                    case 3:
                        switch (b.getData() & 7) {
                            case 2:
                                return new BlockInfo(b.getId(), (byte) ((b.getData() & 8) | 4));
                            case 3:
                                return new BlockInfo(b.getId(), (byte) ((b.getData() & 8) | 5));
                            case 4:
                                return new BlockInfo(b.getId(), (byte) ((b.getData() & 8) | 3));
                            case 5:
                                return new BlockInfo(b.getId(), (byte) ((b.getData() & 8) | 2));
                        }
                        break;
                }
                return b;
            case 50: // Torch
            case 75: // Redstone torch (off)
            case 76: // Redstone torch (on)
                // 1 - on east block
                // 2 - on west block
                // 3 - on south block
                // 4 - on north block
                // 5 - standing on floor.
                // 6 - standing in ground (? wtf is difference)
                switch (offset) {
                    case 1:
                        switch (b.getData()) {
                            case 1:
                                return new BlockInfo(b.getId(), (byte) 3);
                            case 2:
                                return new BlockInfo(b.getId(), (byte) 4);
                            case 3:
                                return new BlockInfo(b.getId(), (byte) 2);
                            case 4:
                                return new BlockInfo(b.getId(), (byte) 1);
                        }
                        break;
                    case 2:
                        switch (b.getData()) {
                            case 1:
                                return new BlockInfo(b.getId(), (byte) 2);
                            case 2:
                                return new BlockInfo(b.getId(), (byte) 1);
                            case 3:
                                return new BlockInfo(b.getId(), (byte) 4);
                            case 4:
                                return new BlockInfo(b.getId(), (byte) 3);
                        }
                        break;
                    case 3:
                        switch (b.getData()) {
                            case 1:
                                return new BlockInfo(b.getId(), (byte) 4);
                            case 2:
                                return new BlockInfo(b.getId(), (byte) 3);
                            case 3:
                                return new BlockInfo(b.getId(), (byte) 1);
                            case 4:
                                return new BlockInfo(b.getId(), (byte) 2);
                        }
                        break;
                }
                return b;
            case 53: // Oak stairs.
            case 67: // Cobblestone stairs.
            case 108: // Brick stairs.
            case 109: // Stone brick stairs.
            case 114: // Nether brick stairs.
            case 128: // Sandstone stairs.
            case 134: // spruce wood stairs.
            case 135: // birch wood stairs.
            case 136: // jungle wood stairs.
                // 0 ascending east
                // 1 ascending west.
                // 2 ascending south.
                // 3 ascending north.
                // bit 3 - upside down
                switch (offset) {
                    case 1:
                        switch (b.getData() & 3) {
                            case 0:
                                return new BlockInfo(b.getId(), (byte) (2 | (b.getData() & 0xC)));
                            case 1:
                                return new BlockInfo(b.getId(), (byte) (3 | (b.getData() & 0xC)));
                            case 2:
                                return new BlockInfo(b.getId(), (byte) (1 | (b.getData() & 0xC)));
                            case 3:
                                return new BlockInfo(b.getId(), (byte) (b.getData() & 0xC));
                        }
                        break;
                    case 2:
                        switch (b.getData() & 3) {
                            case 0:
                                return new BlockInfo(b.getId(), (byte) (1 | (b.getData() & 0xC)));
                            case 1:
                                return new BlockInfo(b.getId(), (byte) (b.getData() & 0xC));
                            case 2:
                                return new BlockInfo(b.getId(), (byte) (3 | (b.getData() & 0xC)));
                            case 3:
                                return new BlockInfo(b.getId(), (byte) (2 | (b.getData() & 0xC)));
                        }
                        break;
                    case 3:
                        switch (b.getData() & 3) {
                            case 0:
                                return new BlockInfo(b.getId(), (byte) (3 | (b.getData() & 0xC)));
                            case 1:
                                return new BlockInfo(b.getId(), (byte) (2 | (b.getData() & 0xC)));
                            case 2:
                                return new BlockInfo(b.getId(), (byte) (b.getData() & 0xC));
                            case 3:
                                return new BlockInfo(b.getId(), (byte) (1 | (b.getData() & 0xC)));
                        }
                        break;
                }
                return b;
            case 63: // Sign post.
                // Add 4 per 90 degrees.
                return new BlockInfo(b.getId(), (byte) ((b.getData() + 4 * offset) & 0xf));
            case 69: // Lever
                // 1 - facing east.
                // 2 - facing west.
                // 3 - facing south.
                // 4 - facing north.
                // 5 - ground (north/south).
                // 6 - ground (east/west).
                // 7 - ceiling (north/south).
                // 0 - ceiling (east/west).
                switch (offset) {
                    case 1:
                        switch (b.getData()&7) {
                            case 1:
                                return new BlockInfo(b.getId(), (byte) 3);
                            case 2:
                                return new BlockInfo(b.getId(), (byte) 4);
                            case 3:
                                return new BlockInfo(b.getId(), (byte) 2);
                            case 4:
                                return new BlockInfo(b.getId(), (byte) 1);
                            case 5:
                                return new BlockInfo(b.getId(), (byte) 6);
                            case 6:
                                return new BlockInfo(b.getId(), (byte) 5);
                            case 7:
                                return new BlockInfo(b.getId(), (byte) 0);
                            case 0:
                                return new BlockInfo(b.getId(), (byte) 7);
                        }
                        break;
                    case 2:
                        switch (b.getData()&7) {
                            case 1:
                                return new BlockInfo(b.getId(), (byte) 2);
                            case 2:
                                return new BlockInfo(b.getId(), (byte) 1);
                            case 3:
                                return new BlockInfo(b.getId(), (byte) 4);
                            case 4:
                                return new BlockInfo(b.getId(), (byte) 3);
                        }
                        break;
                    case 3:
                        switch (b.getData()&7) {
                            case 1:
                                return new BlockInfo(b.getId(), (byte) 4);
                            case 2:
                                return new BlockInfo(b.getId(), (byte) 3);
                            case 3:
                                return new BlockInfo(b.getId(), (byte) 1);
                            case 4:
                                return new BlockInfo(b.getId(), (byte) 2);
                            case 5:
                                return new BlockInfo(b.getId(), (byte) 6);
                            case 6:
                                return new BlockInfo(b.getId(), (byte) 5);
                            case 7:
                                return new BlockInfo(b.getId(), (byte) 0);
                            case 0:
                                return new BlockInfo(b.getId(), (byte) 7);
                        }
                        break;
                }
                return b;
            case 64: // Wooden door.
            case 71: // Iron door.
                // If bit 4 is not set (bottom part of door).
                // bit 1 & 2:
                // 0 - facing west.
                // 1 - facing north.
                // 2 - facing east.
                // 3 - facing south.
                // bit 3: preserve (open state of door).
                if ((b.getData() & 8) != 0) {
                    return b;
                }
                return new BlockInfo(b.getId(), (byte) ((b.getData() & 12) | ((b.getData() + offset) & 3)));
            case 77: // Stone button
            case 143: // Wooden button
                // 1 - facing east
                // 2 - facing west
                // 3 - facing south
                // 4 - facing north
                // bit 4: preserve.
                switch (offset) {
                    case 1:
                        switch (b.getData() & 7) {
                            case 1:
                                return new BlockInfo(b.getId(), (byte) ((b.getData() & 8) | 3));
                            case 2:
                                return new BlockInfo(b.getId(), (byte) ((b.getData() & 8) | 4));
                            case 3:
                                return new BlockInfo(b.getId(), (byte) ((b.getData() & 8) | 2));
                            case 4:
                                return new BlockInfo(b.getId(), (byte) ((b.getData() & 8) | 1));
                        }
                        break;
                    case 2:
                        switch (b.getData() & 7) {
                            case 1:
                                return new BlockInfo(b.getId(), (byte) ((b.getData() & 8) | 2));
                            case 2:
                                return new BlockInfo(b.getId(), (byte) ((b.getData() & 8) | 1));
                            case 3:
                                return new BlockInfo(b.getId(), (byte) ((b.getData() & 8) | 4));
                            case 4:
                                return new BlockInfo(b.getId(), (byte) ((b.getData() & 8) | 3));
                        }
                        break;
                    case 3:
                        switch (b.getData() & 7) {
                            case 1:
                                return new BlockInfo(b.getId(), (byte) ((b.getData() & 8) | 4));
                            case 2:
                                return new BlockInfo(b.getId(), (byte) ((b.getData() & 8) | 3));
                            case 3:
                                return new BlockInfo(b.getId(), (byte) ((b.getData() & 8) | 1));
                            case 4:
                                return new BlockInfo(b.getId(), (byte) ((b.getData() & 8) | 2));
                        }
                        break;
                }
                return b;
            case 86: // Pumpkin
            case 91: // Jack'o'lantern
                // 0 - south
                // 1 - west
                // 2 - north
                // 3 - east
                // Other values: preserve.
                if (b.getData() > 3) {
                    return b;
                }
                return new BlockInfo(b.getId(), (byte) ((b.getData() + offset)&3));
            case 93: // repeater (off)
            case 94: // repeater (on).
                // bit 1 & 2
                // 0 - north
                // 1 - east
                // 2 - south
                // 3 - west.
                // bit 3 & 4: preserve (delay).
                return new BlockInfo(b.getId(), (byte) ((b.getData() & 12) | ((b.getData() & 3) + offset) & 3));
            case 96: // trapdoor.
                // bit 1 & 2
                // 0 - south.
                // 1 - north.
                // 2 - east.
                // 3 - west.
                // bit 3 & 4 preserve.
                switch (offset) {
                    case 1:
                        switch (b.getData() & 3) {
                            case 0:
                                return new BlockInfo(b.getId(), (byte) ((b.getData() & 12) | 3));
                            case 1:
                                return new BlockInfo(b.getId(), (byte) ((b.getData() & 12) | 2));
                            case 2:
                                return new BlockInfo(b.getId(), (byte) ((b.getData() & 12) | 0));
                            case 3:
                                return new BlockInfo(b.getId(), (byte) ((b.getData() & 12) | 1));
                        }
                        break;
                    case 2:
                        switch (b.getData() & 3) {
                            case 0:
                                return new BlockInfo(b.getId(), (byte) ((b.getData() & 12) | 1));
                            case 1:
                                return new BlockInfo(b.getId(), (byte) ((b.getData() & 12) | 0));
                            case 2:
                                return new BlockInfo(b.getId(), (byte) ((b.getData() & 12) | 3));
                            case 3:
                                return new BlockInfo(b.getId(), (byte) ((b.getData() & 12) | 2));
                        }
                        break;
                    case 3:
                        switch (b.getData() & 3) {
                            case 0:
                                return new BlockInfo(b.getId(), (byte) ((b.getData() & 12) | 2));
                            case 1:
                                return new BlockInfo(b.getId(), (byte) ((b.getData() & 12) | 3));
                            case 2:
                                return new BlockInfo(b.getId(), (byte) ((b.getData() & 12) | 1));
                            case 3:
                                return new BlockInfo(b.getId(), (byte) ((b.getData() & 12) | 0));
                        }
                        break;
                }
                return b; // unreachable.
            case 106: // vines.
                // bit 1: south.
                // bit 2: west.
                // bit 3: north.
                // bit 4: east.
                return new BlockInfo(b.getId(), (byte) (((b.getData() >> offset) & 0xf) | (b.getData() << (4 - offset))));
            case 107: // Fence gate
                // 0 - south
                // 1 - west
                // 2 - north
                // 3 - east
                // bit 3 preserve, bit 4?
                return new BlockInfo(b.getId(), (byte) ((b.getData() & 12) | ((b.getData() & 3) + offset) & 3));
            case 120: // End portal frame
                // 0 - south
                // 1 - west
                // 2 - north
                // 3 - east
                // bit 3 - activated.
                return new BlockInfo(b.getId(), (byte) ((b.getData() & 12) | ((b.getData() & 3) + offset) & 3));
            case 127: // Cocoa pod.
                // 0 - north
                // 1 - east
                // 2 - south
                // 3 - west
                // bit 3&4 preserve (size).
                return new BlockInfo(b.getId(), (byte) ((b.getData() & 12) | ((b.getData() & 3) + offset) & 3));
            case 131: // Tripwire hook
                // 0 - south
                // 1 - west
                // 2 - north
                // 3 - east
                // bit 3&4 preserve (tripped and armed).
                return new BlockInfo(b.getId(), (byte) ((b.getData() & 12) | ((b.getData() & 3) + offset) & 3));
            case 144: // Mob head.
                // 1 - floor
                // 2 - facing north
                // 3 - facing south
                // 4 - facing east
                // 5 - facing west
                // Others: invalid.
                switch (offset) {
                    case 1:
                        switch (b.getId()) {
                            case 2:
                                return new BlockInfo(b.getId(), (byte) 4);
                            case 3:
                                return new BlockInfo(b.getId(), (byte) 5);
                            case 4:
                                return new BlockInfo(b.getId(), (byte) 3);
                            case 5:
                                return new BlockInfo(b.getId(), (byte) 2);
                        }
                        break;
                    case 2:
                        switch (b.getId()) {
                            case 2:
                                return new BlockInfo(b.getId(), (byte) 3);
                            case 3:
                                return new BlockInfo(b.getId(), (byte) 2);
                            case 4:
                                return new BlockInfo(b.getId(), (byte) 5);
                            case 5:
                                return new BlockInfo(b.getId(), (byte) 4);
                        }
                        break;
                    case 3:
                        switch (b.getId()) {
                            case 2:
                                return new BlockInfo(b.getId(), (byte) 5);
                            case 3:
                                return new BlockInfo(b.getId(), (byte) 4);
                            case 4:
                                return new BlockInfo(b.getId(), (byte) 2);
                            case 5:
                                return new BlockInfo(b.getId(), (byte) 3);
                        }
                        break;
                }
                return b;
            case 145: // Anvil
                // bit 0
                // unset: north/south
                // set: east/west
                if (offset == 2) {
                    return b;
                } else {
                    return new BlockInfo(b.getId(), (byte) (b.getData() ^ 1));
                }
            case 99: // Huge brown mushroom
            case 100: // Huge red mushroom
                //0 	Fleshy piece 	Pores on all sides
                //1 	Corner piece 	Cap texture on top, west and north
                //2 	Side piece 	Cap texture on top and north
                //3 	Corner piece 	Cap texture on top, north and east
                //4 	Side piece 	Cap texture on top and west
                //5 	Top piece 	Cap texture on top
                //6 	Side piece 	Cap texture on top and east
                //7 	Corner piece 	Cap texture on top, south and west
                //8 	Side piece 	Cap texture on top and south
                //9 	Corner piece 	Cap texture on top, east and south
                //10 	Stem piece 	Stem texture on all four sides, pores on top and bottom
                //14 	All Cap 	Cap texture on all six sides
                //15 	All stem 	Stem texture on all six sides
                switch (b.getData()) {
                    case 1:
                        switch (offset) {
                            case 1:
                                return new BlockInfo(b.getId(), (byte) 3);
                            case 2:
                                return new BlockInfo(b.getId(), (byte) 9);
                            case 3:
                                return new BlockInfo(b.getId(), (byte) 7);
                        }
                    case 2:
                        switch (offset) {
                            case 1:
                                return new BlockInfo(b.getId(), (byte) 6);
                            case 2:
                                return new BlockInfo(b.getId(), (byte) 8);
                            case 3:
                                return new BlockInfo(b.getId(), (byte) 4);
                        }
                    case 3:
                        switch (offset) {
                            case 1:
                                return new BlockInfo(b.getId(), (byte) 9);
                            case 2:
                                return new BlockInfo(b.getId(), (byte) 7);
                            case 3:
                                return new BlockInfo(b.getId(), (byte) 1);
                        }
                    case 4:
                        switch (offset) {
                            case 1:
                                return new BlockInfo(b.getId(), (byte) 2);
                            case 2:
                                return new BlockInfo(b.getId(), (byte) 6);
                            case 3:
                                return new BlockInfo(b.getId(), (byte) 8);
                        }
                    case 6:
                        switch (offset) {
                            case 1:
                                return new BlockInfo(b.getId(), (byte) 8);
                            case 2:
                                return new BlockInfo(b.getId(), (byte) 4);
                            case 3:
                                return new BlockInfo(b.getId(), (byte) 2);
                        }
                    case 7:
                        switch (offset) {
                            case 1:
                                return new BlockInfo(b.getId(), (byte) 1);
                            case 2:
                                return new BlockInfo(b.getId(), (byte) 3);
                            case 3:
                                return new BlockInfo(b.getId(), (byte) 9);
                        }
                    case 8:
                        switch (offset) {
                            case 1:
                                return new BlockInfo(b.getId(), (byte) 4);
                            case 2:
                                return new BlockInfo(b.getId(), (byte) 2);
                            case 3:
                                return new BlockInfo(b.getId(), (byte) 6);
                        }
                    case 9:
                        switch (offset) {
                            case 1:
                                return new BlockInfo(b.getId(), (byte) 7);
                            case 2:
                                return new BlockInfo(b.getId(), (byte) 1);
                            case 3:
                                return new BlockInfo(b.getId(), (byte) 3);
                        }
                }
                return b;
            case 156: // Quartz.
                // 3 = n/s
                // 4 = e/w
                if (offset == 2)
                    return b;
                switch (b.getData()) {
                    case 3:
                        return new BlockInfo(b.getId(),(byte)4);
                    case 4:
                        return new BlockInfo(b.getId(),(byte)3);
                }
                return b;
            default:
                return b;
        }
    }
}
