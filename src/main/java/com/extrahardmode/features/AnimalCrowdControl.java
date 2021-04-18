package com.extrahardmode.features;

import com.extrahardmode.ExtraHardMode;
import com.extrahardmode.config.RootConfig;
import com.extrahardmode.config.RootNode;
import com.extrahardmode.config.messages.MessageNode;
import com.extrahardmode.module.MsgModule;
import com.extrahardmode.service.ListenerModule;
import java.util.List;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.metadata.FixedMetadataValue;
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

    private boolean isEntityAnimal(Entity a) {
        return (a instanceof Animals)
                && a.getType() != EntityType.HORSE
                && a.getType() != EntityType.WOLF
                && a.getType() != EntityType.OCELOT;
    }
    
    private int getCurrentDensity(Entity e) {
        
        List<Entity> cattle = e.getNearbyEntities(3, 3, 3);
        int density = 0;

        //this will be used to check if animal is far from other animals
        for (Entity a : cattle) {
            if (isEntityAnimal(a)) {
                density++;
            } 
        }
        
        return density;
    }

    /**
     * When farm gets overcrowded
     *
     * Check if overcrowded if so slowly kill farm animals
     */
    @EventHandler(ignoreCancelled = true)
    public void onAnimalOverCrowd(CreatureSpawnEvent event) {
        final Entity e = event.getEntity();

        //If entity is not an animal, we don't care
        if (!isEntityAnimal(e)) return;

        final World world = e.getWorld();

        final boolean animalOverCrowdControl = CFG.getBoolean(RootNode.ANIMAL_OVERCROWD_CONTROL, world.getName());
        final int threshold = CFG.getInt(RootNode.ANIMAL_OVERCROWD_THRESHOLD, world.getName());

        //First check if config allow this feature
        if (!animalOverCrowdControl) return;
        
        //Just to check if animal is part of a Pet Plugin assuming spawned pet have nametags already given
        if(e.getCustomName() != null) return;

        //Get nearby entities from newly spawned animals
        List<Entity> cattle = e.getNearbyEntities(3, 3, 3);
        int density = 0;

        /**
         * Loop and check if entity is an animal while looping count how many
         * animals have spawned by incrementing density
         */
        for (Entity a : cattle) {
            if (!isEntityAnimal(a)) continue;
            density++;
            
            
            //Check if the amount of animals is bigger than the threshold given
            if (density < threshold) continue;
            final Animals animal = (Animals) a;
            if(animal.hasMetadata("hasRunnable")) continue;
            /**
             * This creates a runnable assign to each animals will close once if
             * animal is far enough from other animals or animal is dead
             */
            animal.setMetadata("hasRunnable", new FixedMetadataValue(this.plugin, true));
            new BukkitRunnable() {

                int dizziness = 0;
                int maxDizziness = 7; //basically max seconds before getting damaged
                
                @Override
                public void run() {

                    
                    if (animal.isDead() || getCurrentDensity(e) <= threshold) {
                        animal.removeMetadata("hasRunnable", plugin);
                        animal.removeMetadata("isClaustrophobic", plugin);
                        this.cancel();
                        return;
                    } else if (dizziness >= maxDizziness) {
                        double health = animal.getHealth();
                        animal.damage(0.5, animal);
                        if (animal.getHealth() == health)
                            animal.damage(0.5); //Attempt to override protection plugins like Worldguard
                        animal.setVelocity(new Vector()); //Triggers animal's "run away" AI
                        dizziness = 0;
                    }
                    
                    if(!(animal.hasMetadata("isClaustrophobic"))) {
                        animal.setMetadata("isClaustrophobic", new FixedMetadataValue(plugin, true));
                    }
                    
                    if(dizziness < maxDizziness) {
                       world.spawnParticle(Particle.VILLAGER_ANGRY, animal.getLocation(), 1); //TODO: confirm if this works
                    }
                    dizziness++;
                }
            }.runTaskTimer(this.plugin, 20, 20);
        }
    }

    /**
     * OnPlayerInteract for Animal Overcrowding Control
     *
     * display a message about Animal Overcrowding Control
     */
    @EventHandler(ignoreCancelled = true)
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        //If the entity is not an animal, we don't care
        if (!isEntityAnimal(event.getRightClicked())) return;

        Player player = event.getPlayer();
        Animals animal = (Animals)event.getRightClicked();
        World world = player.getWorld();

        final boolean animalOverCrowdControl = CFG.getBoolean(RootNode.ANIMAL_OVERCROWD_CONTROL, world.getName());

        if (animalOverCrowdControl && animal.hasMetadata("isClaustrophobic")) {
            messenger.send(player, MessageNode.ANIMAL_OVERCROWD_CONTROL);
        }
    }

    /**
     * On Animal Death for Animal Overcrowding Control
     *
     * remove drops and exp from death cause not by player
     */
    @EventHandler(ignoreCancelled = true)
    public void onAnimalDeath(EntityDeathEvent event) {
        //If the entity is not an animal, we don't care
        if (!isEntityAnimal(event.getEntity())) return;

        Animals animal = (Animals)event.getEntity();
        World world = animal.getWorld();

        final boolean animalOverCrowdControl = CFG.getBoolean(RootNode.ANIMAL_OVERCROWD_CONTROL, world.getName());

        if (animalOverCrowdControl && animal.hasMetadata("isClaustrophobic")
                && isEntityAnimal(animal)) {

            event.getDrops().clear();
        }
    }

}
