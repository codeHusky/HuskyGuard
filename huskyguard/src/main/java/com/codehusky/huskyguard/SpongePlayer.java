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

import com.sk89q.worldedit.sponge.SpongeAdapter;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldedit.world.weather.WeatherType;
import com.sk89q.worldedit.world.weather.WeatherTypes;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldedit.sponge.SpongeHuskyGuardPlatform;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.mutable.entity.HealthData;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.ban.BanService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.title.Title;
import org.spongepowered.api.util.ban.Ban;
import org.spongepowered.api.world.weather.Weather;
import org.spongepowered.api.world.weather.Weathers;

public class SpongePlayer extends com.sk89q.worldedit.sponge.SpongePlayer implements LocalPlayer {


    private final boolean silenced;
    private String name;

    public SpongePlayer(SpongeHuskyGuardPlatform platform, Player player, boolean silenced){
        super(platform, player);
        this.silenced = silenced;
    }

    public SpongePlayer(SpongeHuskyGuardPlatform platform, Player player) {
        this(platform,player,false);
    }

    @Override
    public boolean hasGroup(String group) {
        return HuskyGuardPlugin.inst().inGroup(getPlayer(), group);
    }

    @Override
    public void kick(String msg) {
        if (!silenced) {
            getPlayer().kick(Text.of(msg));
        }
    }

    @Override
    public void ban(String msg) {
        if (!silenced) {
            Sponge.getServiceManager().provide(BanService.class).get().addBan(Ban.of(getPlayer().getProfile(),Text.of(msg)));
            getPlayer().kick(Text.of(msg));
        }
    }

    @Override
    public double getHealth() {
        return getPlayer().get(Keys.HEALTH).get();
    }

    @Override
    public void setHealth(double health) {
        getPlayer().offer(Keys.HEALTH,health);
    }

    @Override
    public double getMaxHealth() {
        return getPlayer().get(Keys.MAX_HEALTH).get();
    }

    @Override
    public double getFoodLevel() {
        return getPlayer().get(Keys.FOOD_LEVEL).get();
    }

    @Override
    public void setFoodLevel(double foodLevel) {
        getPlayer().offer(Keys.FOOD_LEVEL,(int) foodLevel);
    }

    @Override
    public double getSaturation() {
        return getPlayer().get(Keys.SATURATION).get();
    }

    @Override
    public void setSaturation(double saturation) {
        getPlayer().offer(Keys.SATURATION,saturation);
    }

    @Override
    public float getExhaustion() {
        return getPlayer().get(Keys.EXHAUSTION).get().floatValue();
    }

    @Override
    public void setExhaustion(float exhaustion) {
        getPlayer().offer(Keys.EXHAUSTION,(double)exhaustion);
    }

    @Override
    public WeatherType getPlayerWeather() {

        Weather playerWeather = getPlayer().getWorld().getWeather();
        return  playerWeather == Weathers.CLEAR ? WeatherTypes.CLEAR : WeatherTypes.RAIN;
    }

    @Override
    public void setPlayerWeather(WeatherType weather) {
        throw new UnsupportedOperationException("codeHusky: Cannot set player-specific weather in Sponge.");
        //getPlayer().setPlayerWeather(weather == WeatherTypes.CLEAR ? org.bukkit.WeatherType.CLEAR : org.bukkit.WeatherType.DOWNFALL);
    }

    @Override
    public void resetPlayerWeather() {
        //getPlayer().resetPlayerWeather();
        WorldGuard.logger.warning("Attempted to reset player weather, but this feature isn't supported.");
    }

    @Override
    public boolean isPlayerTimeRelative() {
        //return getPlayer().isPlayerTimeRelative();
        return false;
    }

    @Override
    public long getPlayerTimeOffset() {
        //return getPlayer().getPlayerTimeOffset();
        return 0L;
    }

    @Override
    public void setPlayerTime(long time, boolean relative) {
        //getPlayer().setPlayerTime(time, relative);
        WorldGuard.logger.warning("Attempted to set player time, but this feature isn't supported.");
    }

    @Override
    public void resetPlayerTime() {
        //getPlayer().resetPlayerTime();
        WorldGuard.logger.warning("Attempted to reset player time, but this feature isn't supported.");
    }

    @Override
    public int getFireTicks() {

        return getPlayer().get(Keys.FIRE_TICKS).get();
    }

    @Override
    public void setFireTicks(int fireTicks) {
        getPlayer().offer(Keys.FIRE_TICKS,fireTicks);
    }

    @Override
    public void setCompassTarget(Location location) {
        getPlayer().offer(Keys.TARGETED_LOCATION, SpongeAdapter.adapt(location).getPosition());
    }

    @Override
    public void sendTitle(String title, String subtitle) {
        if (WorldGuard.getInstance().getPlatform().getGlobalStateManager().get(getWorld()).forceDefaultTitleTimes) {

            getPlayer().sendTitle(Title.builder().title(Text.of(title)).subtitle(Text.of(subtitle)).fadeIn(10).stay(70).fadeOut(20).build());
        } else {
            getPlayer().sendTitle(Title.builder().title(Text.of(title)).subtitle(Text.of(subtitle)).fadeIn(-1).stay(-1).fadeOut(-1).build());
        }
    }

    @Override
    public void resetFallDistance() {
        getPlayer().offer(Keys.FALL_DISTANCE,0F);
    }

    @Override
    public void teleport(Location location, String successMessage, String failMessage) {
        if(getPlayer().setLocationSafely(SpongeAdapter.adapt(location))){
            print(successMessage);
        }else{
            printError(failMessage);
        }

    }

    @Override
    public String[] getGroups() {
        return HuskyGuardPlugin.inst().getGroups(getPlayer());
    }

    @Override
    public void printRaw(String msg) {
        if (!silenced) {
            super.printRaw(msg);
        }
    }

    @Override
    public boolean hasPermission(String perm) {
        return HuskyGuardPlugin.inst().hasPermission(getPlayer(), perm);
    }
}
