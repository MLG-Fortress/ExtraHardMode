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
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetEvent;

/**
 * Changes to how Monsters spawn including:
 */
public class MonsterRules extends ListenerModule
{
    private RootConfig CFG = null;


    public MonsterRules(ExtraHardMode plugin)
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
     * When an Entity spawns
     * <p/>
     * more Monsters in caves
     */
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onEntitySpawn(CreatureSpawnEvent event)
    {
        Location location = event.getLocation();
        World world = location.getWorld();

        final int maxY = CFG.getInt(RootNode.MORE_MONSTERS_MAX_Y, world.getName());
        final int multiplier = CFG.getInt(RootNode.MORE_MONSTERS_MULTIPLIER, world.getName());

        LivingEntity entity = event.getEntity();
        EntityType entityType = entity.getType();

        CreatureSpawnEvent.SpawnReason reason = event.getSpawnReason();

        //We don't know how to handle ghosts. (Mo Creatures)
        if (!entityType.equals(EntityType.UNKNOWN) && reason == CreatureSpawnEvent.SpawnReason.NATURAL)
        {
            // FEATURE: extra monster spawns underground
            if (maxY > 0)
            {
                if (world.getEnvironment() == World.Environment.NORMAL && event.getLocation().getBlockY() < maxY && entity instanceof Monster)
                {
                    if (!entityType.equals(EntityType.SILVERFISH)) //no multiple silverfish per block
                    {
                        //Mc 1.6: "Social" Zombies can spawn very close when calling for help. Do not spawn more monsters if that is the case
                        if (entityType == EntityType.ZOMBIE ? !EntityHelper.arePlayersNearby(event.getLocation(), 16.0) : !EntityHelper.arePlayersNearby(event.getLocation(), 12.0))
                        {
                            for (int i = 1; i < multiplier; i++)
                            {
                                Entity newEntity = EntityHelper.spawnRandomMob(event.getLocation());
                                if (EntityHelper.isLootLess(entity))
                                {
                                    EntityHelper.markLootLess(plugin, (LivingEntity) newEntity);
                                }
                            }
                        }
                    }
                }
            }
        }
    }


    /**
     * when an entity targets something (as in to attack it)...
     *
     * @param event - Event that occurred.
     */
    @EventHandler(ignoreCancelled = true)
    public void onEntityTarget(EntityTargetEvent event)
    {
        Entity entity = event.getEntity();
        World world = entity.getWorld();

        final boolean websEnabled = CFG.getBoolean(RootNode.SPIDERS_DROP_WEB_ON_DEATH, world.getName());

        // FEATURE: a monster which gains a target breaks out of any webbing it might have been trapped within
        if (entity instanceof Monster && websEnabled)
        {
            EntityHelper.clearWebbing(entity);
        }
    }


    /**
     * when an entity is damaged handles
     *
     * @param event - Event that occurred.
     */
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onEntityDamage(EntityDamageEvent event)
    {
        Entity entity = event.getEntity();
        EntityType entityType = entity.getType();

        //TODO Remove

        // FEATURE: don't allow explosions to destroy items on the ground
        // REASONS: enhanced TNT explodes 5 times
        if (entityType == EntityType.DROPPED_ITEM && (event.getCause() == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION || event.getCause() == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION))
        {
            event.setCancelled(true);
        }

        // FEATURE: monsters trapped in webbing break out of the webbing when hit
        if (entity instanceof Monster)
        {
            EntityHelper.clearWebbing(entity);
        }
    }
}
