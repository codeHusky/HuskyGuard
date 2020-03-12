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

import org.spongepowered.api.data.manipulator.mutable.entity.FlyingAbilityData;
import org.spongepowered.api.data.manipulator.mutable.entity.TameableData;
import org.spongepowered.api.data.manipulator.mutable.entity.VehicleData;
import org.spongepowered.api.entity.EnderCrystal;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.explosive.PrimedTNT;
import org.spongepowered.api.entity.hanging.Hanging;
import org.spongepowered.api.entity.living.*;
import org.spongepowered.api.entity.living.animal.Pig;
import org.spongepowered.api.entity.living.complex.EnderDragon;
import org.spongepowered.api.entity.living.golem.Shulker;
import org.spongepowered.api.entity.living.monster.Monster;
import org.spongepowered.api.entity.living.monster.Slime;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.projectile.Projectile;
import org.spongepowered.api.entity.projectile.arrow.Arrow;
import org.spongepowered.api.entity.projectile.arrow.SpectralArrow;
import org.spongepowered.api.entity.projectile.source.ProjectileSource;
import org.spongepowered.api.entity.vehicle.minecart.ChestMinecart;
import org.spongepowered.api.entity.vehicle.minecart.ContainerMinecart;
import org.spongepowered.api.entity.vehicle.minecart.Minecart;
import org.spongepowered.api.entity.vehicle.minecart.TNTMinecart;

import javax.annotation.Nullable;

public final class Entities {

    private Entities() {
    }

    /**
     * Test whether the given entity is tameable and tamed.
     *
     * @param entity the entity, or null
     * @return true if tamed
     */
    public static boolean isTamed(@Nullable Entity entity) {
        if(entity == null) return false;
        return entity.get(TameableData.class).isPresent() && entity.get(TameableData.class).get().owner().exists();
    }

    /**
     * Return if the given entity type is TNT-based.
     *
     * @param entity the entity
     * @return true if TNT based
     */
    public static boolean isTNTBased(Entity entity) {
        return entity instanceof PrimedTNT || entity instanceof TNTMinecart;
    }

    /**
     * Return if the given entity type is a fireball
     * (not including wither skulls).
     *
     * @param type the type
     * @return true if a fireball
     */
    public static boolean isFireball(EntityType type) {
        return type == EntityTypes.FIREBALL || type == EntityTypes.SMALL_FIREBALL;
    }

    /**
     * Test whether the given entity can be ridden if it is right clicked.
     *
     * @param entity the entity
     * @return true if the entity can be ridden
     */
    public static boolean isRiddenOnUse(Entity entity) {
        return entity instanceof Pig ? ((Pig) entity).getPigSaddleData().saddle().get() : entity.get(VehicleData.class).isPresent();
    }

    /**
     * Test whether the given entity type is a vehicle type.
     *
     * @param type the type
     * @return true if the type is a vehicle type
     */
    public static boolean isVehicle(EntityType type) {
        return type == EntityTypes.BOAT
                || isMinecart(type);
    }

    /**
     * Test whether the given entity type is a Minecart type.
     *
     * @param type the type
     * @return true if the type is a Minecart type
     */
    public static boolean isMinecart(EntityType type) {
        return type == EntityTypes.RIDEABLE_MINECART
                || type == EntityTypes.CHESTED_MINECART
                || type == EntityTypes.COMMANDBLOCK_MINECART
                || type == EntityTypes.FURNACE_MINECART
                || type == EntityTypes.HOPPER_MINECART
                || type == EntityTypes.MOB_SPAWNER_MINECART
                || type == EntityTypes.TNT_MINECART;
    }

    /**
     * Get the underlying shooter of a projectile if one exists.
     *
     * @param entity the entity
     * @return the shooter
     */
    public static Entity getShooter(Entity entity) {

        while (entity instanceof Projectile) {
            Projectile projectile = (Projectile) entity;
            ProjectileSource remover = projectile.getShooter();
            if (remover instanceof Entity && remover != entity) {
                entity = (Entity) remover;
            } else {
                return entity;
            }
        }

        return entity;
    }

    /**
     * Test whether an entity is hostile.
     *
     * @param entity the entity
     * @return true if hostile
     */
    public static boolean isHostile(Entity entity) {
        return entity instanceof Monster
                //|| entity instanceof Slime // slimes are considered monsters
                || entity.get(FlyingAbilityData.class).isPresent()
                || entity instanceof EnderDragon
                || entity instanceof Shulker;
    }

    /**
     * Test whether an entity is a non-hostile creature.
     *
     * @param entity
     * @return true if non-hostile
     */
    public static boolean isNonHostile(Entity entity) {
        return !isHostile(entity) && entity instanceof Creature;
    }

    /**
     * Test whether an entity is ambient.
     *
     * @param entity the entity
     * @return true if ambient
     */
    public static boolean isAmbient(Entity entity) {
        return entity instanceof Ambient;
    }

    /**
     * Test whether an entity is an NPC.
     *
     * @param entity the entity
     * @return true if an NPC
     */
    public static boolean isNPC(Entity entity) {
        //TODO: ???
        return entity instanceof Human && !(entity instanceof Player);
    }

    /**
     * Test whether an entity is a creature (a living thing) that is
     * not a player.
     *
     * @param entity the entity
     * @return true if a non-player creature
     */
    public static boolean isNonPlayerCreature(Entity entity) {
        return entity instanceof Living && !(entity instanceof Player);
    }

    /**
     * Test whether using the given entity should be considered "building"
     * rather than merely using an entity.
     *
     * @param entity the entity
     * @return true if considered building
     */
    public static boolean isConsideredBuildingIfUsed(Entity entity) {
        return entity instanceof Hanging
                || entity instanceof ArmorStand
                || entity instanceof EnderCrystal
                || entity instanceof ContainerMinecart;
    }

    public static boolean isPotionArrow(Entity entity) {
        return entity instanceof Arrow; //|| entity instanceof SpectralArrow; //spectral arrows are arrows in sponge
    }

    public static boolean isAoECloud(EntityType type) {
        return type == EntityTypes.AREA_EFFECT_CLOUD;
    }
}
