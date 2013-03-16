/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cyberiantiger.terragen.object;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.zip.GZIPOutputStream;
import org.cyberiantiger.terragen.Coord;
import org.cyberiantiger.terragen.nbt.Tag;
import org.cyberiantiger.terragen.nbt.TagOutputStream;

/**
 *
 * @author antony
 */
public class TGTSupport {
    public static final String EXTENSION = ".tgt";

    public static final byte[] HEADER_UNCOMPRESSED = "TGTR".getBytes(Tag.CHARSET);
    public static final byte[] HEADER_COMPRESSED = "TGTC".getBytes(Tag.CHARSET);

    public void exportFile(ObjectTemplate template, File file, boolean compressed) throws IOException {
        BufferedOutputStream rawOut = new BufferedOutputStream(new FileOutputStream(file));
        rawOut.write(compressed ? HEADER_COMPRESSED : HEADER_UNCOMPRESSED);

        TagOutputStream out = compressed ? new TagOutputStream(new GZIPOutputStream(rawOut)) : new TagOutputStream(rawOut);

        Map<Coord, BlockInfo> m = template.getBlocks();

        out.writeInt(m.size());
        short[] x = new short[m.size()];
        short[] y = new short[m.size()];
        short[] z = new short[m.size()];
        BlockInfo[] b = new BlockInfo[m.size()];
        int i = 0;
        for (Map.Entry<Coord,BlockInfo> e : m.entrySet()) {
            x[i] = (short) e.getKey().getX();
            y[i] = (short) e.getKey().getY();
            z[i] = (short) e.getKey().getZ();
            b[i] = e.getValue();
            i++;
        }
        for (i = 0; i < m.size(); i++) {
            out.writeShort(x[i]);
        }
        for (i = 0; i < m.size(); i++) {
            out.writeShort(y[i]);
        }
        for (i = 0; i < m.size(); i++) {
            out.writeShort(z[i]);
        }
        for (i = 0; i < m.size(); i++) {
            out.write(b[i].getId());
            out.write(b[i].getData());
        }

        // TODO Write tag containing TileEntities, Entities, Properties
    }

    public void importFile(ObjectTemplate template, File file) throws IOException {

    }

}
