
package com.extrahardmode.features;

import com.extrahardmode.ExtraHardMode;
import com.extrahardmode.config.RootConfig;
import com.extrahardmode.config.RootNode;
import com.extrahardmode.config.messages.MessageNode;
import com.extrahardmode.module.MsgModule;
import com.extrahardmode.service.ListenerModule;
import java.util.List;
import org.bukkit.World;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

/**
 *
 * @author Vanmc
 */
public class AnimalCrowdControl extends ListenerModule {
    
    
    private RootConfig CFG;

    private MsgModule messenger;


    public AnimalCrowdControl(ExtraHardMode plugin) {
        super(plugin);
    }

    @Override
    public void starting() {
        super.starting();
        CFG = plugin.getModuleForClass(RootConfig.class);
        messenger = plugin.getModuleForClass(MsgModule.class);
    }
    
   /**
     * When farm gets overcrowded
     *
     * Check if overcrowded if so slowly kill farm animals
     */
    @EventHandler
    public void onAnimalOverCrowd(CreatureSpawnEvent event) {
        final Entity e = event.getEntity();
        World world = e.getWorld();

        final boolean animalOverCrowdControl = CFG.getBoolean(RootNode.ANIMAL_OVERCROWD_CONTROL, world.getName());
        final int threshold = CFG.getInt(RootNode.ANIMAL_OVERCROWD_THRESHOLD, world.getName());

        if (animalOverCrowdControl) {
            List<Entity> cattle = e.getNearbyEntities(3, 3, 3);
            int density = 0;

            for (Entity a : cattle) {
                if (a instanceof Animals
                        && a.getType() != EntityType.HORSE
                        && a.getType() != EntityType.WOLF
                        && a.getType() != EntityType.OCELOT) {
                    density++;
                    if (density > threshold) {
                        final LivingEntity animal = (LivingEntity) a;
                        new BukkitRunnable() {

                            boolean firstRun = true;

                            @Override
                            public void run() {
                                List<Entity> cattle = e.getNearbyEntities(3, 3, 3);
                                int density = 0;

                                for (Entity a : cattle) {
                                    if (a instanceof Animals
                                            && a.getType() != EntityType.HORSE
                                            && a.getType() != EntityType.WOLF
                                            && a.getType() != EntityType.OCELOT) {
                                        density++;
                                    }
                                }
                                if (animal.isDead() || density <= threshold) {
                                    this.cancel();
                                } else {
                                    //Hack to force animal to move away
                                    if (firstRun) {
                                        firstRun = false;
                                        animal.damage(0, animal);
                                        animal.setVelocity(new Vector());
                                    } else {
                                        animal.damage(0.5);
                                    }
                                }
                            }
                        }.runTaskTimer(this.plugin, 20, 20);
                    }
                }
            }
        }
    }

    /**
     * OnPlayerInteract for Animal Overcrowding Control
     *
     * display a message about Animal Overcrowding Control
     */
    @EventHandler
    public void onPlayerInteract(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        LivingEntity animal = (LivingEntity) event.getRightClicked();
        World world = player.getWorld();

        final boolean animalOverCrowdControl = CFG.getBoolean(RootNode.ANIMAL_OVERCROWD_CONTROL, world.getName());

        if (animalOverCrowdControl && animal instanceof Animals 
                && animal.getType() != EntityType.HORSE
                && animal.getType() != EntityType.WOLF
                && animal.getType() != EntityType.OCELOT) {

            messenger.send(player, MessageNode.ANIMAL_OVERCROWD_CONTROL);
        }
    }

    /**
     * On Animal Death for Animal Overcrowding Control
     *
     * remove drops and exp from death cause not by player
     */
    @EventHandler
    public void onAnimalDeath(EntityDeathEvent event) {
        LivingEntity animal = event.getEntity();
        World world = animal.getWorld();

        final boolean animalOverCrowdControl = CFG.getBoolean(RootNode.ANIMAL_OVERCROWD_CONTROL, world.getName());

        if (animalOverCrowdControl && animal instanceof Animals && animal.getKiller() == null
                && animal.getType() != EntityType.HORSE
                && animal.getType() != EntityType.WOLF
                && animal.getType() != EntityType.OCELOT) {

            event.setDroppedExp(0);
            event.getDrops().clear();
        }
    }
    
}
