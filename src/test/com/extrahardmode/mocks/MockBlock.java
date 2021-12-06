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

package com.extrahardmode.mocks;


import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Mock a Block
 * <pre>
 *     getWorld
 * </pre>
 */
public class MockBlock
{
    private final Block block = mock(Block.class);


    public MockBlock setWorld(World world)
    {
        when(block.getWorld()).thenReturn(world);
        return this;
    }


    public MockBlock setLocation(int x, int y, int z)
    {
        when(this.block.getX()).thenReturn(x);
        when(this.block.getY()).thenReturn(y);
        when(this.block.getZ()).thenReturn(z);
        return this;
    }


    public MockBlock setRelative(BlockFace face, Block block)
    {
        when(this.block.getRelative(any(BlockFace.class))).thenReturn(block);
        return this;
    }


    public MockBlock setMaterial(Material material)
    {
        when(block.getType()).thenReturn(material);
        return this;
    }


    public Block get()
    {
        return block;
    }
}
