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

import com.codehusky.huskyguard.SpongeConfigurationManager;
import com.codehusky.huskyguard.HuskyGuardPlugin;
import com.sk89q.worldguard.WorldGuard;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.PluginManager;

/**
 * @author zml2008
 */
public class HuskyGuardServerListener implements Listener {

    private final HuskyGuardPlugin plugin;

    public HuskyGuardServerListener(HuskyGuardPlugin plugin) {
        this.plugin = plugin;
    }

    public void registerEvents() {
        PluginManager pm = plugin.getServer().getPluginManager();
        pm.registerEvents(this, plugin);
    }

    @EventHandler
    public void onPluginEnable(PluginEnableEvent event) {
        if (event.getPlugin().getDescription().getName().equalsIgnoreCase("CommandBook")) {
            ((SpongeConfigurationManager) WorldGuard.getInstance().getPlatform().getGlobalStateManager()).updateCommandBookGodMode();
        }
    }

    @EventHandler
    public void onPluginDisable(PluginDisableEvent event) {
        if (event.getPlugin().getDescription().getName().equalsIgnoreCase("CommandBook")) {
            ((SpongeConfigurationManager) WorldGuard.getInstance().getPlatform().getGlobalStateManager()).updateCommandBookGodMode();
        }
    }
}
