
package com.extrahardmode.features.monsters;


import com.extrahardmode.ExtraHardMode;

import com.extrahardmode.config.RootConfig;
import com.extrahardmode.config.RootNode;
import com.extrahardmode.module.EntityHelper;
import com.extrahardmode.service.ListenerModule;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.CreatureSpawnEvent;

/**
 * KillerBunny
 */
public class KillerBunny extends ListenerModule
{
    private RootConfig CFG;


    public KillerBunny(ExtraHardMode plugin)
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
     * When an Entity spawns: Spawn a killerbunny sometimes instead of a rabbit
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

        final int killerBunnySpawnPercent = CFG.getInt(RootNode.BONUS_KILLERBUNNY_SPAWN_PERCENT, world.getName());

        // FEATURE: killer bunnies spawns naturally 
        if (entityType == EntityType.RABBIT && world.getEnvironment() == World.Environment.NORMAL
                && event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.NATURAL)
        {
            if (plugin.random(killerBunnySpawnPercent))
            {
                event.setCancelled(true);
                Rabbit rabbit = (Rabbit) EntityHelper.spawn(location, EntityType.RABBIT);
                rabbit.setRabbitType(Rabbit.Type.THE_KILLER_BUNNY);
                rabbit.setAdult();
                rabbit.setAgeLock(true);
                rabbit.setBreed(false);
            }
        }
    }

}
