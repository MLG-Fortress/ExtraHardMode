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
import com.extrahardmode.mocks.MockBlock;
import com.extrahardmode.mocks.MockExtraHardMode;
import com.extrahardmode.mocks.MockLocation;
import com.extrahardmode.mocks.MockWorld;
import com.extrahardmode.mocks.events.MockCreatureSpawnEvent;
import com.extrahardmode.module.BlockModule;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TestAntiGrinder
{
    private final ExtraHardMode plugin = new MockExtraHardMode().get();

    private final RootConfig CFG = new RootConfig(plugin);

    private final AntiGrinder module = new AntiGrinder(plugin, CFG, new BlockModule(plugin));


    @Before
    public void prepare()
    {
        //Enable AntiGrinder in the Config
        CFG.set("world", RootNode.INHIBIT_MONSTER_GRINDERS, true);
    }


    @Test
    public void spawnerSpawns()
    {
        CreatureSpawnEvent event = new MockCreatureSpawnEvent(EntityType.BLAZE, "world", CreatureSpawnEvent.SpawnReason.SPAWNER).get();
        assertFalse("Spawners should drop no exp", module.handleEntitySpawn(event));

        event = new MockCreatureSpawnEvent(EntityType.ENDERMAN, "world", CreatureSpawnEvent.SpawnReason.SPAWNER).get();
        assertFalse("Spawners should drop no exp", module.handleEntitySpawn(event));
    }


    @Test
    public void zombieSpawns()
    {
        MockCreatureSpawnEvent event = new MockCreatureSpawnEvent(EntityType.ZOMBIE, "world", CreatureSpawnEvent.SpawnReason.VILLAGE_INVASION);

        //Set a Block at the given Location
        MockBlock block = new MockBlock();
        block.setWorld(event.getWorld().get());
        MockLocation location = event.getLocation();
        location.setBlock(block);
        event.setLocation(location);

        //Set a Block beneath the "SpawnBlock"
        MockBlock relative = new MockBlock();
        relative.setWorld(event.getWorld().get());
        relative.setMaterial(Material.DIRT);
        block.setRelative(BlockFace.DOWN, relative.get());

        //Set the Environment to OverWorld
        MockWorld world = event.getWorld();
        world.setEnvironment(World.Environment.NORMAL);

        assertTrue("Zombie spawn succeeds", module.handleEntitySpawn(event.get()));
    }


    @Test
    public void naturalSpawns()
    {
        MockCreatureSpawnEvent event = new MockCreatureSpawnEvent(EntityType.ZOMBIFIED_PIGLIN, "world", CreatureSpawnEvent.SpawnReason.NATURAL);

        //Set a Block at the given Location
        MockBlock block = new MockBlock();
        block.setWorld(event.getWorld().get());
        MockLocation location = event.getLocation();
        location.setBlock(block);
        event.setLocation(location);

        //Set a Block beneath the "SpawnBlock"
        MockBlock relative = new MockBlock();
        relative.setWorld(event.getWorld().get());
        relative.setMaterial(Material.NETHERRACK);
        block.setRelative(BlockFace.DOWN, relative.get());

        //Set the Environment to OVERWORLD
        MockWorld world = event.getWorld();
        world.setEnvironment(World.Environment.NETHER);

        assertTrue("Natural spawn in the Nether failed", module.handleEntitySpawn(event.get()));


        world.setEnvironment(World.Environment.NETHER);

        //Cobble is not natural for the nether
        relative.setMaterial(Material.COBBLESTONE);
        block.setRelative(BlockFace.DOWN, relative.get());

        assertFalse("Natural spawn in a not natural Location succeeded", module.handleEntitySpawn(event.get()));


        //NetherRack doesn't spawn in the OverWorld
        relative.setMaterial(Material.NETHERRACK);
        block.setRelative(BlockFace.DOWN, relative.get());

        world.setEnvironment(World.Environment.NORMAL);

        assertFalse("Spawn on NetherRack in the OverWorld should have failed", module.handleEntitySpawn(event.get()));
    }
}
