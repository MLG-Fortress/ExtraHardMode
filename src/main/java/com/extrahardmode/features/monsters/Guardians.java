
package com.extrahardmode.features.monsters;


import com.extrahardmode.ExtraHardMode;

import com.extrahardmode.config.RootConfig;
import com.extrahardmode.config.RootNode;
import com.extrahardmode.module.EntityHelper;
import com.extrahardmode.service.ListenerModule;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.CreatureSpawnEvent;

/**
 * Guardians
 */
public class Guardians extends ListenerModule
{
    private RootConfig CFG;


    public Guardians(ExtraHardMode plugin)
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
     * When an Entity spawns: Spawn a Guardians sometimes instead of a Squid
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

        final int guardiansSpawnPercent = CFG.getInt(RootNode.BONUS_GUARDIANS_SPAWN_PERCENT, world.getName());

        if (guardiansSpawnPercent == 0)
            return;

        // FEATURE: Guardians spawns naturally 
        if (entityType == EntityType.SQUID && world.getEnvironment() == World.Environment.NORMAL
                && entity.getLocation().getBlock().getBiome() == Biome.DEEP_OCEAN 
                || entity.getLocation().getBlock().getBiome() == Biome.OCEAN
                && event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.NATURAL)
        {
            if (plugin.random(guardiansSpawnPercent))
            {
                event.setCancelled(true);
                EntityHelper.spawn(location, EntityType.GUARDIAN);
            }
        }
    }

}
