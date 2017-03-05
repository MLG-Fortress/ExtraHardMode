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
import com.extrahardmode.config.ExplosionType;
import com.extrahardmode.config.RootConfig;
import com.extrahardmode.config.RootNode;
import com.extrahardmode.module.EntityHelper;
import com.extrahardmode.service.ListenerModule;
import com.extrahardmode.task.CreateExplosionTask;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.projectiles.ProjectileSource;

/**
 * All the changes to Witches
 * <p/>
 * including:
 * <p/>
 * New Attacks like Explosion potions, spawning of zombies
 */
public class Witches extends ListenerModule
{
    private RootConfig CFG;


    public Witches(ExtraHardMode plugin)
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
     * When an Entity spawns: Spawn a Witch above ground sometimes instead of a Zombie
     *
     * @param event which occurred
     */
    @EventHandler(priority = EventPriority.LOW)
    public void onEntitySpawn(CreatureSpawnEvent event)
    {
        LivingEntity entity = event.getEntity();
        if (EntityHelper.isMarkedAsOurs(entity))
            return;
        Location location = event.getLocation();
        World world = location.getWorld();
        EntityType entityType = entity.getType();

        final int witchSpawnPercent = CFG.getInt(RootNode.BONUS_WITCH_SPAWN_PERCENT, world.getName());

        // FEATURE: more witches above ground (on grass)
        if (entityType == EntityType.ZOMBIE && world.getEnvironment() == World.Environment.NORMAL
                && entity.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() == Material.GRASS
                && event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.NATURAL)
        {
            if (plugin.random(witchSpawnPercent))
            {
                event.setCancelled(true);
                EntityHelper.spawn(location, EntityType.WITCH);
            }
        }
    }


    /**
     * When a potion breaks When Witches throw a potion we sometimes spawn explosions or monsters
     *
     * @param event - Event that occurred.
     */
    @EventHandler(priority = EventPriority.LOW)
    public void onPotionSplash(PotionSplashEvent event)
    {
        ThrownPotion potion = event.getPotion();
        Location location = potion.getLocation();
        World world = location.getWorld();

        final boolean additionalAttacks = CFG.getBoolean(RootNode.WITCHES_ADDITIONAL_ATTACKS, world.getName());

        // FEATURE: enhanced witches. they throw wolf spawner and teleport potions as well as poison potions
        if (additionalAttacks && EntityHelper.shooterType(potion) == EntityType.WITCH)
        {
            Witch witch = (Witch) potion.getShooter();

            int random = plugin.getRandom().nextInt(100);

            boolean makeExplosion = false;

            // 30% summon zombie
            if (random < 30)
            {
                event.setCancelled(true);

                boolean zombieNearby = false;
                for (Entity entity : location.getChunk().getEntities())
                {
                    if (entity.getType() == EntityType.ZOMBIE_VILLAGER)
                    {
                        ZombieVillager zombie = (ZombieVillager) entity;
                        if (zombie.isBaby())
                        {
                            zombieNearby = true;
                            break;
                        }
                    }
                }

                if (!zombieNearby)
                {
                    ZombieVillager zombie = (ZombieVillager) EntityHelper.spawn(location, EntityType.ZOMBIE_VILLAGER);
                    zombie.setBaby(true);
                    if (zombie.getTarget() != null)
                    {
                        zombie.setTarget(witch.getTarget());
                    }

                    EntityHelper.markLootLess(plugin, zombie);
                } else
                {
                    makeExplosion = true;
                }
            } else if (random < 60)
            {
                // 30% teleport
                event.setCancelled(true);
                witch.teleport(location);
            } else if (random < 90)
            {
                // 30% explosion
                event.setCancelled(true);
                makeExplosion = true;
            } else
            {
                // otherwise poison potion (selective target)
                for (LivingEntity target : event.getAffectedEntities())
                {
                    if (target.getType() != EntityType.PLAYER)
                    {
                        event.setIntensity(target, 0.0);
                    }
                }
            }

            // if explosive potion, direct damage to players in the area
            if (makeExplosion)
            {
                // explosion just for show, no damage
                new CreateExplosionTask(plugin, location, ExplosionType.EFFECT, null).run();

                for (LivingEntity target : event.getAffectedEntities())
                {
                    if (target.getType() == EntityType.PLAYER)
                    {
                        target.damage(3);
                    }
                }
            }
        }

    }

}
