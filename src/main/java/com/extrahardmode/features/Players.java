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

package com.extrahardmode.features;


import com.extrahardmode.ExtraHardMode;
import com.extrahardmode.config.RootConfig;
import com.extrahardmode.config.RootNode;
import com.extrahardmode.events.EhmPlayerExtinguishFireEvent;
import com.extrahardmode.events.EhmPlayerInventoryLossEvent;
import com.extrahardmode.module.DataStoreModule;
import com.extrahardmode.module.PlayerData;
import com.extrahardmode.module.PlayerModule;
import com.extrahardmode.service.Feature;
import com.extrahardmode.service.ListenerModule;
import com.extrahardmode.service.config.customtypes.PotionEffectHolder;
import com.extrahardmode.task.ArmorWeightTask;
import com.extrahardmode.task.SetPlayerHealthAndFoodTask;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Horse;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Playerchanges include
 * <p/>
 * less health/food on respawn , loss of some of their inventory , enhanced environmental damage , catching the player
 * on fire if extinguishing fires by hand
 */
public class Players extends ListenerModule
{
    private RootConfig CFG = null;

    private PlayerModule playerModule;

    private boolean isArmorWeightEnabled = false;

    private Map<Player, Integer> armorCheckingPlayers = new HashMap<Player, Integer>();


    /**
     * Constructor
     */
    public Players(ExtraHardMode plugin)
    {
        super(plugin);
    }


    @Override
    public void starting()
    {
        super.starting();
        CFG = plugin.getModuleForClass(RootConfig.class);
        playerModule = plugin.getModuleForClass(PlayerModule.class);
        for (World world : plugin.getServer().getWorlds())
            if (CFG.getBoolean(RootNode.ARMOR_SLOWDOWN_ENABLE, world.getName()))
            {
                isArmorWeightEnabled = true;
                break;
            }
        //In case the plugin is reloaded...
        if (isArmorWeightEnabled && plugin.getServer().getOnlinePlayers().size() > 0)
        {
            for (Player player : plugin.getServer().getOnlinePlayers())
                armorCheckingPlayers.put(player, plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new ArmorWeightTask(plugin, player), 20L * 5, 20L * 3));
        }
    }

    /**
     * Armor weight task
     * Using a runnable per-player instead of one runnable to iterate through all players
     * allows us to be somewhat more compatible with other plugins that change player speed,
     * by not constantly setting the player's walkspeed when not necessary.
     */
    @EventHandler
    void onPlayerJoin(PlayerJoinEvent event)
    {
        if (isArmorWeightEnabled)
            armorCheckingPlayers.put(event.getPlayer(), plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new ArmorWeightTask(plugin, event.getPlayer()), 20L * 5, 20L * 3));
    }

    /**
     * Fix #55 (not resetting player speed when entering a EHM-disabled world)
     */
    @EventHandler(priority = EventPriority.LOW)
    void onPlayerChangeWorld(PlayerChangedWorldEvent event)
    {
        Player player = event.getPlayer();
        if (CFG.getBoolean(RootNode.ARMOR_SLOWDOWN_ENABLE, event.getFrom().getName()))
            if (!CFG.getBoolean(RootNode.ARMOR_SLOWDOWN_ENABLE, player.getWorld().getName()))
                player.setWalkSpeed(0.2f);
    }


    /**
     * FEATURE: respawning players start without full health or food
     *
     * @param respawnEvent - Event that occurred.
     */
    @EventHandler(ignoreCancelled = true)
    public void onPlayerRespawn(PlayerRespawnEvent respawnEvent)
    {
        Player player = respawnEvent.getPlayer();
        World world = player.getWorld();

        final boolean playerBypasses = playerModule.playerBypasses(player, Feature.RESPAWN_FOOD_HEALTH);

        final int respawnHealthPercentage = playerBypasses ? 100
                : CFG.getInt(RootNode.PLAYER_RESPAWN_HEALTH_PERCENTAGE, world.getName());
        final int respawnFood = playerBypasses ? 20
                : CFG.getInt(RootNode.PLAYER_RESPAWN_FOOD_LEVEL, world.getName());

        if (respawnFood < 20 && respawnHealthPercentage > 0 && respawnHealthPercentage < 100)
        {
            //TODO HIGH EhmPlayerRespawnEvent
            SetPlayerHealthAndFoodTask task = new SetPlayerHealthAndFoodTask(player, (int) player.getMaxHealth() * respawnHealthPercentage / 100, respawnFood);
            plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, task, 10L); // half-second delay
        }
        // FEATURE: players can't swim when they're carrying a lot of weight, reset the cached value
        PlayerData playerData = plugin.getModuleForClass(DataStoreModule.class).getPlayerData(player.getName());
        playerData.cachedWeightStatus = -1.0F;
    }


    /**
     * When a Player dies he looses a percentage of his inventory
     */
    @EventHandler
    public void onEntityDeath(PlayerDeathEvent event)
    {
        Player player = event.getEntity();
        World world = player.getWorld();

        final boolean enableItemLoss = CFG.getBoolean(RootNode.PLAYER_DEATH_ITEMS_FORFEIT_ENABLE, world.getName());
        final int deathLossPercent = CFG.getInt(RootNode.PLAYER_DEATH_ITEM_STACKS_FORFEIT_PERCENT, world.getName());
        final boolean playerBypasses = playerModule.playerBypasses(player, Feature.DEATH_INV_LOSS);

        final int toolDmgPercent = CFG.getInt(RootNode.PLAYER_DEATH_TOOLS_DMG_PERCENTAGE, world.getName());
        final List<Material> blacklisted = CFG.getStringListAsMaterialList(RootNode.PLAYER_DEATH_ITEMS_BLACKLIST, world.getName());
        final List<Material> toolIds = CFG.getStringListAsMaterialList(RootNode.PLAYER_DEATH_TOOLS_LIST, world.getName());
        final boolean destroyTools = CFG.getBoolean(RootNode.PLAYER_DEATH_TOOLS_KEEP_DAMAGED, world.getName());

        // FEATURE: some portion of player inventory is permanently lost on death
        if (!playerBypasses && enableItemLoss)
        {
            List<ItemStack> drops = event.getDrops();
            List<ItemStack> removedDrops = new ArrayList<ItemStack>();

            int numberOfStacksToRemove = (int) (drops.size() * (deathLossPercent / 100.0f));
            if (numberOfStacksToRemove == 0 && deathLossPercent > 0)
                numberOfStacksToRemove = 1;
            loop:
            for (int i = 0; i < numberOfStacksToRemove && drops.size() > 0; i++)
            {
                ItemStack toRemove = drops.get(plugin.getRandom().nextInt(drops.size()));
                for (Material material : blacklisted)
                    if (material == toRemove.getType())
                        continue loop; //don't remove blacklisted items
                removedDrops.add(toRemove);
            }
            EhmPlayerInventoryLossEvent inventoryLossEvent = new EhmPlayerInventoryLossEvent(event, drops, removedDrops);
            plugin.getServer().getPluginManager().callEvent(inventoryLossEvent);

            if (!inventoryLossEvent.isCancelled())
            {
                List<ItemStack> evntDrops = inventoryLossEvent.getDrops();
                List<ItemStack> evntDropsRemove = inventoryLossEvent.getStacksToRemove();
                outer:
                for (ItemStack item : evntDropsRemove)
                {
                    for (Material tool : toolIds)
                    {
                        //Damage valuable tools instead of completely destroying them
                        if (tool == item.getType())
                        {
                            short dur = item.getDurability();
                            short maxDurability = item.getType().getMaxDurability();
                            dur += maxDurability / 100 * toolDmgPercent;
                            //Prevent complete destroyal of heavily damaged items
                            if (dur >= maxDurability && !destroyTools)
                                dur = --maxDurability;
                            item.setDurability(dur);
                            continue outer;
                        }
                    }
                    evntDrops.remove(item);
                }
            }
        }
    }


    /**
     * Environmental effects when player is damaged
     */
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)//so we know if the event got cancelled
    public void onEntityDamage(EntityDamageEvent event)
    {
        Entity entity = event.getEntity();
        World world = entity.getWorld();

        if (entity instanceof Player)
        {
            Player player = (Player) entity;
            final boolean enhancedEnvironmentalDmg = CFG.getBoolean(RootNode.ENHANCED_ENVIRONMENTAL_DAMAGE, world.getName());
            final boolean playerBypasses = playerModule.playerBypasses(player, Feature.ENVIRONMENTAL_EFFECTS);

            // FEATURE: extra damage and effects from environmental damage
            if (enhancedEnvironmentalDmg && !playerBypasses)
            {
                EntityDamageEvent.DamageCause cause = event.getCause();

                switch (cause)
                {
                    case BLOCK_EXPLOSION:
                    case ENTITY_EXPLOSION:
                        //TODO EhmPlayerEnvironmentalDamageEvent for each type
                        if (event.getDamage() > 2)
                            applyEffectOnDmg(event, CFG.getPotionEffect(RootNode.ENHANCED_DMG_EXPLOSION, world.getName()), CFG.getDouble(RootNode.ENHANCED_DMG_EXPLOSION_MULT, world.getName()));
                        break;
                    case FALL:
                        applyEffectOnDmg(event, CFG.getPotionEffect(RootNode.ENHANCED_DMG_FALL, world.getName()), CFG.getDouble(RootNode.ENHANCED_DMG_FALL_MULT, world.getName()));
//                        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, (int) (20 * event.getDamage()), 4));
//                        event.setDamage(event.getDamage() * 2);
                        break;
                    case SUFFOCATION:
                        if (player.getVehicle() instanceof Horse)  //Reduced because you can easily glitch into blocks
                            applyEffectOnDmg(event, CFG.getPotionEffect(RootNode.ENHANCED_DMG_SUFFOCATION, world.getName()), CFG.getDouble(RootNode.ENHANCED_DMG_SUFFOCATION_MULT, world.getName()) / 2);
                        else
                            applyEffectOnDmg(event, CFG.getPotionEffect(RootNode.ENHANCED_DMG_SUFFOCATION, world.getName()), CFG.getDouble(RootNode.ENHANCED_DMG_SUFFOCATION_MULT, world.getName()));
//                        event.setDamage(event.getDamage() * 5);
                        break;
                    case LAVA:
                        applyEffectOnDmg(event, CFG.getPotionEffect(RootNode.ENHANCED_DMG_LAVA, world.getName()), CFG.getDouble(RootNode.ENHANCED_DMG_LAVA_MULT, world.getName()));
//                        event.setDamage(event.getDamage() * 2);
                        break;
                    case FIRE_TICK:
                        applyEffectOnDmg(event, CFG.getPotionEffect(RootNode.ENHANCED_DMG_BURN, world.getName()), CFG.getDouble(RootNode.ENHANCED_DMG_BURN_MULT, world.getName()));
//                        player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20, 1));
                        break;
                    case DROWNING:
                        applyEffectOnDmg(event, CFG.getPotionEffect(RootNode.ENHANCED_DMG_DROWNING, world.getName()), CFG.getDouble(RootNode.ENHANCED_DMG_DROWNING_MULT, world.getName()));
                        break;
                    case STARVATION:
                        applyEffectOnDmg(event, CFG.getPotionEffect(RootNode.ENHANCED_DMG_STARVATION, world.getName()), CFG.getDouble(RootNode.ENHANCED_DMG_STARVATION_MULT, world.getName()));
                        break;

                    case CONTACT:
                    case CUSTOM:
                    case ENTITY_ATTACK:
                    case FALLING_BLOCK:
                    case FIRE:
                    case LIGHTNING:
                    case MAGIC:
                    case MELTING:
                    case POISON:
                    case PROJECTILE:
                    case SUICIDE:
                    case THORNS:
                    case VOID:
                    case WITHER:
                        break;
                }
            }
        }


    }


    private void applyEffectOnDmg(EntityDamageEvent event, PotionEffectHolder potionEffect, double multiplier)
    {
        //Assume it's a LivingEntity,
        if (potionEffect != null)
        {
            potionEffect.applyEffect((LivingEntity) event.getEntity(), false);
        }
        event.setDamage((int) (event.getDamage() * multiplier));
    }


    /**
     * when a player interacts with the world
     *
     * @param event - Event that occurred.
     */
    @EventHandler(priority = EventPriority.LOWEST)
    void onPlayerInteract(PlayerInteractEvent event)
    {
        Player player = event.getPlayer();
        World world = event.getPlayer().getWorld();
        Block block = event.getClickedBlock();
        Action action = event.getAction();

        final boolean extinguishingFireIgnites = CFG.getBoolean(RootNode.EXTINGUISHING_FIRE_IGNITES_PLAYERS, world.getName());

        final boolean playerBypasses = playerModule.playerBypasses(player, Feature.DANGEROUS_FIRES);

        // FEATURE: putting out fire up close catches the player on fire
        if (extinguishingFireIgnites && !playerBypasses && block != null && block.getType() != Material.AIR &&
                (action.equals(Action.LEFT_CLICK_BLOCK) || action.equals(Action.LEFT_CLICK_AIR)))
        {
            if (block.getRelative(event.getBlockFace()).getType() == Material.FIRE)
            {
                EhmPlayerExtinguishFireEvent fireEvent = new EhmPlayerExtinguishFireEvent(player, 100);  // 20L ~ 1 seconds; 100L ~ 5 seconds
                plugin.getServer().getPluginManager().callEvent(fireEvent);
                if (!fireEvent.isCancelled())
                    player.setFireTicks(fireEvent.getBurnTicks());
            }
        }
    }


//    //Prevent sprint jumping as a workaround for slower armor
//    @EventHandler(priority = EventPriority.LOWEST)
//    void onPlayerMove(PlayerMoveEvent event)
//    {
//        Player player = event.getPlayer();
//        if (!CFG.getBoolean(RootNode.ARMOR_SLOWDOWN_ENABLE, player.getWorld().getName()))
//            return;
//        final int slowdownPercent = CFG.getInt(RootNode.ARMOR_JUMP_SLOWDOWN_PERCENT, player.getWorld().getName());
//        final float armorPoints = PlayerModule.getArmorPoints(player);
//        if (player.getGameMode() != GameMode.CREATIVE && event.getTo().getY() > event.getFrom().getY())
//        {
//            Block block, control;
//            Vector dir = player.getVelocity();
//            float armorPointsNorm = armorPoints / 0.8F;
//            float factor = (1 - armorPointsNorm * (slowdownPercent / 100F)) / 5; //for every jump 5 move events are called
//            dir.multiply(new Vector(factor, 1, factor));
//            block = player.getLocation().getBlock();
//            control = player.getLocation().getBlock().getRelative(BlockFace.UP, 2);
//            if (block.getType() == Material.AIR && control.getType() == Material.AIR)// && !mapGet(player.getUniqueId(), false))
//            {
////                mJumpingPl.put(player.getUniqueId(), true);
//                event.getPlayer().setVelocity(dir);
//                player.sendMessage("=");
//            }
//        }
////        if (event.getTo().getY() < event.getFrom().getY())
////           mJumpingPl.put(player.getUniqueId(), false);
//    }
//
//
//    Map<UUID, Boolean> mJumpingPl = new HashMap<UUID, Boolean>();
//
//
//    private boolean mapGet(UUID key, boolean defaultVal)
//    {
//        Boolean ret = mJumpingPl.get(key);
//        return ret != null ? ret : defaultVal;
//    }
//


    @EventHandler
    public void onPlayerDisconnect(PlayerQuitEvent event)
    {
        event.getPlayer().setWalkSpeed(0.2F);
        if (isArmorWeightEnabled)
        {
            int cancelId = armorCheckingPlayers.get(event.getPlayer());
            plugin.getServer().getScheduler().cancelTask(cancelId);
        }
    }
}
