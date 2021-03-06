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

import com.codehusky.huskyguard.HuskyGuardPlugin;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.config.ConfigurationManager;
import com.sk89q.worldguard.config.WorldConfiguration;
import com.sk89q.worldguard.session.MoveType;
import com.sk89q.worldguard.util.Locations;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.stream.Collectors;

public class HuskyGuardVehicleListener implements Listener {

    private HuskyGuardPlugin plugin;

    /**
     * Construct the object;
     *
     * @param plugin
     */
    public HuskyGuardVehicleListener(HuskyGuardPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Register events.
     */
    public void registerEvents() {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onVehicleMove(VehicleMoveEvent event) {
        Vehicle vehicle = event.getVehicle();
        if (vehicle.getPassengers().isEmpty()) return;
        List<Player> playerPassengers = vehicle.getPassengers().stream()
                .filter(ent -> ent instanceof Player).map(ent -> (Player) ent).collect(Collectors.toList());
        if (playerPassengers.isEmpty()) {
            return;
        }
        World world = vehicle.getWorld();
        ConfigurationManager cfg = WorldGuard.getInstance().getPlatform().getGlobalStateManager();
        WorldConfiguration wcfg = cfg.get(BukkitAdapter.adapt(world));

        if (wcfg.useRegions) {
            // Did we move a block?
            if (Locations.isDifferentBlock(BukkitAdapter.adapt(event.getFrom()), BukkitAdapter.adapt(event.getTo()))) {
                for (Player player : playerPassengers) {
                    LocalPlayer localPlayer = plugin.wrapPlayer(player);
                    Location lastValid;
                    if ((lastValid = WorldGuard.getInstance().getPlatform().getSessionManager().get(localPlayer)
                            .testMoveTo(localPlayer, BukkitAdapter.adapt(event.getTo()), MoveType.RIDE)) != null) {
                        vehicle.setVelocity(new Vector(0, 0, 0));
                        vehicle.teleport(event.getFrom());
                        if (Locations.isDifferentBlock(lastValid, BukkitAdapter.adapt(event.getFrom()))) {
                            Vector dir = player.getLocation().getDirection();
                            player.teleport(BukkitAdapter.adapt(lastValid).setDirection(dir));
                        }
                        return;
                    }
                }
            }
        }
    }
}
