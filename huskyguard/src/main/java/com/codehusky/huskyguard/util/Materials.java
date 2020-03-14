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

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import org.spongepowered.api.CatalogType;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.effect.potion.PotionEffect;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import com.sk89q.worldguard.protection.flags.Flags;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.effect.potion.PotionEffectType;
import org.spongepowered.api.effect.potion.PotionEffectTypes;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;

import javax.annotation.Nullable;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 * Material utility class.
 */
public final class Materials {
    // this file is cursed
    private static final Logger logger = Logger.getLogger(Materials.class.getSimpleName());

    private static final int MODIFIED_ON_RIGHT = 1;
    private static final int MODIFIED_ON_LEFT = 2;
    private static final int MODIFIES_BLOCKS = 4;

    private static final BiMap<EntityType, ItemType> ENTITY_ITEMS = HashBiMap.create();
    private static final Map<CatalogType, Integer> MATERIAL_FLAGS = new HashMap<>();
    private static final Set<PotionEffectType> DAMAGE_EFFECTS = new HashSet<>();

    private static Set<BlockType> shulkerBoxes = new HashSet<>();

    static {
        shulkerBoxes.add(BlockTypes.SILVER_SHULKER_BOX);
        shulkerBoxes.add(BlockTypes.ORANGE_SHULKER_BOX);
        shulkerBoxes.add(BlockTypes.MAGENTA_SHULKER_BOX);
        shulkerBoxes.add(BlockTypes.LIGHT_BLUE_SHULKER_BOX);
        shulkerBoxes.add(BlockTypes.YELLOW_SHULKER_BOX);
        shulkerBoxes.add(BlockTypes.LIME_SHULKER_BOX);
        shulkerBoxes.add(BlockTypes.PINK_SHULKER_BOX);
        shulkerBoxes.add(BlockTypes.GRAY_SHULKER_BOX);
        shulkerBoxes.add(BlockTypes.WHITE_SHULKER_BOX);
        shulkerBoxes.add(BlockTypes.CYAN_SHULKER_BOX);
        shulkerBoxes.add(BlockTypes.PURPLE_SHULKER_BOX);
        shulkerBoxes.add(BlockTypes.BLUE_SHULKER_BOX);
        shulkerBoxes.add(BlockTypes.BROWN_SHULKER_BOX);
        shulkerBoxes.add(BlockTypes.GREEN_SHULKER_BOX);
        shulkerBoxes.add(BlockTypes.RED_SHULKER_BOX);
        shulkerBoxes.add(BlockTypes.BLACK_SHULKER_BOX);

        ENTITY_ITEMS.put(EntityTypes.PAINTING, ItemTypes.PAINTING);
        ENTITY_ITEMS.put(EntityTypes.TIPPED_ARROW, ItemTypes.ARROW);
        ENTITY_ITEMS.put(EntityTypes.SNOWBALL, ItemTypes.SNOWBALL);
        ENTITY_ITEMS.put(EntityTypes.FIREBALL, ItemTypes.FIRE_CHARGE);
        ENTITY_ITEMS.put(EntityTypes.ENDER_PEARL, ItemTypes.ENDER_PEARL);
        ENTITY_ITEMS.put(EntityTypes.THROWN_EXP_BOTTLE, ItemTypes.EXPERIENCE_BOTTLE);
        ENTITY_ITEMS.put(EntityTypes.ITEM_FRAME, ItemTypes.ITEM_FRAME);
        ENTITY_ITEMS.put(EntityTypes.PRIMED_TNT, ItemTypes.TNT);
        ENTITY_ITEMS.put(EntityTypes.FIREWORK, ItemTypes.FIREWORKS);
        ENTITY_ITEMS.put(EntityTypes.COMMANDBLOCK_MINECART, ItemTypes.COMMAND_BLOCK_MINECART);
        ENTITY_ITEMS.put(EntityTypes.BOAT, ItemTypes.BOAT);
        ENTITY_ITEMS.put(EntityTypes.RIDEABLE_MINECART, ItemTypes.MINECART);
        ENTITY_ITEMS.put(EntityTypes.CHESTED_MINECART, ItemTypes.CHEST_MINECART);
        ENTITY_ITEMS.put(EntityTypes.FURNACE_MINECART, ItemTypes.FURNACE_MINECART);
        ENTITY_ITEMS.put(EntityTypes.TNT_MINECART, ItemTypes.TNT_MINECART);
        ENTITY_ITEMS.put(EntityTypes.HOPPER_MINECART, ItemTypes.HOPPER_MINECART);
        ENTITY_ITEMS.put(EntityTypes.SPLASH_POTION, ItemTypes.POTION);
        ENTITY_ITEMS.put(EntityTypes.EGG, ItemTypes.EGG);
        ENTITY_ITEMS.put(EntityTypes.ARMOR_STAND, ItemTypes.ARMOR_STAND);
        ENTITY_ITEMS.put(EntityTypes.ENDER_CRYSTAL, ItemTypes.END_CRYSTAL);

//        MATERIAL_FLAGS.put(BlockTypes.AIR, 0);
//        MATERIAL_FLAGS.put(BlockTypes.STONE, 0);
//        MATERIAL_FLAGS.put(BlockTypes.GRASS, 0);
//        MATERIAL_FLAGS.put(BlockTypes.DIRT, 0);
//        MATERIAL_FLAGS.put(BlockTypes.COBBLESTONE, 0);
//        MATERIAL_FLAGS.put(BlockTypes.BEDROCK, 0);
//        MATERIAL_FLAGS.put(BlockTypes.WATER, 0);
//        MATERIAL_FLAGS.put(BlockTypes.LAVA, 0);
//        MATERIAL_FLAGS.put(BlockTypes.SAND, 0);
//        MATERIAL_FLAGS.put(BlockTypes.GRAVEL, 0);
//        MATERIAL_FLAGS.put(BlockTypes.GOLD_ORE, 0);
//        MATERIAL_FLAGS.put(BlockTypes.IRON_ORE, 0);
//        MATERIAL_FLAGS.put(BlockTypes.COAL_ORE, 0);
//        MATERIAL_FLAGS.put(BlockTypes.SPONGE, 0);
//        MATERIAL_FLAGS.put(BlockTypes.GLASS, 0);
//        MATERIAL_FLAGS.put(BlockTypes.LAPIS_ORE, 0);
//        MATERIAL_FLAGS.put(BlockTypes.LAPIS_BLOCK, 0);
        MATERIAL_FLAGS.put(BlockTypes.DISPENSER, MODIFIED_ON_RIGHT);
//        MATERIAL_FLAGS.put(BlockTypes.SANDSTONE, 0);
        MATERIAL_FLAGS.put(BlockTypes.NOTEBLOCK, MODIFIED_ON_RIGHT);
//        MATERIAL_FLAGS.put(BlockTypes.GOLDEN_RAIL, 0);
//        MATERIAL_FLAGS.put(BlockTypes.DETECTOR_RAIL, 0);
//        MATERIAL_FLAGS.put(BlockTypes.STICKY_PISTON, 0);
//        MATERIAL_FLAGS.put(BlockTypes.WEB, 0);
//        MATERIAL_FLAGS.put(BlockTypes.GRASS, 0);
//        MATERIAL_FLAGS.put(BlockTypes.DEADBUSH, 0);
//        MATERIAL_FLAGS.put(BlockTypes.PISTON, 0);
//        MATERIAL_FLAGS.put(BlockTypes.PISTON_HEAD, 0);
//        MATERIAL_FLAGS.put(BlockTypes.PISTON_EXTENSION, 0);
//        MATERIAL_FLAGS.put(BlockTypes.SUNFLOWER, 0);
//        MATERIAL_FLAGS.put(BlockTypes.LILAC, 0);
//        MATERIAL_FLAGS.put(BlockTypes.PEONY, 0);
//        MATERIAL_FLAGS.put(BlockTypes.ROSE_BUSH, 0);
//        MATERIAL_FLAGS.put(BlockTypes.BROWN_MUSHROOM, 0);
//        MATERIAL_FLAGS.put(BlockTypes.RED_MUSHROOM, 0);
//        MATERIAL_FLAGS.put(BlockTypes.GOLD_BLOCK, 0);
//        MATERIAL_FLAGS.put(BlockTypes.IRON_BLOCK, 0);
//        MATERIAL_FLAGS.put(BlockTypes.BRICK_BLOCK, 0);
        MATERIAL_FLAGS.put(BlockTypes.TNT, MODIFIED_ON_RIGHT);
//        MATERIAL_FLAGS.put(BlockTypes.BOOKSHELF, 0);
//        MATERIAL_FLAGS.put(BlockTypes.MOSSY_COBBLESTONE, 0);
//        MATERIAL_FLAGS.put(BlockTypes.OBSIDIAN, 0);
//        MATERIAL_FLAGS.put(BlockTypes.TORCH, 0);
//        MATERIAL_FLAGS.put(BlockTypes.FIRE, 0);
        MATERIAL_FLAGS.put(BlockTypes.MOB_SPAWNER, MODIFIED_ON_RIGHT);
        MATERIAL_FLAGS.put(BlockTypes.CHEST, MODIFIED_ON_RIGHT);
//        MATERIAL_FLAGS.put(BlockTypes.REDSTONE_WIRE, 0);
//        MATERIAL_FLAGS.put(BlockTypes.DIAMOND_ORE, 0);
//        MATERIAL_FLAGS.put(BlockTypes.DIAMOND_BLOCK, 0);
//        MATERIAL_FLAGS.put(BlockTypes.CRAFTING_TABLE, 0);
//        MATERIAL_FLAGS.put(BlockTypes.WHEAT, 0);
//        MATERIAL_FLAGS.put(BlockTypes.FARMLAND, 0);
        MATERIAL_FLAGS.put(BlockTypes.FURNACE, MODIFIED_ON_RIGHT);
//        MATERIAL_FLAGS.put(BlockTypes.LADDER, 0);
//        MATERIAL_FLAGS.put(BlockTypes.RAIL, 0);
//        MATERIAL_FLAGS.put(BlockTypes.STONE_STAIRS, 0);
        MATERIAL_FLAGS.put(BlockTypes.LEVER, MODIFIED_ON_RIGHT);
        MATERIAL_FLAGS.put(BlockTypes.STONE_PRESSURE_PLATE, 0);
//        MATERIAL_FLAGS.put(BlockTypes.REDSTONE_ORE, 0);
//        //MATERIAL_FLAGS.put(BlockTypes.REDSTONE_TORCH, 0);
//        MATERIAL_FLAGS.put(BlockTypes.REDSTONE_TORCH, 0);
//        MATERIAL_FLAGS.put(BlockTypes.SNOW, 0);
//        MATERIAL_FLAGS.put(BlockTypes.ICE, 0);
//        MATERIAL_FLAGS.put(BlockTypes.SNOW_LAYER, 0);
//        MATERIAL_FLAGS.put(BlockTypes.CACTUS, 0);
//        MATERIAL_FLAGS.put(BlockTypes.CLAY, 0);
        MATERIAL_FLAGS.put(BlockTypes.JUKEBOX, MODIFIED_ON_RIGHT);
//        MATERIAL_FLAGS.put(BlockTypes.FENCE, 0);
//        MATERIAL_FLAGS.put(BlockTypes.PUMPKIN, 0);
//        MATERIAL_FLAGS.put(BlockTypes.NETHERRACK, 0);
//        MATERIAL_FLAGS.put(BlockTypes.SOUL_SAND, 0);
//        MATERIAL_FLAGS.put(BlockTypes.GLOWSTONE, 0);
//        MATERIAL_FLAGS.put(BlockTypes.PORTAL, 0);
//        MATERIAL_FLAGS.put(BlockTypes.LIT_PUMPKIN, 0);
        MATERIAL_FLAGS.put(BlockTypes.CAKE, MODIFIED_ON_RIGHT);
        MATERIAL_FLAGS.put(BlockTypes.POWERED_REPEATER, MODIFIED_ON_RIGHT);
        MATERIAL_FLAGS.put(BlockTypes.UNPOWERED_REPEATER, MODIFIED_ON_RIGHT);
//        MATERIAL_FLAGS.put(BlockTypes.STAINED_GLASS, 0);
        /*MATERIAL_FLAGS.put(BlockTypes.ACACIA_TRAPDOOR, MODIFIED_ON_RIGHT);
        MATERIAL_FLAGS.put(BlockTypes.BIRCH_TRAPDOOR, MODIFIED_ON_RIGHT);
        MATERIAL_FLAGS.put(BlockTypes.DARK_OAK_TRAPDOOR, MODIFIED_ON_RIGHT);
        MATERIAL_FLAGS.put(BlockTypes.JUNGLE_TRAPDOOR, MODIFIED_ON_RIGHT);*/
        MATERIAL_FLAGS.put(BlockTypes.TRAPDOOR, MODIFIED_ON_RIGHT);
        //MATERIAL_FLAGS.put(BlockTypes.SPRUCE_TRAPDOOR, MODIFIED_ON_RIGHT);
//        MATERIAL_FLAGS.put(BlockTypes.MONSTER_EGG, 0);
        /*MATERIAL_FLAGS.put(BlockTypes.INFESTED_STONE_BRICKS, 0);
        MATERIAL_FLAGS.put(BlockTypes.INFESTED_MOSSY_STONE_BRICKS, 0);
        MATERIAL_FLAGS.put(BlockTypes.INFESTED_CRACKED_STONE_BRICKS, 0);
        MATERIAL_FLAGS.put(BlockTypes.INFESTED_CHISELED_STONE_BRICKS, 0);
        MATERIAL_FLAGS.put(BlockTypes.INFESTED_COBBLESTONE, 0);*/
//        MATERIAL_FLAGS.put(BlockTypes.STONEBRICK, 0);
        /*MATERIAL_FLAGS.put(BlockTypes.MOSSY_STONE_BRICKS, 0);
        MATERIAL_FLAGS.put(BlockTypes.CRACKED_STONE_BRICKS, 0);
        MATERIAL_FLAGS.put(BlockTypes.CHISELED_STONE_BRICKS, 0);*/
//        MATERIAL_FLAGS.put(BlockTypes.BROWN_MUSHROOM_BLOCK, 0);
//        MATERIAL_FLAGS.put(BlockTypes.RED_MUSHROOM_BLOCK, 0);
//        MATERIAL_FLAGS.put(BlockTypes.IRON_BARS, 0);
//        MATERIAL_FLAGS.put(BlockTypes.GLASS_PANE, 0);
//        MATERIAL_FLAGS.put(BlockTypes.MELON_BLOCK, 0);
//        MATERIAL_FLAGS.put(BlockTypes.PUMPKIN_STEM, 0);
//        MATERIAL_FLAGS.put(BlockTypes.MELON_STEM, 0);
//        MATERIAL_FLAGS.put(BlockTypes.VINE, 0);
        MATERIAL_FLAGS.put(BlockTypes.SPRUCE_FENCE_GATE, MODIFIED_ON_RIGHT);
        MATERIAL_FLAGS.put(BlockTypes.ACACIA_FENCE_GATE, MODIFIED_ON_RIGHT);
        MATERIAL_FLAGS.put(BlockTypes.BIRCH_FENCE_GATE, MODIFIED_ON_RIGHT);
        MATERIAL_FLAGS.put(BlockTypes.DARK_OAK_FENCE_GATE, MODIFIED_ON_RIGHT);
        MATERIAL_FLAGS.put(BlockTypes.JUNGLE_FENCE_GATE, MODIFIED_ON_RIGHT);
        MATERIAL_FLAGS.put(BlockTypes.FENCE_GATE, MODIFIED_ON_RIGHT);
//        MATERIAL_FLAGS.put(BlockTypes.BRICK_STAIRS, 0);
//        MATERIAL_FLAGS.put(BlockTypes.MYCELIUM, 0);
//        MATERIAL_FLAGS.put(BlockTypes.WATERLILY, 0);
//        MATERIAL_FLAGS.put(BlockTypes.NETHER_BRICK, 0);
//        MATERIAL_FLAGS.put(BlockTypes.NETHER_BRICK_FENCE, 0);
//        MATERIAL_FLAGS.put(BlockTypes.NETHER_BRICK_STAIRS, 0);
        MATERIAL_FLAGS.put(BlockTypes.ENCHANTING_TABLE, MODIFIED_ON_RIGHT);
        MATERIAL_FLAGS.put(BlockTypes.BREWING_STAND, MODIFIED_ON_RIGHT);
        MATERIAL_FLAGS.put(BlockTypes.CAULDRON, MODIFIED_ON_RIGHT);
//        MATERIAL_FLAGS.put(BlockTypes.END_PORTAL, 0);
//        MATERIAL_FLAGS.put(BlockTypes.END_PORTAL_FRAME, 0);
//        MATERIAL_FLAGS.put(BlockTypes.END_STONE, 0);
        MATERIAL_FLAGS.put(BlockTypes.DRAGON_EGG, MODIFIED_ON_LEFT | MODIFIED_ON_RIGHT);
//        MATERIAL_FLAGS.put(BlockTypes.REDSTONE_LAMP, 0);
//        MATERIAL_FLAGS.put(BlockTypes.COCOA, 0);
//        MATERIAL_FLAGS.put(BlockTypes.SANDSTONE_STAIRS, 0);
//        MATERIAL_FLAGS.put(BlockTypes.EMERALD_ORE, 0);
//        MATERIAL_FLAGS.put(BlockTypes.ENDER_CHEST, 0);
//        MATERIAL_FLAGS.put(BlockTypes.TRIPWIRE_HOOK, 0);
//        MATERIAL_FLAGS.put(BlockTypes.TRIPWIRE, 0);
//        MATERIAL_FLAGS.put(BlockTypes.EMERALD_BLOCK, 0);
        MATERIAL_FLAGS.put(BlockTypes.COMMAND_BLOCK, MODIFIED_ON_RIGHT);
        MATERIAL_FLAGS.put(BlockTypes.BEACON, MODIFIED_ON_RIGHT);
        MATERIAL_FLAGS.put(BlockTypes.ANVIL, MODIFIED_ON_RIGHT);
        //MATERIAL_FLAGS.put(BlockTypes.CHIPPED_ANVIL, MODIFIED_ON_RIGHT);
        //MATERIAL_FLAGS.put(BlockTypes.DAMAGED_ANVIL, MODIFIED_ON_RIGHT);
        MATERIAL_FLAGS.put(BlockTypes.TRAPPED_CHEST, MODIFIED_ON_RIGHT);
//        MATERIAL_FLAGS.put(BlockTypes.HEAVY_WEIGHTED_PRESSURE_PLATE, 0);
//        MATERIAL_FLAGS.put(BlockTypes.LIGHT_WEIGHTED_PRESSURE_PLATE, 0);
        MATERIAL_FLAGS.put(BlockTypes.POWERED_COMPARATOR, MODIFIED_ON_RIGHT);
        MATERIAL_FLAGS.put(BlockTypes.UNPOWERED_COMPARATOR, MODIFIED_ON_RIGHT);
        MATERIAL_FLAGS.put(BlockTypes.DAYLIGHT_DETECTOR, MODIFIED_ON_RIGHT);
//        MATERIAL_FLAGS.put(BlockTypes.REDSTONE_BLOCK, 0);
//        MATERIAL_FLAGS.put(BlockTypes.QUARTZ_ORE, 0);
        MATERIAL_FLAGS.put(BlockTypes.HOPPER, MODIFIED_ON_RIGHT);
//        MATERIAL_FLAGS.put(BlockTypes.QUARTZ_BLOCK, 0);
//        MATERIAL_FLAGS.put(BlockTypes.QUARTZ_STAIRS, 0);
//        MATERIAL_FLAGS.put(BlockTypes.ACTIVATOR_RAIL, 0);
        MATERIAL_FLAGS.put(BlockTypes.DROPPER, MODIFIED_ON_RIGHT);
//        MATERIAL_FLAGS.put(BlockTypes.STAINED_CLAY, 0);
//        MATERIAL_FLAGS.put(BlockTypes.STAINED_GLASS_PANE, 0);
//        MATERIAL_FLAGS.put(BlockTypes.ACACIA_STAIRS, 0);
//        MATERIAL_FLAGS.put(BlockTypes.DARK_OAK_STAIRS, 0);
//        MATERIAL_FLAGS.put(BlockTypes.HAY_BLOCK, 0);
//        MATERIAL_FLAGS.put(BlockTypes.HARD_CLAY, 0);
//        MATERIAL_FLAGS.put(BlockTypes.COAL_BLOCK, 0);
//        MATERIAL_FLAGS.put(BlockTypes.PACKED_ICE, 0);
//        MATERIAL_FLAGS.put(BlockTypes.TALLGRASS, 0);
        //MATERIAL_FLAGS.put(BlockTypes.TALL_SEAGRASS, 0);
//        MATERIAL_FLAGS.put(BlockTypes.LARGE_FERN, 0);

//        MATERIAL_FLAGS.put(BlockTypes.PRISMARINE, 0);
//        MATERIAL_FLAGS.put(BlockTypes.SEA_LANTERN, 0);
//        MATERIAL_FLAGS.put(BlockTypes.SLIME, 0);
//        MATERIAL_FLAGS.put(BlockTypes.IRON_TRAPDOOR, 0);
//        MATERIAL_FLAGS.put(BlockTypes.RED_SANDSTONE, 0);
//        MATERIAL_FLAGS.put(BlockTypes.RED_SANDSTONE_STAIRS, 0);
//        MATERIAL_FLAGS.put(BlockTypes.SPRUCE_FENCE, 0);
//        MATERIAL_FLAGS.put(BlockTypes.BIRCH_FENCE, 0);
//        MATERIAL_FLAGS.put(BlockTypes.JUNGLE_FENCE, 0);
//        MATERIAL_FLAGS.put(BlockTypes.DARK_OAK_FENCE, 0);
//        MATERIAL_FLAGS.put(BlockTypes.ACACIA_FENCE, 0);
        MATERIAL_FLAGS.put(BlockTypes.SPRUCE_DOOR, MODIFIED_ON_RIGHT);
        MATERIAL_FLAGS.put(BlockTypes.BIRCH_DOOR, MODIFIED_ON_RIGHT);
        MATERIAL_FLAGS.put(BlockTypes.JUNGLE_DOOR, MODIFIED_ON_RIGHT);
        MATERIAL_FLAGS.put(BlockTypes.ACACIA_DOOR, MODIFIED_ON_RIGHT);
        MATERIAL_FLAGS.put(BlockTypes.DARK_OAK_DOOR, MODIFIED_ON_RIGHT);

//        MATERIAL_FLAGS.put(BlockTypes.GRASS_PATH, 0);
//        MATERIAL_FLAGS.put(BlockTypes.CHORUS_PLANT, 0);
//        MATERIAL_FLAGS.put(BlockTypes.CHORUS_FLOWER, 0);
//        MATERIAL_FLAGS.put(BlockTypes.BEETROOTS, 0);
//        MATERIAL_FLAGS.put(BlockTypes.END_ROD, 0);
//        MATERIAL_FLAGS.put(BlockTypes.END_BRICKS, 0);
//        MATERIAL_FLAGS.put(BlockTypes.END_GATEWAY, 0);
//        MATERIAL_FLAGS.put(BlockTypes.FROSTED_ICE, 0);
//        MATERIAL_FLAGS.put(BlockTypes.PURPUR_BLOCK, 0);
//        MATERIAL_FLAGS.put(BlockTypes.PURPUR_STAIRS, 0);
//        MATERIAL_FLAGS.put(BlockTypes.PURPUR_PILLAR, 0);
//        MATERIAL_FLAGS.put(BlockTypes.PURPUR_SLAB, 0);
        MATERIAL_FLAGS.put(BlockTypes.STRUCTURE_BLOCK, MODIFIED_ON_LEFT | MODIFIED_ON_RIGHT);
        MATERIAL_FLAGS.put(BlockTypes.REPEATING_COMMAND_BLOCK, MODIFIED_ON_RIGHT);
        MATERIAL_FLAGS.put(BlockTypes.CHAIN_COMMAND_BLOCK , MODIFIED_ON_RIGHT);

//        MATERIAL_FLAGS.put(BlockTypes.MAGMA, 0);
//        MATERIAL_FLAGS.put(BlockTypes.NETHER_WART_BLOCK, 0);
//        MATERIAL_FLAGS.put(BlockTypes.RED_NETHER_BRICK, 0);
//        MATERIAL_FLAGS.put(BlockTypes.BONE_BLOCK, 0);
//        MATERIAL_FLAGS.put(BlockTypes.BARRIER, 0);
//        MATERIAL_FLAGS.put(BlockTypes.STRUCTURE_VOID, 0);
//        // 1.12
//        MATERIAL_FLAGS.put(BlockTypes.CONCRETE, 0);
        /*MATERIAL_FLAGS.put(BlockTypes.BLUE_CONCRETE, 0);
        MATERIAL_FLAGS.put(BlockTypes.BROWN_CONCRETE, 0);
        MATERIAL_FLAGS.put(BlockTypes.CYAN_CONCRETE, 0);
        MATERIAL_FLAGS.put(BlockTypes.GRAY_CONCRETE, 0);
        MATERIAL_FLAGS.put(BlockTypes.GREEN_CONCRETE, 0);
        MATERIAL_FLAGS.put(BlockTypes.LIGHT_BLUE_CONCRETE, 0);
        MATERIAL_FLAGS.put(BlockTypes.YELLOW_CONCRETE, 0);
        MATERIAL_FLAGS.put(BlockTypes.LIGHT_GRAY_CONCRETE, 0);
        MATERIAL_FLAGS.put(BlockTypes.LIME_CONCRETE, 0);
        MATERIAL_FLAGS.put(BlockTypes.MAGENTA_CONCRETE, 0);
        MATERIAL_FLAGS.put(BlockTypes.ORANGE_CONCRETE, 0);
        MATERIAL_FLAGS.put(BlockTypes.PINK_CONCRETE, 0);
        MATERIAL_FLAGS.put(BlockTypes.PURPLE_CONCRETE, 0);
        MATERIAL_FLAGS.put(BlockTypes.RED_CONCRETE, 0);
        MATERIAL_FLAGS.put(BlockTypes.WHITE_CONCRETE, 0);*/
//        MATERIAL_FLAGS.put(BlockTypes.CONCRETE_POWDER, 0);
        /*MATERIAL_FLAGS.put(BlockTypes.BLUE_CONCRETE_POWDER, 0);
        MATERIAL_FLAGS.put(BlockTypes.BROWN_CONCRETE_POWDER, 0);
        MATERIAL_FLAGS.put(BlockTypes.CYAN_CONCRETE_POWDER, 0);
        MATERIAL_FLAGS.put(BlockTypes.GRAY_CONCRETE_POWDER, 0);
        MATERIAL_FLAGS.put(BlockTypes.GREEN_CONCRETE_POWDER, 0);
        MATERIAL_FLAGS.put(BlockTypes.LIGHT_BLUE_CONCRETE_POWDER, 0);
        MATERIAL_FLAGS.put(BlockTypes.YELLOW_CONCRETE_POWDER, 0);
        MATERIAL_FLAGS.put(BlockTypes.LIGHT_GRAY_CONCRETE_POWDER, 0);
        MATERIAL_FLAGS.put(BlockTypes.LIME_CONCRETE_POWDER, 0);
        MATERIAL_FLAGS.put(BlockTypes.MAGENTA_CONCRETE_POWDER, 0);
        MATERIAL_FLAGS.put(BlockTypes.ORANGE_CONCRETE_POWDER, 0);
        MATERIAL_FLAGS.put(BlockTypes.PINK_CONCRETE_POWDER, 0);
        MATERIAL_FLAGS.put(BlockTypes.PURPLE_CONCRETE_POWDER, 0);
        MATERIAL_FLAGS.put(BlockTypes.RED_CONCRETE_POWDER, 0);
        MATERIAL_FLAGS.put(BlockTypes.WHITE_CONCRETE_POWDER, 0);*/

//        MATERIAL_FLAGS.put(BlockTypes.WHITE_GLAZED_TERRACOTTA, 0);
//        MATERIAL_FLAGS.put(BlockTypes.ORANGE_GLAZED_TERRACOTTA, 0);
//        MATERIAL_FLAGS.put(BlockTypes.MAGENTA_GLAZED_TERRACOTTA, 0);
//        MATERIAL_FLAGS.put(BlockTypes.LIGHT_BLUE_GLAZED_TERRACOTTA, 0);
//        MATERIAL_FLAGS.put(BlockTypes.YELLOW_GLAZED_TERRACOTTA, 0);
//        MATERIAL_FLAGS.put(BlockTypes.LIME_GLAZED_TERRACOTTA, 0);
//        MATERIAL_FLAGS.put(BlockTypes.PINK_GLAZED_TERRACOTTA, 0);
//        MATERIAL_FLAGS.put(BlockTypes.GRAY_GLAZED_TERRACOTTA, 0);
//        MATERIAL_FLAGS.put(BlockTypes.SILVER_GLAZED_TERRACOTTA, 0);
//        MATERIAL_FLAGS.put(BlockTypes.CYAN_GLAZED_TERRACOTTA, 0);
//        MATERIAL_FLAGS.put(BlockTypes.PURPLE_GLAZED_TERRACOTTA, 0);
//        MATERIAL_FLAGS.put(BlockTypes.BLUE_GLAZED_TERRACOTTA, 0);
//        MATERIAL_FLAGS.put(BlockTypes.BROWN_GLAZED_TERRACOTTA, 0);
//        MATERIAL_FLAGS.put(BlockTypes.GREEN_GLAZED_TERRACOTTA, 0);
//        MATERIAL_FLAGS.put(BlockTypes.RED_GLAZED_TERRACOTTA, 0);
//        MATERIAL_FLAGS.put(BlockTypes.BLACK_GLAZED_TERRACOTTA, 0);

        // 1.13
        //MATERIAL_FLAGS.put(BlockTypes.ANDESITE, 0);
        //MATERIAL_FLAGS.put(BlockTypes.ATTACHED_MELON_STEM, 0);
        //MATERIAL_FLAGS.put(BlockTypes.ATTACHED_PUMPKIN_STEM, 0);
//        MATERIAL_FLAGS.put(BlockTypes.STAINED_GLASS, 0);
//        MATERIAL_FLAGS.put(BlockTypes.STAINED_GLASS_PANE, 0);
        //MATERIAL_FLAGS.put(BlockTypes.BLACK_TERRACOTTA, 0);
        //MATERIAL_FLAGS.put(BlockTypes.BLUE_ICE, 0);
        //MATERIAL_FLAGS.put(BlockTypes.BLUE_STAINED_GLASS, 0);
        //MATERIAL_FLAGS.put(BlockTypes.BLUE_STAINED_GLASS_PANE, 0);
        //MATERIAL_FLAGS.put(BlockTypes.BLUE_TERRACOTTA, 0);
        //MATERIAL_FLAGS.put(BlockTypes.BROWN_STAINED_GLASS, 0);
        //MATERIAL_FLAGS.put(BlockTypes.BROWN_STAINED_GLASS_PANE, 0);
        //MATERIAL_FLAGS.put(BlockTypes.BROWN_TERRACOTTA, 0);
        //MATERIAL_FLAGS.put(BlockTypes.BUBBLE_COLUMN, 0);
//        MATERIAL_FLAGS.put(BlockTypes.CARROTS, 0);
        //MATERIAL_FLAGS.put(BlockTypes.CARVED_PUMPKIN, 0);
        //MATERIAL_FLAGS.put(BlockTypes.CAVE_AIR, 0);
        //MATERIAL_FLAGS.put(BlockTypes.CHISELED_QUARTZ_BLOCK, 0);
        //MATERIAL_FLAGS.put(BlockTypes.CHISELED_RED_SANDSTONE, 0);
        //MATERIAL_FLAGS.put(BlockTypes.CHISELED_SANDSTONE, 0);
        //MATERIAL_FLAGS.put(BlockTypes.COARSE_DIRT, 0);
        //MATERIAL_FLAGS.put(BlockTypes.CONDUIT, 0);
        //MATERIAL_FLAGS.put(BlockTypes.CUT_RED_SANDSTONE, 0);
        //MATERIAL_FLAGS.put(BlockTypes.CUT_SANDSTONE, 0);
        //MATERIAL_FLAGS.put(BlockTypes.CYAN_STAINED_GLASS, 0);
        //MATERIAL_FLAGS.put(BlockTypes.CYAN_STAINED_GLASS_PANE, 0);
        //MATERIAL_FLAGS.put(BlockTypes.CYAN_TERRACOTTA, 0);
        //MATERIAL_FLAGS.put(BlockTypes.DARK_PRISMARINE, 0);
        //MATERIAL_FLAGS.put(BlockTypes.DIORITE, 0);
        //MATERIAL_FLAGS.put(BlockTypes.DRIED_KELP_BLOCK, 0);
        //MATERIAL_FLAGS.put(BlockTypes.FERN, 0);
        //MATERIAL_FLAGS.put(BlockTypes.GRANITE, 0);
        //MATERIAL_FLAGS.put(BlockTypes.GRAY_STAINED_GLASS, 0);
        //MATERIAL_FLAGS.put(BlockTypes.GRAY_STAINED_GLASS_PANE, 0);
        //MATERIAL_FLAGS.put(BlockTypes.GRAY_TERRACOTTA, 0);
        //MATERIAL_FLAGS.put(BlockTypes.GREEN_STAINED_GLASS, 0);
        //MATERIAL_FLAGS.put(BlockTypes.GREEN_STAINED_GLASS_PANE, 0);
        //MATERIAL_FLAGS.put(BlockTypes.GREEN_TERRACOTTA, 0);
        //MATERIAL_FLAGS.put(BlockTypes.KELP, 0);
        //MATERIAL_FLAGS.put(BlockTypes.KELP_PLANT, 0);
        //MATERIAL_FLAGS.put(BlockTypes.LIGHT_BLUE_STAINED_GLASS, 0);
        //MATERIAL_FLAGS.put(BlockTypes.LIGHT_BLUE_STAINED_GLASS_PANE, 0);
        //MATERIAL_FLAGS.put(BlockTypes.LIGHT_BLUE_TERRACOTTA, 0);
        //MATERIAL_FLAGS.put(BlockTypes.LIGHT_GRAY_STAINED_GLASS, 0);
        //MATERIAL_FLAGS.put(BlockTypes.LIGHT_GRAY_STAINED_GLASS_PANE, 0);
        //MATERIAL_FLAGS.put(BlockTypes.LIGHT_GRAY_TERRACOTTA, 0);
        //MATERIAL_FLAGS.put(BlockTypes.LIME_STAINED_GLASS, 0);
        //MATERIAL_FLAGS.put(BlockTypes.LIME_STAINED_GLASS_PANE, 0);
        //MATERIAL_FLAGS.put(BlockTypes.LIME_TERRACOTTA, 0);
        //MATERIAL_FLAGS.put(BlockTypes.MAGENTA_STAINED_GLASS, 0);
        //MATERIAL_FLAGS.put(BlockTypes.MAGENTA_STAINED_GLASS_PANE, 0);
        //MATERIAL_FLAGS.put(BlockTypes.MAGENTA_TERRACOTTA, 0);
        //MATERIAL_FLAGS.put(BlockTypes.MUSHROOM_STEM, 0);
//        MATERIAL_FLAGS.put(BlockTypes.OBSERVER, 0);
        //MATERIAL_FLAGS.put(BlockTypes.ORANGE_STAINED_GLASS, 0);
        //MATERIAL_FLAGS.put(BlockTypes.ORANGE_STAINED_GLASS_PANE, 0);
        //MATERIAL_FLAGS.put(BlockTypes.ORANGE_TERRACOTTA, 0);
        //MATERIAL_FLAGS.put(BlockTypes.PINK_STAINED_GLASS, 0);
        //MATERIAL_FLAGS.put(BlockTypes.PINK_STAINED_GLASS_PANE, 0);
        //MATERIAL_FLAGS.put(BlockTypes.PINK_TERRACOTTA, 0);
        //MATERIAL_FLAGS.put(BlockTypes.PODZOL, 0);
        //MATERIAL_FLAGS.put(BlockTypes.POLISHED_ANDESITE, 0);
        //MATERIAL_FLAGS.put(BlockTypes.POLISHED_DIORITE, 0);
        //MATERIAL_FLAGS.put(BlockTypes.POLISHED_GRANITE, 0);
        //MATERIAL_FLAGS.put(BlockTypes.POTATOES, 0);
        //MATERIAL_FLAGS.put(BlockTypes.PRISMARINE_BRICKS, 0);
        //MATERIAL_FLAGS.put(BlockTypes.PURPLE_STAINED_GLASS, 0);
        //MATERIAL_FLAGS.put(BlockTypes.PURPLE_STAINED_GLASS_PANE, 0);
        //MATERIAL_FLAGS.put(BlockTypes.PURPLE_TERRACOTTA, 0);
        //MATERIAL_FLAGS.put(BlockTypes.QUARTZ_PILLAR, 0);
        //MATERIAL_FLAGS.put(BlockTypes.RED_SAND, 0);
        //MATERIAL_FLAGS.put(BlockTypes.RED_STAINED_GLASS, 0);
        //MATERIAL_FLAGS.put(BlockTypes.RED_STAINED_GLASS_PANE, 0);
        //MATERIAL_FLAGS.put(BlockTypes.RED_TERRACOTTA, 0);
        //MATERIAL_FLAGS.put(BlockTypes.SEAGRASS, 0);
        //MATERIAL_FLAGS.put(BlockTypes.SEA_PICKLE, 0);
        //MATERIAL_FLAGS.put(BlockTypes.SMOOTH_QUARTZ, 0);
        //MATERIAL_FLAGS.put(BlockTypes.SMOOTH_RED_SANDSTONE, 0);
        //MATERIAL_FLAGS.put(BlockTypes.SMOOTH_SANDSTONE, 0);
        //MATERIAL_FLAGS.put(BlockTypes.SMOOTH_STONE, 0);
        //MATERIAL_FLAGS.put(BlockTypes.TERRACOTTA, 0);
        //MATERIAL_FLAGS.put(BlockTypes.TURTLE_EGG, 0);
        //MATERIAL_FLAGS.put(BlockTypes.VOID_AIR, 0);
        //MATERIAL_FLAGS.put(BlockTypes.WALL_TORCH, 0);
        //MATERIAL_FLAGS.put(BlockTypes.WET_SPONGE, 0);
        //MATERIAL_FLAGS.put(BlockTypes.WHITE_STAINED_GLASS, 0);
        //MATERIAL_FLAGS.put(BlockTypes.WHITE_STAINED_GLASS_PANE, 0);
        //MATERIAL_FLAGS.put(BlockTypes.WHITE_TERRACOTTA, 0);
        //MATERIAL_FLAGS.put(BlockTypes.YELLOW_STAINED_GLASS, 0);
        //MATERIAL_FLAGS.put(BlockTypes.YELLOW_STAINED_GLASS_PANE, 0);
        //MATERIAL_FLAGS.put(BlockTypes.YELLOW_TERRACOTTA, 0);

        //MATERIAL_FLAGS.put(BlockTypes.BAMBOO, 0);
        //MATERIAL_FLAGS.put(BlockTypes.BAMBOO_SAPLING, 0);
        //MATERIAL_FLAGS.put(BlockTypes.BARREL, MODIFIED_ON_RIGHT);
        //MATERIAL_FLAGS.put(BlockTypes.BELL, MODIFIED_ON_RIGHT);
        //MATERIAL_FLAGS.put(BlockTypes.BLAST_FURNACE, MODIFIED_ON_RIGHT);
        //MATERIAL_FLAGS.put(BlockTypes.CAMPFIRE, MODIFIED_ON_RIGHT | MODIFIED_ON_LEFT);
        //MATERIAL_FLAGS.put(BlockTypes.CARTOGRAPHY_TABLE, 0);
        //MATERIAL_FLAGS.put(BlockTypes.COMPOSTER, MODIFIED_ON_RIGHT);
        //MATERIAL_FLAGS.put(BlockTypes.FLETCHING_TABLE, 0);
        //MATERIAL_FLAGS.put(BlockTypes.GRINDSTONE, 0);
        //MATERIAL_FLAGS.put(BlockTypes.JIGSAW, MODIFIED_ON_RIGHT | MODIFIED_ON_LEFT);
        //MATERIAL_FLAGS.put(BlockTypes.LANTERN, 0);
        //MATERIAL_FLAGS.put(BlockTypes.LECTERN, 0);
        //MATERIAL_FLAGS.put(BlockTypes.LOOM, 0);
        //MATERIAL_FLAGS.put(BlockTypes.SCAFFOLDING, 0);
        //MATERIAL_FLAGS.put(BlockTypes.SMITHING_TABLE, 0);
        //MATERIAL_FLAGS.put(BlockTypes.SMOKER, MODIFIED_ON_RIGHT);
        //MATERIAL_FLAGS.put(BlockTypes.STONECUTTER, 0);
        //MATERIAL_FLAGS.put(BlockTypes.SWEET_BERRY_BUSH, MODIFIED_ON_RIGHT);

        MATERIAL_FLAGS.put(ItemTypes.IRON_SHOVEL, MODIFIES_BLOCKS);
//        MATERIAL_FLAGS.put(ItemTypes.IRON_PICKAXE, 0);
        MATERIAL_FLAGS.put(ItemTypes.IRON_AXE, MODIFIES_BLOCKS);
//        MATERIAL_FLAGS.put(ItemTypes.FLINT_AND_STEEL, 0);
//        MATERIAL_FLAGS.put(ItemTypes.APPLE, 0);
//        MATERIAL_FLAGS.put(ItemTypes.BOW, 0);
//        MATERIAL_FLAGS.put(ItemTypes.ARROW, 0);
//        MATERIAL_FLAGS.put(ItemTypes.COAL, 0);
//        MATERIAL_FLAGS.put(ItemTypes.DIAMOND, 0);
//        MATERIAL_FLAGS.put(ItemTypes.IRON_INGOT, 0);
//        MATERIAL_FLAGS.put(ItemTypes.GOLD_INGOT, 0);
//        MATERIAL_FLAGS.put(ItemTypes.IRON_SWORD, 0);
//        MATERIAL_FLAGS.put(ItemTypes.WOODEN_SWORD, 0);
        MATERIAL_FLAGS.put(ItemTypes.WOODEN_SHOVEL, MODIFIES_BLOCKS);
//        MATERIAL_FLAGS.put(ItemTypes.WOODEN_PICKAXE, 0);
        MATERIAL_FLAGS.put(ItemTypes.WOODEN_AXE, MODIFIES_BLOCKS);
//        MATERIAL_FLAGS.put(ItemTypes.STONE_SWORD, 0);
        MATERIAL_FLAGS.put(ItemTypes.STONE_SHOVEL, MODIFIES_BLOCKS);
//        MATERIAL_FLAGS.put(ItemTypes.STONE_PICKAXE, 0);
        MATERIAL_FLAGS.put(ItemTypes.STONE_AXE, MODIFIES_BLOCKS);
//        MATERIAL_FLAGS.put(ItemTypes.DIAMOND_SWORD, 0);
        MATERIAL_FLAGS.put(ItemTypes.DIAMOND_SHOVEL, MODIFIES_BLOCKS);
//        MATERIAL_FLAGS.put(ItemTypes.DIAMOND_PICKAXE, 0);
        MATERIAL_FLAGS.put(ItemTypes.DIAMOND_AXE, MODIFIES_BLOCKS);
//        MATERIAL_FLAGS.put(ItemTypes.STICK, 0);
//        MATERIAL_FLAGS.put(ItemTypes.BOWL, 0);
//        MATERIAL_FLAGS.put(ItemTypes.MUSHROOM_STEW, 0);
//        MATERIAL_FLAGS.put(ItemTypes.GOLDEN_SWORD, 0);
        MATERIAL_FLAGS.put(ItemTypes.GOLDEN_SHOVEL, MODIFIES_BLOCKS);
//        MATERIAL_FLAGS.put(ItemTypes.GOLDEN_PICKAXE, 0);
        MATERIAL_FLAGS.put(ItemTypes.GOLDEN_AXE, MODIFIES_BLOCKS);
//        MATERIAL_FLAGS.put(ItemTypes.STRING, 0);
//        MATERIAL_FLAGS.put(ItemTypes.FEATHER, 0);
//        MATERIAL_FLAGS.put(ItemTypes.GUNPOWDER, 0);
        MATERIAL_FLAGS.put(ItemTypes.WOODEN_HOE, MODIFIES_BLOCKS);
        MATERIAL_FLAGS.put(ItemTypes.STONE_HOE, MODIFIES_BLOCKS);
        MATERIAL_FLAGS.put(ItemTypes.IRON_HOE, MODIFIES_BLOCKS);
        MATERIAL_FLAGS.put(ItemTypes.DIAMOND_HOE, MODIFIES_BLOCKS);
        MATERIAL_FLAGS.put(ItemTypes.GOLDEN_HOE, MODIFIES_BLOCKS);
//        MATERIAL_FLAGS.put(ItemTypes.WHEAT_SEEDS, 0);
//        MATERIAL_FLAGS.put(ItemTypes.BREAD, 0);
//        MATERIAL_FLAGS.put(ItemTypes.LEATHER_HELMET, 0);
//        MATERIAL_FLAGS.put(ItemTypes.LEATHER_CHESTPLATE, 0);
//        MATERIAL_FLAGS.put(ItemTypes.LEATHER_LEGGINGS, 0);
//        MATERIAL_FLAGS.put(ItemTypes.LEATHER_BOOTS, 0);
//        MATERIAL_FLAGS.put(ItemTypes.CHAINMAIL_HELMET, 0);
//        MATERIAL_FLAGS.put(ItemTypes.CHAINMAIL_CHESTPLATE, 0);
//        MATERIAL_FLAGS.put(ItemTypes.CHAINMAIL_LEGGINGS, 0);
//        MATERIAL_FLAGS.put(ItemTypes.CHAINMAIL_BOOTS, 0);
//        MATERIAL_FLAGS.put(ItemTypes.IRON_HELMET, 0);
//        MATERIAL_FLAGS.put(ItemTypes.IRON_CHESTPLATE, 0);
//        MATERIAL_FLAGS.put(ItemTypes.IRON_LEGGINGS, 0);
//        MATERIAL_FLAGS.put(ItemTypes.IRON_BOOTS, 0);
//        MATERIAL_FLAGS.put(ItemTypes.DIAMOND_HELMET, 0);
//        MATERIAL_FLAGS.put(ItemTypes.DIAMOND_CHESTPLATE, 0);
//        MATERIAL_FLAGS.put(ItemTypes.DIAMOND_LEGGINGS, 0);
//        MATERIAL_FLAGS.put(ItemTypes.DIAMOND_BOOTS, 0);
//        MATERIAL_FLAGS.put(ItemTypes.GOLDEN_HELMET, 0);
//        MATERIAL_FLAGS.put(ItemTypes.GOLDEN_CHESTPLATE, 0);
//        MATERIAL_FLAGS.put(ItemTypes.GOLDEN_LEGGINGS, 0);
//        MATERIAL_FLAGS.put(ItemTypes.GOLDEN_BOOTS, 0);
//        MATERIAL_FLAGS.put(ItemTypes.FLINT, 0);
//        MATERIAL_FLAGS.put(ItemTypes.PORKCHOP, 0);
//        MATERIAL_FLAGS.put(ItemTypes.COOKED_PORKCHOP, 0);
//        MATERIAL_FLAGS.put(ItemTypes.PAINTING, 0);
//        MATERIAL_FLAGS.put(ItemTypes.GOLDEN_APPLE, 0);
//        MATERIAL_FLAGS.put(ItemTypes.BUCKET, 0);
//        MATERIAL_FLAGS.put(ItemTypes.WATER_BUCKET, 0);
//        MATERIAL_FLAGS.put(ItemTypes.LAVA_BUCKET, 0);
//        MATERIAL_FLAGS.put(ItemTypes.MINECART, 0);
//        MATERIAL_FLAGS.put(ItemTypes.SADDLE, 0);
//        MATERIAL_FLAGS.put(ItemTypes.IRON_DOOR, 0);
//        MATERIAL_FLAGS.put(ItemTypes.REDSTONE, 0);
//        MATERIAL_FLAGS.put(ItemTypes.SNOWBALL, 0);
//
//        MATERIAL_FLAGS.put(ItemTypes.LEATHER, 0);
//        MATERIAL_FLAGS.put(ItemTypes.MILK_BUCKET, 0);
//        MATERIAL_FLAGS.put(ItemTypes.BRICK_BLOCK, 0);
//        MATERIAL_FLAGS.put(ItemTypes.CLAY_BALL, 0);
//        MATERIAL_FLAGS.put(ItemTypes.REEDS, 0);
//        MATERIAL_FLAGS.put(ItemTypes.PAPER, 0);
//        MATERIAL_FLAGS.put(ItemTypes.BOOK, 0);
//        MATERIAL_FLAGS.put(ItemTypes.SLIME_BALL, 0);
//        MATERIAL_FLAGS.put(ItemTypes.CHEST_MINECART, 0);
//        MATERIAL_FLAGS.put(ItemTypes.FURNACE_MINECART, 0);
//        MATERIAL_FLAGS.put(ItemTypes.EGG, 0);
//        MATERIAL_FLAGS.put(ItemTypes.COMPASS, 0);
//        MATERIAL_FLAGS.put(ItemTypes.FISHING_ROD, 0);
//        MATERIAL_FLAGS.put(ItemTypes.CLOCK, 0);
//        MATERIAL_FLAGS.put(ItemTypes.GLOWSTONE_DUST, 0);
//        MATERIAL_FLAGS.put(ItemTypes.FISH, 0);
//        MATERIAL_FLAGS.put(ItemTypes.COOKED_FISH, 0);
//        MATERIAL_FLAGS.put(ItemTypes.DYE, 0);
        /*MATERIAL_FLAGS.put(ItemTypes.BLACK_DYE, MODIFIES_BLOCKS);
        MATERIAL_FLAGS.put(ItemTypes.BLUE_DYE, MODIFIES_BLOCKS);
        MATERIAL_FLAGS.put(ItemTypes.BROWN_DYE, MODIFIES_BLOCKS);
        MATERIAL_FLAGS.put(ItemTypes.CYAN_DYE, MODIFIES_BLOCKS);
        MATERIAL_FLAGS.put(ItemTypes.GRAY_DYE, MODIFIES_BLOCKS);
        MATERIAL_FLAGS.put(ItemTypes.GREEN_DYE, MODIFIES_BLOCKS);
        MATERIAL_FLAGS.put(ItemTypes.LIGHT_BLUE_DYE, MODIFIES_BLOCKS);
        MATERIAL_FLAGS.put(ItemTypes.LIGHT_GRAY_DYE, MODIFIES_BLOCKS);
        MATERIAL_FLAGS.put(ItemTypes.LIME_DYE, MODIFIES_BLOCKS);
        MATERIAL_FLAGS.put(ItemTypes.MAGENTA_DYE, MODIFIES_BLOCKS);
        MATERIAL_FLAGS.put(ItemTypes.ORANGE_DYE, MODIFIES_BLOCKS);
        MATERIAL_FLAGS.put(ItemTypes.PINK_DYE, MODIFIES_BLOCKS);
        MATERIAL_FLAGS.put(ItemTypes.PURPLE_DYE, MODIFIES_BLOCKS);
        MATERIAL_FLAGS.put(ItemTypes.RED_DYE, MODIFIES_BLOCKS);
        MATERIAL_FLAGS.put(ItemTypes.WHITE_DYE, MODIFIES_BLOCKS);
        MATERIAL_FLAGS.put(ItemTypes.YELLOW_DYE, MODIFIES_BLOCKS);
        MATERIAL_FLAGS.put(ItemTypes.COCOA_BEANS, 0);
        MATERIAL_FLAGS.put(ItemTypes.BONE_MEAL, MODIFIES_BLOCKS);*/
//        MATERIAL_FLAGS.put(ItemTypes.BONE, 0);
//        MATERIAL_FLAGS.put(ItemTypes.SUGAR, 0);
//        MATERIAL_FLAGS.put(ItemTypes.COOKIE, 0);
//        MATERIAL_FLAGS.put(ItemTypes.MAP, 0);
        MATERIAL_FLAGS.put(ItemTypes.SHEARS, MODIFIES_BLOCKS);
//        MATERIAL_FLAGS.put(ItemTypes.MELON, 0);
//        MATERIAL_FLAGS.put(ItemTypes.PUMPKIN_SEEDS, 0);
//        MATERIAL_FLAGS.put(ItemTypes.MELON_SEEDS, 0);
//        MATERIAL_FLAGS.put(ItemTypes.BEEF, 0);
//        MATERIAL_FLAGS.put(ItemTypes.COOKED_BEEF, 0);
//        MATERIAL_FLAGS.put(ItemTypes.CHICKEN, 0);
//        MATERIAL_FLAGS.put(ItemTypes.COOKED_CHICKEN, 0);
//        MATERIAL_FLAGS.put(ItemTypes.ROTTEN_FLESH, 0);
//        MATERIAL_FLAGS.put(ItemTypes.ENDER_PEARL, 0);
//        MATERIAL_FLAGS.put(ItemTypes.BLAZE_ROD, 0);
//        MATERIAL_FLAGS.put(ItemTypes.GHAST_TEAR, 0);
//        MATERIAL_FLAGS.put(ItemTypes.GOLD_NUGGET, 0);
//        MATERIAL_FLAGS.put(ItemTypes.NETHER_WART, 0);
//        MATERIAL_FLAGS.put(ItemTypes.POTION, 0);
//        MATERIAL_FLAGS.put(ItemTypes.GLASS_BOTTLE, 0);
//        MATERIAL_FLAGS.put(ItemTypes.SPIDER_EYE, 0);
//        MATERIAL_FLAGS.put(ItemTypes.FERMENTED_SPIDER_EYE, 0);
//        MATERIAL_FLAGS.put(ItemTypes.BLAZE_POWDER, 0);
//        MATERIAL_FLAGS.put(ItemTypes.MAGMA_CREAM, 0);
//        MATERIAL_FLAGS.put(ItemTypes.ENDER_EYE, 0);
//        MATERIAL_FLAGS.put(ItemTypes.SPECKLED_MELON, 0);
//        MATERIAL_FLAGS.put(ItemTypes.EXPERIENCE_BOTTLE, 0);
//        MATERIAL_FLAGS.put(ItemTypes.FIRE_CHARGE, 0);
//        MATERIAL_FLAGS.put(ItemTypes.WRITABLE_BOOK, 0);
//        MATERIAL_FLAGS.put(ItemTypes.WRITTEN_BOOK, 0);
//        MATERIAL_FLAGS.put(ItemTypes.EMERALD, 0);
//        MATERIAL_FLAGS.put(ItemTypes.ITEM_FRAME, 0);
//        MATERIAL_FLAGS.put(ItemTypes.CARROT, 0);
//        MATERIAL_FLAGS.put(ItemTypes.POTATO, 0);
//        MATERIAL_FLAGS.put(ItemTypes.BAKED_POTATO, 0);
//        MATERIAL_FLAGS.put(ItemTypes.POISONOUS_POTATO, 0);
//        MATERIAL_FLAGS.put(ItemTypes.FILLED_MAP, 0);
//        MATERIAL_FLAGS.put(ItemTypes.GOLDEN_CARROT, 0);
//        MATERIAL_FLAGS.put(ItemTypes.SKULL, 0);
        /*MATERIAL_FLAGS.put(ItemTypes.CREEPER_WALL_HEAD, 0);
        MATERIAL_FLAGS.put(ItemTypes.DRAGON_HEAD, 0);
        MATERIAL_FLAGS.put(ItemTypes.DRAGON_WALL_HEAD, 0);
        MATERIAL_FLAGS.put(ItemTypes.PLAYER_HEAD, 0);
        MATERIAL_FLAGS.put(ItemTypes.PLAYER_WALL_HEAD, 0);
        MATERIAL_FLAGS.put(ItemTypes.ZOMBIE_HEAD, 0);
        MATERIAL_FLAGS.put(ItemTypes.ZOMBIE_WALL_HEAD, 0);
        MATERIAL_FLAGS.put(ItemTypes.SKELETON_SKULL, 0);
        MATERIAL_FLAGS.put(ItemTypes.SKELETON_WALL_SKULL, 0);
        MATERIAL_FLAGS.put(ItemTypes.WITHER_SKELETON_SKULL, 0);
        MATERIAL_FLAGS.put(ItemTypes.WITHER_SKELETON_WALL_SKULL, 0);*/
//        MATERIAL_FLAGS.put(ItemTypes.CARROT_ON_A_STICK, 0);
//        MATERIAL_FLAGS.put(ItemTypes.NETHER_STAR, 0);
//        MATERIAL_FLAGS.put(ItemTypes.PUMPKIN_PIE, 0);
//        MATERIAL_FLAGS.put(ItemTypes.FIREWORKS, 0);
//        MATERIAL_FLAGS.put(ItemTypes.FIREWORK_CHARGE, 0);
//        MATERIAL_FLAGS.put(ItemTypes.ENCHANTED_BOOK, 0);
//        MATERIAL_FLAGS.put(ItemTypes.NETHER_BRICK, 0);
//        MATERIAL_FLAGS.put(ItemTypes.QUARTZ, 0);
//        MATERIAL_FLAGS.put(ItemTypes.TNT_MINECART, 0);
//        MATERIAL_FLAGS.put(ItemTypes.HOPPER_MINECART, 0);
//        MATERIAL_FLAGS.put(ItemTypes.LEAD, 0);
//        MATERIAL_FLAGS.put(ItemTypes.NAME_TAG, 0);
//        MATERIAL_FLAGS.put(ItemTypes.COMMAND_BLOCK_MINECART, 0);
//
//        MATERIAL_FLAGS.put(ItemTypes.PRISMARINE_SHARD, 0);
//        MATERIAL_FLAGS.put(ItemTypes.PRISMARINE_CRYSTALS, 0);
//        MATERIAL_FLAGS.put(ItemTypes.RABBIT, 0);
//        MATERIAL_FLAGS.put(ItemTypes.COOKED_RABBIT, 0);
//        MATERIAL_FLAGS.put(ItemTypes.RABBIT_STEW, 0);
//        MATERIAL_FLAGS.put(ItemTypes.RABBIT_FOOT, 0);
//        MATERIAL_FLAGS.put(ItemTypes.RABBIT_HIDE, 0);
//        MATERIAL_FLAGS.put(ItemTypes.ARMOR_STAND, 0);
//        //MATERIAL_FLAGS.put(ItemTypes.LEATHER_HORSE_ARMOR, 0);
//        MATERIAL_FLAGS.put(ItemTypes.IRON_HORSE_ARMOR, 0);
//        MATERIAL_FLAGS.put(ItemTypes.GOLDEN_HORSE_ARMOR, 0);
//        MATERIAL_FLAGS.put(ItemTypes.DIAMOND_HORSE_ARMOR, 0);
//        MATERIAL_FLAGS.put(ItemTypes.MUTTON, 0);
//        MATERIAL_FLAGS.put(ItemTypes.COOKED_MUTTON, 0);
//
//        MATERIAL_FLAGS.put(ItemTypes.BEETROOT, 0);
//        MATERIAL_FLAGS.put(ItemTypes.BEETROOT_SOUP, 0);
//        MATERIAL_FLAGS.put(ItemTypes.BEETROOT_SEEDS, 0);
//        MATERIAL_FLAGS.put(ItemTypes.CHORUS_FRUIT, 0);
//        MATERIAL_FLAGS.put(ItemTypes.CHORUS_FRUIT_POPPED, 0);
//        MATERIAL_FLAGS.put(ItemTypes.SHIELD, 0);
//        MATERIAL_FLAGS.put(ItemTypes.SPECTRAL_ARROW, 0);
//        MATERIAL_FLAGS.put(ItemTypes.TIPPED_ARROW, 0);
//        MATERIAL_FLAGS.put(ItemTypes.DRAGON_BREATH, 0);
//        MATERIAL_FLAGS.put(ItemTypes.LINGERING_POTION, 0);
//        MATERIAL_FLAGS.put(ItemTypes.ELYTRA, 0);
//        MATERIAL_FLAGS.put(ItemTypes.END_CRYSTAL, 0);
//
//        MATERIAL_FLAGS.put(ItemTypes.TOTEM_OF_UNDYING, 0);
//        MATERIAL_FLAGS.put(ItemTypes.SHULKER_SHELL, 0);
//        MATERIAL_FLAGS.put(ItemTypes.KNOWLEDGE_BOOK, 0);

        //MATERIAL_FLAGS.put(ItemTypes.CHARCOAL, 0);
        //MATERIAL_FLAGS.put(ItemTypes.COD_BUCKET, 0);
        //MATERIAL_FLAGS.put(ItemTypes.COOKED_SALMON, 0);
        //MATERIAL_FLAGS.put(ItemTypes.DEBUG_STICK, 0);
        //MATERIAL_FLAGS.put(ItemTypes.DRIED_KELP, 0);
        //MATERIAL_FLAGS.put(ItemTypes.ENCHANTED_GOLDEN_APPLE, 0);
        //MATERIAL_FLAGS.put(ItemTypes.HEART_OF_THE_SEA, 0);
        //MATERIAL_FLAGS.put(ItemTypes.IRON_NUGGET, 0);
        //MATERIAL_FLAGS.put(ItemTypes.LAPIS_LAZULI, 0);
        //MATERIAL_FLAGS.put(ItemTypes.NAUTILUS_SHELL, 0);
        //MATERIAL_FLAGS.put(ItemTypes.PHANTOM_MEMBRANE, 0);
        //MATERIAL_FLAGS.put(ItemTypes.PUFFERFISH, 0);
        //MATERIAL_FLAGS.put(ItemTypes.PUFFERFISH_BUCKET, 0);
        //MATERIAL_FLAGS.put(ItemTypes.SALMON, 0);
        //MATERIAL_FLAGS.put(ItemTypes.SALMON_BUCKET, 0);
        //MATERIAL_FLAGS.put(ItemTypes.SCUTE, 0);
        //MATERIAL_FLAGS.put(ItemTypes.SPLASH_POTION, 0);
        //MATERIAL_FLAGS.put(ItemTypes.TURTLE_HELMET, 0);
        //MATERIAL_FLAGS.put(ItemTypes.TRIDENT, 0);
        //MATERIAL_FLAGS.put(ItemTypes.TROPICAL_FISH, 0);
        //MATERIAL_FLAGS.put(ItemTypes.TROPICAL_FISH_BUCKET, 0);

        /*MATERIAL_FLAGS.put(ItemTypes.CREEPER_BANNER_PATTERN, 0);
        MATERIAL_FLAGS.put(ItemTypes.FLOWER_BANNER_PATTERN, 0);
        MATERIAL_FLAGS.put(ItemTypes.GLOBE_BANNER_PATTERN, 0);
        MATERIAL_FLAGS.put(ItemTypes.MOJANG_BANNER_PATTERN, 0);
        MATERIAL_FLAGS.put(ItemTypes.SKULL_BANNER_PATTERN, 0);*/
        //MATERIAL_FLAGS.put(ItemTypes.CROSSBOW, 0);
        //MATERIAL_FLAGS.put(ItemTypes.SUSPICIOUS_STEW, 0);
        //MATERIAL_FLAGS.put(ItemTypes.SWEET_BERRIES, 0);

        // 1.15
        /*try {
            MATERIAL_FLAGS.put(BlockTypes.BEEHIVE, MODIFIED_ON_RIGHT);
            MATERIAL_FLAGS.put(BlockTypes.BEE_NEST, MODIFIED_ON_RIGHT);
            MATERIAL_FLAGS.put(BlockTypes.HONEY_BLOCK, 0);
            MATERIAL_FLAGS.put(BlockTypes.HONEYCOMB_BLOCK, 0);
            MATERIAL_FLAGS.put(BlockTypes.HONEY_BOTTLE, 0);
            MATERIAL_FLAGS.put(BlockTypes.HONEYCOMB, 0);
        } catch (NoSuchFieldError ignored) {
        }*/

        // Fake tags
        for (BlockType m : shulkerBoxes) {
            MATERIAL_FLAGS.put(m, MODIFIED_ON_RIGHT);
        }

        // Generated via tag
        //for (Material door : Tag.DOORS.getValues()) {
        //    MATERIAL_FLAGS.put(door, MODIFIED_ON_RIGHT);
        //}
        MATERIAL_FLAGS.put(BlockTypes.WOODEN_DOOR, MODIFIED_ON_RIGHT);
        MATERIAL_FLAGS.put(BlockTypes.DARK_OAK_DOOR, MODIFIED_ON_RIGHT);
        MATERIAL_FLAGS.put(BlockTypes.ACACIA_DOOR, MODIFIED_ON_RIGHT);
        MATERIAL_FLAGS.put(BlockTypes.BIRCH_DOOR, MODIFIED_ON_RIGHT);
        MATERIAL_FLAGS.put(BlockTypes.JUNGLE_DOOR, MODIFIED_ON_RIGHT);
        MATERIAL_FLAGS.put(BlockTypes.SPRUCE_DOOR, MODIFIED_ON_RIGHT);
        MATERIAL_FLAGS.put(BlockTypes.TRAPDOOR, MODIFIED_ON_RIGHT);

        /*for (Material boat : Tag.ITEMS_BOATS.getValues()) {
            MATERIAL_FLAGS.put(boat, 0);
        }*/


        /*for (Material button : Tag.BUTTONS.getValues()) {
            MATERIAL_FLAGS.put(button, MODIFIED_ON_RIGHT);
        }*/
        MATERIAL_FLAGS.put(BlockTypes.STONE_BUTTON, MODIFIED_ON_RIGHT);
        MATERIAL_FLAGS.put(BlockTypes.WOODEN_BUTTON, MODIFIED_ON_RIGHT);
        /*for (Material pot : Tag.FLOWER_POTS.getValues()) {
            MATERIAL_FLAGS.put(pot, MODIFIED_ON_RIGHT);
        }*/
        MATERIAL_FLAGS.put(BlockTypes.FLOWER_POT, MODIFIED_ON_RIGHT);
        /*for (Material bed : Tag.BEDS.getValues()) {
            MATERIAL_FLAGS.put(bed, MODIFIED_ON_RIGHT);
        }*/
        MATERIAL_FLAGS.put(BlockTypes.BED, MODIFIED_ON_RIGHT);

//
//        Stream.concat(Stream.concat(
//                Tag.CORAL_BLOCKS.getValues().stream(),
//                Tag.CORALS.getValues().stream()),
//                Tag.WALL_CORALS.getValues().stream()).forEach(m -> {
//            MATERIAL_FLAGS.put(m, 0);
//            Material dead = BlockTypes.getMaterial("DEAD_" + m.name());
//            if (dead != null) {
//                MATERIAL_FLAGS.put(dead, 0);
//            }
//        });

        // Check for missing items/blocks
        for (BlockType material : Sponge.getRegistry().getAllOf(BlockType.class)) {


            if (!MATERIAL_FLAGS.containsKey(material)) {
                MATERIAL_FLAGS.put(material, 0);
                //logger.fine("Missing material definition for " + (BlockTypes.isBlock() ? "block " : "item ") + BlockTypes.name());
            }
        }

        for (ItemType material : Sponge.getRegistry().getAllOf(ItemType.class)) {

            if (!MATERIAL_FLAGS.containsKey(material)) {
                MATERIAL_FLAGS.put(material, 0);
                //logger.fine("Missing material definition for " + (BlockTypes.isBlock() ? "block " : "item ") + BlockTypes.name());
            }
        }

//        DAMAGE_EFFECTS.add(PotionEffectTypes.SPEED);
        DAMAGE_EFFECTS.add(PotionEffectTypes.SLOWNESS);
//        DAMAGE_EFFECTS.add(PotionEffectTypes.FAST_DIGGING);
        DAMAGE_EFFECTS.add(PotionEffectTypes.MINING_FATIGUE);
//        DAMAGE_EFFECTS.add(PotionEffectTypes.INCREASE_DAMAGE);
//        DAMAGE_EFFECTS.add(PotionEffectTypes.HEAL);
        DAMAGE_EFFECTS.add(PotionEffectTypes.INSTANT_DAMAGE);
//        DAMAGE_EFFECTS.add(PotionEffectTypes.JUMP);
        DAMAGE_EFFECTS.add(PotionEffectTypes.NAUSEA);
//        DAMAGE_EFFECTS.add(PotionEffectTypes.REGENERATION);
//        DAMAGE_EFFECTS.add(PotionEffectTypes.DAMAGE_RESISTANCE);
//        DAMAGE_EFFECTS.add(PotionEffectTypes.FIRE_RESISTANCE);
//        DAMAGE_EFFECTS.add(PotionEffectTypes.WATER_BREATHING);
//        DAMAGE_EFFECTS.add(PotionEffectTypes.INVISIBILITY);
        DAMAGE_EFFECTS.add(PotionEffectTypes.BLINDNESS);
//        DAMAGE_EFFECTS.add(PotionEffectTypes.NIGHT_VISION);
        DAMAGE_EFFECTS.add(PotionEffectTypes.HUNGER);
        DAMAGE_EFFECTS.add(PotionEffectTypes.WEAKNESS);
        DAMAGE_EFFECTS.add(PotionEffectTypes.POISON);
        DAMAGE_EFFECTS.add(PotionEffectTypes.WITHER);
//        DAMAGE_EFFECTS.add(PotionEffectTypes.HEALTH_BOOST);
//        DAMAGE_EFFECTS.add(PotionEffectTypes.ABSORPTION);
//        DAMAGE_EFFECTS.add(PotionEffectTypes.SATURATION);
        DAMAGE_EFFECTS.add(PotionEffectTypes.GLOWING);
        DAMAGE_EFFECTS.add(PotionEffectTypes.LEVITATION);
//        DAMAGE_EFFECTS.add(PotionEffectTypes.LUCK);
        DAMAGE_EFFECTS.add(PotionEffectTypes.UNLUCK);
//        DAMAGE_EFFECTS.add(PotionEffectTypes.SLOW_FALLING);
//        DAMAGE_EFFECTS.add(PotionEffectTypes.CONDUIT_POWER);
//        DAMAGE_EFFECTS.add(PotionEffectTypes.DOLPHINS_GRACE);
        //DAMAGE_EFFECTS.add(PotionEffectTypes.BAD);
//        DAMAGE_EFFECTS.add(PotionEffectTypes.HERO_OF_THE_VILLAGE);
    }

    private Materials() {
    }

    /**
     * Get the related material for an entity type.
     *
     * @param type the entity type
     * @return the related material or {@code null} if one is not known or exists
     */
    @Nullable
    public static ItemType getRelatedMaterial(EntityType type) {
        return ENTITY_ITEMS.get(type);
    }

    /**
     * Get the related entity type for a BlockTypes.
     *
     * @param material the material
     * @return the related entity type or {@code null} if one is not known or exists
     */
    @Nullable
    public static EntityType getRelatedEntity(ItemType material) {
        return ENTITY_ITEMS.inverse().get(material);
    }

    /**
     * Get the material of the block placed by the given bucket, defaulting
     * to water if the bucket type is not known.
     *
     * <p>If a non-bucket material is given, it will be assumed to be
     * an unknown bucket type. If the given bucket doesn't have a block form
     * (it can't be placed), then water will be returned (i.e. for milk).
     * Be aware that either the stationary or non-stationary material may be
     * returned.</p>
     *
     * @param type the bucket material
     * @return the block material
     */
    public static BlockType getBucketBlockMaterial(ItemType type) {
        if (ItemTypes.LAVA_BUCKET.equals(type)) {
            return BlockTypes.LAVA;
        }
        //assume water
        return BlockTypes.WATER;
    }

    /**
     * Test whether the given material is a mushroom.
     *
     * @param material the material
     * @return true if a mushroom block
     */
    public static boolean isMushroom(BlockType material) {
        return material == BlockTypes.RED_MUSHROOM || material == BlockTypes.BROWN_MUSHROOM;
    }

    /**
     * Test whether the given material is a leaf block.
     *
     * @param material the material
     * @return true if a leaf block
     */
    public static boolean isLeaf(BlockType material) {
        return material == BlockTypes.LEAVES || material == BlockTypes.LEAVES2;
    }

    /**
     * Test whether the given material is a liquid block.
     *
     * @param material the material
     * @return true if a liquid block
     */
    public static boolean isLiquid(BlockType material) {
        return isWater(material) || isLava(material);
    }

    /**
     * Test whether the given material is water.
     *
     * @param material the material
     * @return true if a water block
     */
    public static boolean isWater(BlockType material) {
        return material == BlockTypes.WATER || material == BlockTypes.FLOWING_WATER;
    }

    /**
     * Test whether the given material is lava.
     *
     * @param material the material
     * @return true if a lava block
     */
    public static boolean isLava(BlockType material) {
        return material == BlockTypes.LAVA;
    }

    /**
     * Test whether the given material is a portal BlockTypes.
     *
     * @param material the material
     * @return true if a portal block
     */
    public static boolean isPortal(BlockType material) {
        return material == BlockTypes.PORTAL || material == BlockTypes.END_PORTAL;
    }

    /**
     * Test whether the given material is a rail block.
     *
     * @param material the material
     * @return true if a rail block
     */
    public static boolean isRailBlock(BlockType material) {
        return material == BlockTypes.RAIL || material == BlockTypes.ACTIVATOR_RAIL || material == BlockTypes.DETECTOR_RAIL || material == BlockTypes.GOLDEN_RAIL;
    }

    /**
     * Test whether the given material is a piston block, not including
     * the "technical blocks" such as the piston extension block.
     *
     * @param material the material
     * @return true if a piston block
     */
    public static boolean isPistonBlock(BlockType material) {
        return material == BlockTypes.PISTON
                || material == BlockTypes.STICKY_PISTON
                || material == BlockTypes.PISTON_EXTENSION
                || material == BlockTypes.PISTON_HEAD;
    }

    /**
     * Test whether the given material is a Minecart.
     *
     * @param material the material
     * @return true if a Minecart item
     */
    public static boolean isMinecart(ItemType material) {
        return material == ItemTypes.MINECART
                || material == ItemTypes.COMMAND_BLOCK_MINECART
                || material == ItemTypes.TNT_MINECART
                || material == ItemTypes.HOPPER_MINECART
                || material == ItemTypes.FURNACE_MINECART
                || material == ItemTypes.CHEST_MINECART;
    }

    /**
     * Test whether the given material is a Boat.
     *
     * @param material the material
     * @return true if a Boat item
     */
    public static boolean isBoat(ItemType material) {
        return material == ItemTypes.BIRCH_BOAT || material == ItemTypes.ACACIA_BOAT || material == ItemTypes.DARK_OAK_BOAT || material == ItemTypes.JUNGLE_BOAT || material == ItemTypes.BOAT ;
    }

    /**
     * Test whether the given material is an inventory block.
     *
     * @param material the material
     * @return true if an inventory block
     */
    public static boolean isInventoryBlock(BlockType material) {
        return material == BlockTypes.CHEST
                || material == BlockTypes.JUKEBOX
                || material == BlockTypes.DISPENSER
                || material == BlockTypes.FURNACE
                || material == BlockTypes.BREWING_STAND
                || material == BlockTypes.TRAPPED_CHEST
                || material == BlockTypes.HOPPER
                || material == BlockTypes.DROPPER
                //|| material == BlockTypes.BARREL
                //|| material == BlockTypes.BLAST
                //|| material == BlockTypes.SMOKER
                || shulkerBoxes.contains(material);
    }

    public static boolean isSpawnEgg(ItemType material) {
        if(material == null) return false;
        /*if (ItemTypes.SPIDER_SPAWN_EGG.equals(material) || ItemTypes.BAT_SPAWN_EGG.equals(material) || ItemTypes.BEE_SPAWN_EGG.equals(material) || ItemTypes.BLAZE_SPAWN_EGG.equals(material) || ItemTypes.CAT_SPAWN_EGG.equals(material) || ItemTypes.CAVE_SPIDER_SPAWN_EGG.equals(material) || ItemTypes.CHICKEN_SPAWN_EGG.equals(material) || ItemTypes.COD_SPAWN_EGG.equals(material) || ItemTypes.COW_SPAWN_EGG.equals(material) || ItemTypes.CREEPER_SPAWN_EGG.equals(material) || ItemTypes.DOLPHIN_SPAWN_EGG.equals(material) || ItemTypes.DONKEY_SPAWN_EGG.equals(material) || ItemTypes.DROWNED_SPAWN_EGG.equals(material) || ItemTypes.ELDER_GUARDIAN_SPAWN_EGG.equals(material) || ItemTypes.ENDERMAN_SPAWN_EGG.equals(material) || ItemTypes.ENDERMITE_SPAWN_EGG.equals(material) || ItemTypes.EVOKER_SPAWN_EGG.equals(material) || ItemTypes.FOX_SPAWN_EGG.equals(material) || ItemTypes.GHAST_SPAWN_EGG.equals(material) || ItemTypes.GUARDIAN_SPAWN_EGG.equals(material) || ItemTypes.HORSE_SPAWN_EGG.equals(material) || ItemTypes.HUSK_SPAWN_EGG.equals(material) || ItemTypes.LLAMA_SPAWN_EGG.equals(material) || ItemTypes.MAGMA_CUBE_SPAWN_EGG.equals(material) || ItemTypes.MOOSHROOM_SPAWN_EGG.equals(material) || ItemTypes.MULE_SPAWN_EGG.equals(material) || ItemTypes.OCELOT_SPAWN_EGG.equals(material) || ItemTypes.PANDA_SPAWN_EGG.equals(material) || ItemTypes.PARROT_SPAWN_EGG.equals(material) || ItemTypes.PHANTOM_SPAWN_EGG.equals(material) || ItemTypes.PIG_SPAWN_EGG.equals(material) || ItemTypes.PILLAGER_SPAWN_EGG.equals(material) || ItemTypes.POLAR_BEAR_SPAWN_EGG.equals(material) || ItemTypes.PUFFERFISH_SPAWN_EGG.equals(material) || ItemTypes.RABBIT_SPAWN_EGG.equals(material) || ItemTypes.RAVAGER_SPAWN_EGG.equals(material) || ItemTypes.SALMON_SPAWN_EGG.equals(material) || ItemTypes.SHEEP_SPAWN_EGG.equals(material) || ItemTypes.SHULKER_SPAWN_EGG.equals(material) || ItemTypes.SILVERFISH_SPAWN_EGG.equals(material) || ItemTypes.SKELETON_HORSE_SPAWN_EGG.equals(material) || ItemTypes.SKELETON_SPAWN_EGG.equals(material) || ItemTypes.SLIME_SPAWN_EGG.equals(material) || ItemTypes.SQUID_SPAWN_EGG.equals(material) || ItemTypes.STRAY_SPAWN_EGG.equals(material) || ItemTypes.TRADER_LLAMA_SPAWN_EGG.equals(material) || ItemTypes.TROPICAL_FISH_SPAWN_EGG.equals(material) || ItemTypes.TURTLE_SPAWN_EGG.equals(material) || ItemTypes.VEX_SPAWN_EGG.equals(material) || ItemTypes.VILLAGER_SPAWN_EGG.equals(material) || ItemTypes.VINDICATOR_SPAWN_EGG.equals(material) || ItemTypes.WANDERING_TRADER_SPAWN_EGG.equals(material) || ItemTypes.WITCH_SPAWN_EGG.equals(material) || ItemTypes.WITHER_SKELETON_SPAWN_EGG.equals(material) || ItemTypes.WOLF_SPAWN_EGG.equals(material) || ItemTypes.ZOMBIE_HORSE_SPAWN_EGG.equals(material) || ItemTypes.ZOMBIE_PIGMAN_SPAWN_EGG.equals(material) || ItemTypes.ZOMBIE_SPAWN_EGG.equals(material) || ItemTypes.ZOMBIE_VILLAGER_SPAWN_EGG.equals(material)) {
            return true;
        }*/

        return ItemTypes.SPAWN_EGG.equals(material);
    }

    public static EntityType getEntitySpawnEgg(ItemStackSnapshot material) {
        // Uhh
        if(material.get(Keys.SPAWNABLE_ENTITY_TYPE).isPresent()){
            return material.get(Keys.SPAWNABLE_ENTITY_TYPE).get();
        }
        return EntityTypes.PIG; //default?
        /*if (ItemTypes.SPIDER_SPAWN_EGG.equals(material)) {
            return EntityTypes.SPIDER;
        } else if (ItemTypes.BAT_SPAWN_EGG.equals(material)) {
            return EntityTypes.BAT;
        } else if (ItemTypes.BEE_SPAWN_EGG.equals(material) || ItemTypes.BLAZE_SPAWN_EGG.equals(material)) {//return EntityTypes.BEE;

            return EntityTypes.BLAZE;
        } else if (ItemTypes.CAT_SPAWN_EGG.equals(material)) {
            return EntityTypes.OCELOT;
        } else if (ItemTypes.CAVE_SPIDER_SPAWN_EGG.equals(material)) {
            return EntityTypes.CAVE_SPIDER;
        } else if (ItemTypes.CHICKEN_SPAWN_EGG.equals(material)) {
            return EntityTypes.CHICKEN;
        } else if (ItemTypes.COD_SPAWN_EGG.equals(material) || ItemTypes.COW_SPAWN_EGG.equals(material)) {//return EntityTypes.COD;

            return EntityTypes.COW;
        } else if (ItemTypes.CREEPER_SPAWN_EGG.equals(material)) {
            return EntityTypes.CREEPER;
        } else if (ItemTypes.DOLPHIN_SPAWN_EGG.equals(material) || ItemTypes.DONKEY_SPAWN_EGG.equals(material)) {//return EntityTypes.DOLPHIN;

            return EntityTypes.DONKEY;
        } else if (ItemTypes.DROWNED_SPAWN_EGG.equals(material) || ItemTypes.ELDER_GUARDIAN_SPAWN_EGG.equals(material)) {//return EntityTypes.DROWNED;

            return EntityTypes.ELDER_GUARDIAN;
        } else if (ItemTypes.ENDERMAN_SPAWN_EGG.equals(material)) {
            return EntityTypes.ENDERMAN;
        } else if (ItemTypes.ENDERMITE_SPAWN_EGG.equals(material)) {
            return EntityTypes.ENDERMITE;
        } else if (ItemTypes.EVOKER_SPAWN_EGG.equals(material) || ItemTypes.FOX_SPAWN_EGG.equals(material) || ItemTypes.GHAST_SPAWN_EGG.equals(material)) {//return EntityTypes.EVOKER;

            //return EntityTypes.FOX;

            return EntityTypes.GHAST;
        } else if (ItemTypes.GUARDIAN_SPAWN_EGG.equals(material)) {
            return EntityTypes.GUARDIAN;
        } else if (ItemTypes.HORSE_SPAWN_EGG.equals(material)) {
            return EntityTypes.HORSE;
        } else if (ItemTypes.HUSK_SPAWN_EGG.equals(material)) {
            return EntityTypes.HUSK;
        } else if (ItemTypes.LLAMA_SPAWN_EGG.equals(material)) {
            return EntityTypes.LLAMA;
        } else if (ItemTypes.MAGMA_CUBE_SPAWN_EGG.equals(material)) {
            return EntityTypes.MAGMA_CUBE;
        } else if (ItemTypes.MOOSHROOM_SPAWN_EGG.equals(material)) {
            return EntityTypes.MUSHROOM_COW;
        } else if (ItemTypes.MULE_SPAWN_EGG.equals(material)) {
            return EntityTypes.MULE;
        } else if (ItemTypes.OCELOT_SPAWN_EGG.equals(material)) {
            return EntityTypes.OCELOT;
        } else if (ItemTypes.PANDA_SPAWN_EGG.equals(material) || ItemTypes.PARROT_SPAWN_EGG.equals(material)) {//return EntityTypes.PANDA;

            return EntityTypes.PARROT;
        } else if (ItemTypes.PHANTOM_SPAWN_EGG.equals(material) || ItemTypes.PILLAGER_SPAWN_EGG.equals(material) || ItemTypes.POLAR_BEAR_SPAWN_EGG.equals(material)) {//return EntityTypes.PHANTOM;

            //return EntityTypes.PILLAGER;

            return EntityTypes.POLAR_BEAR;
        } else if (ItemTypes.PUFFERFISH_SPAWN_EGG.equals(material) || ItemTypes.RABBIT_SPAWN_EGG.equals(material)) {//return EntityTypes.PUFFERFISH;

            return EntityTypes.RABBIT;
        } else if (ItemTypes.RAVAGER_SPAWN_EGG.equals(material) || ItemTypes.SALMON_SPAWN_EGG.equals(material) || ItemTypes.SHEEP_SPAWN_EGG.equals(material)) {//return EntityTypes.RAVAGER;

            //return EntityTypes.SALMON;

            return EntityTypes.SHEEP;
        } else if (ItemTypes.SHULKER_SPAWN_EGG.equals(material)) {
            return EntityTypes.SHULKER;
        } else if (ItemTypes.SILVERFISH_SPAWN_EGG.equals(material)) {
            return EntityTypes.SILVERFISH;
        } else if (ItemTypes.SKELETON_HORSE_SPAWN_EGG.equals(material)) {
            return EntityTypes.SKELETON_HORSE;
        } else if (ItemTypes.SKELETON_SPAWN_EGG.equals(material)) {
            return EntityTypes.SKELETON;
        } else if (ItemTypes.SLIME_SPAWN_EGG.equals(material)) {
            return EntityTypes.SLIME;
        } else if (ItemTypes.SQUID_SPAWN_EGG.equals(material)) {
            return EntityTypes.SQUID;
        } else if (ItemTypes.STRAY_SPAWN_EGG.equals(material)) {
            return EntityTypes.STRAY;
        } else if (ItemTypes.TRADER_LLAMA_SPAWN_EGG.equals(material) || ItemTypes.TROPICAL_FISH_SPAWN_EGG.equals(material) || ItemTypes.TURTLE_SPAWN_EGG.equals(material) || ItemTypes.VEX_SPAWN_EGG.equals(material)) {//return EntityTypes.TRADER_LLAMA;

            //return EntityTypes.TROPICAL_FISH;

            //return EntityTypes.TURTLE;

            return EntityTypes.VEX;
        } else if (ItemTypes.VILLAGER_SPAWN_EGG.equals(material)) {
            return EntityTypes.VILLAGER;
        } else if (ItemTypes.VINDICATOR_SPAWN_EGG.equals(material) || ItemTypes.WANDERING_TRADER_SPAWN_EGG.equals(material) || ItemTypes.WITCH_SPAWN_EGG.equals(material)) {//return EntityTypes.VINDICATOR;

            //return EntityTypes.WANDERING_TRADER;

            return EntityTypes.WITCH;
        } else if (ItemTypes.WITHER_SKELETON_SPAWN_EGG.equals(material)) {
            return EntityTypes.WITHER_SKELETON;
        } else if (ItemTypes.WOLF_SPAWN_EGG.equals(material)) {
            return EntityTypes.WOLF;
        } else if (ItemTypes.ZOMBIE_HORSE_SPAWN_EGG.equals(material)) {
            return EntityTypes.ZOMBIE_HORSE;
        } else if (ItemTypes.ZOMBIE_PIGMAN_SPAWN_EGG.equals(material)) {
            return EntityTypes.PIG_ZOMBIE;
        } else if (ItemTypes.ZOMBIE_SPAWN_EGG.equals(material)) {
            return EntityTypes.ZOMBIE;
        } else if (ItemTypes.ZOMBIE_VILLAGER_SPAWN_EGG.equals(material)) {
            return EntityTypes.ZOMBIE_VILLAGER;
        }
        return EntityTypes.PIG;*/
    }

    public static boolean isBed(BlockType material) {
        return material == BlockTypes.BED;
    }

    /**
     * Test whether the material is a crop.
     * @param type the material
     * @return true if the material is a crop
     */
    public static boolean isCrop(BlockType type) {
        return type == BlockTypes.WHEAT
                || type == BlockTypes.CARROTS
                || type == BlockTypes.POTATOES
                || type == BlockTypes.BEETROOTS
                || type == BlockTypes.MELON_STEM
                || type == BlockTypes.PUMPKIN_STEM
                || type == BlockTypes.PUMPKIN
                || type == BlockTypes.MELON_BLOCK
                || type == BlockTypes.CACTUS
                || type == BlockTypes.REEDS
                /*|| type == BlockTypes.BAMBOO
                || type == BlockTypes.BAMBOO_SAPLING*/;
    }

    /**
     * Test whether the given material is affected by
     * {@link Flags#USE}.
     *
     * <p>Generally, materials that are considered by this method are those
     * that are not inventories but can be used.</p>
     *
     * @param material the material
     * @return true if covered by the use flag
     */
    public static boolean isUseFlagApplicable(BlockType material) {
        if (material == BlockTypes.STONE_BUTTON || material == BlockTypes.WOODEN_BUTTON
                || material == BlockTypes.ACACIA_DOOR || material == BlockTypes.BIRCH_DOOR || material == BlockTypes.DARK_OAK_DOOR || /*material == BlockTypes.IRON_DOOR ||*/ material == BlockTypes.JUNGLE_DOOR || material == BlockTypes.SPRUCE_DOOR || material == BlockTypes.WOODEN_DOOR
                /*|| material == BlockTypes.HEAVY_WEIGHTED_PRESSURE_PLATE*/ || material == BlockTypes.LIGHT_WEIGHTED_PRESSURE_PLATE || material == BlockTypes.STONE_PRESSURE_PLATE || material == BlockTypes.WOODEN_PRESSURE_PLATE
                || material == BlockTypes.TRAPDOOR) {
            return true;
        }
        if (BlockTypes.LEVER.equals(material) || BlockTypes.ACACIA_FENCE_GATE.equals(material) || BlockTypes.DARK_OAK_FENCE_GATE.equals(material) || BlockTypes.JUNGLE_FENCE_GATE.equals(material) || BlockTypes.BIRCH_FENCE_GATE.equals(material) || BlockTypes.SPRUCE_FENCE_GATE.equals(material) || BlockTypes.FENCE_GATE.equals(material) || BlockTypes.LIGHT_WEIGHTED_PRESSURE_PLATE.equals(material) || BlockTypes.HEAVY_WEIGHTED_PRESSURE_PLATE.equals(material) || BlockTypes.STONE_PRESSURE_PLATE.equals(material) || BlockTypes.ANVIL.equals(material) || BlockTypes.ENCHANTING_TABLE.equals(material)) {//case BlockTypes.LECTERN:


            // case BlockTypes.DAMAGED_ANVIL:
            // case BlockTypes.CHIPPED_ANVIL:

            //case BlockTypes.BELL:
            //case BlockTypes.LOOM:
            //case BlockTypes.CARTOGRAPHY_TABLE:
            //case BlockTypes.STONECUTTER:
            //case BlockTypes.GRINDSTONE:
            return true;
        }
        return false;
    }

    /**
     * Test whether the given material is a block that is modified when it is
     * left or right clicked.
     *
     * <p>This test is conservative, returning true for blocks that it is not
     * aware of.</p>
     *
     * @param material the material
     * @param rightClick whether it is a right click
     * @return true if the block is modified
     */
    public static boolean isBlockModifiedOnClick(BlockType material, boolean rightClick) {
        Integer flags = MATERIAL_FLAGS.get(material);
        return flags == null
                || (rightClick && (flags & MODIFIED_ON_RIGHT) == MODIFIED_ON_RIGHT)
                || (!rightClick && (flags & MODIFIED_ON_LEFT) == MODIFIED_ON_LEFT);
    }

    /**
     * Test whether the given item modifies a given block when right clicked.
     *
     * <p>This test is conservative, returning true for items that it is not
     * aware of or does not have the details for.</p>
     *
     * @param item the item
     * @param block the block
     * @return true if the item is applied to the block
     */
    public static boolean isItemAppliedToBlock(ItemType item, BlockType block) {
        Integer flags = MATERIAL_FLAGS.get(item);
        return flags == null || (flags & MODIFIES_BLOCKS) == MODIFIES_BLOCKS;
    }

    /**
     * Test whether the given material should be tested as "building" when
     * it is used.
     *
     * @param type the type
     * @return true to be considered as used
     */
    public static boolean isConsideredBuildingIfUsed(BlockType type) {
        return type == BlockTypes.POWERED_REPEATER || type == BlockTypes.UNPOWERED_REPEATER
            || type == BlockTypes.POWERED_COMPARATOR || type == BlockTypes.UNPOWERED_COMPARATOR
            || type == BlockTypes.FLOWER_POT;
    }

    /**
     * Test whether a list of potion effects contains one or more potion
     * effects used for doing damage.
     *
     * @param effects A collection of effects
     * @return True if at least one damage effect exists
     */
    public static boolean hasDamageEffect(Collection<PotionEffect> effects) {
        for (PotionEffect effect : effects) {
            if (DAMAGE_EFFECTS.contains(effect.getType())) {
                return true;
            }
        }

        return false;
    }

    // should match instances of ItemArmor

    /**
     * Check if the material is equippable armor (i.e. that it is equipped on right-click
     * not necessarily that it can be put in the armor slots)
     *
     * @param type material to check
     * @return true if equippable armor
     */
    public static boolean isArmor(ItemType type) {
        if (ItemTypes.LEATHER_HELMET.equals(type) || ItemTypes.LEATHER_CHESTPLATE.equals(type) || ItemTypes.LEATHER_LEGGINGS.equals(type) || ItemTypes.LEATHER_BOOTS.equals(type) || ItemTypes.CHAINMAIL_HELMET.equals(type) || ItemTypes.CHAINMAIL_CHESTPLATE.equals(type) || ItemTypes.CHAINMAIL_LEGGINGS.equals(type) || ItemTypes.CHAINMAIL_BOOTS.equals(type) || ItemTypes.IRON_HELMET.equals(type) || ItemTypes.IRON_CHESTPLATE.equals(type) || ItemTypes.IRON_LEGGINGS.equals(type) || ItemTypes.IRON_BOOTS.equals(type) || ItemTypes.DIAMOND_HELMET.equals(type) || ItemTypes.DIAMOND_CHESTPLATE.equals(type) || ItemTypes.DIAMOND_LEGGINGS.equals(type) || ItemTypes.DIAMOND_BOOTS.equals(type) || ItemTypes.GOLDEN_HELMET.equals(type) || ItemTypes.GOLDEN_CHESTPLATE.equals(type) || ItemTypes.GOLDEN_LEGGINGS.equals(type) || ItemTypes.GOLDEN_BOOTS.equals(type) || /*ItemTypes.TURTLE_HELMET.equals(type) ||*/ ItemTypes.ELYTRA.equals(type)) {
            return true;
        }
        return false;
    }

}
