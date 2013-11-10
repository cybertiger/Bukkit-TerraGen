/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cyberiantiger.terragen.object;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.cyberiantiger.minecraft.Coord;

/**
 *
 * @author antony
 */
public class BO2Support {

    public static final String EXTENSION = ".bo2";

    public static void exportFile(ObjectTemplate template, File file) throws IOException {
        OutputStream bout = new FileOutputStream(file);
        final PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(bout)));

        out.println("[META]");
        out.println("version=2.0");

        for(Map.Entry<String,String> e : template.getProperties().entrySet()) {
            out.print(e.getKey());
            out.print('=');
            out.println(e.getValue());
        }

        out.println("[DATA]");

        Map<Coord, BlockInfo> blocks = template.getBlocks();

        StringBuilder line = new StringBuilder();
        for (Map.Entry<Coord, BlockInfo> e : blocks.entrySet()) {
            Coord coord = e.getKey();
            BlockInfo block = e.getValue();
            int id = block.getId();
            byte data = block.getData();
            line.append(coord.getX());
            line.append(',');
            line.append(coord.getZ());
            line.append(',');
            line.append(coord.getY());
            line.append(':');
            line.append(String.valueOf(id));
            if (data != 0) {
                line.append('.');
                line.append(String.valueOf(data));
            }
            out.println(line);
            line.setLength(0);
        }

        out.close();
    }

    private static enum ParseState {

        NONE,
        META,
        DATA;
    }
    
    private static final Pattern DUMB_FORMAT_META = Pattern.compile("(\\w+?)=(.*)");
    private static final Pattern DUMB_FORMAT_DATA = Pattern.compile("(-?\\d+),(-?\\d+),(-?\\d+):(\\d+)(\\.(\\d+))?");

    public static void importFile(ObjectTemplate template, File file) throws IOException {
        template.clear();
        BufferedReader reader = new BufferedReader(new FileReader(file));

        String line;

        ParseState state = ParseState.NONE;
        Matcher m;

        while ((line = reader.readLine()) != null) {
            if (line.equals("[META]")) {
                state = ParseState.META;
                continue;
            } else if (line.equals("[DATA]")) {
                state = ParseState.DATA;
                continue;
            }
            switch (state) {
                case NONE:
                    break;
                case META:
                    m = DUMB_FORMAT_META.matcher(line);
                    if (m.matches()) {
                        String key = m.group(1);
                        String value = m.group(2);
                        if (!"version".equals(key)) {
                            template.getProperties().put(key, value);
                        }
                    }
                    break;
                case DATA:
                    m = DUMB_FORMAT_DATA.matcher(line);

                    if (m.matches()) {
                        try {
                            int x = Integer.parseInt(m.group(1));
                            int z = Integer.parseInt(m.group(2));
                            int y = Integer.parseInt(m.group(3));
                            int id = Integer.parseInt(m.group(4));
                            byte data = 0;
                            String s = m.group(6);
                            if (s != null) {
                                data = Byte.parseByte(m.group(6));
                            }

                            template.addBlock(new Coord(x, y, z), new BlockInfo(id, data));
                        } catch (NumberFormatException nfe) {
                            nfe.printStackTrace(); // Really shouldn't happen
                        }
                        break;
                    }
            }
            continue;
        }
    }
}
