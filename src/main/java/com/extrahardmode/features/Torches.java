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
import com.extrahardmode.LooseTags;
import com.extrahardmode.config.RootConfig;
import com.extrahardmode.config.RootNode;
import com.extrahardmode.config.messages.MessageNode;
import com.extrahardmode.module.MsgModule;
import com.extrahardmode.module.PlayerModule;
import com.extrahardmode.service.Feature;
import com.extrahardmode.service.ListenerModule;
import com.extrahardmode.service.PermissionNode;
import com.extrahardmode.task.RemoveExposedTorchesTask;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

/**
 * Torches
 * <p/>
 * can't be attached to loose blocks , get washed away when it rains
 */
public class Torches extends ListenerModule
{
    private RootConfig CFG;

    private MsgModule messenger;

    private PlayerModule playerModule;


    public Torches(ExtraHardMode plugin)
    {
        super(plugin);
    }


    @Override
    public void starting()
    {
        super.starting();
        CFG = plugin.getModuleForClass(RootConfig.class);
        messenger = plugin.getModuleForClass(MsgModule.class);
        playerModule = plugin.getModuleForClass(PlayerModule.class);
    }


    /**
     * When a block is placed
     * <p/>
     * players can't attach torches to loose blocks like sand/dirt
     */
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onBlockPlace(BlockPlaceEvent placeEvent)
    {
        Player player = placeEvent.getPlayer();
        Block block = placeEvent.getBlock();
        Material blockType = null; //cached only if either feature is enabled. Helps minimize extra calls to this semi-expensive(?) method
        World world = block.getWorld();

        final boolean limitedTorchPlacement = CFG.getBoolean(RootNode.LIMITED_TORCH_PLACEMENT, world.getName());
        final boolean soundFizzEnabled = CFG.getBoolean(RootNode.SOUNDS_TORCH_FIZZ, world.getName());
        final int torchMinY = CFG.getInt(RootNode.STANDARD_TORCH_MIN_Y, world.getName());
        final boolean playerBypasses = playerModule.playerBypasses(player, Feature.TORCHES);

        // FEATURE: no standard torches, jack o lanterns, or fire on top of netherrack near diamond level
        if (torchMinY > 0 && !playerBypasses)
        {
            if (world.getEnvironment() == World.Environment.NORMAL && block.getY() < torchMinY)
            {
                blockType = block.getType();
                switch (blockType)
                {
                    case FIRE:
                        if (block.getRelative(BlockFace.DOWN).getType() != Material.NETHERRACK)
                            break;
                    case TORCH:
                    case WALL_TORCH:
                    case JACK_O_LANTERN:
                        messenger.send(player, MessageNode.NO_TORCHES_HERE, PermissionNode.SILENT_NO_TORCHES_HERE, Sound.ENTITY_GENERIC_EXTINGUISH_FIRE, 20);
                        placeEvent.setCancelled(true);
                        return;
                }
            }
        }

        // FEATURE: players can't attach torches to common "soft" blocks
        if (limitedTorchPlacement && !playerBypasses)
        {
            if (blockType == null)
                blockType = block.getType();

            if (LooseTags.TORCH.isTagged(blockType))
            {

                BlockData blockData = block.getBlockData();
                Material attachmentMaterial = (blockData instanceof Directional)
                        // wall torch
                        ? block.getRelative(((Directional) blockData).getFacing().getOppositeFace()).getType()
                        // torch on ground
                        : block.getRelative(BlockFace.DOWN).getType();

                switch (attachmentMaterial)
                {
                    case DIRT:
                    case GRASS_BLOCK:
                    case GRASS:
                    case SAND:
                    case GRAVEL:
                    case COARSE_DIRT:
                    case MYCELIUM:
                    case PODZOL:
                    case SOUL_SAND:
                        if (soundFizzEnabled)
                            messenger.send(player, MessageNode.LIMITED_TORCH_PLACEMENTS, PermissionNode.SILENT_LIMITED_TORCH_PLACEMENT, Sound.ENTITY_GENERIC_EXTINGUISH_FIRE, 20);
                        placeEvent.setCancelled(true);
                }
            }
        }
    }


    /**
     * When the weather changes... rainfall breaks exposed torches (exposed to the sky)
     *
     * @param event - Event that occurred.
     */
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onWeatherChange(WeatherChangeEvent event)
    {
        World world = event.getWorld();

        plugin.debug(world, "WeatherChangeEvent called. toWeatherState: " + event.toWeatherState());

        final boolean rainBreaksTorchesEnabled = CFG.getBoolean(RootNode.RAIN_BREAKS_TORCHES, world.getName());
        final boolean rainExtinguishesCampfiresEnabled = CFG.getBoolean(RootNode.RAIN_EXTINGUISHES_CAMPFIRES, world.getName());
        final boolean snowBreaksCrops = CFG.getBoolean(RootNode.SNOW_BREAKS_CROPS, world.getName());

        if (event.toWeatherState()) //is it raining
        {
            if (rainBreaksTorchesEnabled || rainExtinguishesCampfiresEnabled || snowBreaksCrops)
            {
                plugin.debug(world, "WeatherChangeEvent says the sky is now falling and will proceed to massacre torches (and exposed crops in snow biomes)");
                // plan to remove torches chunk by chunk gradually throughout the rainperiod
                Chunk[] chunks = world.getLoadedChunks();
                if (chunks.length > 0)
                {
                    int i;
                    int startOffset = plugin.getRandom().nextInt(chunks.length);
                    for (i = 0; i < chunks.length; i++)
                    {
                        Chunk chunk = chunks[(startOffset + i) % chunks.length];

                        RemoveExposedTorchesTask task = new RemoveExposedTorchesTask(plugin, chunk);
                        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, task, i * 100L);
                    }
                    plugin.debug(world, "Scheduled " + i + " tasks.");
                }
            }
        }
    }
}
