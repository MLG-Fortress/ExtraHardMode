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
import com.extrahardmode.config.ExplosionType;
import com.extrahardmode.config.RootConfig;
import com.extrahardmode.config.RootNode;
import com.extrahardmode.events.fakeevents.FakeEntityExplodeEvent;
import com.extrahardmode.module.BlockModule;
import com.extrahardmode.module.UtilityModule;
import com.extrahardmode.service.ListenerModule;
import com.extrahardmode.task.CreateExplosionTask;
import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/** Various changes to Explosions including: */
public class Explosions extends ListenerModule
{
    private RootConfig CFG;

    private BlockModule blockModule;

    private final String tag = "extrahardmode.explosion.fallingblock";


    //     ___ ___  _  _ ___ _____ ___ _   _  ___ _____ ___  ___
    //    / __/ _ \| \| / __|_   _| _ \ | | |/ __|_   _/ _ \| _ \
    //   | (_| (_) | .` \__ \ | | |   / |_| | (__  | || (_) |   /
    //    \___\___/|_|\_|___/ |_| |_|_\\___/ \___| |_| \___/|_|_\
    //
    public Explosions(ExtraHardMode plugin)
    {
        super(plugin);
    }


    //    ___ _____ _   ___ _____ ___ _  _  ___
    //   / __|_   _/_\ | _ \_   _|_ _| \| |/ __|
    //   \__ \ | |/ _ \|   / | |  | || .` | (_ |
    //   |___/ |_/_/ \_\_|_\ |_| |___|_|\_|\___|
    //
    @Override
    public void starting()
    {
        super.starting();
        CFG = plugin.getModuleForClass(RootConfig.class);
        blockModule = plugin.getModuleForClass(BlockModule.class);
    }


    /**
     * Regular listener:
     * Bigger (custom) explosions
     */
    //    ___ ___ ___ _   _ _      _   ___   _    ___ ___ _____ ___ _  _ ___ ___
    //   | _ \ __/ __| | | | |    /_\ | _ \ | |  |_ _/ __|_   _| __| \| | __| _ \
    //   |   / _| (_ | |_| | |__ / _ \|   / | |__ | |\__ \ | | | _|| .` | _||   /
    //   |_|_\___\___|\___/|____/_/ \_\_|_\ |____|___|___/ |_| |___|_|\_|___|_|_\
    //
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH) //RoboMWM - High priority to allow plugins to clear the blocklist if they so choose
    public void regularExplosions(EntityExplodeEvent event)
    {
        if (event instanceof FakeEntityExplodeEvent || !(event.getEntity() instanceof Ghast || event.getEntity() instanceof TNTPrimed))
            return;

        final Entity sourceEntity = event.getEntity();
        final World world = event.getLocation().getWorld();
        final Location location = sourceEntity.getLocation();

        final boolean flyOtherPlugins = CFG.getBoolean(RootNode.EXPLOSIONS_FYLING_BLOCKS_ENABLE_OTHER, world.getName());
        final boolean customGhastExplosion = CFG.getBoolean(RootNode.EXPLOSIONS_GHASTS_ENABLE, world.getName());
        final boolean customTntExplosion = CFG.getBoolean(RootNode.EXPLOSIONS_TNT_ENABLE, world.getName());
        final boolean multipleExplosions = CFG.getBoolean(RootNode.BETTER_TNT, world.getName());
        //cancel explosion if no worldDamage should be done
        final boolean tntWorldDamage = event.getLocation().getBlockY() > CFG.getInt(RootNode.EXPLOSIONS_Y, world.getName())
                ? CFG.getBoolean(RootNode.EXPLOSIONS_TNT_ABOVE_WORLD_GRIEF, world.getName())
                : CFG.getBoolean(RootNode.EXPLOSIONS_TNT_BELOW_WORLD_GRIEF, world.getName());

        // TNT
        if (sourceEntity instanceof TNTPrimed)
        {
            if (customTntExplosion && event.blockList().size() > 0 && (flyOtherPlugins || event.getYield() == 0.25)) //getYield value of 0.25 somewhat ensures this is a vanilla TNT explosion.
            {
                if (!multipleExplosions)
                {
                    CreateExplosionTask explosionTask = new CreateExplosionTask(plugin, location, ExplosionType.TNT, sourceEntity);
                    plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, explosionTask, 1L);
                } else //multiple explosions will also handle the custom size
                {
                    multipleExplosions(location, sourceEntity, ExplosionType.TNT);
                }

                if (!tntWorldDamage && CFG.isEnabledIn(world.getName()))
                    event.setCancelled(true);
            }
        }

        // GHASTS
        else if (sourceEntity instanceof Fireball)
        {
            if (customGhastExplosion)
            {
                Fireball fireball = (Fireball) sourceEntity;
                if (fireball.getShooter() instanceof Ghast)
                {
                    event.setCancelled(true);
                    // same as vanilla TNT, plus fire
                    new CreateExplosionTask(plugin, sourceEntity.getLocation(), ExplosionType.GHAST_FIREBALL, sourceEntity).run();
                }
            }
        }
    }


    /**
     * This gets called late so we know the explosion has been allowed
     *
     * @param event event that occurred
     */
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST) //so it probably got cancelled already
    public void onLateExplosion(EntityExplodeEvent event)
    {
        if (event instanceof FakeEntityExplodeEvent)
            return;

        final Entity sourceEntity = event.getEntity();
        final World world = event.getLocation().getWorld();
        final String worldName = world.getName();
        final Location location = event.getLocation();
        final Collection<Block> blocks = event.blockList();

        final boolean flyingBlocks = CFG.getBoolean(RootNode.EXPLOSIONS_FYLING_BLOCKS_ENABLE, world.getName());

        final boolean flyOtherPlugins = CFG.getBoolean(RootNode.EXPLOSIONS_FYLING_BLOCKS_ENABLE_OTHER, worldName);
        final int flyPercentage = CFG.getInt(RootNode.EXPLOSIONS_FLYING_BLOCKS_PERCENTAGE, worldName);
        final double upVel = CFG.getDouble(RootNode.EXPLOSIONS_FLYING_BLOCKS_UP_VEL, worldName);
        final double spreadVel = CFG.getDouble(RootNode.EXPLOSIONS_FLYING_BLOCKS_SPREAD_VEL, worldName);

        // PHYSICS
        if (flyingBlocks && (flyOtherPlugins || sourceEntity != null))
        {
            applyExplosionPhysics(blocks, location, flyPercentage, upVel, spreadVel);

            if (CFG.getBoolean(RootNode.MORE_FALLING_BLOCKS_ENABLE, worldName))
            {
                blockModule.physicsCheck(location.add(0, 5, 0).getBlock(), 5, true, 3); //loosen ceiling
                blockModule.physicsCheck(location.add(0, -3, 0).getBlock(), 5, true, 6); //ground loosen
            }
        }
    }


    //RoboMWM - remove "compatibility" (see com.extrahardmode.module.ExplosionCompatStorage for more details)
    /*
    /**
     * Provide compatibility for block protection and logging plugins.
     * <pre>
     *     1. call world.createExplosion()
     *     2. save responsible entity and location with {@link com.extrahardmode.module.ExplosionCompatStorage}
     *     --> our code starts here
     *     3. compare location and call an additional {@link com.extrahardmode.events.fakeevents.FakeEntityExplodeEvent}
     *     with blocklist() from the original Event
     *     4. in any case the actual event gets cancelled,
     *     so it doesn't get logged twice and doesn't get processed any further
     *     5. this code will break blocks manually as a workaround,
     *     because the actual explosion that would break the blocks has to be cancelled
     * </pre>
     *
    //     ___ ___  __  __ ___  _ _____ ___ ___ ___ _    ___ _______   __
    //    / __/ _ \|  \/  | _ \/_\_   _|_ _| _ )_ _| |  |_ _|_   _\ \ / /
    //   | (_| (_) | |\/| |  _/ _ \| |  | || _ \| || |__ | |  | |  \ V /
    //    \___\___/|_|  |_|_|/_/ \_\_| |___|___/___|____|___| |_|   |_|
    //

    @EventHandler(priority = EventPriority.LOWEST)
    public void provideCompatibility(EntityExplodeEvent event)
    {
        if (event instanceof FakeEntityExplodeEvent)
            return;
        //Only (our) custom explosions have no entity
        if (event.getEntity() == null)
        {
            ExplosionCompatStorage explosionStorage = plugin.getModuleForClass(ExplosionCompatStorage.class);
            if (!explosionStorage.queueEmpty())
            {
                //Just make sure that this explosion is in fact from us
                Location savedLoc = explosionStorage.getCenterLocation();
                Location eventLoc = event.getLocation();
                if (savedLoc.getBlockX() == eventLoc.getBlockX() && savedLoc.getBlockY() == eventLoc.getBlockY() && savedLoc.getBlockZ() == eventLoc.getBlockZ())
                {
                    // There is no way for us to pass the actual cause (the entity) of an explosion to bukkit other than this additional event
                    FakeEntityExplodeEvent compatEvent = new FakeEntityExplodeEvent(explosionStorage.getExplosionCause(), explosionStorage.getCenterLocation(), event.blockList(), event.getYield());
                    plugin.getServer().getPluginManager().callEvent(compatEvent);

                    if (compatEvent.isCancelled())
                        //We cancel the event because we only want the event with the correct Entity to be logged
                        event.setCancelled(true);
                    else //do our additional processing
                        explosionLogic(compatEvent);

                    //Some plugins might decide to clear the blocklist instead of cancelling the event, in that case the modified blocklist is the same
                    //Handle blockbreaking and setting fire ourselves
                    for (Block block : event.blockList())
                        switch (block.getType())
                        {
                            case FIRE:
                                //block.setType(Material.FIRE); do nuthing
                                break;
                            case AIR:
                                break; //dunno why some plugins log breaking of air :D
                            default:
                                block.breakNaturally();
                        }
                    List<Block> copy = new ArrayList<Block>(event.blockList());
                    event.blockList().clear(); //we don't want this event to be recorded, but we still want the explosion particles
                    compatEvent.blockList().addAll(copy);

                    explosionStorage.clearQueue();
                }
            }
        }
    }
    */


    //    _      _   _  _ ___ ___ _  _  ___   ___ _    ___   ___ _  _____
    //   | |    /_\ | \| |   \_ _| \| |/ __| | _ ) |  / _ \ / __| |/ / __|
    //   | |__ / _ \| .` | |) | || .` | (_ | | _ \ |_| (_) | (__| ' <\__ \
    //   |____/_/ \_\_|\_|___/___|_|\_|\___| |___/____\___/ \___|_|\_\___/
    //
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true) //so we are last and if a block protection plugin cancelled the event we know it
    public void handleLandedBlocksFromPhysics(EntityChangeBlockEvent event)
    {
        final int distance = (int) Math.pow(CFG.getInt(RootNode.EXPLOSIONS_FLYING_BLOCKS_AUTOREMOVE_RADIUS, event.getBlock().getWorld().getName()), 2);
        if (event.getEntity() instanceof FallingBlock)
        {
            Block block = event.getBlock();
            FallingBlock fallBaby = (FallingBlock) event.getEntity();
            if (fallBaby.hasMetadata(tag))
            {
                Object obj = fallBaby.getMetadata(tag).size() > 0 ? fallBaby.getMetadata(tag).get(0).value() : null;
                if (obj instanceof Location)
                {
                    Location loc = (Location) obj;
                    //Compare the distance to the original explosion, dont place block if the block landed far away (dont make landscape ugly)
                    if (event.getBlock().getLocation().distanceSquared(loc) > distance)
                    {
                        event.setCancelled(true);
                        fallBaby.remove();
                    }
                    //If close place the block as if the player broke it first: stone -> cobble, gras -> dirt etc.
                    else
                    {
                        Material type = BlockModule.getDroppedMaterial(fallBaby.getBlockData().getMaterial());
                        if (type.isBlock() && type == fallBaby.getBlockData().getMaterial()) //preserve blockdata. See issue #69 //Alternatively could block#setType in second condition
                            return;
                        else if (type.isBlock())
                            block.setType(type);
                        else //if block doesnt drop something that can be placed again... thin glass, redstone ore
                            block.setType(Material.AIR);
                        event.setCancelled(true);
                    }
                }
            }
        }
    }


    /**
     * This method only gets called by us once we have evaluated that we are allowed to trigger an explosion
     *
     * @param event event that occurred
     */
    public void explosionLogic(EntityExplodeEvent event)
    {
        Validate.notNull(event.getEntity(), "Entity was null [explosionLogic], but shouldn't be! x:" + event.getLocation().getBlockX() + " y:" + event.getLocation().getBlockY() + " z:" + event.getLocation().getBlockZ());
        final Entity sourceEntity = event.getEntity();
        final World world = event.getLocation().getWorld();
//        final String worldName = world.getName();
//        final Location location = sourceEntity.getLocation();
        final Collection<Block> blocks = event.blockList();

//        final boolean flyingBlocks = CFG.getBoolean(RootNode.EXPLOSIONS_FYLING_BLOCKS_ENABLE, world.getName());

        final boolean turnStoneToCobble = CFG.getBoolean(RootNode.EXPLOSIONS_TURN_STONE_TO_COBLE, world.getName());

//        final boolean flyOtherPlugins = CFG.getBoolean(RootNode.EXPLOSIONS_FYLING_BLOCKS_ENABLE_OTHER, worldName);
//        final int flyPercentage = CFG.getInt(RootNode.EXPLOSIONS_FLYING_BLOCKS_PERCENTAGE, worldName);
//        final double upVel = CFG.getDouble(RootNode.EXPLOSIONS_FLYING_BLOCKS_UP_VEL, worldName);
//        final double spreadVel = CFG.getDouble(RootNode.EXPLOSIONS_FLYING_BLOCKS_SPREAD_VEL, worldName);


        if (sourceEntity instanceof Creeper || sourceEntity instanceof TNTPrimed)
        {
            event.setYield(1); //so people have enough blocks to fill creeper holes and because TNT explodes multiple times
        }

        // FEATURE: in hardened stone mode, TNT only softens stone to cobble
        if (turnStoneToCobble) //
        {
            changeBlockTypes(blocks, Material.STONE, Material.COBBLESTONE);
        }

    }

    //    ___ ___ _   _ ___   _   ___ _    ___   ___ _   _ _  _  ___ _____ ___ ___  _  _ ___  ______
    //   | _ \ __| | | / __| /_\ | _ ) |  | __| | __| | | | \| |/ __|_   _|_ _/ _ \| \| / __| \ \ \ \
    //   |   / _|| |_| \__ \/ _ \| _ \ |__| _|  | _|| |_| | .` | (__  | |  | | (_) | .` \__ \  > > > >
    //   |_|_\___|\___/|___/_/ \_\___/____|___| |_|  \___/|_|\_|\___| |_| |___\___/|_|\_|___/ /_/_/_/
    //


    /**
     * Creates 4 additional explosions around a location to make the shape of the crater random and more natural
     *
     * @param location      center location to create explosions
     * @param sourceEntity  entity that caused the explosion
     * @param explosionType type determines the size of the explosion
     */
    public void multipleExplosions(Location location, Entity sourceEntity, ExplosionType explosionType)
    {
        // create more explosions nearby
        long serverTime = location.getWorld().getFullTime();
        int random1 = (int) (serverTime + location.getBlockZ()) % 8;
        int random2 = (int) (serverTime + location.getBlockX()) % 8;

        Location[] locations = new Location[]
                {
                        location.add(random1, 1, random2),
                        location.add(-random2, 0, random1 / 2),
                        location.add(-random1 / 2, -1, -random2),
                        location.add(random1 / 2, 0, -random2 / 2)
                };

        final int explosionsNum = locations.length;

        for (int i = 0; i < explosionsNum; i++)
        {
            CreateExplosionTask task = new CreateExplosionTask(plugin, locations[i], explosionType, sourceEntity);
            plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, task, 3L * (i + 1));
        }
    }


    public void changeBlockTypes(Collection<Block> blocks, Material previousType, Material newType)
    {
        Validate.notNull(previousType);
        Validate.notNull(newType);

        Iterator<Block> iter = blocks.iterator();
        while (iter.hasNext())
        {
            Block block = iter.next();
            if (block.getType() == previousType)
            {
                block.setType(newType);
                iter.remove(); //?
            }
        }
    }


    /**
     * Make blocks fly
     *
     * @param blocks        list of blocks
     * @param center        center from which to spread blocks out
     * @param flyPercentage percentage of blocks affected
     * @param upVel         how fast to propel upwards
     * @param spreadVel     how fast to propel on horizontal axis
     */
    public void applyExplosionPhysics(Collection<Block> blocks, final Location center, final int flyPercentage, final double upVel, final double spreadVel)
    {
        final List<FallingBlock> fallingBlockList = new ArrayList<FallingBlock>();
        for (Block block : blocks)
        {
            if (block.getType().isSolid())
            {
                //Only a few of the blocks fly as an effect
                if (plugin.random(flyPercentage))
                {
                    FallingBlock fall = block.getLocation().getWorld().spawnFallingBlock(block.getLocation(), block.getBlockData());
                    fall.setMetadata(tag, new FixedMetadataValue(plugin, block.getLocation())); //decide on the distance if block should be placed
                    //fall.setMetadata("drops", new FixedMetadataValue(plugin, block.getDrops()));
                    fall.setDropItem(CFG.getBoolean(RootNode.MORE_FALLING_BLOCKS_DROP_ITEM, block.getWorld().getName()));
                    UtilityModule.moveUp(fall, upVel);
                    //block.setType(Material.AIR);
                    fallingBlockList.add(fall);
                }
            }
        }

        plugin.getServer().getScheduler().runTaskLater(plugin, new Runnable()
        {
            @Override
            public void run()
            {
                for (FallingBlock fall : fallingBlockList)
                {
                    UtilityModule.moveAway(fall, center, spreadVel);
                }
            }
        }, 2L);
    }
}