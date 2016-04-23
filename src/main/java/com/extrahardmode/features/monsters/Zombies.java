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
import com.extrahardmode.module.BlockModule;
import com.extrahardmode.module.EntityHelper;
import com.extrahardmode.module.PlayerModule;
import com.extrahardmode.module.temporaryblock.TemporaryBlock;
import com.extrahardmode.module.temporaryblock.TemporaryBlockBreakEvent;
import com.extrahardmode.module.temporaryblock.TemporaryBlockHandler;
import com.extrahardmode.service.Feature;
import com.extrahardmode.service.ListenerModule;
import com.extrahardmode.service.OurRandom;
import com.extrahardmode.service.config.customtypes.PotionEffectHolder;
import com.extrahardmode.task.RespawnZombieTask;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Skull;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;

/** Zombies <p> can resurrect themselves , make players slow when hit </p> */
public class Zombies extends ListenerModule
{
    private RootConfig CFG;

    private PlayerModule playerModule;

    private TemporaryBlockHandler temporaryBlockHandler;

    private boolean hasReinforcements = false;


    public Zombies(ExtraHardMode plugin)
    {
        super(plugin);
    }


    @Override
    public void starting()
    {
        super.starting();
        CFG = plugin.getModuleForClass(RootConfig.class);
        playerModule = plugin.getModuleForClass(PlayerModule.class);
        temporaryBlockHandler = plugin.getModuleForClass(TemporaryBlockHandler.class);
        try
        {
            CreatureSpawnEvent.SpawnReason doesEnumExist = CreatureSpawnEvent.SpawnReason.REINFORCEMENTS;
            hasReinforcements = true;
        } catch (NoSuchFieldError e)
        {
            hasReinforcements = false;
        }
    }


    /**
     * When a zombie dies
     * <p/>
     * sometimes reanimate the zombie
     */
    @EventHandler
    public void onEntityDeath(EntityDeathEvent event)
    {
        LivingEntity entity = event.getEntity();
        World world = entity.getWorld();

        int zombiesReanimatePercent = CFG.getInt(RootNode.ZOMBIES_REANIMATE_PERCENT, world.getName());
        boolean placeSkulls = CFG.getBoolean(RootNode.ZOMBIES_REANIMATE_SKULLS, world.getName());

        // FEATURE: zombies may reanimate if not on fire when they die
        if (zombiesReanimatePercent > 0 && !EntityHelper.hasFlagIgnore(entity))
        {
            if (entity.getType() == EntityType.ZOMBIE)
            {
                Zombie zombie = (Zombie) entity;

                Player player = null;
                if (zombie.getTarget() instanceof Player)
                    player = (Player) zombie.getTarget();

                //Zombies which have respawned already are less likely to respawn
                int respawnCount = entity.getMetadata("extrahardmode.zombie.respawncount").size() > 0 ? entity.getMetadata("extrahardmode.zombie.respawncount").get(0).asInt() : 0;
                respawnCount++;
                zombiesReanimatePercent = (int) ((1.0D / respawnCount) * zombiesReanimatePercent);

                if (!zombie.isVillager() && entity.getFireTicks() < 1 && OurRandom.percentChance(zombiesReanimatePercent))
                {
                    //Save the incremented respawncount
                    entity.setMetadata("extrahardmode.zombie.respawncount", new FixedMetadataValue(plugin, respawnCount));
                    TemporaryBlock tempBlock = null;
                    //Water washes skulls away which then drop to the ground, cancelling the BlockFromToEvent didn't prevent the skull from dropping
                    Material type = entity.getLocation().getBlock().getType();
                    if (placeSkulls && (type != Material.WATER && type != Material.STATIONARY_WATER))
                    {
                        Block block = entity.getLocation().getBlock();
                        //Don't replace blocks that aren't air, but aren't solid either
                        if (block.getType() != Material.AIR)
                        {
                            Location location = block.getLocation();
                            location.setY(location.getY()+1);
                            block = location.getBlock();
                        }
                        block.setType(Material.SKULL);
                        Skull skull = (Skull) block.getState();
                        skull.setSkullType(SkullType.ZOMBIE);
                        //Random rotation
                        BlockFace[] faces = BlockModule.getHorizontalAdjacentFaces();
                        skull.setRotation(faces[OurRandom.nextInt(faces.length)]);
                        skull.update();
                        tempBlock = temporaryBlockHandler.addTemporaryBlock(skull.getLocation(), "respawn_skull");
                    }
                    RespawnZombieTask task = new RespawnZombieTask(plugin, entity.getLocation(), player, tempBlock);
                    int respawnSeconds = plugin.getRandom().nextInt(6) + 3; // 3-8 seconds
                    plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, task, 20L * respawnSeconds); // /20L ~ 1 second
                }
            }
        }
    }


    /**
     * When an Entity is damaged
     * <p/>
     * When a player is damaged by a zombie make him slow
     */
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onEntityDamage(EntityDamageEvent event)
    {
        Entity entity = event.getEntity();
        World world = entity.getWorld();
        final PotionEffectHolder effect = CFG.getPotionEffect(RootNode.ZOMBIES_DEBILITATE_PLAYERS_EFFECT, world.getName());
        final boolean stackEffect = CFG.getBoolean(RootNode.ZOMBIES_DEBILITATE_PLAYERS_EFFECT_STACK, world.getName());
        final int maxEffectAmplifier = CFG.getInt(RootNode.ZOMBIES_DEBILITATE_PLAYERS_EFFECT_STACK_MAX, world.getName());

        if (entity instanceof Player)
        {
            Player player = (Player) entity;

            final boolean zombiesSlowPlayers = CFG.getBoolean(RootNode.ZOMBIES_DEBILITATE_PLAYERS, world.getName());
            final boolean playerBypasses = playerModule.playerBypasses(player, Feature.MONSTER_ZOMBIES);

            // is this an entity damaged by entity event?
            EntityDamageByEntityEvent damageByEntityEvent = null;
            if (event instanceof EntityDamageByEntityEvent)
            {
                damageByEntityEvent = (EntityDamageByEntityEvent) event;
            }

            // FEATURE: zombies can apply a debilitating effect
            if (zombiesSlowPlayers && !playerBypasses)
            {
                if (damageByEntityEvent != null && damageByEntityEvent.getDamager() instanceof Zombie)
                {
                    //TODO EhmZombieSlowEvent
                    if (stackEffect && effect != null && player.hasPotionEffect(effect.getBukkitEffectType()))
                    {
                        int amplifier = 1;
                        for (PotionEffect potion : player.getActivePotionEffects())
                            if (potion.getType().equals(effect.getBukkitEffectType()))
                            {
                                amplifier = potion.getAmplifier();
                                break;
                            }
                        if (amplifier + 1 < maxEffectAmplifier)
                            amplifier++;
                        player.removePotionEffect(effect.getBukkitEffectType());
                        player.addPotionEffect(new PotionEffect(effect.getBukkitEffectType(), effect.getDuration(), amplifier));
                    } else if (effect != null)
                        effect.applyEffect(player, false);
                }
            }
        }
    }


    /** Flag Zombies that have been called in as reinforcements to not respawn */
    @EventHandler
    public void onZombieReinforcements(CreatureSpawnEvent event)
    {
        if (hasReinforcements && event.getEntity() instanceof Zombie && event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.REINFORCEMENTS)
        {
            EntityHelper.flagIgnore(plugin, event.getEntity());
        }
    }


    @EventHandler
    public void onSkullBroken(TemporaryBlockBreakEvent event)
    {
        final int dropPercentage = CFG.getInt(RootNode.ZOMBIE_REANIMATE_SKULLS_DROP_PERCENTAGE, event.getBlock().getLoc().getWorld().getName());
        TemporaryBlock temporaryBlock = event.getBlock();
        Object[] data = temporaryBlock.getData();
        if (data.length == 1 && data[0] instanceof String && data[0].equals("respawn_skull"))
        {
            //Clear item drops: this is the only way
            if (dropPercentage == 0 || !OurRandom.percentChance(dropPercentage))
            {
                event.getBlockBreakEvent().setCancelled(true);
                event.getBlockBreakEvent().getBlock().setType(Material.AIR);
            }
        }
    }
}
