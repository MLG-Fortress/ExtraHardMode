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

package com.extrahardmode.events;


import org.bukkit.Location;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Called whenever an Enderman teleports a Player to them
 *
 * @author Diemex
 */
public class EhmEndermanTeleportEvent extends Event implements Cancellable
{
    private boolean cancelled;

    private final Player player;

    private final Enderman enderman;

    private Location teleportTo;


    /**
     * Constructor
     */
    public EhmEndermanTeleportEvent(Player player, Enderman enderman, Location teleportTo)
    {
        this.player = player;
        this.enderman = enderman;
        this.teleportTo = teleportTo;
    }


    /**
     * Constructor
     *
     * @param shooter           Player shooting the arrow
     */
    public EhmEndermanTeleportEvent(Player shooter, Enderman enderman, Location teleportTo, boolean cancelled)
    {
        this(shooter, enderman, teleportTo);
        this.cancelled = cancelled;
    }


    /**
     * @return the player being teleported
     */
    public Player getPlayer()
    {
        return player;
    }


    /**
     * @return the Enderman teleporting the Player
     */
    public Enderman getEnderman()
    {
        return enderman;
    }


    /**
     * @return the Location where the Player will be teleported to
     */
    public Location getTeleportTo()
    {
        return teleportTo;
    }


    /**
     * Set the Location where the Enderman should teleport the Player
     *
     * @param teleportTo location to set
     */
    public void setTeleportTo(Location teleportTo)
    {
        this.teleportTo = teleportTo;
    }


    /**
     * @return if Event got cancelled
     */
    @Override
    public boolean isCancelled()
    {
        return cancelled;
    }


    /**
     * @param cancelled set if the Event is cancelled which mean that the Skeleton will take normal damage
     */
    @Override
    public void setCancelled(boolean cancelled)
    {
        this.cancelled = cancelled;
    }


    private static final HandlerList HANDLERS = new HandlerList();


    public HandlerList getHandlers()
    {
        return HANDLERS;
    }


    public static HandlerList getHandlerList()
    {
        return HANDLERS;
    }
}
