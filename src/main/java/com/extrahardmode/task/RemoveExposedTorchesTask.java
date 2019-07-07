
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
import org.bukkit.block.data.type.Campfire;
import org.bukkit.block.data.type.Snow;

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
        final boolean rainExtinguishesCampfires = CFG.getBoolean(RootNode.RAIN_EXTINGUISHES_CAMPFIRES, this.chunk.getWorld().getName());
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
                            case WALL_TORCH:
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
                            case CAMPFIRE:
                            {
                                if (rainExtinguishesCampfires && temperature < 1.0)
                                {
                                    Campfire campfire = (Campfire) block.getBlockData();
                                    campfire.setLit(false);
                                    block.setBlockData(campfire);
                                }
                                break loopDown;
                            }
                            case WHEAT_SEEDS: //TODO: 1.13: need to confirm if = CROPS and below
                            case MELON_STEM:
                            case ATTACHED_MELON_STEM:
                            case MELON:
                            case CARROTS:
                            case PUMPKIN_STEM:
                            case ATTACHED_PUMPKIN_STEM:
                            case PUMPKIN: //I followed suit with the melon and added pumpkin
                            case POTATOES:
                            case ROSE_BUSH: //RED_ROSE //ROSE_RED
                            case DANDELION: //YELLOW FLOWER
                            case GRASS: //I still can't recall if the replacement for LONG_GRASS is GRASS or TALL_GRASS...
                            case TALL_GRASS:
                            case BEETROOTS:
                            {
                                if (snowBreaksCrops && temperature <= 0.15) //cold biomes in which snow falls
                                {
                                    if (plugin.getRandom().nextInt(5) == 1)
                                        block.breakNaturally();
                                    //Snow can't be placed if its tilled soil
                                    if (block.getRelative(BlockFace.DOWN).getType() == Material.FARMLAND)
                                        block.getRelative(BlockFace.DOWN).setType(Material.DIRT);
                                    Snow snow = (Snow)Material.SNOW.createBlockData();
                                    if (plugin.getRandom().nextBoolean())
                                    {
                                        snow.setLayers(1);
                                    } else
                                    {
                                        snow.setLayers(2);
                                    }
                                    block.setBlockData(snow);
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
