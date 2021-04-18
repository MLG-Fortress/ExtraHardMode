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


import com.extrahardmode.ExtraHardMode;
import com.extrahardmode.config.RootConfig;
import com.extrahardmode.service.EHMModule;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/** Manages miscellaneous data. */
public class DataStoreModule extends EHMModule
{

    /** In-memory cache for player data */
    private final Map<String, PlayerData> playerNameToPlayerDataMap = new ConcurrentHashMap<String, PlayerData>();

    /** List of previous locations. */
    private final List<SimpleEntry<Player, Location>> previousLocations = new CopyOnWriteArrayList<SimpleEntry<Player, Location>>();

    /** List of Players fighting the dragon */
    private final List<String> playersFightingDragon = new ArrayList<String>();

    /** Config */
    private RootConfig CFG;


    /**
     * Constructor.
     *
     * @param plugin - Plugin instance.
     */
    public DataStoreModule(ExtraHardMode plugin)
    {
        super(plugin);
    }


    /**
     * TestConstructor (dependency injection)
     *
     * @param plugin Plugin instance
     * @param CFG    configinstance
     */
    public DataStoreModule(ExtraHardMode plugin, RootConfig CFG)
    {
        super(plugin);
        this.CFG = CFG;
    }


    @Override
    public void starting()
    {
        CFG = plugin.getModuleForClass(RootConfig.class);
    }


    @Override
    public void closing()
    {
        playerNameToPlayerDataMap.clear();
        previousLocations.clear();
        playersFightingDragon.clear();
    }


    /**
     * Retrieves player data from memory
     *
     * @param playerName - Name of player.
     *
     * @return PlayerData associated with it.
     */
    public PlayerData getPlayerData(String playerName)
    {
        // first, look in memory
        PlayerData playerData = this.playerNameToPlayerDataMap.get(playerName);

        // if not there, create a fresh entry
        if (playerData == null)
        {
            playerData = new PlayerData();
            this.playerNameToPlayerDataMap.put(playerName, playerData);
        }

        // try the hash map again. if it's STILL not there, we have a bug to fix
        return this.playerNameToPlayerDataMap.get(playerName);
    }


    /**
     * Get the list of previous locations of players.
     *
     * @return List of players to location entries.
     */
    public List<SimpleEntry<Player, Location>> getPreviousLocations()
    {
        return previousLocations;
    }


    /**
     * Gets all Players fighting the Dragon
     *
     * @return List containing all Playernames
     */
    public List<String> getPlayers()
    {
        return playersFightingDragon;
    }

}
