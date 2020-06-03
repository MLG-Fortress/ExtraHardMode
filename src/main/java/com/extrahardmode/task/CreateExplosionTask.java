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

package com.extrahardmode.task;


import com.extrahardmode.ExtraHardMode;
import com.extrahardmode.config.ExplosionType;
import com.extrahardmode.config.RootConfig;
import com.extrahardmode.config.RootNode;
import com.extrahardmode.module.EntityHelper;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

/**
 * Creates Explosions. The type determines the power, if there should be fire and the blockDmg. The size of the
 * explosion is determined by the y-level. There are basically 2 settings for every explosion, below and above the
 * specified y-level.
 * <p/>
 * Fires an Explosion Event before every Event with a creeper as Entity
 */
public class CreateExplosionTask implements Runnable
{
    /**
     * Plugin reference to get the server etc.
     */
    private final ExtraHardMode plugin;

    /**
     * Location of explosion.
     */
    private final Location location;

    /**
     * Type that holds information about size and things like blockDmg and Fire
     */
    private final ExplosionType type;

    /**
     * Config
     */
    private final RootConfig CFG;

    /**
     * Instance of a the Entity which caused the Explosion
     */
    private Entity explosionCause;


    /**
     * Constructor.
     *
     * @param location - Location to make explosion occur.
     * @param type     Type that determines size and possible blockdamage or fire of explosion.
     */
    public CreateExplosionTask(ExtraHardMode plugin, Location location, ExplosionType type, Entity entity)
    {
        this.location = location;
        this.type = type;
        this.plugin = plugin;
        this.explosionCause = entity;
        CFG = plugin.getModuleForClass(RootConfig.class);
    }


    @Override
    public void run()
    {
        createExplosion(location, type);
    }


    /**
     * Creates a Explosion, can be different above/below a certain y-level
     */
    void createExplosion(Location loc, ExplosionType type)
    {
        int power = type.getPowerA();
        boolean setFire = type.isFireA();
        boolean damageWorld = type.allowBlockDmgA();
        String worldName = loc.getWorld().getName();

        final int border = CFG.getInt(RootNode.EXPLOSIONS_Y, loc.getWorld().getName());

        if (loc.getY() <= border)
        {
            switch (type)
            {
                case CREEPER:
                    power = CFG.getInt(RootNode.EXPLOSIONS_CREEPERS_BELOW_POWER, worldName);
                    setFire = CFG.getBoolean(RootNode.EXPLOSIONS_CREEPERS_BELOW_FIRE, worldName);
                    damageWorld = CFG.getBoolean(RootNode.EXPLOSIONS_CREEPERS_BELOW_WORLD_GRIEF, worldName);
                    break;
                case CREEPER_CHARGED:
                    power = CFG.getInt(RootNode.EXPLOSIONS_CHARGED_CREEPERS_BELOW_POWER, worldName);
                    setFire = CFG.getBoolean(RootNode.EXPLOSIONS_CHARGED_CREEPERS_BELOW_FIRE, worldName);
                    damageWorld = CFG.getBoolean(RootNode.EXPLOSIONS_CHARGED_CREEPERS_BELOW_WORLD_GRIEF, worldName);
                    break;
                case TNT:
                    power = CFG.getInt(RootNode.EXPLOSIONS_TNT_BELOW_POWER, worldName);
                    setFire = CFG.getBoolean(RootNode.EXPLOSIONS_TNT_BELOW_FIRE, worldName);
                    damageWorld = CFG.getBoolean(RootNode.EXPLOSIONS_TNT_BELOW_WORLD_GRIEF, worldName);
                    break;
                case OVERWORLD_BLAZE:
                    power = CFG.getInt(RootNode.EXPLOSIONS_BLAZE_BELOW_POWER, worldName);
                    setFire = CFG.getBoolean(RootNode.EXPLOSIONS_BLAZE_BELOW_FIRE, worldName);
                    damageWorld = CFG.getBoolean(RootNode.EXPLOSIONS_BLAZE_BELOW_WORLD_GRIEF, worldName);
                    break;
                case GHAST_FIREBALL:
                    power = CFG.getInt(RootNode.EXPLOSIONS_GHAST_BELOW_POWER, worldName);
                    setFire = CFG.getBoolean(RootNode.EXPLOSIONS_GHAST_BELOW_FIRE, worldName);
                    damageWorld = CFG.getBoolean(RootNode.EXPLOSIONS_GHAST_BELOW_WORLD_GRIEF, worldName);
                    break;
                default:
                    power = type.getPowerB();
                    setFire = type.isFireB();
                    damageWorld = type.allowBlockDmgB();
            }
        } else if (loc.getY() > border)
        {
            switch (type)
            {
                case CREEPER:
                    power = CFG.getInt(RootNode.EXPLOSIONS_CREEPERS_ABOVE_POWER, worldName);
                    setFire = CFG.getBoolean(RootNode.EXPLOSIONS_CREEPERS_ABOVE_FIRE, worldName);
                    damageWorld = CFG.getBoolean(RootNode.EXPLOSIONS_CREEPERS_ABOVE_WORLD_GRIEF, worldName);
                    break;
                case CREEPER_CHARGED:
                    power = CFG.getInt(RootNode.EXPLOSIONS_CHARGED_CREEPERS_ABOVE_POWER, worldName);
                    setFire = CFG.getBoolean(RootNode.EXPLOSIONS_CHARGED_CREEPERS_ABOVE_FIRE, worldName);
                    damageWorld = CFG.getBoolean(RootNode.EXPLOSIONS_CHARGED_CREEPERS_ABOVE_WORLD_GRIEF, worldName);
                    break;
                case TNT:
                    power = CFG.getInt(RootNode.EXPLOSIONS_TNT_ABOVE_POWER, worldName);
                    setFire = CFG.getBoolean(RootNode.EXPLOSIONS_TNT_ABOVE_FIRE, worldName);
                    damageWorld = CFG.getBoolean(RootNode.EXPLOSIONS_TNT_ABOVE_WORLD_GRIEF, worldName);
                    break;
                case OVERWORLD_BLAZE:
                    power = CFG.getInt(RootNode.EXPLOSIONS_BLAZE_ABOVE_POWER, worldName);
                    setFire = CFG.getBoolean(RootNode.EXPLOSIONS_BLAZE_ABOVE_FIRE, worldName);
                    damageWorld = CFG.getBoolean(RootNode.EXPLOSIONS_BLAZE_ABOVE_WORLD_GRIEF, worldName);
                    break;
                case GHAST_FIREBALL:
                    power = CFG.getInt(RootNode.EXPLOSIONS_GHAST_ABOVE_POWER, worldName);
                    setFire = CFG.getBoolean(RootNode.EXPLOSIONS_GHAST_ABOVE_FIRE, worldName);
                    damageWorld = CFG.getBoolean(RootNode.EXPLOSIONS_GHAST_ABOVE_WORLD_GRIEF, worldName);
                    break;
                default:
                    power = type.getPowerA();
                    setFire = type.isFireA();
                    damageWorld = type.allowBlockDmgA();
            }
        }

        //if (validateLocationSafe(loc, type))
        //{
        //if (CompatHandler.isExplosionProtected(loc))
        //    damageWorld = false;

        //RoboMWM - we no longer provide "compatibility" for block logging plugins - they can listen to BlockExplodeEvent
        //Said "compatibility" also seemed to break some plugins like Creeperheal????
        /*
        if (explosionCause != null) //ignore pure "visual" explosions
            plugin.getModuleForClass(ExplosionCompatStorage.class).queueExplosion(location, explosionCause);
        */


        //entity should be ignored so our code doesn't think that it's a regular creeper etc.
        EntityHelper.flagIgnore(plugin, explosionCause);
        loc.getWorld().createExplosion(loc.getX(), loc.getY(), loc.getZ(), power, setFire, damageWorld);
        //}
    }


    /**
     * Validate if the given Location is not protected by a protection plugin
     *
     * @param loc
     *         The Location
     * @param type
     *         ExplosionType determining the size of the Explosion and size of the area to check
     */
    /*boolean validateLocationSafe(Location loc, ExplosionType type)
    {
        boolean isSafe = true;

        return isSafe;
    }*/


    /**
     * Check pillars in the 4 outer corners of a virtual cube around the explosion. If the protection plugin uses a
     * cuboid selection system then there is no way that an explosion can reach into a protected area without atleast on
     * corner touching it
     */
    /*private ArrayList<Block> getBlockList(Location loc, int boomSize)
    {
        //Doesn't aim to be accurate, just to prevent explosions on the edges of protected land
        ArrayList<Block> boundaries = new ArrayList<Block>();
        Location cubeLoc = loc.clone();

        for (int i = 0; i < 8; i++) //4 outer walls
        {
            //10(5*2) blocks per cornerPillar, no matter the size of the explosion. (x+n-1)/n == x/n but always rounds up
            for (int j = -boomSize; j < boomSize; j += (boomSize + 4) / 5)
            {
                switch (i)
                {
                    case 0:
                        cubeLoc = new Location(loc.getWorld(), loc.getX() + (double) boomSize, loc.getY() + (double) j, loc.getZ() + (double) boomSize);
                        break;
                    case 1:
                        cubeLoc = new Location(loc.getWorld(), loc.getX() - (double) boomSize, loc.getY() + (double) j, loc.getZ() + (double) boomSize);
                        break;
                    case 2:
                        cubeLoc = new Location(loc.getWorld(), loc.getX() + (double) boomSize, loc.getY() + (double) j, loc.getZ() - (double) boomSize);
                        break;
                    case 3:
                        cubeLoc = new Location(loc.getWorld(), loc.getX() - (double) boomSize, loc.getY() + (double) j, loc.getZ() - (double) boomSize);
                        break;
                    case 4: //Locations in the middle inbetween the corners. Needed if explosion bigger than claim
                        cubeLoc = new Location(loc.getWorld(), loc.getX() - (double) (boomSize / 2), loc.getY() + (double) j, loc.getZ() - (double) (boomSize / 2));
                        break;
                    case 5:
                        cubeLoc = new Location(loc.getWorld(), loc.getX() - (double) (boomSize / 2), loc.getY() + (double) j, loc.getZ() - (double) (boomSize / 2));
                        break;
                    case 6:
                        cubeLoc = new Location(loc.getWorld(), loc.getX() - (double) (boomSize / 2), loc.getY() + (double) j, loc.getZ() - (double) (boomSize / 2));
                        break;
                    case 7:
                        cubeLoc = new Location(loc.getWorld(), loc.getX() - (double) (boomSize / 2), loc.getY() + (double) j, loc.getZ() - (double) (boomSize / 2));
                        break;
                }
                boundaries.add(cubeLoc.getBlock());
            }
        }
        return boundaries;
    }*/
}