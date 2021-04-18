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
import com.extrahardmode.compatibility.CompatHandler;
import com.extrahardmode.config.RootConfig;
import com.extrahardmode.config.RootNode;
import com.extrahardmode.module.BlockModule;
import com.extrahardmode.module.EntityHelper;
import com.extrahardmode.module.PlayerModule;
import com.extrahardmode.service.Feature;
import com.extrahardmode.service.ListenerModule;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;

/**
 * Physics include
 * <p/>
 * More FallingBlocks , Breaking Netherrack causes fires , Players get damaged by FallingBlocks when hit
 */
public class Physics extends ListenerModule
{
    private RootConfig CFG;

    private BlockModule blockModule;

    private PlayerModule playerModule;


    public Physics(ExtraHardMode plugin)
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
     * When a player places a block...
     * <p/>
     * Check the surrounding blocks for gravity
     *
     * @param placeEvent - Event that occurred
     */
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH/*so this gets called after the building limitations*/)
    public void onBlockPlace(BlockPlaceEvent placeEvent)
    {
        Player player = placeEvent.getPlayer();
        Block block = placeEvent.getBlock();
        World world = block.getWorld();

        final boolean physixEnabled = CFG.getBoolean(RootNode.MORE_FALLING_BLOCKS_ENABLE, world.getName());
        final boolean playerBypasses = playerModule.playerBypasses(player, Feature.MORE_FALLING_BLOCKS);

        if (physixEnabled && !playerBypasses)
        {
            //TODO EhmPhysicCheckEvent
            blockModule.physicsCheck(block, 10, true, 0);
        }
    }


    /**
     * When a player breaks a block...
     * <p/>
     * Check if the surrounding blocks should fall
     *
     * @param breakEvent - Event that occurred.
     */
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onBlockBreak(BlockBreakEvent breakEvent)
    {
        Block block = breakEvent.getBlock();
        World world = block.getWorld();
        Player player = breakEvent.getPlayer();

        final boolean moreFallingBlocksEnabled = CFG.getBoolean(RootNode.MORE_FALLING_BLOCKS_ENABLE, world.getName());
        final int netherRackFirePercent = CFG.getInt(RootNode.BROKEN_NETHERRACK_CATCHES_FIRE_PERCENT, world.getName());
        final boolean playerBypasses = playerModule.playerBypasses(player, Feature.MORE_FALLING_BLOCKS);


        // FEATURE: more falling blocks
        if (moreFallingBlocksEnabled && !playerBypasses)
        {
            //TODO EhmPhysicCheckEvent
            blockModule.physicsCheck(block, 10, true, 5);
        }

        // FEATURE: breaking netherrack may start a fire
        if (netherRackFirePercent > 0 && block.getType() == Material.NETHERRACK && !playerBypasses)
        {
            Block underBlock = block.getRelative(BlockFace.DOWN);
            if (underBlock.getType() == Material.NETHERRACK && plugin.random(netherRackFirePercent))
            {
                //TODO EhmNetherrackFireEvent
                breakEvent.setCancelled(true);
                block.setType(Material.FIRE);
            }
        }
    }


    /**
     * Called when an Entity forms a Block - Damage Player when a FallingBlock hits him
     * provide compatibility for block loggers that don't log correctly
     */
    @EventHandler(priority = EventPriority.HIGHEST) //so we are pretty late and hopefully don't get cancelled afterwards
    public void whenBlockLands(EntityChangeBlockEvent event)
    {
        Entity entity = event.getEntity();
//        World world = entity.getWorld();
//
//        final int damageAmount = CFG.getInt(RootNode.MORE_FALLING_BLOCKS_DMG_AMOUNT, world.getName());
//        final boolean environmentalDmg = CFG.getBoolean(RootNode.ENHANCED_ENVIRONMENTAL_DAMAGE, world.getName());
//
//        //Only when Block has been marked to deal damage
//        if (entity.getType().equals(EntityType.FALLING_BLOCK) && damageAmount > 0 && EntityHelper.isMarkedForProcessing(entity))
//        {
//            List<Entity> entities = entity.getNearbyEntities(0, 1, 0);
//            for (Entity ent : entities)
//            {
//                if (ent instanceof LivingEntity)
//                {
//                    LivingEntity entityWithDamagedHead = (LivingEntity) ent;
//                    //Frighten the player
//                    entityWithDamagedHead.damage(damageAmount, entity);
//                    if (environmentalDmg)
//                        entityWithDamagedHead.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 140, 10));
//                }
//            }
//        }

        if (entity instanceof FallingBlock && EntityHelper.isMarkedAsOurs(entity))
        {
            BlockState newState = event.getBlock().getState();
            newState.setType(event.getTo());
            CompatHandler.logFallingBlockLand(newState);

            blockModule.physicsCheck(event.getBlock().getRelative(BlockFace.DOWN), 10, false, 1);
        }
    }

}
