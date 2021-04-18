package com.extrahardmode.features;


import com.extrahardmode.ExtraHardMode;
import com.extrahardmode.config.RootConfig;
import com.extrahardmode.config.RootNode;
import com.extrahardmode.config.messages.MessageNode;
import com.extrahardmode.config.messages.MsgCategory;
import com.extrahardmode.events.*;
import com.extrahardmode.module.BlockModule;
import com.extrahardmode.module.MsgModule;
import com.extrahardmode.service.FindAndReplace;
import com.extrahardmode.service.ListenerModule;
import com.extrahardmode.task.WeightCheckTask;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * @author Diemex
 */
public class Tutorial extends ListenerModule
{
    private MsgModule messenger;

    private RootConfig CFG;

    private BlockModule blockModule;


    public Tutorial(ExtraHardMode plugin)
    {
        super(plugin);
    }


    @Override
    public void starting()
    {
        super.starting();
        messenger = plugin.getModuleForClass(MsgModule.class);
        CFG = plugin.getModuleForClass(RootConfig.class);
        blockModule = plugin.getModuleForClass(BlockModule.class);
    }


    /**
     * When an Entity targets another Entity
     * <p/>
     * Display some warnings to a Player when he is targetted by a dangerous mob
     */
    @EventHandler
    public void onEntityTarget(EntityTargetEvent event)
    {
        if (event.getTarget() instanceof Player)
        {
            final Player player = (Player) event.getTarget();
            final World world = player.getWorld();

            switch (event.getEntity().getType())
            {
                case CREEPER:
                {
                    if (CFG.getBoolean(RootNode.CHARGED_CREEPERS_EXPLODE_ON_HIT, world.getName()) && CFG.getInt(RootNode.CHARGED_CREEPER_SPAWN_PERCENT, world.getName()) > 0)
                    {
                        Creeper creeper = (Creeper) event.getEntity();
                        if (creeper.isPowered())
                            messenger.send(player, MessageNode.CHARGED_CREEPER_TARGET);
                    }
                    break;
                }
                case BLAZE:
                {
                    switch (world.getEnvironment())
                    {
                        case NORMAL:
                            if (CFG.getBoolean(RootNode.BLAZES_EXPLODE_ON_DEATH, world.getName()))
                                messenger.send(player, MessageNode.BLAZE_TARGET_NORMAL);
                            break;
                        case NETHER:
                            if (CFG.getInt(RootNode.BONUS_NETHER_BLAZE_SPAWN_PERCENT, world.getName()) > 0)
                                messenger.send(player, MessageNode.BLAZE_TARGET_NETHER);
                            break;
                    }
                    break;
                }
                case GHAST:
                {
                    if (CFG.getInt(RootNode.GHASTS_DEFLECT_ARROWS, world.getName()) > 0)
                        messenger.send(player, MessageNode.GHAST_TARGET);
                    break;
                }
                case ZOMBIFIED_PIGLIN:
                {
                    if (CFG.getBoolean(RootNode.ALWAYS_ANGRY_PIG_ZOMBIES, world.getName()))
                        messenger.send(player, MessageNode.PIGZOMBIE_TARGET);
                    if (CFG.getInt(RootNode.NETHER_PIGS_DROP_WART, world.getName()) > 0)
                        plugin.getServer().getScheduler().runTaskLater(plugin, new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                messenger.send(player, MessageNode.PIGZOMBIE_TARGET_WART);
                            }
                        }, 300L);
                    break;
                }
                case MAGMA_CUBE:
                {
                    if (CFG.getBoolean(RootNode.MAGMA_CUBES_BECOME_BLAZES_ON_DAMAGE, world.getName()))
                        messenger.send(player, MessageNode.MAGMACUBE_TARGET);
                    break;
                }
                case SKELETON:
                {
                    //TODO shoot silverfish
                    break;
                }
                case SPIDER:
                {
                    //TODO web
                    break;
                }
                case WITCH:
                {
                    //TODO zombies, poison explosions
                    break;
                }
                case ENDERMAN:
                {
                    if (CFG.getBoolean(RootNode.IMPROVED_ENDERMAN_TELEPORTATION, world.getName()))
                        messenger.send(player, MessageNode.ENDERMAN_GENERAL);
                    break;
                }
                case ZOMBIE:
                {
                    if (CFG.getBoolean(RootNode.ZOMBIES_DEBILITATE_PLAYERS, world.getName()))
                        messenger.send(player, MessageNode.ZOMBIE_SLOW_PLAYERS);
                    break;
                }
            }
        }
    }


    /**
     * Inform Players about the respawning Zombies
     */
    @EventHandler(ignoreCancelled = true)
    public void onZombieRespawn(EhmZombieRespawnEvent event)
    {
        final Player player = event.getPlayer();
        if (player != null)
        {
            messenger.send(player, MessageNode.ZOMBIE_RESPAWN);
        }
    }


    /**
     * Warn players before entering the nether
     */
    @EventHandler
    public void onPlayerChangeWorld(PlayerChangedWorldEvent event)
    {
        if (Arrays.asList(CFG.getEnabledWorlds()).contains(event.getPlayer().getWorld().getName()))
        {
            final Player player = event.getPlayer();
            if (player.getWorld().getEnvironment() == World.Environment.NETHER)
            {
                messenger.send(player, MessageNode.NETHER_WARNING);
            }
        }
    }


    /**
     * Inform Players about creepers dropping tnt
     */
    @EventHandler
    public void onCreeperDropTnt(EhmCreeperDropTntEvent event)
    {
        final Player player = event.getPlayer();
        if (player != null)
        {
            messenger.send(player, MessageNode.CREEPER_DROP_TNT);
        }
    }


    /**
     * When a Skeleton deflects an arrow
     */
    @EventHandler(ignoreCancelled = true)
    public void onSkeletonDeflect(EhmSkeletonDeflectEvent event)
    {
        if (event.getShooter() != null)
        {
            final Player player = event.getShooter();
            messenger.send(player, MessageNode.SKELETON_DEFLECT);
        }
    }


    /**
     * Let Players know that they can use ice
     */
    @EventHandler
    public void onPlayerFillBucket(PlayerBucketFillEvent event)
    {
        if (CFG.getBoolean(RootNode.DONT_MOVE_WATER_SOURCE_BLOCKS, event.getPlayer().getWorld().getName()))
        {
            final Player player = event.getPlayer();
            messenger.send(player, MessageNode.BUCKET_FILL);
        }
    }


    /**
     * Messages when planting with antifarming
     */
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event)
    {
        if (CFG.getBoolean(RootNode.WEAK_FOOD_CROPS, event.getBlock().getWorld().getName()))
        {
            final Player player = event.getPlayer();
            final Block block = event.getBlock();
            //Too dark
            if (block.getType() == Material.FARMLAND)
            {
                Block above = block.getRelative(BlockFace.UP);
                if (above.getLightFromSky() < 10)
                {
                    messenger.send(player, MessageNode.ANTIFARMING_NO_LIGHT);
                }
            }

            Block below = block.getRelative(BlockFace.DOWN);

            //Unwatered
            if (blockModule.isPlant(block.getType()) && below.getState().getData().getData() == (byte) 0)
            {
                messenger.send(player, MessageNode.ANTIFARMING_UNWATERD);
            }

            //Warn players before they build big farms in the desert
            if (block.getType() == Material.DIRT)
            {
                try
                {
                    switch (block.getBiome())
                    {
                        case DESERT:
                        case DESERT_HILLS:
                        {
                            messenger.send(player, MessageNode.ANTIFARMING_DESSERT_WARNING);
                            break;
                        }
                    }
                }
                catch (IllegalArgumentException e) {} //ignore custom biomes

            }
        }
    }


    /**
     * Inform about not being able to extinguish fire with bare hands
     */
    @EventHandler
    public void onExtinguishFire(EhmPlayerExtinguishFireEvent event)
    {
        messenger.send(event.getPlayer(), MessageNode.EXTINGUISH_FIRE);
    }


    /**
     * @param event event that occurred
     */
    @EventHandler
    public void onPlayerInventoryLoss(EhmPlayerInventoryLossEvent event)
    {
        StringBuilder items = new StringBuilder();

        //Merge the item amounts: 1 stone, 1 stone => 2 stones
        List<ItemStack> lostItems = new ArrayList<ItemStack>();
        for (int i = 0; i < event.getStacksToRemove().size(); i++)
        {
            ItemStack item = event.getStacksToRemove().get(i);
            //Does an item of the same type exist already?
            int index = -1;
            for (int lostI = 0; lostI < lostItems.size(); lostI++)
            {
                ItemStack lost = lostItems.get(lostI);
                if (lost.getType() == item.getType())
                {
                    index = lostI;
                    break;
                }
            }
            if (index >= 0)
                lostItems.get(index).setAmount(lostItems.get(index).getAmount() + item.getAmount());
            else
                lostItems.add(item);

        }

        //Build the output String
        for (ItemStack item : lostItems)
        {
            if (items.length() > 0)
                items.append(", ");
            items.append(item.getType().toString());
        }

        //Only print if items have been removed
        if (event.getStacksToRemove().size() > 0)
        {
            messenger.send(event.getPlayer(), MessageNode.LOST_ITEMS, new FindAndReplace(items.toString(), MessageNode.Variables.ITEMS.getVarNames()));
            messenger.send(event.getPlayer(), MessageNode.LOST_ITEMS_PLAYER);
        }
    }


    /**
     * Display the weight of the inventory
     */
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event)
    {
        if (CFG.getBoolean(RootNode.NO_SWIMMING_IN_ARMOR, event.getWhoClicked().getWorld().getName())
                && event.getWhoClicked() instanceof Player && messenger.popupsAreEnabled(MsgCategory.NOTIFICATION))
            WeightCheckTask.updateLastCLick(event.getWhoClicked().getUniqueId());
    }

    //TODO Farming: NetherWart, Mushrooms

    //TODO OnSheepDye
}
