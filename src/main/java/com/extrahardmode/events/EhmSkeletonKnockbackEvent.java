package com.extrahardmode.events;


import org.bukkit.entity.Entity;
import org.bukkit.entity.Skeleton;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.util.Vector;

/**
 * Called when a Skeleton deflects an arrow, e.g doesn't take damage fom an Arrow
 *
 * @author Diemex
 */
public class EhmSkeletonKnockbackEvent extends Event implements Cancellable
{
    private boolean cancelled;

    private final Entity entity;

    private final Skeleton skeleton;

    private Vector velocity;

    private final int knockbackPercentage;


    /**
     * Constructor
     *
     * @param entity              the Entity getting shot by the Skeli
     * @param skeleton            Skeleton shooting the Player
     * @param knockbackPercentage the percentage of arrows knocking the Player back
     */
    public EhmSkeletonKnockbackEvent(Entity entity, Skeleton skeleton, Vector velocity, int knockbackPercentage)
    {
        this.entity = entity;
        this.skeleton = skeleton;
        this.velocity = velocity;
        this.knockbackPercentage = knockbackPercentage;
    }


    /**
     * Constructor
     *
     * @param entity            the Entity getting shot by the Skeli
     * @param skeleton          Skeleton shooting the Player
     * @param deflectPercentage the percentage of arrows knocking the Player back
     * @param cancelled         if the Event should be cancelled by default
     */
    public EhmSkeletonKnockbackEvent(Entity entity, Skeleton skeleton, Vector velocity, int deflectPercentage, boolean cancelled)
    {
        this(entity, skeleton, velocity, deflectPercentage);
        this.cancelled = cancelled;
    }


    /**
     * @return the Skeleton that is being shot at
     */
    public Skeleton getSkeleton()
    {
        return skeleton;
    }


    /**
     * @return the percentage of arrows getting deflected
     */
    public int getKnockbackPercentage()
    {
        return knockbackPercentage;
    }


    /**
     * Get the velocity of the Player being knocked back
     *
     * @return the velocity
     */
    public Vector getVelocity()
    {
        return velocity;
    }


    /**
     * Set the velocity of the Player being knocked back
     */
    public void setVelocity(Vector velocity)
    {
        this.velocity = velocity;
    }


    /**
     * @return the entity getting knocked back
     */
    public Entity getEntity()
    {
        return entity;
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