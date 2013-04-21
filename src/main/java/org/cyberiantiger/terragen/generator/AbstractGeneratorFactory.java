/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cyberiantiger.terragen.generator;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author antony
 */
public abstract class AbstractGeneratorFactory implements GeneratorFactory {

    protected Map<String,String> parseArgsMap(String args) {
        Map<String,String> params = new HashMap<String,String>();
        List<Arg> l = parseArgsList(args);
        for (Arg a : l) {
            params.put(a.getKey(), a.getValue());
        }
        return params;
    }

    protected List<Arg> parseArgsList(String args) {
        String[] parts = args.split("&");
        List<Arg> ret = new ArrayList<Arg>();
        for (String s : parts) {
            if (s.length() == 0)
                continue;
            ret.add(new Arg(s));
        }
        return ret;
    }

    protected static class Arg {
        private final String key;
        private final String value;

        public Arg(String s) {
            int i = s.indexOf('=');
            if (i == -1) {
                key = URLDecoder.decode(s);
                value = "";
            } else {
                key = URLDecoder.decode(s.substring(0,i));
                value = URLDecoder.decode(s.substring(i+1));
            }
        }

        public String getKey() {
            return key;
        }

        public String getValue() {
            return value;
        }


    }

}
