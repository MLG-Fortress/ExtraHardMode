package com.extrahardmode.service.config.customtypes;


import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.HashMap;
import java.util.Map;

/**
 * Holds a relationship. BlockTypes can be retrieved by their key BlockType. F.e stone -> cobblestone
 *
 * @deprecated Was originally used to hold the now-deprecated BlockTypes. Maybe now just overkill? idk.
 *
 * Only used for the "soften surrounding stone" feature, turning one block (e.g. stone) into another (e.g. cobblestone)
 */
@Deprecated
public class BlockRelationsList
{
    private Map<Material, Material> mBlockRelations = new HashMap<>();
    /**
     * An empty list
     */
    public final static BlockRelationsList EMPTY_LIST = new BlockRelationsList();


    /**
     * Add Relationships from a string loaded from a config
     *
     * @param configString expected format: block1@meta-block2@meta. If it doesn't match it won't be added
     */
    public void addFromConfig(String configString)
    {
        if (configString == null)
            return;

        String[] splitted = configString.split("-");
        if (splitted.length < 2)
            return;

        Material block1 = Material.matchMaterial(splitted[0]);
        Material block2 = Material.matchMaterial(splitted[1]);
        if (block1 != null && block2 != null)
            add(block1, block2);
    }


    /**
     * Saves this so it can be from the config again
     *
     * @return list of block relations
     */
    public String[] toConfigStrings()
    {
        if (mBlockRelations.size() == 0)
            return new String[]{""};
        String[] configStrings = new String[mBlockRelations.size()];
        int i = 0;
        for (Map.Entry<Material, Material> relation : mBlockRelations.entrySet())
        {
            configStrings[i] = relation.getKey().toString() + "-" + relation.getValue().toString();
            i++;
        }
        return configStrings;
    }


    public void add(Material block1, Material block2)
    {
        mBlockRelations.put(block1, block2);
    }


    public Material get(Block block)
    {
        Material material = block.getType();
        for (Map.Entry<Material, Material> entry : mBlockRelations.entrySet())
            if (entry.getKey() == material)
                return entry.getValue();
        return null;
    }


    public boolean contains(Block block)
    {
        return get(block) != null;
    }
}
