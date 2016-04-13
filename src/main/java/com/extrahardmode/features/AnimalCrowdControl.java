
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
        World world = e.getWorld();
        final boolean animalOverCrowdControl = CFG.getBoolean(RootNode.ANIMAL_OVERCROWD_CONTROL, world.getName());
        //First check if config allow this feature
        if (!animalOverCrowdControl) 
            return;
            
        final Entity e = event.getEntity();    
        final int threshold = CFG.getInt(RootNode.ANIMAL_OVERCROWD_THRESHOLD, world.getName());
            
        //Get nearby entities from newly spawn animals
        List<Entity> cattle = e.getNearbyEntities(3, 3, 3);
        int density = 0;

      /**
        *Loop and check if entity is an animal while 
        *looping count how many animals have spawned
        * by incrementing density
        */
        for (Entity a : cattle) {
            if (a instanceof Animals
                    && a.getType() != EntityType.HORSE
                    && a.getType() != EntityType.WOLF
                    && a.getType() != EntityType.OCELOT) {
                density++;
                
                //Check if the amount of animals is bigger than the threshold given
                if (density > threshold) {
                    final LivingEntity animal = (LivingEntity) a;
                    
                  /**
                    *This creates a runnable assign to each animals will close once
                    *if animal is far enough from other animals or animal is dead
                    */ 
                    new BukkitRunnable() {

                        boolean firstRun = true;

                        @Override
                        public void run() {
                            List<Entity> cattle = e.getNearbyEntities(3, 3, 3);
                            int density = 0;

                            //this will be used to check if animal is far from other animals
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
                                /**
                                 *Hack to force animal to move away exploits the default AI of animals
                                 *the set Velocity make sure that no knockback is given
                                 */
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

    /**
     * OnPlayerInteract for Animal Overcrowding Control
     *
     * display a message about Animal Overcrowding Control
     */
    @EventHandler
    public void onPlayerInteract(PlayerInteractEntityEvent event) {
        World world = player.getWorld();
        final boolean animalOverCrowdControl = CFG.getBoolean(RootNode.ANIMAL_OVERCROWD_CONTROL, world.getName());
        if (!animalOverCrowdControl)
            return;
            
        Player player = event.getPlayer();
        LivingEntity animal = (LivingEntity) event.getRightClicked();

        if (animal instanceof Animals 
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
        World world = animal.getWorld();
        final boolean animalOverCrowdControl = CFG.getBoolean(RootNode.ANIMAL_OVERCROWD_CONTROL, world.getName());
        if (!animalOverCrowdControl)
            return;
        
        LivingEntity animal = event.getEntity();
        
        if (animal instanceof Animals && animal.getKiller() == null
                && animal.getType() != EntityType.HORSE
                && animal.getType() != EntityType.WOLF
                && animal.getType() != EntityType.OCELOT) {

            event.setDroppedExp(0);
            event.getDrops().clear();
        }
    }
    
}
