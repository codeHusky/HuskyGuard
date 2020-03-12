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

package com.codehusky.huskyguard.session;

import com.codehusky.huskyguard.SpongePlayer;
import com.codehusky.huskyguard.HuskyGuardPlugin;
import com.codehusky.huskyguard.event.player.ProcessPlayerEvent;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.session.AbstractSessionManager;
import com.sk89q.worldguard.session.Session;
import com.sk89q.worldguard.util.profile.Profile;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Collection;

/**
 * Keeps tracks of sessions and also does session-related handling
 * (flags, etc.).
 */
public class SpongeSessionManager extends AbstractSessionManager implements Runnable, Listener {

    /**
     * Re-initialize handlers and clear "last position," "last state," etc.
     * information for all players.
     */
    @Override
    public void resetAllStates() {
        Collection<? extends Player> players = Bukkit.getServer().getOnlinePlayers();
        for (Player player : players) {
            SpongePlayer spongePlayer = new SpongePlayer(HuskyGuardPlugin.inst(), player);
            Session session = getIfPresent(spongePlayer);
            if (session != null) {
                session.resetState(spongePlayer);
            }
        }
    }

    @EventHandler
    public void onPlayerProcess(ProcessPlayerEvent event) {
        // Pre-load a session
        LocalPlayer player = HuskyGuardPlugin.inst().wrapPlayer(event.getPlayer());
        get(player).initialize(player);
        WorldGuard.getInstance().getExecutorService().submit(() ->
            WorldGuard.getInstance().getProfileCache().put(new Profile(player.getUniqueId(), player.getName())));
    }

    @Override
    public void run() {
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            LocalPlayer localPlayer = HuskyGuardPlugin.inst().wrapPlayer(player);
            get(localPlayer).tick(localPlayer);
        }
    }

    @Override
    public boolean hasBypass(LocalPlayer player, World world) {
        if (player instanceof SpongePlayer) {
            if (((SpongePlayer) player).getPlayer().hasMetadata("NPC")
                    && WorldGuard.getInstance().getPlatform().getGlobalStateManager().get(world).fakePlayerBuildOverride)
                return true;
        }
        return super.hasBypass(player, world);
    }
}
