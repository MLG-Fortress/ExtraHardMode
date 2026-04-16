package com.extrahardmode.events.fakeevents;


import org.bukkit.ExplosionResult;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityExplodeEvent;

import java.util.List;

/**
 * @author Diemex
 */
public class FakeEntityExplodeEvent extends EntityExplodeEvent
{
    public FakeEntityExplodeEvent(Entity what, Location location, List<Block> blocks, float yield)
    {
        super(what, location, blocks, yield, ExplosionResult.DESTROY);
    }
}
