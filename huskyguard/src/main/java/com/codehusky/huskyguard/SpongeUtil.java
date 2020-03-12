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
import com.sk89q.worldguard.blacklist.target.BlockTarget;
import com.sk89q.worldguard.blacklist.target.ItemTarget;
import com.sk89q.worldguard.blacklist.target.Target;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataSerializable;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.world.Location;

import static com.google.common.base.Preconditions.checkNotNull;

public class SpongeUtil {

    private SpongeUtil() {
    }



    /**
     * Get a blacklist target for the given block.
     *
     * @param block the block
     * @param effectiveMaterial The effective material, if different
     * @return a target
     */
    public static Target createTarget(BlockState block, BlockType effectiveMaterial) {
        checkNotNull(block);
        checkNotNull(block.getType());

        if (block.getType() == effectiveMaterial) {
            return createTarget(block.getType());
        } else {
            return createTarget(effectiveMaterial);
        }
    }

    /**
     * Get a blacklist target for the given block.
     *
     * @param block the block
     * @return a target
     */
    public static Target createTarget(BlockType block) {
        checkNotNull(block);
        return createTarget(block);
    }

    /**
     * Get a blacklist target for the given item.
     *
     * @param item the item
     * @return a target
     */
    public static Target createTarget(ItemStack item) {
        checkNotNull(item);
        checkNotNull(item.getType());
        return createTarget(item.getType()); // Delegate it, ItemStacks can contain both Blocks and Items in Spigot
    }

    /**
     * Get a blacklist target for the given material.
     *
     * @param material the material
     * @return a target
     */
    public static Target createTarget(ItemType material) {
        checkNotNull(material);

            return new ItemTarget(com.sk89q.worldedit.world.item.ItemType.REGISTRY.get(material.getId()));

    }

    public static int getDurability( DataSerializable stack){
        return stack.toContainer().getInt(DataQuery.of("UnsafeDamage")).orElse(0);
    }
}
