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

package com.sk89q.worldedit.sponge;

import com.codehusky.huskyguard.*;
import com.codehusky.huskyguard.SpongePlayer;
import com.codehusky.huskyguard.protection.events.flags.FlagContextCreateEvent;
import com.codehusky.huskyguard.session.SpongeSessionManager;
import com.codehusky.huskyguard.util.report.*;
import com.sk89q.worldedit.extension.platform.Watchdog;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.sponge.SpongePlatform;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import com.sk89q.worldedit.util.report.ReportList;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.world.gamemode.GameMode;
import com.sk89q.worldedit.world.gamemode.GameModes;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.internal.platform.DebugHandler;
import com.sk89q.worldguard.internal.platform.StringMatcher;
import com.sk89q.worldguard.internal.platform.WorldGuardPlatform;
import com.sk89q.worldguard.protection.flags.FlagContext;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.session.SessionManager;
import com.sk89q.worldguard.util.profile.cache.ProfileCache;
import com.sk89q.worldguard.util.profile.resolver.*;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;

import javax.annotation.Nullable;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class SpongeHuskyGuardPlatform extends SpongePlatform implements WorldGuardPlatform {

    private SessionManager sessionManager;
    private SpongeConfigurationManager configuration;
    private SpongeRegionContainer regionContainer;
    private SpongeDebugHandler debugHandler;
    private StringMatcher stringMatcher;

    public SpongeHuskyGuardPlatform() {
        super();
    }

    @Nullable
    @Override
    public Watchdog getWatchdog() {
        return null;
    }

    @Override
    public String getPlatformName() {
        return "Sponge-codeHusky";
    }

    @Override
    public String getPlatformVersion() {
        return HuskyGuardPlugin.inst().getDescription().getVersion();
    }

    @Override
    public void notifyFlagContextCreate(FlagContext.FlagContextBuilder flagContextBuilder) {
        Bukkit.getServer().getPluginManager().callEvent(new FlagContextCreateEvent(flagContextBuilder));
    }

    @Override
    public SpongeConfigurationManager getGlobalStateManager() {
        return configuration;
    }

    @Override
    public StringMatcher getMatcher() {
        return stringMatcher;
    }

    @Override
    public SessionManager getSessionManager() {
        return this.sessionManager;
    }

    @Override
    public void broadcastNotification(String message) {
        Bukkit.broadcast(message, "worldguard.notify");
        Set<Permissible> subs = Bukkit.getPluginManager().getPermissionSubscriptions("worldguard.notify");
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            if (!(subs.contains(player) && player.hasPermission("worldguard.notify")) &&
                    HuskyGuardPlugin.inst().hasPermission(player, "worldguard.notify")) { // Make sure the player wasn't already broadcasted to.
                player.sendMessage(message);
            }
        }
        WorldGuard.logger.info(message);
    }

    @Override
    public void broadcastNotification(TextComponent component) {
        List<LocalPlayer>
                wgPlayers = Bukkit.getServer().getOnlinePlayers().stream().map(player -> HuskyGuardPlugin.inst().wrapPlayer(player)).collect(
                Collectors.toList());

        for (LocalPlayer player : wgPlayers) {
            if (player.hasPermission("worldguard.notify")) {
                player.print(component);
            }
        }
    }

    @Override
    public void load() {
        stringMatcher = new SpongeStringMatcher();
        sessionManager = new SpongeSessionManager();
        configuration = new SpongeConfigurationManager(HuskyGuardPlugin.inst());
        configuration.load();
        regionContainer = new SpongeRegionContainer(HuskyGuardPlugin.inst());
        regionContainer.initialize();
        debugHandler = new SpongeDebugHandler(HuskyGuardPlugin.inst());
    }

    @Override
    public void unload() {
        configuration.unload();
        regionContainer.shutdown();
    }

    @Override
    public RegionContainer getRegionContainer() {
        return this.regionContainer;
    }

    @Override
    public DebugHandler getDebugHandler() {
        return debugHandler;
    }

    @Override
    public GameMode getDefaultGameMode() {
        return GameModes.get(Sponge.getServer().getDefaultGameMode().name().toLowerCase());
    }

    @Override
    public Path getConfigDir() {
        return HuskyGuardPlugin.inst().getDataFolder().toPath();
    }

    @Override
    public void stackPlayerInventory(LocalPlayer localPlayer) {
        boolean ignoreMax = localPlayer.hasPermission("worldguard.stack.illegitimate");
        boolean ignoreDamaged = localPlayer.hasPermission("worldguard.stack.damaged");

        Player player = ((SpongePlayer) localPlayer).getPlayer();
        ArrayList<ItemStack> stacks = new ArrayList<>();
        player.getInventory().slots().forEach(slot -> {
            slot.peek().ifPresent(stacks::add);
        });
        ItemStack[] items = (ItemStack[]) stacks.toArray();
        int len = items.length;

        int affected = 0;

        for (int i = 0; i < len; i++) {
            ItemStack item = items[i];

            // Avoid infinite stacks and stacks with durability
            if (item == null || item.getQuantity() <= 0
                    || (!ignoreMax && item.getMaxStackQuantity() == 1)) {
                continue;
            }

            int max = ignoreMax ? 64 : item.getMaxStackQuantity();

            if (item.getQuantity() < max) {
                int needed = max - item.getQuantity(); // Number of needed items until max

                // Find another stack of the same type
                for (int j = i + 1; j < len; j++) {
                    ItemStack item2 = items[j];

                    // Avoid infinite stacks and stacks with durability
                    if (item2 == null || item2.getQuantity() <= 0
                            || (!ignoreMax && item.getMaxStackQuantity() == 1)) {
                        continue;
                    }

                    // Same type?
                    // Blocks store their color in the damage value
                    if (item2.getType() == item.getType() &&
                            (ignoreDamaged || SpongeUtil.getDurability(item) == SpongeUtil.getDurability(item2)) &&
                            (item.toContainer().equals(item2.toContainer()))) {
                        // This stack won't fit in the parent stack
                        if (item2.getQuantity() > needed) {
                            item.setQuantity(max);
                            item2.setQuantity(item2.getQuantity() - needed);
                            break;
                            // This stack will
                        } else {


                            item.setQuantity(item.getQuantity() + item2.getQuantity());
                            item2.setQuantity(0);
                            needed = max - item.getQuantity();
                        }

                        affected++;
                    }
                }
            }
        }

        /*if (affected > 0) {
            player.getInventory().setContents(items);
        }*/
    }

    @Override
    public void addPlatformReports(ReportList report) {
        report.add(new ServerReport());
        report.add(new PluginReport());
        report.add(new SchedulerReport());
        report.add(new ServicesReport());
        report.add(new WorldReport());
        report.add(new PerformanceReport());
    }

    @Override
    public ProfileService createProfileService(ProfileCache profileCache) {
        List<ProfileService> services = new ArrayList<>();
        services.add(SpongePlayerService.getInstance());
        services.add(HttpRepositoryService.forMinecraft());
        return new CacheForwardingService(new CombinedProfileService(services),
                profileCache);
    }

    @Nullable
    @Override
    public ProtectedRegion getSpawnProtection(World world) {
        if (world instanceof SpongeWorld) {
            org.spongepowered.api.world.World bWorld = ((SpongeWorld) world).getWorld();
            if (bWorld.getUniqueId().equals(((org.spongepowered.api.world.World)Sponge.getServer().getWorlds().toArray()[0]).getUniqueId())) {
                int radius = Sponge.getPlatform().;
                if (radius > 0) {
                    BlockVector3 spawnLoc = BukkitAdapter.asBlockVector(bWorld.getSpawnLocation());
                    return new ProtectedCuboidRegion("__spawn_protection__",
                            spawnLoc.subtract(radius, 0, radius).withY(world.getMinimumPoint().getY()),
                            spawnLoc.add(radius, 0, radius).withY(world.getMaxY()));
                }
            }
        }
        return null;
    }
}
