package com.extrahardmode.events;


import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * @author Diemex
 */
public class EhmPlayerExtinguishFireEvent extends Event implements Cancellable
{
    /**
     * Player who broke the Stone
     */
    private final Player player;

    /**
     * Amount in ticks where the Player burns
     */
    private int burnTicks;

    /**
     * Is Event cancelled
     */
    private boolean cancelled;


    /**
     * Constructor
     *
     * @param player    player that hit fire
     * @param burnTicks ticks player will be on fire
     */
    public EhmPlayerExtinguishFireEvent(Player player, int burnTicks)
    {
        this.player = player;
        this.burnTicks = burnTicks;
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
     * Get the amount in ticks the Player is going to burn
     *
     * @return burnticks
     */
    public int getBurnTicks()
    {
        return burnTicks;
    }


    /**
     * Set the amount in ticks where the Player burns
     */
    public void setBurnTicks(int burnTicks)
    {
        this.burnTicks = burnTicks;
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
