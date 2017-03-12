
package com.extrahardmode.features.monsters;


import com.extrahardmode.ExtraHardMode;

import com.extrahardmode.config.RootConfig;
import com.extrahardmode.config.RootNode;
import com.extrahardmode.module.EntityHelper;
import com.extrahardmode.service.ListenerModule;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.CreatureSpawnEvent;

/**
 * Cave Spider
 */
public class CaveSpider extends ListenerModule
{
    private RootConfig CFG;


    public CaveSpider(ExtraHardMode plugin)
    {
        super(plugin);
    }


    @Override
    public void starting()
    {
        super.starting();
        CFG = plugin.getModuleForClass(RootConfig.class);
    }


    /**
     * When an Entity spawns: Spawn a Cave Spider sometimes instead of a spider in Swamps
     *
     * @param event which occurred
     */
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onEntitySpawn(CreatureSpawnEvent event)
    {
        LivingEntity entity = event.getEntity();
        if (EntityHelper.isMarkedAsOurs(entity))
            return;
        Location location = event.getLocation();
        World world = location.getWorld();
        EntityType entityType = entity.getType();

        final int cavespiderSpawnPercent = CFG.getInt(RootNode.BONUS_CAVESPIDER_SPAWN_PERCENT, world.getName());

        // FEATURE: CAVE SPIDERs spawns naturally in swamps.
        if (entityType == EntityType.SPIDER && world.getEnvironment() == World.Environment.NORMAL
        		&& entity.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() == Material.GRASS
                && entity.getLocation().getBlock().getBiome() == Biome.SWAMPLAND  
                || entity.getLocation().getBlock().getBiome() == Biome.MUTATED_SWAMPLAND 
                && event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.NATURAL)
        {
            if (plugin.random(cavespiderSpawnPercent))
            {
                event.setCancelled(true);
                EntityHelper.spawn(location, EntityType.CAVE_SPIDER);
            }
        }
    }

}
