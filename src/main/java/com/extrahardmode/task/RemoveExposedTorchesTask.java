
/*
 * This file is part of
 * ExtraHardMode Server Plugin for Minecraft
 *
 * Copyright (C) 2012 Ryan Hamshire
 * Copyright (C) 2013 Diemex
 *
 * ExtraHardMode is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ExtraHardMode is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero Public License
 * along with ExtraHardMode.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.extrahardmode.task;


import com.extrahardmode.ExtraHardMode;
import com.extrahardmode.config.RootConfig;
import com.extrahardmode.config.RootNode;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

/**
 * Task to remove exposed torches.
 */
public class RemoveExposedTorchesTask implements Runnable
{
    /**
     * Plugin instance.
     */
    private final ExtraHardMode plugin;

    /**
     * Chunk to iterate over.
     */
    private final Chunk chunk;

    /**
     * Config instance
     */
    private final RootConfig CFG;

    /**
     * If checks for rain should be bypassed (debugging/testing)
     */
    private final boolean force;


    /**
     * Constructor.
     *
     * @param plugin - Plugin instance.
     * @param chunk  - Target chunk.
     */
    public RemoveExposedTorchesTask(ExtraHardMode plugin, Chunk chunk)
    {
        this(plugin, chunk, false);
    }


    /**
     * Constructor.
     *
     * @param plugin - Plugin instance.
     * @param chunk  - Target chunk.
     */
    public RemoveExposedTorchesTask(ExtraHardMode plugin, Chunk chunk, boolean force)
    {
        this.plugin = plugin;
        this.chunk = chunk;
        CFG = this.plugin.getModuleForClass(RootConfig.class);
        this.force = force;
    }


    @Override
    public void run()
    {
        final boolean rainBreaksTorches = CFG.getBoolean(RootNode.RAIN_BREAKS_TORCHES, this.chunk.getWorld().getName());
        final boolean snowBreaksCrops = CFG.getBoolean(RootNode.WEAK_FOOD_CROPS, this.chunk.getWorld().getName()) && CFG.getBoolean(RootNode.SNOW_BREAKS_CROPS, this.chunk.getWorld().getName());

        if (this.chunk.getWorld().hasStorm() || force)
        {
            for (int x = 0; x < 16; x++)
            {
                for (int z = 0; z < 16; z++)
                {
                    /* Biome is saved on a per column basis */
                    loopDown:
                    for (int y = chunk.getWorld().getMaxHeight() - 1; y > 0; y--)
                    {
                        Block block = chunk.getBlock(x, y, z);
                        double temperature = block.getTemperature();
                        Material blockType = block.getType();

                        switch (blockType)
                        {
                            case AIR: /* we continue down until we hit something which isn't AIR */
                                continue loopDown;
                            case TORCH:
                            {
                                if (rainBreaksTorches && temperature < 1.0) //excludes warmer biomes like mesa and desert in which no rain falls
                                {
                                    /* Reduce lag by torches lying on the ground */
                                    if (plugin.getRandom().nextInt(5) == 1)
                                    {
                                        block.breakNaturally();
                                    } else
                                    {
                                        block.setType(Material.AIR);
                                    }
                                }
                                break loopDown;
                            }
                            case CROPS:
                            case MELON_STEM:
                            case CARROT:
                            case PUMPKIN_STEM:
                            case POTATO:
                            case RED_ROSE:
                            case YELLOW_FLOWER:
                            case LONG_GRASS:
                            case BEETROOT_BLOCK:
                            {
                                if (snowBreaksCrops && temperature <= 0.15) //cold biomes in which snow falls
                                {
                                    if (plugin.getRandom().nextInt(5) == 1)
                                        block.breakNaturally();
                                    //Snow can't be placed if its tilled soil
                                    if (block.getRelative(BlockFace.DOWN).getType() == Material.SOIL)
                                        block.getRelative(BlockFace.DOWN).setType(Material.DIRT);
                                    block.setType(Material.SNOW);
                                    if (plugin.getRandom().nextBoolean())
                                    {
                                        block.setData((byte) 1);
                                    } else
                                    {
                                        block.setData((byte) 2);
                                    }
                                }
                                break loopDown;
                            }
                            default: /* Anything which isn't AIR will protect torches and Crops */
                            {
                                break loopDown;
                            }
                        }
                    }
                }
            }
        }
    }
}
