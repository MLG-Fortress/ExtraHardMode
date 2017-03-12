package com.extrahardmode.module.temporaryblock;


import com.extrahardmode.ExtraHardMode;
import com.extrahardmode.service.ListenerModule;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TemporaryBlockHandler extends ListenerModule
{
    private Map<LiteLocation, TemporaryBlock> temporaryBlockList = new HashMap<LiteLocation, TemporaryBlock>();


    public TemporaryBlockHandler(ExtraHardMode plugin)
    {
        super(plugin);
    }


    /**
     * int addTemporaryBlock(Block block)
     * removeBlock (int)
     * onBlockBreak -> mark as broken
     * onTempBlockBreakEvent
     * onZombieRespawnTask -> check if broken
     */
    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event)
    {
        if (fireTemporaryBlockBreakEvent(event.getBlock()))
        {
            event.setCancelled(true);
            event.getBlock().setType(Material.AIR);
        }
    }

    //Also account for water
    @EventHandler(ignoreCancelled = true)
    public void onWaterBreakBlock(BlockFromToEvent event)
    {
        if (fireTemporaryBlockBreakEvent(event.getToBlock()))
        {
            event.setCancelled(true); //TODO: only way to prevent skull from dropping as item?
            event.getToBlock().setType(Material.AIR);
        }
    }

    //And explosions
    @EventHandler(ignoreCancelled = true)
    public void onEntityExplosionBreak(EntityExplodeEvent event)
    {
        ArrayList<Block> blocks = new ArrayList<Block>(event.blockList());
        for (Block block : blocks)
        {
            if (fireTemporaryBlockBreakEvent(block))
            {
                event.blockList().remove(block);
                block.setType(Material.AIR);
            }
        }
    }

    //And also other plugin-caused explosions (and beds in the nether)
    @EventHandler(ignoreCancelled = true)
    public void onBlockExplosionBreak(BlockExplodeEvent event)
    {
        ArrayList<Block> blocks = new ArrayList<Block>(event.blockList());
        for (Block block : blocks)
        {
            if (fireTemporaryBlockBreakEvent(block))
            {
                event.blockList().remove(block);
                block.setType(Material.AIR);
            }
        }
    }

    private boolean fireTemporaryBlockBreakEvent(Block block)
    {
        if (temporaryBlockList.containsKey(LiteLocation.fromLocation(block.getLocation())))
        {
            TemporaryBlock temporaryBlock = temporaryBlockList.remove(LiteLocation.fromLocation(block.getLocation()));
            temporaryBlock.isBroken = true;
            TemporaryBlockBreakEvent event = new TemporaryBlockBreakEvent(temporaryBlock);
            plugin.getServer().getPluginManager().callEvent(event);
            return event.isCancelled();
        }
        return false;
    }



    public TemporaryBlock addTemporaryBlock(Location loc, Object... data)
    {
        TemporaryBlock temporaryBlock = new TemporaryBlock(loc, data);
        temporaryBlockList.put(LiteLocation.fromLocation(loc), temporaryBlock);
        return temporaryBlock;
    }
}
