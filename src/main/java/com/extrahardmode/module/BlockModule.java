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


package com.extrahardmode.module;


import com.extrahardmode.ExtraHardMode;
import com.extrahardmode.compatibility.CompatHandler;
import com.extrahardmode.config.RootConfig;
import com.extrahardmode.config.RootNode;
import com.extrahardmode.service.EHMModule;
import com.extrahardmode.task.BlockPhysicsCheckTask;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.FallingBlock;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.material.MaterialData;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Module that manages blocks and physics logic.
 */
public class BlockModule extends EHMModule
{
    /**
     * Marks a block/location for whatever reason... currently used by waterbucket restrictions
     */
    private final String MARK = "ExtraHardMode.Mark";

    private RootConfig CFG;

    private final Pattern slabPattern = Pattern.compile("(?!DOUBLE).*STEP");


    /**
     * Constructor.
     *
     * @param plugin - plugin instance.
     */
    public BlockModule(ExtraHardMode plugin)
    {
        super(plugin);
    }


    @Override
    public void starting()
    {
        CFG = plugin.getModuleForClass(RootConfig.class);
    }


    /**
     * Schedule the physics task
     *
     * @param block          - Target block.
     * @param recursionCount - Number of times to execute.
     * @param forceCheck     - Whether to force adjacent blocks to be checked for the first iteration
     * @param wait           - how many ticks to wait before the next task, mainly to prevent crashes when FallingBlocks collide
     */
    public void physicsCheck(Block block, int recursionCount, boolean forceCheck, int wait)
    {
        int id = plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new BlockPhysicsCheckTask(plugin, block, recursionCount, forceCheck), wait);
        // check if it was scheduled. If not, notify in console.
        if (id == -1)
        {
            plugin.getLogger().severe("Failed schedule BlockPhysicsCheck task!");
        }
    }


    /**
     * Makes one single block subject to gravity
     *
     * @param block          Block to apply physics to.
     * @param damageEntities if Entities should be damaged
     * @return the UUID of this FallingBlock
     */
    public UUID applyPhysics(Block block, boolean damageEntities)
    {
        /* Spawning Falling Blocks with type = AIR crashes the Minecraft client */
        if (block.getType() == Material.AIR)
            return null;

        // grass and mycel become dirt when they fall
        if ((block.getType() == Material.GRASS_BLOCK || block.getType() == Material.MYCELIUM) && CFG.getBoolean(RootNode.MORE_FALLING_BLOCKS_TURN_TO_DIRT, block.getWorld().getName()))
            block.setType(Material.DIRT);

        FallingBlock fallingBlock = block.getWorld().spawnFallingBlock(block.getLocation().add(0.5D, 0.0D, 0.5D), block.getBlockData());
        fallingBlock.setDropItem(CFG.getBoolean(RootNode.MORE_FALLING_BLOCKS_DROP_ITEM, block.getWorld().getName()));
        // remove original block
        CompatHandler.logFallingBlockFall(block);
        block.setType(Material.AIR);

        final boolean breakTorches = CFG.getBoolean(RootNode.MORE_FALLING_BLOCKS_BREAK_TORCHES, block.getWorld().getName());
        //TODO expand on this, it's only rudimentary, doesnt break torches if there are multiple fallingblocks (only breaks the first)
        if (breakTorches)
        {
            Block current = block;
            Block below = block.getRelative(BlockFace.DOWN);
            //Check downwards and break any obstructing blocks
            for (int y = current.getY(); y > 0; y--)
            {
                //Only breaks if the block below is solid and the block on top is transparent
                if (below.getType().isSolid())
                {
                    if (breaksFallingBlock(current.getType()))
                        current.breakNaturally();
                    //Will land on the block below
                    break;
                }
                current = below;
                below = current.getRelative(BlockFace.DOWN);
            }
        }

        if (damageEntities) //mark so we know the block is from us
            EntityHelper.markForProcessing(plugin, fallingBlock);

        EntityHelper.markAsOurs(plugin, fallingBlock);

        //TODO: Figure out how to make cancelable (ultra low priority)
        plugin.getServer().getPluginManager().callEvent(new EntityChangeBlockEvent(fallingBlock, block, Material.AIR.createBlockData()));

        return fallingBlock.getUniqueId();
    }


    /**
     * Mark this block for whatever reason
     * <p/>
     * remember to remove the mark as block metadata persists
     *
     * @param block to mark
     */
    public void mark(Block block)
    {
        block.setMetadata(MARK, new FixedMetadataValue(plugin, true));
    }


    /**
     * Removes Metadata from the block
     *
     * @param block to remove the metadata from
     */
    public void removeMark(Block block)
    {
        block.removeMetadata(MARK, plugin);
    }


    /**
     * Has this block been marked?
     *
     * @param block to check
     * @return if it has been marked
     */
    public boolean isMarked(Block block)
    {
        return block.getMetadata(MARK).size() > 0;
    }


    /**
     * Check if the given plant at the block should die.
     *
     * @param block        - Block to check.
     * @param newDataValue - Data value to replace.
     * @return True if plant should die, else false.
     */
    public boolean plantDies(Block block, MaterialData newDataValue)
    {
        World world = block.getWorld();

        final boolean weakFoodCropsEnabled = CFG.getBoolean(RootNode.WEAK_FOOD_CROPS, world.getName());
        final int lossRate = CFG.getInt(RootNode.WEAK_FOOD_CROPS_LOSS_RATE, world.getName());
        final boolean aridDesertsEnabled = CFG.getBoolean(RootNode.ARID_DESSERTS, world.getName());


        if (weakFoodCropsEnabled)
        {
            // not evaluated until the plant is nearly full grown
            //For some plants (netherwart, beetroot), this is at data value 3.

            int fullGrowthValue = 7;
            switch (block.getType())
            {
                case BEETROOTS:
                    fullGrowthValue = 3;
                    break;
                case WHEAT:
                case CARROTS:
                case POTATOES:
                    break;
                default:
                    return false;
            }

            //TODO: 1.13
            if (newDataValue.getData() >= fullGrowthValue)
            {
                int deathProbability = lossRate;

                // plants in the dark always die
                if (block.getLightFromSky() < 10)
                {
                    deathProbability = 100;
                } else
                {
                    Biome biome = block.getBiome();

                    // the desert environment is very rough on crops
                    if ((biome == Biome.DESERT || biome == Biome.DESERT_HILLS) && aridDesertsEnabled)
                    {
                        deathProbability += 50;
                    }

                    // unwatered crops are more likely to die
                    Block belowBlock = block.getRelative(BlockFace.DOWN);
                    byte moistureLevel = 0;
                    if (belowBlock.getType() == Material.FARMLAND)
                    {
                        moistureLevel = belowBlock.getData();
                    }

                    if (moistureLevel == 0)
                    {
                        deathProbability += 25;
                    }
                }

                if (plugin.random(deathProbability))
                {
                    return true;
                }
            }
        }

        return false;
    }


    /**
     * Get all "touching" BlockFaces including top/bottom
     */
    public BlockFace[] getTouchingFaces()
    {
        return new BlockFace[]{
                BlockFace.WEST,
                BlockFace.NORTH,
                BlockFace.EAST,
                BlockFace.SOUTH,
                BlockFace.UP,
                BlockFace.DOWN
        };
    }


    /**
     * All horizontal Blockfaces including diagonal onea
     *
     * @return Blockfaces[]
     */
    public static BlockFace[] getHorizontalAdjacentFaces()
    {
        return new BlockFace[]{
                BlockFace.WEST,
                BlockFace.NORTH_WEST,
                BlockFace.NORTH,
                BlockFace.NORTH_EAST,
                BlockFace.EAST,
                BlockFace.SOUTH_EAST,
                BlockFace.SOUTH,
                BlockFace.SOUTH_WEST
        };
    }


    /**
     * Get all the blocks in a specific area centered around the Location passed in
     *
     * @param loc    Center of the search area
     * @param height how many blocks up to check
     * @param radius of the search (cubic search radius)
     * @param tag    of Material to search for
     * @return all the Block with the given Type in the specified radius
     */
    public Block[] getBlocksInArea(Location loc, int height, int radius, Tag tag)
    {
        List<Block> blocks = new ArrayList<Block>();
        //Height
        for (int y = 0; y < height; y++)
        {
            for (int x = -radius; x <= radius; x++)
            {
                for (int z = -radius; z <= radius; z++)
                {
                    Block checkBlock = loc.getBlock().getRelative(x, y, z);
                    if (tag.isTagged(checkBlock.getType()))
                    {
                        blocks.add(checkBlock);
                    }
                }
            }
        }
        return blocks.toArray(new Block[blocks.size()]);
    }


    /**
     * Will a FallingBlock which lands on this Material break and drop to the ground?
     *
     * @param mat to check
     * @return boolean
     */
    public boolean breaksFallingBlock(Material mat)
    {
        return (mat.isTransparent() &&
                mat != Material.NETHER_PORTAL &&
                mat != Material.END_PORTAL) ||
                mat == Material.COBWEB ||
                mat == Material.DAYLIGHT_DETECTOR ||
                Tag.TRAPDOORS.isTagged(mat) ||
                Tag.SIGNS.isTagged(mat) ||
                Tag.WALL_SIGNS.isTagged(mat) ||
                //Match all slabs besides double slab
                slabPattern.matcher(mat.name()).matches();
    }


    /**
     * Returns if Material is a plant that should be affected by the farming Rules
     */
    public boolean isPlant(Material material)
    {
        return material.equals(Material.WHEAT)
                || material.equals(Material.POTATO)
                || material.equals(Material.CARROT)
                || material.equals(Material.MELON_STEM)
                || material.equals(Material.PUMPKIN_STEM)
                || material.equals(Material.BEETROOTS);
    }


    /**
     * Is this Material food for horses?
     *
     * @param material material to test
     * @return true if vegetable
     */
    public static boolean isHorseFood(Material material)
    {
        return material.equals(Material.CARROT)
                || material.equals(Material.POTATO)
                || material.equals(Material.APPLE)
                //|| material.equals(Material.HAY_BLOCK)
                || material.equals(Material.WHEAT);
    }


    /**
     * Is the given material a tool, e.g. doesn't stack
     */
    public static boolean isTool(Material material)
    {
        return material.name().endsWith("AXE") //axe & pickaxe
                || material.name().endsWith("SHOVEL")
                || material.name().endsWith("SWORD")
                || material.name().endsWith("HOE")
                || material.name().endsWith("BUCKET") //water, milk, lava,..
                || material.equals(Material.BOW)
                || material.equals(Material.FISHING_ROD)
                || material.equals(Material.CLOCK)
                || material.equals(Material.COMPASS)
                || material.equals(Material.FLINT_AND_STEEL);
    }


    /**
     * is the given material armor
     */
    public boolean isArmor(Material material)
    {
        return material.name().endsWith("HELMET")
                || material.name().endsWith("CHESTPLATE")
                || material.name().endsWith("LEGGINGS")
                || material.name().endsWith("BOOTS");
    }


    /**
     * Consider this block a natural block for spawning?
     */
    public boolean isNaturalSpawnMaterial(Material material)
    {
        return material == Material.GRASS_BLOCK
                || material == Material.DIRT
                || material == Material.STONE
                || material == Material.SAND
                || material == Material.GRAVEL
                || material == Material.MOSSY_COBBLESTONE
                || material == Material.OBSIDIAN
                || material == Material.COBBLESTONE
                || material == Material.BEDROCK
                || material == Material.AIR      //Ghast, Bat
                || material == Material.WATER;  //Squid
    }


    /**
     * Is this a natural block for netherspawning?
     */
    public boolean isNaturalNetherSpawn(Material material)
    {
        switch (material)
        {
            case NETHERRACK:
            case NETHER_BRICK: //I'm guessing this is the nether brick item, not the block. If so, this should be removed.
            case NETHER_BRICKS:
            case NETHER_BRICK_SLAB:
            case SOUL_SAND:
            case GRAVEL:
            case AIR:
                return true;
        }
        return false;
    }


    /**
     * Determine if block is of the axis and placed in a weird angle. Dunno how to explain :D
     *
     * @return true if tje block is of the axis and placement should be blocked
     */
    public static boolean isOffAxis(Block playerBlock, Block placed, Block against)
    {
        /* Disallow placing where the x's are if there is air beneath the block. This fixes the torch/fence exploit
                 x|x
        x         |      x
       ===========P===========
                  |       x
                x |
         */
        if (placed.getRelative(BlockFace.DOWN).getType() == Material.AIR)
            if (placed.getX() != against.getX() /*placed onto the side*/ && playerBlock.getX() == against.getX())
                return true;
            else if (placed.getZ() != against.getZ() && playerBlock.getZ() == against.getZ())
                return true;
        return false;
    }


    /**
     * Get the Material that will be dropped if this Block is broken by a player e.g. stone -> cobblestone ice -> nothing Note: This method doesn't have all blocks and is only
     * meant for blocks that you dont want to drop like grass/ice blocks
     *
     * @param mat to get the drop for
     */
    public static Material getDroppedMaterial(Material mat)
    {
        if (Tag.LEAVES.isTagged(mat))
            return Material.AIR;

        switch (mat)
        {
            case GRASS_BLOCK:
            case FARMLAND:
                return Material.DIRT;
            case STONE:
                return Material.COBBLESTONE;
            case COAL_ORE:
                return Material.COAL;
            case LAPIS_ORE:
                return Material.INK_SAC;
            case EMERALD_ORE:
                return Material.EMERALD;
            case REDSTONE_ORE:
                return Material.REDSTONE;
            case DIAMOND_ORE:
                return Material.DIAMOND;
            case NETHER_QUARTZ_ORE:
                return Material.QUARTZ;
            case ICE:
            case SPAWNER:
                return Material.AIR;
        }
        return mat;
    }


    public static boolean isOneOf(Block block, Material... materials)
    {
        for (Material material : materials)
            if (block.getType() == material)
                return true;
        return false;
    }


    @Override
    public void closing()
    {/*ignored*/}
}
