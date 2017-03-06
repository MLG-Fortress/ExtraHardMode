
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
 * Vindicator
 */
public class Vindicator extends ListenerModule
{
    private RootConfig CFG;


    public Vindicator(ExtraHardMode plugin)
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
     * When an Entity spawns: Spawn a Vindicator sometimes instead of a Skeleton
     *
     * @param event which occurred
     */
    @EventHandler(priority = EventPriority.LOW)
    public void onEntitySpawn(CreatureSpawnEvent event)
    {
        LivingEntity entity = event.getEntity();
        if (EntityHelper.isMarkedAsOurs(entity))
            return;
        Location location = event.getLocation();
        World world = location.getWorld();
        EntityType entityType = entity.getType();

        final int vindicatorSpawnPercent = CFG.getInt(RootNode.BONUS_VINDICATOR_SPAWN_PERCENT, world.getName());

        // FEATURE: Vindicator spawns naturally 
        if (entityType == EntityType.SKELETON && world.getEnvironment() == World.Environment.NORMAL
                && entity.getLocation().getBlock().getBiome() == Biome.ROOFED_FOREST 
                || entity.getLocation().getBlock().getBiome() == Biome.MUTATED_ROOFED_FOREST
                && event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.NATURAL)
        {
            if (plugin.random(vindicatorSpawnPercent))
            {
                event.setCancelled(true);
                EntityHelper.spawn(location, EntityType.VINDICATOR);
            }
        }
    }

}
