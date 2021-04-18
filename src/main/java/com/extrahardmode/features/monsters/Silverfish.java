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

package com.extrahardmode.features.monsters;


import com.extrahardmode.ExtraHardMode;
import com.extrahardmode.config.RootConfig;
import com.extrahardmode.config.RootNode;
import com.extrahardmode.service.ListenerModule;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Changes to SilverFish including:
 * <p/>
 * Block entering of blocks Drop cobble when slain
 */
public class Silverfish extends ListenerModule
{
    private RootConfig CFG = null;


    public Silverfish(ExtraHardMode plugin)
    {
        super(plugin);
    }


    @Override
    public void starting()
    {
        super.starting();
        CFG = plugin.getModuleForClass(RootConfig.class);
    }


    /**
     * when an entity tries to change a block (does not include player block changes) don't allow silverfish to change blocks
     *
     * @param event - Event that occurred.
     */
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onEntityChangeBlock(EntityChangeBlockEvent event)
    {
        Block block = event.getBlock();
        World world = block.getWorld();

        final boolean silverFishCantEnter = CFG.getBoolean(RootNode.SILVERFISH_CANT_ENTER_BLOCKS, world.getName());

        //Prevent Silverfish from entering blocks?
        if (silverFishCantEnter)
        {
            if (event.getEntity().getType() == EntityType.SILVERFISH && event.getTo() == Material.INFESTED_STONE) //TODO: check for other infested variants? (1.13 change)
            {
                event.setCancelled(true);
            }
        }
    }


    /** When an entity dies, drop cobble for SilverFish */
    @EventHandler
    public void onEntityDeath(EntityDeathEvent event)
    {
        LivingEntity entity = event.getEntity();
        World world = entity.getWorld();

        //TODO Silverfish drop random loot
        final boolean dropCobble = CFG.getBoolean(RootNode.SILVERFISH_DROP_COBBLE, world.getName());

        // FEATURE: silverfish drop cobblestone
        if (dropCobble && entity.getType() == EntityType.SILVERFISH)
        {
            event.getDrops().add(new ItemStack(Material.COBBLESTONE));
        }
    }
}
