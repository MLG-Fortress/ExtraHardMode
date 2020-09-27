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
import com.extrahardmode.config.messages.MessageNode;
import com.extrahardmode.module.BlockModule;
import com.extrahardmode.module.MsgModule;
import com.extrahardmode.module.PlayerModule;
import com.extrahardmode.service.Feature;
import com.extrahardmode.service.ListenerModule;
import com.extrahardmode.task.EvaporateWaterTask;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.SheepRegrowWoolEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.StructureGrowEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

/**
 * Antifarming module
 */
public class AntiFarming extends ListenerModule
{
    private RootConfig CFG;

    private PlayerModule playerModule;

    private BlockModule blockModule;


    public AntiFarming(ExtraHardMode plugin)
    {
        super(plugin);
    }


    @Override
    public void starting()
    {
        super.starting();
        CFG = plugin.getModuleForClass(RootConfig.class);
        playerModule = plugin.getModuleForClass(PlayerModule.class);
        blockModule = plugin.getModuleForClass(BlockModule.class);
    }


    /**
     * when a player interacts with the world
     * <p/>
     * No bonemeal on mushrooms , bonemeal doesn't always succeed
     *
     * @param event - Event that occurred.
     */
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    void onPlayerInteract(PlayerInteractEvent event)
    {
        Player player = event.getPlayer();
        World world = event.getPlayer().getWorld();
        Action action = event.getAction();

        final boolean noBonemealOnMushrooms = CFG.getBoolean(RootNode.NO_BONEMEAL_ON_MUSHROOMS, world.getName());
        final boolean playerBypasses = playerModule.playerBypasses(player, Feature.ANTIFARMING);

        // FEATURE: bonemeal doesn't work on mushrooms
        if (noBonemealOnMushrooms && action == Action.RIGHT_CLICK_BLOCK && !playerBypasses)
        {
            Block block = event.getClickedBlock();
            if (block.getType() == Material.RED_MUSHROOM || block.getType() == Material.BROWN_MUSHROOM)
            {
                // what's the player holding?
                Material materialInHand = event.getItem().getType();

                // if bonemeal, cancel the event
                if (materialInHand == Material.BONE_MEAL)
                {
                    event.setCancelled(true);
                }
            }
        }
    }


    /**
     * When a player breaks a block...
     * <p/>
     * no netherwart farming
     */
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onBlockBreak(BlockBreakEvent breakEvent)
    {
        Player player = breakEvent.getPlayer();
        Block block = breakEvent.getBlock();
        World world = block.getWorld();

        final boolean noFarmingNetherWart = CFG.getBoolean(RootNode.NO_FARMING_NETHER_WART, world.getName());
        final boolean playerBypasses = playerModule.playerBypasses(player, Feature.ANTIFARMING);

        // FEATURE: no nether wart farming (always drops exactly 1 nether wart when broken)
        if (!playerBypasses && noFarmingNetherWart)
        {
            if (block.getType() == Material.NETHER_WART)
            {
                block.getDrops().clear();
                block.getDrops().add(new ItemStack(Material.NETHER_WART));
            }
        }
    }


    /**
     * When a player places a block...
     * <p/>
     * no farming nether wart
     */
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onBlockPlace(BlockPlaceEvent placeEvent)
    {
        Player player = placeEvent.getPlayer();
        Block block = placeEvent.getBlock();
        World world = block.getWorld();

        final boolean noFarmingNetherWart = CFG.getBoolean(RootNode.NO_FARMING_NETHER_WART, world.getName());
        final boolean playerBypasses = playerModule.playerBypasses(player, Feature.ANTIFARMING);

        // FEATURE: no farming/placing nether wart
        if (!playerBypasses && noFarmingNetherWart && block.getType() == Material.NETHER_WART)
        {
            placeEvent.setCancelled(true);
            return;
        }
    }


    /**
     * When a block grows...
     * <p/>
     * fewer seeds = shrinking crops. when a plant grows to its full size, it may be replaced by a dead shrub
     */
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onBlockGrow(BlockGrowEvent event)
    {
        World world = event.getBlock().getWorld();

        final boolean weakCropsEnabled = CFG.getBoolean(RootNode.WEAK_FOOD_CROPS, world.getName());

        // FEATURE:
        if (weakCropsEnabled && plugin.getModuleForClass(BlockModule.class).plantDies(event.getBlock(), event.getNewState().getData()))
        {
            event.setCancelled(true);
            //shrub gets removed on farmland
            event.getBlock().getRelative(BlockFace.DOWN).setType(Material.DIRT);
            event.getBlock().setType(Material.DEAD_BUSH); // dead shrub
        }
    }


    /**
     * when a tree or mushroom grows...
     * <p/>
     * no big plant growth in deserts
     *
     * @param event - Event that occurred.
     */
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onStructureGrow(StructureGrowEvent event)
    {
        World world = event.getWorld();
        Block block = event.getLocation().getBlock();

        boolean aridDesertsEnabled = CFG.getBoolean(RootNode.ARID_DESSERTS, world.getName());


        if (aridDesertsEnabled)
        {
            Biome biome = block.getBiome();
            if (biome == Biome.DESERT || biome == Biome.DESERT_HILLS)
            {
                event.setCancelled(true);
            }
        }
    }


    /**
     * when a dispenser dispenses...
     *
     * @param event - Event that occurred.
     */
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    void onBlockDispense(BlockDispenseEvent event)
    {
        World world = event.getBlock().getWorld();

        final boolean dontMoveWaterEnabled = CFG.getBoolean(RootNode.DONT_MOVE_WATER_SOURCE_BLOCKS, world.getName());

        // FEATURE: can't move water source blocks, including buckets of fish
        if (dontMoveWaterEnabled)
        {
            Material material = event.getItem().getType();

            // only care about water
            if (material == Material.WATER_BUCKET || material == Material.COD_BUCKET ||
                material == Material.PUFFERFISH_BUCKET || material == Material.SALMON_BUCKET ||
                material == Material.TROPICAL_FISH_BUCKET)
            {
                // plan to evaporate the water next tick
                Block block = event.getVelocity().toLocation(world).getBlock();
                EvaporateWaterTask task = new EvaporateWaterTask(block, plugin);
                plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, task, 1L);
            }
        }
    }


    /**
     * when a sheep regrows its wool...
     *
     * @param event - Event that occurred.
     */
    @EventHandler(ignoreCancelled = true)
    public void onSheepRegrowWool(SheepRegrowWoolEvent event)
    {
        World world = event.getEntity().getWorld();

        boolean sheepRegrowWhiteEnabled = CFG.getBoolean(RootNode.SHEEP_REGROW_WHITE_WOOL, world.getName());

        // FEATURE: sheep are all white, and may be dyed only temporarily
        if (sheepRegrowWhiteEnabled)
        {
            Sheep sheep = event.getEntity();
            if (sheep.isSheared())
                sheep.setColor(DyeColor.WHITE);
        }
    }


    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onEntitySpawn(CreatureSpawnEvent event)
    {
        LivingEntity entity = event.getEntity();
        CreatureSpawnEvent.SpawnReason reason = event.getSpawnReason();
        World world = event.getLocation().getWorld();

        final boolean sheepRegrowWhiteEnabled = CFG.getBoolean(RootNode.SHEEP_REGROW_WHITE_WOOL, world.getName());

        //Breed Sheep spawn white
        if (sheepRegrowWhiteEnabled && entity.getType() == EntityType.SHEEP)
        {
            Sheep sheep = (Sheep) entity;
            if (reason.equals(CreatureSpawnEvent.SpawnReason.BREEDING))
            {
                sheep.setColor(DyeColor.WHITE);
                return;
            }
        }
    }


    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onSquidSpawn(CreatureSpawnEvent event)
    {
        LivingEntity entity = event.getEntity();
        CreatureSpawnEvent.SpawnReason reason = event.getSpawnReason();
        World world = event.getLocation().getWorld();

        final boolean restrictedSquidSpawns = CFG.getBoolean(RootNode.SQUID_ONLY_SPAWN_IN_OCEAN, world.getName());

        if (restrictedSquidSpawns && entity.getType() == EntityType.SQUID && reason.equals(CreatureSpawnEvent.SpawnReason.NATURAL))
        {
            switch (entity.getLocation().getBlock().getBiome())
            {
                case DEEP_OCEAN:
                case OCEAN:
                    return;
                default:
                    event.setCancelled(true);
            }
        }
    }


    @EventHandler
    public void onEntityDeath(EntityDeathEvent event)
    {
        LivingEntity entity = event.getEntity();
        World world = entity.getWorld();

        final boolean animalExpNerfEnabled = CFG.getBoolean(RootNode.ANIMAL_EXP_NERF, world.getName());

        // FEATURE: animals don't drop experience (because they're easy to "farm")
        if (animalExpNerfEnabled && entity instanceof Animals)
        {
            event.setDroppedExp(0);
        }
    }


    /**
     * When an Iron Golem dies
     */
    @EventHandler
    public void onGolemDeath(EntityDeathEvent event)
    {
        Entity entity = event.getEntity();

        final boolean golemNerf = CFG.getBoolean(RootNode.IRON_GOLEM_NERF, entity.getWorld().getName());

        if (event.getEntity() instanceof IronGolem && golemNerf)
        {
            event.getDrops().clear();
        }
    }


    /**
     * when a player crafts something...
     *
     * @param event - Event that occurred.
     */
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onItemCrafted(CraftItemEvent event)
    {
        World world = null;
        Material result = event.getRecipe().getResult().getType();
        InventoryHolder human = event.getInventory().getHolder();
        Player player = null;
        if (human instanceof Player)
        {
            player = (Player) human;
            world = player.getWorld();
        }

        final boolean cantCraftMelons = world != null && CFG.getBoolean(RootNode.CANT_CRAFT_MELONSEEDS, world.getName());
        final boolean playerBypasses = playerModule.playerBypasses(player, Feature.ANTIFARMING);


        if (!playerBypasses && cantCraftMelons)
        {
            // FEATURE: no crafting melon seeds
            if (result == Material.MELON_SEEDS || result == Material.PUMPKIN_SEEDS)
            {
                event.setCancelled(true);
                plugin.getModuleForClass(MsgModule.class).send(player, MessageNode.NO_CRAFTING_MELON_SEEDS);
                return;
            }
        }
    }


    /**
     * when a player empties a bucket...
     *
     * @param event - Event that occurred.
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerEmptyBucket(PlayerBucketEmptyEvent event)
    {
        Player player = event.getPlayer();
        World world = player.getWorld();

        final boolean dontMoveWaterEnabled = CFG.getBoolean(RootNode.DONT_MOVE_WATER_SOURCE_BLOCKS, world.getName());
        final boolean playerBypasses = playerModule.playerBypasses(player, Feature.ANTIFARMING);

        Material material = event.getBucket();

        // FEATURE: can't move water source blocks, including buckets of fish
        if (!playerBypasses && dontMoveWaterEnabled &&
                (material == Material.WATER_BUCKET || material == Material.COD_BUCKET ||
                material == Material.PUFFERFISH_BUCKET || material == Material.SALMON_BUCKET ||
                material == Material.TROPICAL_FISH_BUCKET))
        {
            // plan to change this block into a non-source block on the next tick
            Block block = event.getBlock();
            blockModule.mark(block);
            EvaporateWaterTask task = new EvaporateWaterTask(block, plugin);
            plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, task, 10L);
        }
    }


    /**
     * When a player fills a bucket
     * <p/>
     * prevent players from quickly picking up buckets again (around lava etc.)
     */
    @EventHandler(ignoreCancelled = true)
    public void onPlayerFillBucket(PlayerBucketFillEvent event)
    {
        Block block = event.getBlockClicked().getRelative(event.getBlockFace());
        if (blockModule.isMarked(block))
        {
            event.setCancelled(true);
            final Player player = event.getPlayer();
            //Bucket displays as full, derpy inventories, run next tick
            plugin.getServer().getScheduler().runTask(plugin, new Runnable()
            {
                @Override
                public void run()
                {
                    if (player != null)
                        player.updateInventory();
                }
            });
        }
    }

    /**
     * When a player place kelp or seagrass in markd water.
     * 
     * @param event Event that occurred.
     */
    @EventHandler(ignoreCancelled = true)
    public void onPlaceKelpOrSeaGrass(BlockPlaceEvent event) {
        Block placedBlock = event.getBlockPlaced();
        if ((placedBlock.getType() == Material.KELP || placedBlock.getType() == Material.SEAGRASS)
                && blockModule.isMarked(placedBlock)) {
            event.setCancelled(true);
        }
    }
}
