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

package com.codehusky.huskyguard;

import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldedit.sponge.SpongeAdapter;
import com.sk89q.worldedit.sponge.SpongeWorld;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.config.WorldConfiguration;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.world.LoadWorldEvent;
import org.spongepowered.api.event.world.UnloadWorldEvent;
import org.spongepowered.api.event.world.chunk.LoadChunkEvent;
import org.spongepowered.api.event.world.chunk.UnloadChunkEvent;
import org.spongepowered.api.world.Chunk;
import org.w3c.dom.events.EventListener;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class SpongeRegionContainer extends RegionContainer {

    /**
     * Invalidation frequency in ticks.
     */
    private static final int CACHE_INVALIDATION_INTERVAL = 2;

    private final HuskyGuardPlugin plugin;

    /**
     * Create a new instance.
     *
     * @param plugin the plugin
     */
    public SpongeRegionContainer(HuskyGuardPlugin plugin) {
        this.plugin = plugin;
    }
    @Listener
    public void onWorldLoad(LoadWorldEvent event) {
        load(SpongeAdapter.adapt(event.getTargetWorld()));
    }

    @Listener
    public void onWorldUnload(UnloadWorldEvent event) {
        unload(SpongeAdapter.adapt(event.getTargetWorld()));
    }

    @Listener
    public void onChunkLoad(LoadChunkEvent event) {
        RegionManager manager = get(SpongeAdapter.adapt(event.getTargetChunk().getWorld()));
        if (manager != null) {
            Chunk chunk = event.getTargetChunk();
            manager.loadChunk(BlockVector2.at(chunk.getPosition().getX(), chunk.getPosition().getZ()));
        }
    }

    @Listener
    public void onChunkUnload(UnloadChunkEvent event) {
        RegionManager manager = get(SpongeAdapter.adapt(event.getTargetChunk().getWorld()));
        if (manager != null) {
            Chunk chunk = event.getTargetChunk();
            manager.unloadChunk(BlockVector2.at(chunk.getPosition().getX(), chunk.getPosition().getZ()));
        }
    }
    @Override
    public void initialize() {
        super.initialize();
        Sponge.getEventManager().registerListeners(this, plugin);

        Sponge.getScheduler().createTaskBuilder().execute(cache::invalidateAll).delayTicks(CACHE_INVALIDATION_INTERVAL).intervalTicks(CACHE_INVALIDATION_INTERVAL).submit(plugin);
    }

    public void shutdown() {
        container.shutdown();
    }

    @Override
    @Nullable
    protected RegionManager load(World world) {
        checkNotNull(world);

        WorldConfiguration config = WorldGuard.getInstance().getPlatform().getGlobalStateManager().get(world);
        if (!config.useRegions) {
            return null;
        }

        RegionManager manager;

        synchronized (lock) {
            manager = container.load(world.getName());

            if (manager != null) {
                // Bias the region data for loaded chunks
                List<BlockVector2> positions = new ArrayList<>();
                for (Chunk chunk : ((SpongeWorld) world).getWorld().getLoadedChunks()) {
                    positions.add(BlockVector2.at(chunk.getPosition().getX(), chunk.getPosition().getZ()));
                }
                manager.loadChunks(positions);
            }
        }

        return manager;
    }

}
