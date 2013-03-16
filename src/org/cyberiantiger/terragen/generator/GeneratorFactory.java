/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cyberiantiger.terragen.generator;

import org.bukkit.generator.ChunkGenerator;
import org.cyberiantiger.terragen.TerraGen;

/**
 *
 * @author antony
 */
public interface GeneratorFactory {

    public ChunkGenerator create(TerraGen gen, String paramString);

}
