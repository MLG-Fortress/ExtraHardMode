package com.extrahardmode.features;

import com.extrahardmode.ExtraHardMode;
import com.extrahardmode.config.RootConfig;
import com.extrahardmode.config.RootNode;
import com.extrahardmode.config.messages.MessageNode;
import com.extrahardmode.module.MsgModule;
import com.extrahardmode.module.ParticleEffect;
import com.extrahardmode.module.ParticleEffect.OrdinaryColor;
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
        return a instanceof Animals
                && a.getType() != EntityType.HORSE
                && a.getType() != EntityType.WOLF
                && a.getType() != EntityType.OCELOT;
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

        //First check if config allow this feature
        if (!animalOverCrowdControl) return;
        //Get nearby entities from newly spawn animals
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
            final LivingEntity animal = (LivingEntity) a;
            if(animal.hasMetadata("hasRunnable")) continue;
            /**
             * This creates a runnable assign to each animals will close once if
             * animal is far enough from other animals or animal is dead
             */
            animal.setMetadata("hasRunnable", new FixedMetadataValue(this.plugin, true));
            new BukkitRunnable() {

                int dizzenes = 0;
                int maxDizzenes = 20;
                
                @Override
                public void run() {
                    
                    
                    List<Entity> cattle = e.getNearbyEntities(3, 3, 3);
                    int density = 0;

                    //this will be used to check if animal is far from other animals
                    for (Entity a : cattle) {
                        if (isEntityAnimal(a)) {
                            density++;
                        } 
                    }

                    if (animal.isDead() || density <= threshold) {
                        animal.setMetadata("hasRunnable", new FixedMetadataValue(plugin, false));
                        animal.setMetadata("isClaustrophobic", new FixedMetadataValue(plugin, 0));
                        this.cancel();
                    } else if (animal.hasMetadata("isClaustrophobic") && dizzenes >= maxDizzenes){
                        /**
                         * Hack to force animal to move away exploits the
                         * default AI of animals the set Velocity make sure that
                         * no knockback is given
                         */
                        animal.damage(0.5, animal);
                        animal.setVelocity(new Vector());
                        animal.setMetadata("isClaustrophobic", new FixedMetadataValue(plugin, 3));
                    }
                    
                    dizzenes++;
                    if(!(animal.hasMetadata("isClaustrophobic")) && dizzenes < maxDizzenes) {
                       animal.setMetadata("isClaustrophobic", new FixedMetadataValue(plugin, 1));
                       
                       new BukkitRunnable() {

                           @Override
                           public void run() {
                              if(animal.getMetadata("isClaustrophobic").get(0).asInt() == 3) {
                                  this.cancel();
                              }
                              ParticleEffect.SPELL_MOB.display(new OrdinaryColor(34,139,34), animal.getLocation(), 5);
                           }
                       }.runTaskTimer(plugin, 20, 20);;
                    }
                }
            }.runTaskTimer(this.plugin, 100, 100);
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

        if (animalOverCrowdControl && isEntityAnimal(animal) 
                && animal.hasMetadata("isClaustrophobic")) {
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

        if (animalOverCrowdControl && animal.hasMetadata("isClaustrophobic")
                && isEntityAnimal(animal)) {

            event.getDrops().clear();
        }
    }

}
