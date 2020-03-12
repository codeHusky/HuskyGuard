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

package com.codehusky.huskyguard.listener;

import com.codehusky.huskyguard.SpongeWorldConfiguration;
import com.codehusky.huskyguard.HuskyGuardPlugin;
import com.codehusky.huskyguard.cause.Cause;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.config.ConfigurationManager;
import com.sk89q.worldguard.config.WorldConfiguration;
import com.sk89q.worldguard.domains.Association;
import com.sk89q.worldguard.protection.DelayedRegionOverlapAssociation;
import com.sk89q.worldguard.protection.association.Associables;
import com.sk89q.worldguard.protection.association.RegionAssociable;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import io.papermc.lib.PaperLib;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Abstract listener to ease creation of listeners.
 */
class AbstractListener implements Listener {

    private final HuskyGuardPlugin plugin;

    /**
     * Construct the listener.
     *
     * @param plugin an instance of WorldGuardPlugin
     */
    public AbstractListener(HuskyGuardPlugin plugin) {
        checkNotNull(plugin);
        this.plugin = plugin;
    }

    /**
     * Register events.
     */
    public void registerEvents() {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    /**
     * Get the plugin.
     *
     * @return the plugin
     */
    protected HuskyGuardPlugin getPlugin() {
        return plugin;
    }

    /**
     * Get the global configuration.
     *
     * @return the configuration
     */
    protected static ConfigurationManager getConfig() {
        return WorldGuard.getInstance().getPlatform().getGlobalStateManager();
    }

    /**
     * Get the world configuration given a world.
     *
     * @param world The world to get the configuration for.
     * @return The configuration for {@code world}
     */
    protected static WorldConfiguration getWorldConfig(World world) {
        return getConfig().get(world);
    }

    /**
     * Get the world configuration given a player.
     *
     * @param player The player to get the wold from
     * @return The {@link WorldConfiguration} for the player's world
     */
    protected static WorldConfiguration getWorldConfig(LocalPlayer player) {
        return getWorldConfig((World) player.getExtent());
    }

    /**
     * Return whether region support is enabled.
     *
     * @param world the world
     * @return true if region support is enabled
     */
    protected static boolean isRegionSupportEnabled(World world) {
        return getWorldConfig(world).useRegions;
    }

    protected RegionAssociable createRegionAssociable(Cause cause) {
        Object rootCause = cause.getRootCause();

        if (!cause.isKnown()) {
            return Associables.constant(Association.NON_MEMBER);
        } else if (rootCause instanceof Player) {
            return getPlugin().wrapPlayer((Player) rootCause);
        } else if (rootCause instanceof User) {
            return getPlugin().wrapOfflinePlayer((User) rootCause);
        } else if (rootCause instanceof Entity) {
            RegionQuery query = WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery();
            final Entity entity = (Entity) rootCause;
            Location loc;
            if (PaperLib.isPaper()
                    && ((SpongeWorldConfiguration) getWorldConfig(BukkitAdapter.adapt(entity.getWorld()))).usePaperEntityOrigin) {
                loc = entity.getOrigin();
                if (loc == null) {
                    loc = entity.getLocation();
                }
            } else {
                loc = entity.getLocation();
            }
            return new DelayedRegionOverlapAssociation(query, BukkitAdapter.adapt(loc));
        } else if (rootCause instanceof Block) {
            RegionQuery query = WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery();
            return new DelayedRegionOverlapAssociation(query, BukkitAdapter.adapt(((Block) rootCause).getLocation()));
        } else {
            return Associables.constant(Association.NON_MEMBER);
        }
    }
}
