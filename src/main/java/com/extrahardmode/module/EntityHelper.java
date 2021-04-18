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


package com.extrahardmode.module;


import com.extrahardmode.compatibility.CompatHandler;
import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.projectiles.ProjectileSource;

import java.util.List;
import java.util.Random;

/** Module that contains logic dealing with entities. */
public class EntityHelper
{

    /** Getter for environmental damage for the specified entity */
    private static final String IGNORE = "extrahardmode.ignore.me";

    /** Getter to set a flag to ignore a entity in further processing */
    private static final String ENVIRONMENTAL_DAMAGE = "extrahard_environmentalDamage";

    /** Process this Entity */
    private static final String PROCESS_ENTITY = "extrahardmode_process_entity";

    /** Our Entity - created by us */
    private static final String OURS = "extrahardmode_our_entity";


    /**
     * Marks an entity so that the plugin can remember not to drop loot or experience if it's killed.
     *
     * @param entity - Entity to modify.
     */
    public static void markLootLess(Plugin plugin, LivingEntity entity)
    {
        entity.setMetadata(ENVIRONMENTAL_DAMAGE, new FixedMetadataValue(plugin, entity.getMaxHealth()));
    }


    /**
     * Tracks total environmental damage done to an entity
     *
     * @param entity - Entity to check.
     * @param damage - Amount of damage.
     */
    public static void addEnvironmentalDamage(Plugin plugin, LivingEntity entity, double damage)
    {
        double currentTotalDamage = 0.0;
        List<MetadataValue> meta = entity.getMetadata(ENVIRONMENTAL_DAMAGE);
        if (meta.size() > 0)
            currentTotalDamage = meta.get(0).asDouble();
        entity.setMetadata(ENVIRONMENTAL_DAMAGE, new FixedMetadataValue(plugin, currentTotalDamage + damage));
    }


    /**
     * Checks whether an entity should drop items when it dies
     *
     * @param entity - Entity to check.
     *
     * @return True if the entity is lootable, else false.
     */
    public static boolean isLootLess(LivingEntity entity)
    {
        double currentTotalDamage = 0.0;
        List<MetadataValue> meta = entity.getMetadata(ENVIRONMENTAL_DAMAGE);
        if (meta.size() > 0)
            currentTotalDamage = meta.get(0).asDouble();
        // wither is exempt. he can't be farmed because creating him requires combining non-farmable components
        return !(entity instanceof Wither) && (currentTotalDamage > entity.getMaxHealth() / 2.0);
    }


    /**
     * Clears any webbing which may be trapping this entity (assumes two-block-tall entity)
     *
     * @param entity - Entity to help.
     */
    public static void clearWebbing(Entity entity)
    {
        Block feetBlock = entity.getLocation().getBlock();
        Block headBlock = feetBlock.getRelative(BlockFace.UP);

        Block[] blocks = {feetBlock, headBlock};
        for (Block block : blocks)
        {
            if (block.getType() == Material.COBWEB)
            {
                block.setType(Material.AIR);
            }
        }
    }


    /** Flag an entity to be ignored in further processing. E.g if an event could be called multiple times */
    public static void flagIgnore(Plugin plugin, Entity entity)
    {
        if (entity != null)
            entity.setMetadata(IGNORE, new FixedMetadataValue(plugin, true));
    }


    /** Check if an entity has been flagged to be ignored */
    public static boolean hasFlagIgnore(Entity entity)
    {
        return entity != null && entity.hasMetadata(IGNORE);
    }


    /** Mark an Entity to be processed. E.g when only a small number of Entities should be processed */
    public static void markForProcessing(Plugin plugin, Entity entity)
    {
        Validate.notNull(entity, "Entity can't be null");
        {
            entity.setMetadata(PROCESS_ENTITY, new FixedMetadataValue(plugin, true));
        }
    }


    /** Check if an entity has been flagged to be processed */
    public static boolean isMarkedForProcessing(Entity entity)
    {
        Validate.notNull(entity, "Entity can't be null");
        List<MetadataValue> meta = entity.getMetadata(PROCESS_ENTITY);

        return entity.hasMetadata(PROCESS_ENTITY) && meta != null;
    }


    /** Mark an Entity to be processed. E.g when only a small number of Entities should be processed */
    public static void markAsOurs(Plugin plugin, Entity entity)
    {
        Validate.notNull(entity, "Entity can't be null");
        {
            entity.setMetadata(OURS, new FixedMetadataValue(plugin, true));
        }
    }


    /** Check if an entity has been flagged to be processed */
    public static boolean isMarkedAsOurs(Entity entity)
    {
        Validate.notNull(entity, "Entity can't be null");
        List<MetadataValue> meta = entity.getMetadata(PROCESS_ENTITY);

        return entity.hasMetadata(OURS) && meta != null;
    }


    /** Is the Monster farmable cattle, which drops something on death? */
    public static boolean isCattle(Entity entity)
    {
        return entity instanceof Cow
                || entity instanceof Chicken
                || entity instanceof Pig;
    }


    /** Simple check if there is enough space for a monster to spawn */
    public static boolean simpleIsLocSafeSpawn(Location loc)
    {
        //quickly check if 2 blocks above this is clear
        Block oneAbove = loc.getBlock();
        Block twoAbove = oneAbove.getRelative(BlockFace.UP, 1);
        return oneAbove.getType().equals(Material.AIR) && twoAbove.getType().equals(Material.AIR);
    }


    /**
     * Checks if Location is safe, if in air will return the a valid Block to spawn on or null
     *
     * @return valid Block or null if no valid Block
     */
    public static Location isLocSafeSpawn(Location location)
    {
        Block playerBlock = location.getBlock();

        // the playerBlock should always be air, but if the player stands on a slab he actually is in the slab, checking a few blocks under because player could have jumped etc..
        if (playerBlock.getType().equals(Material.AIR))
        {
            for (int i = 0; i <= 3; i++)
            {
                playerBlock = location.getBlock().getRelative(BlockFace.DOWN, 1);

                if (playerBlock.getType().equals(Material.AIR))
                {
                    location.subtract(0, 1, 0);
                    playerBlock = location.getBlock();
                    // the playerBlock is now the block where the monster
                    // should spawn on, next up: verify block
                } else
                {
                    break;
                }
            }
        }
        // no spawning on steps, stairs and transparent blocks
        if (playerBlock.getType().name().endsWith("STEP") || playerBlock.getType().name().endsWith("STAIRS")
                || playerBlock.getType().isTransparent() || !playerBlock.getType().isOccluding() || playerBlock.getType().equals(Material.AIR))
        {
            // don't spawn here
            return null;
        }

        return location;
    }


    /**
     * Spawn Monsters with their gear, use instead of world.spawn(), do not spawn Entities which are not LivingEntities
     *
     * @return a reference to the spawned Entity, might be dead if the monster can't spawn in that location or null if the EntityType was not a LivingEntity
     */
    public static LivingEntity spawn(Location loc, EntityType type)
    {
        LivingEntity entity = null;
        {
            Entity ent = loc.getWorld().spawnEntity(loc, type);
            if (ent instanceof LivingEntity)
                entity = (LivingEntity) ent;
        }
        if (entity != null)
            switch (type)
            {
                case SKELETON:
                    entity.getEquipment().setItemInHand(new ItemStack(Material.BOW));
                    break;
                case ZOMBIFIED_PIGLIN:
                    entity.getEquipment().setItemInHand(new ItemStack(Material.GOLDEN_SWORD));
                    break;
                case ENDER_DRAGON:
                    entity.setAI(true);
                    EnderDragon dragon = (EnderDragon)entity;
                    dragon.setPhase(EnderDragon.Phase.CIRCLING);
            }
        if (entity != null && CompatHandler.canMonsterSpawn(loc))
            entity.remove();
        return entity;
    }


    /** Spawns a random monster with the probabilities given by the config */
    public static Entity spawnRandomMob(Location loc)
    {
        int randomMonster = new Random().nextInt(90);
        EntityType monsterType;

        // decide which kind and how many monsters are more or less evenly distributed
        if (randomMonster < 5)
        {
            monsterType = EntityType.SILVERFISH; /*5%*/
        } else if (randomMonster < 25)
        {
            monsterType = EntityType.SKELETON;   /*20%*/
        } else if (randomMonster < 45)
        {
            monsterType = EntityType.ZOMBIE;     /*20%*/
        } else if (randomMonster < 65)
        {
            monsterType = EntityType.CREEPER;    /*20%*/
        } else
        {
            monsterType = EntityType.SPIDER;     /*25%*/
        }

        return spawn(loc, monsterType);
    }


    /**
     * Test if there is one ore more Players near a Location. This method should be used to check if the distance between players and spawned mobs is big enough.
     *
     * @param loc      location around which to check
     * @param distance distance around the location to check for players
     *
     * @return false if no players found, true if there where one or more players
     */
    public static boolean arePlayersNearby(Location loc, double distance)
    {
        double squared = Math.pow(distance, 2.0);
        List<Player> otherEntities = loc.getWorld().getPlayers();
        for (Player player : otherEntities)
        {
            //if (player.getLocation().getWorld() != loc.getWorld()) //Perhaps in the rare case of an async player teleport? This shouldn't ever be true but there was a bug report on it :S
                //continue;
            double playerDist = player.getLocation().distanceSquared(loc);
            if (playerDist < squared)
                return true;
        }
        return false;
    }

    /**
     * Computes the EntityType of the given Projectile's shooter, so we can add damage or effect to impacts, nerf things, etc.
     * @param projectile
     * @return
     */
    public static EntityType shooterType(Projectile projectile) {
        ProjectileSource source = projectile.getShooter();
        if ((source instanceof LivingEntity) == false) {
            return EntityType.UNKNOWN;
        }

        LivingEntity entity = (LivingEntity) source;
        return entity.getType();
    }

}
