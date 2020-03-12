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

import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.blocks.BaseItemStack;
import com.sk89q.worldedit.entity.BaseEntity;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.extent.inventory.BlockBag;
import com.sk89q.worldedit.function.mask.Mask;
import com.sk89q.worldedit.internal.cui.CUIEvent;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.Vector3;
import com.sk89q.worldedit.session.SessionKey;
import com.sk89q.worldedit.util.Direction;
import com.sk89q.worldedit.util.HandSide;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldedit.util.auth.AuthorizationException;
import com.sk89q.worldedit.util.formatting.text.Component;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.world.block.BaseBlock;
import com.sk89q.worldedit.world.block.BlockStateHolder;
import com.sk89q.worldedit.world.gamemode.GameMode;
import com.sk89q.worldedit.world.weather.WeatherType;
import com.sk89q.worldedit.world.weather.WeatherTypes;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;

import javax.annotation.Nullable;
import java.io.File;
import java.util.Locale;
import java.util.UUID;

public class SpongePlayer implements LocalPlayer {

    protected final HuskyGuardPlugin plugin;
    private final boolean silenced;
    private String name;
    private Player player;

    public SpongePlayer(HuskyGuardPlugin plugin, Player player) {
        this(plugin, player, false);
    }

    SpongePlayer(HuskyGuardPlugin plugin, Player player, boolean silenced) {
        this.plugin = plugin;
        this.silenced = silenced;
        this.player = player;
    }

    @Override
    public String getName() {
        if (this.name == null) {
            // getName() takes longer than before in newer versions of Minecraft
            this.name = player.getName();
        }
        return name;
    }

    public User getPlayer() {
        return this.player;

    }

    @Override
    public boolean hasGroup(String group) {
        return plugin.inGroup(getPlayer(), group);
    }

    @Override
    public void kick(String msg) {
        if (!silenced) {
            getPlayer().kickPlayer(msg);
        }
    }

    @Override
    public void ban(String msg) {
        if (!silenced) {
            Bukkit.getBanList(Type.NAME).addBan(getName(), null, null, null);
            getPlayer().kickPlayer(msg);
        }
    }

    @Override
    public double getHealth() {
        return getPlayer().getHealth();
    }

    @Override
    public void setHealth(double health) {
        getPlayer().setHealth(health);
    }

    @Override
    public double getMaxHealth() {
        return getPlayer().getMaxHealth();
    }

    @Override
    public double getFoodLevel() {
        return getPlayer().getFoodLevel();
    }

    @Override
    public void setFoodLevel(double foodLevel) {
        getPlayer().setFoodLevel((int) foodLevel);
    }

    @Override
    public double getSaturation() {
        return getPlayer().getSaturation();
    }

    @Override
    public void setSaturation(double saturation) {
        getPlayer().setSaturation((float) saturation);
    }

    @Override
    public float getExhaustion() {
        return getPlayer().getExhaustion();
    }

    @Override
    public void setExhaustion(float exhaustion) {
        getPlayer().setExhaustion(exhaustion);
    }

    @Override
    public WeatherType getPlayerWeather() {
        org.bukkit.WeatherType playerWeather = getPlayer().getPlayerWeather();
        return playerWeather == null ? null : playerWeather == org.bukkit.WeatherType.CLEAR ? WeatherTypes.CLEAR : WeatherTypes.RAIN;
    }

    @Override
    public void setPlayerWeather(WeatherType weather) {
        getPlayer().setPlayerWeather(weather == WeatherTypes.CLEAR ? org.bukkit.WeatherType.CLEAR : org.bukkit.WeatherType.DOWNFALL);
    }

    @Override
    public void resetPlayerWeather() {
        getPlayer().resetPlayerWeather();
    }

    @Override
    public boolean isPlayerTimeRelative() {
        return getPlayer().isPlayerTimeRelative();
    }

    @Override
    public long getPlayerTimeOffset() {
        return getPlayer().getPlayerTimeOffset();
    }

    @Override
    public void setPlayerTime(long time, boolean relative) {
        getPlayer().setPlayerTime(time, relative);
    }

    @Override
    public void resetPlayerTime() {
        getPlayer().resetPlayerTime();
    }

    @Override
    public int getFireTicks() {
        return getPlayer().getFireTicks();
    }

    @Override
    public void setFireTicks(int fireTicks) {
        getPlayer().setFireTicks(fireTicks);
    }

    @Override
    public void setCompassTarget(Location location) {
        getPlayer().setCompassTarget(BukkitAdapter.adapt(location));
    }

    @Override
    public void sendTitle(String title, String subtitle) {
        if (WorldGuard.getInstance().getPlatform().getGlobalStateManager().get(getWorld()).forceDefaultTitleTimes) {
            getPlayer().sendTitle(title, subtitle, 10, 70, 20);
        } else {
            getPlayer().sendTitle(title, subtitle, -1, -1, -1);
        }
    }

    @Override
    public void resetFallDistance() {
        getPlayer().setFallDistance(0);
    }

    @Override
    public void teleport(Location location, String successMessage, String failMessage) {
        PaperLib.teleportAsync(getPlayer(), BukkitAdapter.adapt(location))
                .thenApply(success -> {
                    if (success) {
                        print(successMessage);
                    } else {
                        printError(failMessage);
                    }
                    return success;
                });
    }

    @Override
    public String[] getGroups() {
        return plugin.getGroups(getPlayer());
    }

    @Override
    public void checkPermission(String permission) throws AuthorizationException {

    }

    @Override
    public void printRaw(String msg) {
        if (!silenced) {
            super.printRaw(msg);
        }
    }

    @Override
    public void printDebug(String msg) {

    }

    @Override
    public void print(String msg) {

    }

    @Override
    public void printError(String msg) {

    }

    @Override
    public void print(Component component) {

    }

    @Override
    public boolean canDestroyBedrock() {
        return false;
    }

    @Override
    public boolean isPlayer() {
        return false;
    }

    @Override
    public File openFileOpenDialog(String[] extensions) {
        return null;
    }

    @Override
    public File openFileSaveDialog(String[] extensions) {
        return null;
    }

    @Override
    public void dispatchCUIEvent(CUIEvent event) {

    }

    @Override
    public Locale getLocale() {
        return null;
    }

    @Override
    public boolean hasPermission(String perm) {
        return plugin.hasPermission(getPlayer(), perm);
    }

    @Override
    public World getWorld() {
        return null;
    }

    @Override
    public boolean isHoldingPickAxe() {
        return false;
    }

    @Override
    public Direction getCardinalDirection(int yawOffset) {
        return null;
    }

    @Override
    public BaseItemStack getItemInHand(HandSide handSide) {
        return null;
    }

    @Override
    public BaseBlock getBlockInHand(HandSide handSide) throws WorldEditException {
        return null;
    }

    @Override
    public void giveItem(BaseItemStack itemStack) {

    }

    @Override
    public BlockBag getInventoryBlockBag() {
        return null;
    }

    @Override
    public GameMode getGameMode() {
        return null;
    }

    @Override
    public void setGameMode(GameMode gameMode) {

    }

    @Override
    public void findFreePosition(Location searchPos) {

    }

    @Override
    public void setOnGround(Location searchPos) {

    }

    @Override
    public void findFreePosition() {

    }

    @Override
    public boolean ascendLevel() {
        return false;
    }

    @Override
    public boolean descendLevel() {
        return false;
    }

    @Override
    public boolean ascendToCeiling(int clearance) {
        return false;
    }

    @Override
    public boolean ascendToCeiling(int clearance, boolean alwaysGlass) {
        return false;
    }

    @Override
    public boolean ascendUpwards(int distance) {
        return false;
    }

    @Override
    public boolean ascendUpwards(int distance, boolean alwaysGlass) {
        return false;
    }

    @Override
    public void floatAt(int x, int y, int z, boolean alwaysGlass) {

    }

    @Override
    public Location getBlockOn() {
        return null;
    }

    @Override
    public Location getBlockTrace(int range, boolean useLastBlock) {
        return null;
    }

    @Override
    public Location getBlockTrace(int range, boolean useLastBlock, @Nullable Mask stopMask) {
        return null;
    }

    @Override
    public Location getBlockTraceFace(int range, boolean useLastBlock) {
        return null;
    }

    @Override
    public Location getBlockTraceFace(int range, boolean useLastBlock, @Nullable Mask stopMask) {
        return null;
    }

    @Override
    public Location getBlockTrace(int range) {
        return null;
    }

    @Override
    public Location getSolidBlockTrace(int range) {
        return null;
    }

    @Override
    public Direction getCardinalDirection() {
        return null;
    }

    @Override
    public boolean passThroughForwardWall(int range) {
        return false;
    }

    @Override
    public void setPosition(Vector3 pos, float pitch, float yaw) {

    }

    @Override
    public <B extends BlockStateHolder<B>> void sendFakeBlock(BlockVector3 pos, @Nullable B block) {

    }

    @Nullable
    @Override
    public BaseEntity getState() {
        return null;
    }

    @Override
    public boolean remove() {
        return false;
    }

    @Override
    public Location getLocation() {
        return null;
    }

    @Override
    public boolean setLocation(Location location) {
        return false;
    }

    @Override
    public Extent getExtent() {
        return null;
    }

    @Override
    public SessionKey getSessionKey() {
        return null;
    }

    @Nullable
    @Override
    public <T> T getFacet(Class<? extends T> cls) {
        return null;
    }

    @Override
    public UUID getUniqueId() {
        return null;
    }
}
