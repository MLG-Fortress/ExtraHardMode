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

package com.extrahardmode.features;


import com.extrahardmode.ExtraHardMode;
import com.extrahardmode.config.RootConfig;
import com.extrahardmode.config.RootNode;
import com.extrahardmode.module.BlockModule;
import com.extrahardmode.module.PlayerModule;
import com.extrahardmode.service.Feature;
import com.extrahardmode.service.ListenerModule;
import com.extrahardmode.task.FallingLogsTask;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * When chopping down trees the logs fall down and loose logs fall down on the side and can injure you
 */
public class RealisticChopping extends ListenerModule
{
    /**
     * Config Instance
     */
    private RootConfig CFG;

    /**
     * Stuff like FallingBlocks etc.
     */
    private BlockModule blockModule;

    /**
     * Permissions etc.
     */
    private PlayerModule playerModule;


    /**
     * Constructor
     */
    public RealisticChopping(ExtraHardMode plugin)
    {
        super(plugin);
    }


    @Override
    public void starting()
    {
        super.starting();
        CFG = plugin.getModuleForClass(RootConfig.class);
        blockModule = plugin.getModuleForClass(BlockModule.class);
        playerModule = plugin.getModuleForClass(PlayerModule.class);
    }


    /**
     * When a player breaks a block...
     *
     * @param breakEvent - Event that occurred.
     */
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onBlockBreak(BlockBreakEvent breakEvent)
    {
        Block block = breakEvent.getBlock();
        World world = block.getWorld();
        Player player = breakEvent.getPlayer();

        final boolean betterTreeChoppingEnabled = false; //CFG.getBoolean(RootNode.BETTER_TREE_CHOPPING, world.getName());
        final boolean playerHasBypass = playerModule.playerBypasses(player, Feature.REALISTIC_CHOPPING);

        // FEATURE: trees chop more naturally
        if (Tag.LOGS.isTagged(block.getType()) && betterTreeChoppingEnabled && !playerHasBypass)
        {
            //Are there any leaves above the log? -> tree
            boolean isTree = false;
            for (int i = 1; i < 30; i++)
            {
                Material upType = block.getRelative(BlockFace.UP, i).getType();
                //skip to next iteration
                //if something other than log/air this is most likely part of a building
                if (Tag.LEAVES.isTagged(upType))
                {
                    isTree = true;
                    break;
                }
                else if (!Tag.LOGS.isTagged(upType))
                {
                    break;
                }
            }

            if (isTree)
            {
                Block aboveLog = block.getRelative(BlockFace.UP);
                for (int limit = 0; limit < 30; limit++)
                {
                    Material aboveLogType = aboveLog.getType();//can air fall?
                    //we reached something that is not part of a tree or leaves
                    if (aboveLogType == Material.AIR)
                    {
                        List<Block> logs = new LinkedList<Block>(Arrays.asList(blockModule.getBlocksInArea(aboveLog.getLocation(), 3, 5, Tag.LOGS)));
                        for (Block log : logs)
                        {
                            //TODO EhmRealisticChoppingLooseLogEvent
                            //check 2 blocks down for logs to see if it it's a stem
                            if (!Tag.LOGS.isTagged(log.getRelative(BlockFace.DOWN).getType()))
                                plugin.getServer().getScheduler().runTaskLater(plugin, new FallingLogsTask(plugin, log), plugin.getRandom().nextInt(50/*so they don't fall at once*/));
                        }
                    }
                    else if (Tag.LOGS.isTagged(aboveLogType))
                    {
                        blockModule.applyPhysics(aboveLog, false);
                    }
                    else
                    {
                        break;
                    }
                    aboveLog = aboveLog.getRelative(BlockFace.UP);
                }
            }
        }
    }
}
