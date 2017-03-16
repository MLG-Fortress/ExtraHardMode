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
import com.extrahardmode.module.EntityHelper;
import com.extrahardmode.service.ListenerModule;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.weather.LightningStrikeEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Changes to ZombiePigmen including:
 * <p/>
 * Always angry , drop netherwart in the nether , spawn on lighting strikes
 */
public class PigMen extends ListenerModule
{
    private RootConfig CFG;


    public PigMen(ExtraHardMode plugin)
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
     * When an Entity dies (Piggie)
     * <p/>
     * Drop netherwart in fortresses and elsewhere in the nether
     */
    @EventHandler
    public void onEntityDeath(EntityDeathEvent event)
    {
        LivingEntity entity = event.getEntity();
        World world = entity.getWorld();

        final boolean pigWartFortress = CFG.getBoolean(RootNode.FORTRESS_PIGS_DROP_WART, world.getName());
        final int pigWartDropEveryWherePercent = CFG.getInt(RootNode.NETHER_PIGS_DROP_WART, world.getName());

        // FEATURE: pig zombies drop nether wart when slain in nether fortresses
        if (world.getEnvironment().equals(World.Environment.NETHER) && entity instanceof PigZombie)
        {
            Block underBlock = entity.getLocation().getBlock().getRelative(BlockFace.DOWN);
            if (pigWartFortress && underBlock.getType() == Material.NETHER_BRICK)
                event.getDrops().add(new ItemStack(Material.NETHER_STALK));

                // FEATURE: pig zombies sometimes drop nether wart when slain elsewhere
            else if (pigWartDropEveryWherePercent > 0 && plugin.random(pigWartDropEveryWherePercent))
                event.getDrops().add(new ItemStack(Material.NETHER_STALK));
        }
    }


    /**
     * When an Entity spawns
     * <p/>
     * Makes Pigmen always angry
     */
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onEntitySpawn(CreatureSpawnEvent event)
    {
        Location location = event.getLocation();
        World world = location.getWorld();
        LivingEntity entity = event.getEntity();

        final boolean pigsAlwaysAggro = CFG.getBoolean(RootNode.ALWAYS_ANGRY_PIG_ZOMBIES, world.getName());

        // FEATURE: always-angry pig zombies
        if (pigsAlwaysAggro)
        {
            if (entity instanceof PigZombie)
            {
                PigZombie pigZombie = (PigZombie) entity;
                pigZombie.setAnger(Integer.MAX_VALUE);
            }
        }
    }


    /**
     * when a chunk loads... Always angry pigzombies
     *
     * @param event - Event that occurred.
     */
    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event)
    {
        Chunk chunk = event.getChunk();
        World world = chunk.getWorld();

        final boolean pigAlwaysAggro = CFG.getBoolean(RootNode.ALWAYS_ANGRY_PIG_ZOMBIES, world.getName());

        // FEATURE: always-angry pig zombies
        if (pigAlwaysAggro)
        {
            for (Entity entity : chunk.getEntities())
            {
                if (entity instanceof PigZombie)
                {
                    PigZombie pigZombie = (PigZombie) entity;
                    pigZombie.setAnger(Integer.MAX_VALUE);
                }
            }
        }
    }


    @EventHandler
    public void onPlayerDamaged(EntityDamageByEntityEvent event)
    {
        int damagePercentage = CFG.getInt(RootNode.PIG_ZOMBIE_DMG_PERCENT, event.getEntity().getWorld().getName()) / 100;
        if (damagePercentage <= 0)
            return;
        if (event.getEntity() instanceof Player && event.getDamager() instanceof PigZombie)
        {
            event.setDamage(event.getDamage() * damagePercentage);
        }
    }



}