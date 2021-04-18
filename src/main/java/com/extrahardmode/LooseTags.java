package com.extrahardmode;


import org.bukkit.Material;

import java.util.HashSet;
import java.util.Set;

/**
 * Created on 10/17/2018.
 *
 * @author RoboMWM
 */
public enum LooseTags
{
    TORCH;

    private Set<Material> materials = new HashSet<>();


    LooseTags()
    {
        for (Material material : Material.values())
        {
            if (material.name().contains(this.name()) && !material.name().contains("LEGACY"))
                materials.add(material);
        }
    }


    public boolean isTagged(Material material)
    {
        return materials.contains(material);
    }
}
