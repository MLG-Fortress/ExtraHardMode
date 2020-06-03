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
import com.extrahardmode.module.BlockModule;
import org.apache.commons.lang.Validate;
import org.bukkit.block.Block;

/**
 * Gradually let's Logs which have been marked as loose fall down.
 */
public class FallingLogsTask implements Runnable
{
    /**
     * Reference to the plugin using this class
     */
    private final ExtraHardMode plugin;

    /**
     * BlockModule to spawn FallingBlocks
     */
    private final BlockModule blockModule;

    /**
     * Block to apply physics to
     */
    private final Block block;


    /**
     * Constructor
     *
     * @param plugin reference to the plugin
     * @param block  to apply physics to
     */

    public FallingLogsTask(ExtraHardMode plugin, Block block)
    {
        Validate.notNull(block, "Block can't be null");
        Validate.notNull(plugin, "Plugin can't be null");

        this.block = block;
        this.plugin = plugin;
        blockModule = plugin.getModuleForClass(BlockModule.class);
    }


    //TODO: 1.13
    //Needs to look for leaves/logs that are the same species as the block, instead of just any logs/leaves
    //Or just flat out remove, since there are other tree feller plugins that may be better.
    @Override
    public void run()
    {
//        if (block != null)
//        {
//            /* Prevent wooden structures near trees from being affected*/
//            if (blockModule.getBlocksInArea(block.getLocation(), 2, 1, Material.LEAVES).length > 3 ||
//                    blockModule.getBlocksInArea(block.getLocation(), 2, 1, Material.LEAVES_2).length > 3)
//            {
//                //Clear the area below of leaves
//                Block below = block;
//                List<Block> looseLogs = new ArrayList<Block>();
//                List<Block> tempBlocks = new ArrayList<Block>();
//                looseLogs.add(block);
//                checkBelow:
//                for (int i = 0; below.getY() > 0; i++)
//                {
//                    below = below.getRelative(BlockFace.DOWN);
//                    switch (below.getType())
//                    {
//                        case AIR:
//                        {
//                            //go one down
//                            //All blocks above this can fall now that there is an air block
//                            looseLogs.addAll(tempBlocks);
//                            tempBlocks.clear();
//                            break;
//                        }
//                        case LEAVES:
//                        case LEAVES_2:
//                        {
//                            below.breakNaturally();
//                            break;
//                        }
//                        case LOG:
//                        case LOG_2:
//                        {
//                            //Prevent Logs on adjacent sides (Jungle Tree) from turning to FallingBlocks and some of them turning into items
//                            switch (below.getRelative(BlockFace.DOWN).getType())
//                            {
//                                case AIR:
//                                case LEAVES:
//                                    tempBlocks.add(below);
//                            }
//                            break;
//                        }
//                        default: //we hit the block where the FallingBlock will land
//                        {
//                            if (blockModule.breaksFallingBlock(below.getType()))
//                            {
//                                below.breakNaturally();
//                            } else
//                            {
//                                break checkBelow;
//                            }
//                        }
//                    }
//                }
//
//                for (int i = 0; i < looseLogs.size(); i++)
//                {
//                    final Block looseLog = looseLogs.get(i);
//                    plugin.getServer().getScheduler().runTaskLater(plugin, new Runnable()
//                    {
//                        @Override
//                        public void run()
//                        {
//                            blockModule.applyPhysics(looseLog, true);
//                        }
//                    }, i /*delay to prevent FallingBlock collision*/);
//
//                }
//            }
//        }
    }
}