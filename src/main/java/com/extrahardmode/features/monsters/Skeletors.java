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
import com.extrahardmode.events.EhmSkeletonDeflectEvent;
import com.extrahardmode.module.EntityHelper;
import com.extrahardmode.service.ListenerModule;
import com.extrahardmode.service.OurRandom;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.block.Biome;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

/**
 * Changes to Skeletons include:
 * <p/>
 * Immunity to arrows
 */
public class Skeletors extends ListenerModule
{
    /** Configuration */
    private RootConfig CFG;

    private final static String key_knockbackArrow = "ehm.skeletors.knockbackarrow";
    private final static String key_slownessArrow = "ehm.skeletors.slownessArrow";
    private final static String key_fireArrow = "ehm.skeletons.explosionArrows";
    private final static String key_spawnedMinions = "extrahardmode.skeleton.minions";
    private final static String key_totalSpawnedMinions = "extrahardmode.skeleton.minions.totalcount";
    private final static String key_parent = "extrahardmode.minion.parent";
    private final static String key_minionTag = "extrahardmode.skeleton.minion";


    /**
     * A constructor
     *
     * @param plugin owning plugin
     */
    public Skeletors(ExtraHardMode plugin)
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
     * When an entity takes damage
     * <p/>
     * skeletons are immune to arrows
     */
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onPlayerHitByArrow(EntityDamageByEntityEvent event)
    {
        if (event.getEntity() instanceof LivingEntity)
        {
            LivingEntity entity = (LivingEntity) event.getEntity();
            World world = entity.getWorld();

            //final int deflect = CFG.getInt(RootNode.SKELETONS_DEFLECT_ARROWS, world.getName());
            final double knockBackStrength = CFG.getDouble(RootNode.SKELETONS_FIREWORK_KNOCKBACK_VEL, world.getName());
            final int slownessLength = CFG.getInt(RootNode.SKELETONS_SNOWBALLS_SLOW_LEN, world.getName());
            final int fireTicks = CFG.getInt(RootNode.SKELETONS_FIREBALL_PLAYER_FIRETICKS, world.getName());

            if (event.getDamager() instanceof Projectile && event.getEntity() instanceof Player)
            {
                Projectile bullet = (Projectile) event.getDamager();
                // FEATURE: skeletons can knock back
                // knock back target with half the arrow's velocity
                if (bullet.hasMetadata(key_knockbackArrow))
                    entity.setVelocity(bullet.getVelocity().multiply(knockBackStrength));
                else if (bullet.hasMetadata(key_slownessArrow))
                    entity.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, slownessLength, 3));
                else if (bullet.hasMetadata(key_fireArrow))
                {
                    //Allow for a variable amount of fireticks
                    int ticksBefore = entity.getFireTicks() >= 100 ? entity.getFireTicks() - 100 : 0; //fireticks from the arrow are already applied
                    entity.setFireTicks(ticksBefore + fireTicks);
                }
            }
        }
    }


    @EventHandler(ignoreCancelled = true)
    public void onSkeliDamagedByArrow(EntityDamageByEntityEvent event)
    {
        Entity entity = event.getEntity();

        final int deflectPercent = CFG.getInt(RootNode.SKELETONS_DEFLECT_ARROWS, entity.getWorld().getName());

        // FEATURE: arrows pass through skeletons
        if (entity instanceof Skeleton && deflectPercent > 0)
        {
            Entity damageSource = event.getDamager();

            // only arrows
            if (damageSource instanceof Arrow)
            {
                Arrow arrow = (Arrow) damageSource;

                Player player = arrow.getShooter() instanceof Player ? (Player) arrow.getShooter() : null;
                EhmSkeletonDeflectEvent skeliEvent = new EhmSkeletonDeflectEvent(player, (Skeleton) entity, deflectPercent, !plugin.random(deflectPercent));
                plugin.getServer().getPluginManager().callEvent(skeliEvent);

                // percent chance
                if (!skeliEvent.isCancelled())
                {

                    // cancel the damage
                    event.setCancelled(true);

                    // teleport the arrow a single block farther along its flight path, note that .6 and 12 were the unexplained recommended values, for speed and spread, reflectively, in the bukkit wiki
                    arrow.remove();
                    entity.getWorld().spawnArrow(arrow.getLocation().add((arrow.getVelocity().normalize()).multiply(2)), arrow.getVelocity(), 0.6f, 12.0f);
                }
            }
        }
    }


    /**
     * when an entity shoots a bow...
     * <p/>
     * skeletons shoot silverfish
     *
     * @param event - Event that occurred.
     */
    @EventHandler(ignoreCancelled = true)
    public void onShootProjectile(ProjectileLaunchEvent event)
    {
        Location location = event.getEntity().getLocation();
        World world = location.getWorld();

        final boolean snowballs = CFG.getBoolean(RootNode.SKELETONS_SNOWBALLS_ENABLE, world.getName());
        final int snowballsPercent = CFG.getInt(RootNode.SKELETONS_SNOWBALLS_PERCENT, world.getName());

        final boolean fireworks = CFG.getBoolean(RootNode.SKELETONS_FIREWORK_ENABLE, world.getName());
        final int fireworksPercent = CFG.getInt(RootNode.SKELETONS_FIREWORK_PERCENT, world.getName());

        final boolean explosionArrowEnable = CFG.getBoolean(RootNode.SKELETONS_FIREBALL_ENABLE, world.getName());//CFG.getBoolean(RootNode.);
        final int explosionPercent = CFG.getInt(RootNode.SKELETONS_FIREBALL_PERCENTAGE, world.getName());

        final boolean silverfishEnable = CFG.getBoolean(RootNode.SKELETONS_RELEASE_SILVERFISH_ENABLE, world.getName());
        final int silverfishPercent = CFG.getInt(RootNode.SKELETONS_RELEASE_SILVERFISH_PERCENT, world.getName());
        final int currentLimit = CFG.getInt(RootNode.SKELETONS_RELEASE_SILVERFISH_LIMIT, world.getName());
        final int totalLimit = CFG.getInt(RootNode.SKELETONS_RELEASE_SILVERFISH_LIMIT_TOTAL, world.getName());

        // FEATURE: skeletons sometimes release silverfish to attack their targets
        if (event.getEntity() instanceof Arrow && event.getEntity().getShooter() instanceof Skeleton)
        {
            Arrow arrow = (Arrow) event.getEntity();
            Skeleton skeleton = (Skeleton) event.getEntity().getShooter();
            //Slowness Arrows
            if (snowballs && OurRandom.percentChance(snowballsPercent))
            {
                arrow.setMetadata(key_slownessArrow, new FixedMetadataValue(plugin, true));
                Snowball snowball = world.spawn(arrow.getLocation(), Snowball.class);
                snowball.setShooter(arrow.getShooter());
                snowball.setVelocity(arrow.getVelocity());
            }
            //Arrow can have multiple effects
            //Knockback Arrows
            else if (fireworks && OurRandom.percentChance(fireworksPercent))
            {
                arrow.setMetadata(key_knockbackArrow, new FixedMetadataValue(plugin, true));
                Firework peng = world.spawn(arrow.getLocation(), Firework.class);
                peng.setVelocity(arrow.getVelocity());
            }
            //Explosion arrows
            else if (explosionArrowEnable && OurRandom.percentChance(explosionPercent))
            {
                arrow.setMetadata(key_fireArrow, new FixedMetadataValue(plugin, true));
                SmallFireball fireball = world.spawn(arrow.getLocation(), SmallFireball.class);
                fireball.setVelocity(arrow.getVelocity());
                //Silverfish
            } else if (skeleton.getTarget() instanceof Player && silverfishEnable && OurRandom.percentChance(silverfishPercent)) //To prevent tons of Silverfish
            {
                //respect summoning limits
                if (getMinionsSpawnedBySkeli(skeleton, plugin).size() < currentLimit
                        && getTotalMinionsSummonedBySkeli(skeleton, plugin) < totalLimit)
                {
                    // replace arrow with silverfish
                    event.setCancelled(true);

                    // replace with silverfish, quarter velocity of arrow, wants to attack same target as skeleton
                    Creature silverFish = (Creature) skeleton.getWorld().spawnEntity(skeleton.getLocation().add(0.0, 1.5, 0.0), EntityType.SILVERFISH);
                    silverFish.setVelocity(arrow.getVelocity().multiply(0.25));
                    silverFish.setTarget(skeleton.getTarget());

                    EntityHelper.markLootLess(plugin, silverFish); // this silverfish doesn't drop loot
                    setMinion(plugin, silverFish);
                    setParentOfMinion(skeleton, silverFish, plugin);
                    addMinionToSkeli(skeleton, silverFish, plugin);
                }
            }
        }
    }


    @EventHandler(ignoreCancelled = true)
    public void onSilverfishSpawn(CreatureSpawnEvent event)
    {
        final boolean tempFix = CFG.getBoolean(RootNode.SILVERFISH_TEMP_POTION_EFFECT_FIX, event.getLocation().getWorld().getName());
        if (event.getEntityType() == EntityType.SILVERFISH && tempFix)
            event.getEntity().addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, Integer.MAX_VALUE, 1, false));
    }


    /** When a skeleton dies kill all the spawned silverfish aswell */
    @EventHandler
    public void onSkeletonDeath(EntityDeathEvent event)
    {
        final boolean removeSilverfish = CFG.getBoolean(RootNode.SKELETONS_RELEASE_SILVERFISH_KILL, event.getEntity().getWorld().getName());
        if (removeSilverfish && event.getEntity() instanceof Skeleton)
        {
            //Kill all silverfish, but do it slowly as if they are burning up
            for (LivingEntity silverfish : event.getEntity().getWorld().getLivingEntities())
                if (isMinion(silverfish))
                {
                    for (UUID id : getMinionsSpawnedBySkeli(event.getEntity(), plugin))
                    {
                        if (silverfish.getUniqueId() == id)
                        {
                            //silverfish.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 1));
                            silverfish.setFireTicks(Integer.MAX_VALUE);
                            //new SlowKillTask(silverfish, plugin);
                        }
                    }
                }
        }
    }


    @EventHandler
    public void onMinionDeath(EntityDeathEvent event)
    {
        final boolean removeSilverfish = CFG.getBoolean(RootNode.SKELETONS_RELEASE_SILVERFISH_KILL, event.getEntity().getWorld().getName());

        if (removeSilverfish)
        {
            //Remove the silverfish from the spawnned list of silverfish in the skeli
            LivingEntity entity = event.getEntity();
            if (isMinion(entity))
            {
                UUID parent = getParentOfMinion(entity, plugin);

                //Try to find the parent by id
                for (LivingEntity worldEntity : entity.getWorld().getLivingEntities())
                {
                    if (worldEntity.getUniqueId() == parent)
                        removeMinionFromSkeli(entity.getUniqueId(), worldEntity);
                }
            }
        }
    }


    /**
     * Add a minion to the list of minions spawned by this entity
     *
     * @param summoner entity that summoned another entiry
     * @param minion   minion that has been summoned
     * @param plugin   owning plugin for metadata
     */
    @SuppressWarnings("unchecked")
    public static void addMinionToSkeli(LivingEntity summoner, LivingEntity minion, Plugin plugin)
    {
        List<UUID> idList = new ArrayList<UUID>(1);
        //Get minions already set and append the new Minion
        List<MetadataValue> meta = summoner.getMetadata(key_spawnedMinions);
        for (MetadataValue val : meta)
            if (val.getOwningPlugin() == plugin)
                if (val.value() instanceof List)
                    idList = (List<UUID>) val.value();
        idList.add(minion.getUniqueId());
        summoner.setMetadata(key_spawnedMinions, new FixedMetadataValue(plugin, idList));

        //Increment the total count of minions summoned
        int totalCount = getTotalMinionsSummonedBySkeli(summoner, plugin);
        totalCount++;
        summoner.setMetadata(key_totalSpawnedMinions, new FixedMetadataValue(plugin, totalCount));
    }


    /**
     * Remove the minion from the list of summoned minions
     *
     * @param minionId id of the minion
     * @param summoner the entity that summoned the minion
     */
    public static void removeMinionFromSkeli(UUID minionId, LivingEntity summoner)
    {
        List<MetadataValue> meta = summoner.getMetadata(key_spawnedMinions);
        if (!meta.isEmpty())
        {
            MetadataValue value = meta.get(0);
            if (value.value() instanceof List)
            {
                Iterator<UUID> iter = ((List<UUID>) value.value()).iterator();
                while (iter.hasNext())
                {
                    if (minionId == iter.next())
                        iter.remove();
                }
            }
        }
    }


    /**
     * Get all the Ids of the Minions spawned by this CustomSkeleton
     *
     * @param entity entity to get the minions for
     * @param plugin owning plugin to access meta
     *
     * @return Unique ids of all minions that have been spawned (minions can be dead)
     */
    @SuppressWarnings("unchecked")
    public static List<UUID> getMinionsSpawnedBySkeli(LivingEntity entity, Plugin plugin)
    {
        List<MetadataValue> meta = entity.getMetadata(key_spawnedMinions);
        List<UUID> ids = new ArrayList<UUID>(meta.size());
        for (MetadataValue val : meta)
            if (val.getOwningPlugin() == plugin)
                if (val.value() instanceof List)
                    ids = (List<UUID>) val.value();
        return ids;
    }


    /**
     * Get the total number of minions summoned by this entity
     *
     * @param entity entity to get minion count
     * @param plugin owning plugin to access MetaData
     *
     * @return count or 0 if not set
     */
    public static int getTotalMinionsSummonedBySkeli(LivingEntity entity, Plugin plugin)
    {
        List<MetadataValue> meta = entity.getMetadata(key_totalSpawnedMinions);
        int totalCount = 0;
        if (!meta.isEmpty())
        {
            MetadataValue value = meta.get(0);
            if (value.value() instanceof Integer)
                totalCount = (Integer) value.value();
        }
        return totalCount;
    }


    public static void setMinion(Plugin plugin, LivingEntity entity)
    {
        entity.setMetadata(key_minionTag, new FixedMetadataValue(plugin, true));
    }


    public static boolean isMinion(LivingEntity entity)
    {
        return entity.hasMetadata(key_minionTag);
    }


    /**
     * Set the parent that summoned this minion
     *
     * @param summoner parent summoner
     * @param minion   summoned minion
     * @param owning   plugin that spawned the minion
     */
    public static void setParentOfMinion(LivingEntity summoner, LivingEntity minion, Plugin owning)
    {
        minion.setMetadata(key_parent, new FixedMetadataValue(owning, summoner.getUniqueId()));
    }


    /**
     * Get the parent that summoned the minion
     *
     * @param minion minion to get the parent for
     * @param plugin owning plugin for MetaData
     *
     * @return id of parent or id of minion if parent not set
     */
    public static UUID getParentOfMinion(LivingEntity minion, Plugin plugin)
    {
        List<MetadataValue> meta = minion.getMetadata(key_parent);
        if (!meta.isEmpty())
        {
            MetadataValue metaVal = meta.get(0);
            if (metaVal.value() instanceof UUID)
                return (UUID) metaVal.value();
        }
        return minion.getUniqueId();
    }
    /**
     * When an Entity spawns: Spawn a Skeleton sometimes instead of a EnderMan in the end.
     *
     * @param event which occurred
     */
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onEntitySpawn(CreatureSpawnEvent event)
    {
        LivingEntity entity = event.getEntity();
        if (EntityHelper.isMarkedAsOurs(entity))
            return;
        Location location = event.getLocation();
        World world = location.getWorld();
        EntityType entityType = entity.getType();

        final int cavespiderSpawnPercent = CFG.getInt(RootNode.BONUS_SKELETON_SPAWN_PERCENT, world.getName());

        // FEATURE: Skeletons spawns naturally in The End.
        if (entityType == EntityType.ENDERMAN && world.getEnvironment() == World.Environment.THE_END
                && event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.NATURAL)
        {
            if (plugin.random(cavespiderSpawnPercent))
            {
                event.setCancelled(true);
                EntityHelper.spawn(location, EntityType.SKELETON);
            }
        }
    }
}