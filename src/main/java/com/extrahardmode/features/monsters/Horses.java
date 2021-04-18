/*
 * This file is part of
 * ExtraHardMode Server Plugin for Minecraft
 *
 * Copyright (C) 2012 Ryan Hamshire
 * Copyright (C) 2013 Diemex
 *
 * ExtraHardMode is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ExtraHardMode is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero Public License
 * along with ExtraHardMode.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.extrahardmode.features.monsters;


import com.extrahardmode.ExtraHardMode;
import com.extrahardmode.config.RootConfig;
import com.extrahardmode.config.RootNode;
import com.extrahardmode.service.ListenerModule;
import org.bukkit.entity.Horse;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.HorseInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

/** @author Diemex */
public class Horses extends ListenerModule
{
    /** Store the food value of each horse */
    private Map<UUID/*HorseId*/, Integer/*Food Value*/> healthMap = new HashMap<UUID, Integer>(8);

    /** Is the horse being ridden and should we drain its food */
    private Set<UUID> horsesBeingRidden = new HashSet<UUID>(8);

    private RootConfig CFG;

    /** This horse has been right clicked with food and player shouldn't mount the horse */
    private Set<UUID> enterMap = new HashSet<UUID>(1);

    private final String horseMessage = "extrahardmode.horse.health";


    public Horses(ExtraHardMode plugin)
    {
        super(plugin);
    }


    @Override
    public void starting()
    {
        super.starting();
        CFG = plugin.getModuleForClass(RootConfig.class);
    }


    /** Block using of horse inventory in caves, prevent usage of horses as transportable chests */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onHorseInvClick(InventoryClickEvent event)
    {
        final Inventory inv = event.getInventory();
        final int maxHeight = CFG.getInt(RootNode.HORSE_CHEST_BLOCK_BELOW, event.getWhoClicked().getWorld().getName());

        if (inv instanceof HorseInventory && event.getWhoClicked().getLocation().getBlockY() < maxHeight)
        {
            Inventory horseInv = event.getView().getTopInventory();
            int clickedSlot = event.getRawSlot();

            //In a horse inventory the first two slots are saddle + armor, a mule only has a saddle + potentially a chest
            //Block usage of the chest in caves, but allow taking of the saddle
            if (horseInv.getSize() > 2 && ((clickedSlot < horseInv.getSize() && clickedSlot > 0) || event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY))
                event.setCancelled(true);
        }
    }


    //TODO Save who is on a horse and who not


    /** Greatly increase damage to horses */
    @EventHandler
    public void onHorseDamage(EntityDamageEvent event)
    {
        String world = event.getEntity().getWorld().getName();
        final boolean enhancedEnvironmentaldamage = CFG.getBoolean(RootNode.ENHANCED_ENVIRONMENTAL_DAMAGE, world);

        if (event.getEntity() instanceof Horse && enhancedEnvironmentaldamage)
        {
            final LivingEntity horse = (LivingEntity) event.getEntity();
            final EntityDamageEvent.DamageCause cause = event.getCause();

            switch (cause)
            {
                case BLOCK_EXPLOSION:
                case ENTITY_EXPLOSION:
                    if (event.getDamage() > 2.0)
                        horse.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 20 * 15, 3));
                    break;
                case FALL:
                    horse.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, (int) (20 * event.getDamage()), 4));
                    event.setDamage(event.getDamage() * 2);
                    break;
                case SUFFOCATION:
                    event.setDamage(event.getDamage() * 5);
                    break;
                case LAVA:
                    event.setDamage(event.getDamage() * 2);
                    break;
                case FIRE_TICK:
                    horse.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20, 1));
                    break;
            }
        }
    }

    /**
     * Make horse armor wear out
     */
    /**
     * Make saddle wear out
     */

    /**
     * Make horses require food.
     * Rightclicking a horse with food feeds it, player shouldnt enter the horse afterwards
     */
    /*@EventHandler
    public void onvehicleEnter(VehicleEnterEvent event)
    {
        plugin.getServer().broadcastMessage(event.getEntered().getType() + " Entered " + event.getVehicle().getType());
        if (enterMap.contains(event.getVehicle().getUniqueId()))
        {
            //TODO player view resetting
            enterMap.remove(event.getVehicle().getUniqueId());
            event.setCancelled(true);
        }
    }*/


    /**
     * Make horses require food
     */
    /*@EventHandler
    public void onVehicleExit(VehicleExitEvent event)
    {
        plugin.getServer().broadcastMessage(event.getExited().getType() + " Exited " + event.getVehicle().getType());
    }*/


    /**
     * When a Player right clicks a horse with food in his hand fill up the food meter of the horse
     */
    /*@EventHandler
    public void onPlayerInteract(PlayerInteractEntityEvent event)
    {
        final Player player = event.getPlayer();
        final Horse horse = event.getRightClicked() instanceof Horse ? (Horse) event.getRightClicked() : null;

        plugin.getServer().broadcastMessage("Right Clicked: " + event.getRightClicked().getType());
        plugin.getServer().getLogger().info(player.getItemInHand().getType() + " horse food " + (BlockModule.isHorseFood(player.getItemInHand().getType()) ? "true" : "false"));

        //Player feeds horse when he has a veggie in his hand and the horse is tamed
        if (horse != null && horse.isTamed() && BlockModule.isHorseFood(player.getItemInHand().getType()))
        {
            final UUID horseId = event.getRightClicked().getUniqueId();
            int oldValue = healthMap.containsKey(horseId) ? healthMap.get(horseId) : 0, newValue;

            switch (player.getItemInHand().getType())
            {
                //TODO lol good nutrition
                case CARROT_ITEM:
                case POTATO_ITEM:
                case WHEAT:
                    newValue = incrementFood(horseId, 5);
                    break;
                case HAY_BLOCK:
                    newValue = incrementFood(horseId, 40);
                    break;
                default:
                    throw new UnsupportedOperationException(player.getItemInHand().getType() + " has been added to eatable blocks but no food value has been defined.");
            }

            //TODO visual effects sound when eating
            //Consume food if the horse ate it
            if (newValue > oldValue)
                player.getItemInHand().setAmount(player.getItemInHand().getAmount() - 1);

            //TODO horses dont spawn with full food
            //TODO only tamed horses
            //Update player on new value
            if (oldValue < 20 && newValue >= 20)
                messenger.send(player, MessageNode.HORSE_FEED_LOW);
            else if (oldValue < 50 && newValue >= 50)
                messenger.send(player, MessageNode.HORSE_FEED_MIDDLE);
            else if (oldValue < 80 && newValue >= 80)
                messenger.send(player, MessageNode.HORSE_FEED_HIGH);

            //Save that the Player just feed the horse and cancel the upcoming VehicleEnterEvent
            enterMap.add(horseId);
        }
    }*/


    /**
     * Increment the food level of the given horse
     *
     * @param horse  entity id of the horse
     * @param amount amount to add to the food bar
     *
     * @return new value
     */
    public int incrementFood(UUID horse, int amount)
    {
        int oldValue = healthMap.containsKey(horse) ? healthMap.get(horse) : 0;
        if (oldValue + amount < 100)
            healthMap.put(horse, oldValue + amount);
        return oldValue + amount;
    }


    /**
     * Increment the food level of the given horse
     *
     * @param horse  entity id of the horse
     * @param amount amount to subtract from the food bar
     */
    public void decrementFood(UUID horse, int amount)
    {
        int oldValue = healthMap.containsKey(horse) ? healthMap.get(horse) : 0;
        healthMap.put(horse, oldValue - amount);
    }
}
