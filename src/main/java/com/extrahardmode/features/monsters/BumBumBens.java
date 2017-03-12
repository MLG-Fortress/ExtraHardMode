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
import com.extrahardmode.events.EhmCreeperDropTntEvent;
import com.extrahardmode.events.fakeevents.FakeEntityExplodeEvent;
import com.extrahardmode.module.EntityHelper;
import com.extrahardmode.module.PlayerModule;
import com.extrahardmode.service.Feature;
import com.extrahardmode.service.ListenerModule;
import com.extrahardmode.task.CoolCreeperExplosion;
import com.extrahardmode.task.CreateExplosionTask;
import org.bukkit.Effect;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.*;
import org.bukkit.potion.PotionEffectType;

/**
 * Changes to Creepers including:
 * <p/>
 * Naturally spawning Charged Creepers , Charged Creepers exloding on hit ,
 */
public class BumBumBens extends ListenerModule
{
    private RootConfig CFG = null;

    private PlayerModule playerModule;


    public BumBumBens(ExtraHardMode plugin)
    {
        super(plugin);
    }


    @Override
    public void starting()
    {
        super.starting();
        CFG = plugin.getModuleForClass(RootConfig.class);
        playerModule = plugin.getModuleForClass(PlayerModule.class);
    }


    /**
     * When an Entity spawns
     * <p/>
     * naturally spawning Charged Creepers
     */
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onEntitySpawn(CreatureSpawnEvent event)
    {
        LivingEntity entity = event.getEntity();
        if (EntityHelper.isMarkedAsOurs(entity))
            return;
        EntityType entityType = entity.getType();
        World world = entity.getWorld();

        final int chargedSpawnPercent = CFG.getInt(RootNode.CHARGED_CREEPER_SPAWN_PERCENT, world.getName());

        // FEATURE: charged creeper spawns
        if (entityType == EntityType.CREEPER)
        {
            if (plugin.random(chargedSpawnPercent))
            {
                ((Creeper) entity).setPowered(true);
            }
        }
    }


    /**
     * When an Entity dies
     * <p/>
     * Creepers may drop tnt
     */
    @EventHandler
    public void onEntityDeath(EntityDeathEvent event)
    {
        LivingEntity entity = event.getEntity();
        World world = entity.getWorld();

        final int creeperDropTNTPercent = CFG.getInt(RootNode.CREEPERS_DROP_TNT_ON_DEATH_PERCENT, world.getName());
        final int creeperDropTntMaxY = CFG.getInt(RootNode.CREEPERS_DROP_TNT_ON_DEATH_MAX_Y, world.getName());
        final boolean creeperSound = CFG.getBoolean(RootNode.SOUND_CREEPER_TNT, world.getName());

        // FEATURE: creepers may drop activated TNT when they die
        if (creeperDropTNTPercent > 0)
        {
            if (entity.getType() == EntityType.CREEPER && plugin.random(creeperDropTNTPercent)
                    && creeperDropTntMaxY > entity.getLocation().getBlockY())
            {
                final Player player = entity.getKiller();
                EhmCreeperDropTntEvent dropTntEvent = new EhmCreeperDropTntEvent(player, (Creeper) entity, entity.getLocation());
                plugin.getServer().getPluginManager().callEvent(dropTntEvent);
                if (!dropTntEvent.isCancelled())
                {
                    world.spawnEntity(entity.getLocation(), EntityType.PRIMED_TNT);
                    if (creeperSound)
                        world.playEffect(entity.getLocation(), Effect.GHAST_SHRIEK, 1, 35);
                }
            }
        }
    }


    /**
     * When an Entity takes damage
     * <p/>
     * Charged creepers explode on hit , burning creepers will cause a big explosion
     */
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onEntityDamage(EntityDamageEvent event)
    {
        Entity entity = event.getEntity();
        EntityType entityType = entity.getType();
        World world = entity.getWorld();

        // is this an entity damaged by entity event?
        EntityDamageByEntityEvent damageByEntityEvent = null;
        if (event instanceof EntityDamageByEntityEvent)
        {
            damageByEntityEvent = (EntityDamageByEntityEvent) event;
        }

        final boolean chargedExplodeOnHit = CFG.getBoolean(RootNode.CHARGED_CREEPERS_EXPLODE_ON_HIT, world.getName());
        final boolean flamingCreepersExplode = CFG.getBoolean(RootNode.FLAMING_CREEPERS_EXPLODE, world.getName());
        final boolean customCharged = CFG.getBoolean(RootNode.EXPLOSIONS_CHARGED_CREEPERS_ENABLE, world.getName());


        // FEATURE: charged creepers explode on hit
        if (chargedExplodeOnHit)
        {
            if ((entityType == EntityType.CREEPER) && !entity.isDead())
            {
                Creeper creeper = (Creeper) entity;
                if (creeper.isPowered())
                {
                    Player damager = null;
                    //Always explode when damaged by a player
                    if (damageByEntityEvent != null)
                    {
                        if (damageByEntityEvent.getDamager() instanceof Player)
                        {   //Normal Damage from a player
                            damager = (Player) damageByEntityEvent.getDamager();
                            if (damager != null && playerModule.playerBypasses(damager, Feature.MONSTER_BUMBUMBENS))
                                return;
                        } else if (damageByEntityEvent.getDamager() instanceof Projectile)
                        {   //Damaged by an arrow shot by a player
                            Projectile bullet = (Projectile) damageByEntityEvent.getDamager();
                            if (bullet.getShooter() instanceof Player)//otherwise skeli/dispenser etc.
                                damager = (Player) bullet.getShooter();
                            if (damager != null && playerModule.playerBypasses(damager, Feature.MONSTER_BUMBUMBENS))
                                return;
                        }
                    }
                    if (creeper.getTarget() == null && damager == null)
                    {   //If not targetting a player this is an explosion we don't need. Trying to prevent unecessary world damage
                        return;
                    }
                    EntityHelper.markLootLess(plugin, (LivingEntity) entity);
                    entity.remove();
                    if (customCharged)
                        new CreateExplosionTask(plugin, entity.getLocation(), ExplosionType.CREEPER_CHARGED, entity).run(); // equal to a TNT blast
                    return;
                }
            }
        }


        //FEATURE: a burning creeper will create a nice explosion + fireworks and will fly in the air
        //Will only trigger if creeper died from fire not from a sword with fireaspect or bow
        if (flamingCreepersExplode)
        {
            if (entityType.equals(EntityType.CREEPER))
            {
                Creeper creeper = (Creeper) entity;

                if ((event.getCause().equals(EntityDamageEvent.DamageCause.FIRE)
                        || event.getCause().equals(EntityDamageEvent.DamageCause.FIRE_TICK)
                        || event.getCause().equals(EntityDamageEvent.DamageCause.LAVA))
                        && !creeper.hasPotionEffect(PotionEffectType.FIRE_RESISTANCE))
                {
                    if (!EntityHelper.hasFlagIgnore(entity))
                    {
                        EntityHelper.flagIgnore(plugin, entity);
                        CoolCreeperExplosion bigBoom = new CoolCreeperExplosion(creeper, plugin);
                        bigBoom.run();
                    }
                }
            }
        }
    }


    /**
     * When something explodes
     * <p/>
     * Increase size of Creeper explosions
     */
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    //give some time for other plugins to block the event
    public void onExplosion(EntityExplodeEvent event)
    {
        if (event instanceof FakeEntityExplodeEvent)
            return;
        Entity entity = event.getEntity();
        World world = event.getLocation().getWorld();

        final boolean customCreeper = CFG.getBoolean(RootNode.EXPLOSIONS_CREEPERS_ENABLE, world.getName());

        // FEATURE: bigger creeper explosions (for more-frequent cave-ins)
        // Charged creeper explosion is handled in onEntityDamage
        if (customCreeper && entity instanceof Creeper)
        {
            event.setCancelled(true);
            EntityHelper.flagIgnore(plugin, entity);//Ignore this creeper in further calls to this method
            if (((Creeper) entity).isPowered())
                new CreateExplosionTask(plugin, entity.getLocation(), ExplosionType.CREEPER_CHARGED, entity).run();
            else //normal creeper
                new CreateExplosionTask(plugin, entity.getLocation(), ExplosionType.CREEPER, entity).run();
        }
    }
}
