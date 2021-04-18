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


import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

/**
 * This Event is called when EHM determines if a tool is supposed to be affected by the hardened stone code. It will be called everytime when a Player breaks stone also if the tool
 * wouldn't be able to break the Block.
 * <p/>
 * This allows for plugins to step over the toolcheck and add their own tools. (bukkitforge etc.)
 *
 * @author Max
 */

public class EhmHardenedStoneEvent extends Event
{
    /**
     * Player who broke the Stone
     */
    private final Player player;

    /**
     * The Tool which broke the block
     */
    private final ItemStack tool;

    /**
     * The amount of blocks this tool can mine
     */
    private int numOfBlocks;


    /**
     * Constructor
     *
     * @param tool        the tool which broke the Stone
     * @param numOfBlocks amount of blocks tool can mine
     */
    public EhmHardenedStoneEvent(Player player, ItemStack tool, int numOfBlocks)
    {
        this.player = player;
        this.tool = tool;
        this.numOfBlocks = numOfBlocks;
    }


    /**
     * Get the Player involved in this Event
     *
     * @return player
     */
    public Player getPlayer()
    {
        return player;
    }


    /**
     * Get the tool the Player used to break the stone
     *
     * @return ItemStack Player is holding
     */
    public ItemStack getTool()
    {
        return tool;
    }


    /**
     * Get the material
     *
     * @return material of the Tool which broke the Stone
     */
    public Material getToolId()
    {
        return tool.getType();
    }


    /**
     * Get the Material of the tool
     *
     * @return the Material of a tool as Bukkit enum
     */
    public Material getToolMaterial()
    {
        return tool.getType();
    }


    /**
     * Set the amount of blocks the tool can mine Just set this higher than 0 and ehm will allow the tool to break blocks
     */
    public void setNumOfBlocks(short blocks)
    {
        this.numOfBlocks = blocks;
    }


    /**
     * Get the amount of stone blocks this tool would be able to break.
     * <p/>
     * Will be 0 if the tool isn't able to break stone by default.
     */
    public int getNumOfBlocks()
    {
        return numOfBlocks;
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