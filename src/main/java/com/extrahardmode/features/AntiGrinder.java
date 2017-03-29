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
import com.extrahardmode.module.EntityHelper;
import com.extrahardmode.service.ListenerModule;
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
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;

/** A MonsterGrinder Inhibitor which disables drops for Monsters which appear to be farmed or which have been killed in conditions where the Player had a clear advantage */
public class AntiGrinder extends ListenerModule
{
    private RootConfig CFG;

    private BlockModule blockModule;


    /**
     * For Testing Purposes
     * <p/>
     * Dependency Injection Constructor
     *
     * @param plugin      plugin instance
     * @param CFG         instantiated RootConfig
     * @param blockModule BlockModule
     */
    public AntiGrinder(ExtraHardMode plugin, RootConfig CFG, BlockModule blockModule)
    {
        super(plugin);
        this.CFG = CFG;
        this.blockModule = blockModule;
    }


    /** Your basic constructor of choice */
    public AntiGrinder(ExtraHardMode plugin)
    {
        super(plugin);
    }


    @Override
    public void starting()
    {
        super.starting();
        CFG = plugin.getModuleForClass(RootConfig.class);
        blockModule = plugin.getModuleForClass(BlockModule.class);
    }


    /**
     * When an Animal/Monster spawns check if the Location is "natural"
     *
     * @return true succeeded and false if cancelled or marked lootless
     */
    @EventHandler(priority = EventPriority.LOW)
    public boolean onEntitySpawn(CreatureSpawnEvent event)
    {
        Location location = event.getLocation();
        World world = location.getWorld();
        LivingEntity entity = event.getEntity();
        CreatureSpawnEvent.SpawnReason reason = event.getSpawnReason();

        final boolean inhibitMonsterGrindersEnabled = CFG.getBoolean(RootNode.INHIBIT_MONSTER_GRINDERS, world.getName());

        // FEATURE: inhibited monster grinders/farms
        if (inhibitMonsterGrindersEnabled && entity instanceof Monster)
        {
            switch (reason)
            {
                case SPAWNER:
                {
                    // Block all Spawner drops completely
                    EntityHelper.markLootLess(plugin, entity);
                    return false;
                }
                case NATURAL:
                case VILLAGE_INVASION:
                {
                    // consider environment to stop monsters from spawning in non-natural places
                    World.Environment environment = location.getWorld().getEnvironment();

                    Material underBlockType = location.getBlock().getRelative(BlockFace.DOWN).getType();
                    switch (environment)
                    {
                        case NORMAL:
                            if (!blockModule.isNaturalSpawnMaterial(underBlockType))
                            {
                                event.setCancelled(true);
                                return false;
                            }
                            break;
                        case NETHER:
                            if (!blockModule.isNaturalNetherSpawn(underBlockType))
                            {
                                event.setCancelled(true);
                                return false;
                            }
                            break;
                        case THE_END:
                            if (underBlockType != Material.ENDER_STONE && underBlockType != Material.OBSIDIAN && underBlockType != Material.AIR/*dragon*/)
                            {
                                event.setCancelled(true);
                                return false;
                            }
                            break;
                        default:
                            break;
                    }
                }
            }
        }
        return true;
    }


    /**
     * When an entity dies check if loot should be blocked due to AntiGrinder
     *
     * @return true if drops loot, false if loot was blocked
     */
    @EventHandler
    public boolean onEntityDeath(EntityDeathEvent event)
    {
        LivingEntity entity = event.getEntity();
        World world = entity.getWorld();

        final boolean inhibitMonsterGrindersEnabled = CFG.getBoolean(RootNode.INHIBIT_MONSTER_GRINDERS, world.getName());

        // FEATURE: monsters which take environmental damage or spawn from spawners don't drop loot and exp (monster grinder inhibitor)
        if (inhibitMonsterGrindersEnabled && entity instanceof Monster)
        {
            if (EntityHelper.isLootLess(entity))
            {
                clearDrops(event);
                return false;
            }
            else
            {   //Evaluate if this kill was a too easy kill
                switch (entity.getType())
                {
                    case WITHER:
                    case GUARDIAN:
                    case ELDER_GUARDIAN:
                        return true;
                    case SKELETON:
                    case ENDERMAN:
                    {
                        // tall monsters can get stuck when they spawn like WitherSkeletons
                        if (entity.getEyeLocation().getBlock().getType() != Material.AIR)
                            return clearDrops(event);
                        break;
                    }
                    default:
                    {
                        // no loot for monsters which die standing in water, to make building grinders even more difficult
                        Block block = entity.getLocation().getBlock();
                        Block underBlock = block.getRelative(BlockFace.DOWN);

                        BlockFace[] adjacentFaces = blockModule.getHorizontalAdjacentFaces();
                        Block[] adjacentBlocks = new Block[adjacentFaces.length * 2 + 1];

                        //All Blocks directly surrounding the Monster
                        adjacentBlocks[0] = block;
                        for (int i = 0; i < adjacentFaces.length; i++)
                        {
                            adjacentBlocks[i + 1] = block.getRelative(adjacentFaces[i]);
                        }
                        for (int i = 0; i < adjacentFaces.length; i++)
                        {
                            adjacentBlocks[i + adjacentFaces.length] = underBlock.getRelative(adjacentFaces[i]);
                        }

                        for (Block adjacentBlock : adjacentBlocks)
                        {
                            if (adjacentBlock != null && (adjacentBlock.getType() == Material.WATER || adjacentBlock.getType() == Material.STATIONARY_WATER))
                                return clearDrops(event);
                        }

                        // also no loot for monsters who can't reach their (melee) killers
                        Player killer = entity.getKiller();
                        if (killer != null)
                        {
                            Location monsterEyeLocation = entity.getEyeLocation();
                            Location playerEyeLocation = killer.getEyeLocation();

                            // interpolate locations
                            Location[] locations = new Location[]{
                                    new Location(monsterEyeLocation.getWorld(), 0.2 * monsterEyeLocation.getX() + 0.8 * playerEyeLocation.getX(),
                                            monsterEyeLocation.getY(), 0.2 * monsterEyeLocation.getZ() + 0.8 * playerEyeLocation.getZ()),
                                    new Location(monsterEyeLocation.getWorld(), 0.5 * monsterEyeLocation.getX() + 0.5 * playerEyeLocation.getX(),
                                            monsterEyeLocation.getY(), 0.5 * monsterEyeLocation.getZ() + 0.5 * playerEyeLocation.getZ()),
                                    new Location(monsterEyeLocation.getWorld(), 0.8 * monsterEyeLocation.getX() + 0.2 * playerEyeLocation.getX(),
                                            monsterEyeLocation.getY(), 0.8 * monsterEyeLocation.getZ() + 0.2 * playerEyeLocation.getZ()),};

                            for (Location middleLocation : locations)
                            {
                                // monster is blocked at eye level, unable to advance toward killer
                                if (middleLocation.getBlock().getType() != Material.AIR)
                                    return clearDrops(event);
                                    // monster doesn't have room above to hurdle a foot level block, unable to advance toward killer
                                else
                                {
                                    Block bottom = middleLocation.getBlock().getRelative(BlockFace.DOWN);
                                    Block top = middleLocation.getBlock().getRelative(BlockFace.UP);
                                    if (top.getType() != Material.AIR &&
                                            bottom.getType() != Material.AIR
                                            || bottom.getType() == Material.FENCE
                                            || bottom.getType() == Material.FENCE_GATE
                                            || bottom.getType() == Material.COBBLE_WALL
                                            || bottom.getType() == Material.NETHER_FENCE)
                                    {
                                        return clearDrops(event);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return true;
    }


    /**
     * Utility method to clear the drops
     *
     * @return false which means that the drops have been cleared
     */
    private boolean clearDrops(EntityDeathEvent event)
    {
        event.setDroppedExp(0);
        event.getDrops().clear();
        return false;
    }


    /**
     * When an entity takes damage
     * <p/>
     * check if the damage is environmental or from a player
     */
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onEntityDamage(EntityDamageEvent event)
    {
        Entity entity = event.getEntity();
        World world = entity.getWorld();

        final boolean inhibitMonsterGrindersEnabled = CFG.getBoolean(RootNode.INHIBIT_MONSTER_GRINDERS, world.getName());

        // FEATURE: monsters which take environmental damage don't drop loot or experience (monster grinder inhibitor)
        if (inhibitMonsterGrindersEnabled && entity instanceof LivingEntity)
        {
            EntityDamageEvent.DamageCause damageCause = event.getCause();
            if (damageCause != EntityDamageEvent.DamageCause.ENTITY_ATTACK && damageCause != EntityDamageEvent.DamageCause.PROJECTILE && damageCause != EntityDamageEvent.DamageCause.BLOCK_EXPLOSION)
            {
                EntityHelper.addEnvironmentalDamage(plugin, (LivingEntity) entity, event.getDamage());
            }
            //Prevent people from using dogs to grind monsters
            else if (event instanceof EntityDamageByEntityEvent)
            {
                EntityDamageByEntityEvent byEntityEvent = (EntityDamageByEntityEvent) event;
                if (byEntityEvent.getDamager() instanceof Wolf)
                {
                    EntityHelper.addEnvironmentalDamage(plugin, (LivingEntity) entity, event.getDamage());
                }
            }
        }
    }

}
