/*
 * WorldGuard, a suite of tools for Minecraft
 * Copyright (C) sk89q <http://www.sk89q.com>
 * Copyright (C) WorldGuard team and contributors
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.codehusky.huskyguard.util;

import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.block.tileentity.Bed;
import org.spongepowered.api.block.tileentity.TileEntity;
import org.spongepowered.api.block.tileentity.carrier.Chest;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Utility methods to deal with blocks.
 */
public final class Blocks {

    private Blocks() {
    }

    /**
     * Get a list of connected blocks to the given block, not including
     * the given block.
     *
     * @param block the block
     * @return a list of connected blocks, not including the given block
     */
    public static List<Location<World>> getConnected(Location<World> block) {
        BlockState state = block.getBlock(); //PaperLib.getBlockState(block, false).getState();
        //BlockData data = state.getBlockData();
        Optional<TileEntity> te  = block.getTileEntity();
        if (state.getType() == BlockTypes.BED) { // get other side of bed
            Bed bed = (Bed) te.get();
            List<Location<World>> connected = new ArrayList<>();
            bed.get(Keys.CONNECTED_DIRECTIONS).get().forEach(dir -> {
                connected.add(new Location<>(block.getExtent(),block.getBlockPosition().add(dir.asBlockOffset())));
            });
            return connected;
        } else if (state.getType() == BlockTypes.CHEST && te.isPresent()) { // get other side of chest
            Chest chest = (Chest)te.get();
            List<Location<World>> connected = new ArrayList<>();
            chest.getConnectedChests().forEach(c -> {
                if(!c.getLocation().equals(block))
                    connected.add(c.getLocation());
            });
            return connected;
        } else {
            return Collections.emptyList();
        }
    }

}
