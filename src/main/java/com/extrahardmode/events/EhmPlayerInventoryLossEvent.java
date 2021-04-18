package com.extrahardmode.events;


import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * Called whenever a Player died and items will be removed from his inventory
 *
 * @author Diemex
 */
public class EhmPlayerInventoryLossEvent extends Event implements Cancellable
{
    /**
     * Player who died
     */
    private final Player player;


    /**
     * DeathEvent to modify the Message for example
     */
    private final PlayerDeathEvent event;

    /**
     * All the items a Player has lost by dying
     */
    private List<ItemStack> drops;

    /**
     * Stacks with these indexes will be removed
     */
    private List<ItemStack> stacksToRemove;

    /**
     * Is Event cancelled
     */
    private boolean cancelled;


    /**
     * Constructor
     *
     * @param event death event that caused the loss of items
     * @param drops  all items that got lost
     * @param stacksToRemove stacks that will be removed
     */
    public EhmPlayerInventoryLossEvent(PlayerDeathEvent event, List<ItemStack> drops, List<ItemStack> stacksToRemove)
    {
        this.event = event;
        this.player = event.getEntity();
        this.drops = drops;
        this.stacksToRemove = stacksToRemove;
    }


    /**
     * Get the DeathEvent associated with this inventory loss event
     *
     * @return event
     */
    public PlayerDeathEvent getDeathEvent()
    {
        return event;
    }


    /**
     * Get the Player involved in this Event
     *
     * @return the Player
     */
    public Player getPlayer()
    {
        return player;
    }


    /**
     * Get the items which a Player lost by dying
     *
     * @return burnticks
     */
    public List<ItemStack> getDrops()
    {
        return drops;
    }


    /**
     * Set the items that got lost by this player dying
     */
    public void setDrops(List<ItemStack> drops)
    {
        this.drops = drops;
    }


    /**
     * Get the indices of the Stacks to remove
     *
     * @return stacks to remove
     */
    public List<ItemStack> getStacksToRemove()
    {
        return stacksToRemove;
    }


    /**
     * Set all the indexes of stacks to be removed... make sure the indeces actually exist
     */
    public void setStacksToRemove(List<ItemStack> stacksToRemove)
    {
        this.stacksToRemove = stacksToRemove;
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
