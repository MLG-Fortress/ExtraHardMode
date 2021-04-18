package com.extrahardmode.module;


import com.extrahardmode.ExtraHardMode;
import com.extrahardmode.service.EHMModule;
import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

/** @author Diemex */
public class ExplosionCompatStorage extends EHMModule
{
    private Entity explosionCause;
    private Location centerLocation;


    /**
     * Constructor.
     *
     * @param plugin - Plugin instance.
     */
    public ExplosionCompatStorage(ExtraHardMode plugin)
    {
        super(plugin);
    }


    /**
     * Queue an explosion so we can process it accordingly.
     * <p/>
     * This will catch an explosion in an EntityExplodeEvent. Only if the entity is null we call an extra event with the actual cause of the explosion. If the extra event we called
     * got cancelled we will also cancel our null-entity event.
     *
     * @param centerLocation the location where the explosion occurred
     * @param cause          the entity responsible for this explosion
     */
    public void queueExplosion(Location centerLocation, Entity cause)
    {
        Validate.notNull(centerLocation, "No location provided");
        Validate.notNull(cause, "No valid explosion cause");

        this.centerLocation = centerLocation;
        this.explosionCause = cause;
    }


    public Location getCenterLocation()
    {
        return centerLocation;
    }


    public Entity getExplosionCause()
    {
        return explosionCause;
    }


    /** Clear the current explosion from the queue. */
    public void clearQueue()
    {
        this.explosionCause = null;
        this.centerLocation = null;
    }


    public boolean queueEmpty()
    {
        return explosionCause == null && centerLocation == null;
    }


    @Override
    public void starting()
    {
    }


    @Override
    public void closing()
    {
        explosionCause = null;
        centerLocation = null;
    }
}
