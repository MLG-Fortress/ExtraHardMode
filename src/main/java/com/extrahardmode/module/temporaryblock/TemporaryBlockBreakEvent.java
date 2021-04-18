package com.extrahardmode.module.temporaryblock;


import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class TemporaryBlockBreakEvent extends Event implements Cancellable
{
    //private final BlockEvent event;
    private final TemporaryBlock temporaryBlock;
    private boolean cancel = false;


    @Override
    public void setCancelled(boolean cancel)
    {
        this.cancel = cancel;
    }


    @Override
    public boolean isCancelled()
    {
        return this.cancel;
    }


    public TemporaryBlockBreakEvent(TemporaryBlock temporaryBlock)
    {
        this.temporaryBlock = temporaryBlock;
    }


    public TemporaryBlock getTemporaryBlock()
    {
        return temporaryBlock;
    }


//    public BlockBreakEvent getBlockBreakEvent()
//    {
//        return event;
//    }

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
