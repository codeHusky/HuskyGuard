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

package com.codehusky.huskyguard.cause;

import com.codehusky.huskyguard.internal.HGMetadata;
import com.google.common.base.Joiner;
import com.google.common.collect.Sets;
import io.papermc.lib.PaperLib;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.metadata.DataSerializable;
import org.spongepowered.api.data.DataSerializable;
import org.spongepowered.api.entity.AreaEffectCloud;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.explosive.PrimedTNT;
import org.spongepowered.api.entity.living.Creature;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.projectile.Projectile;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import javax.annotation.Nullable;
import java.util.*;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * An instance of this object describes the actors that played a role in
 * causing an event, with the ability to describe a situation where one actor
 * controls several other actors to create the event.
 *
 * <p>For example, if a player fires an arrow that hits an item frame, the player
 * is the initiator, while the arrow is merely controlled by the player to
 * hit the item frame.</p>
 */
public final class Cause {

    private static final String CAUSE_KEY = "worldguard.cause";
    private static final Cause UNKNOWN = new Cause(Collections.emptyList(), false);

    private final List<Object> causes;
    private final boolean indirect;

    /**
     * Create a new instance.
     *
     * @param causes a list of causes
     * @param indirect whether the cause is indirect
     */
    private Cause(List<Object> causes, boolean indirect) {
        checkNotNull(causes);
        this.causes = causes;
        this.indirect = indirect;
    }

    /**
     * Test whether the traced cause is indirect.
     *
     * <p>If the cause is indirect, then the root cause may not be notified,
     * for example.</p>
     *
     * @return true if the cause is indirect
     */
    public boolean isIndirect() {
        return indirect;
    }

    /**
     * Return whether a cause is known. This method will return true if
     * the list of causes is empty or the list of causes only contains
     * objects that really are not root causes (i.e primed TNT).
     *
     * @return true if known
     */
    public boolean isKnown() {
        if (causes.isEmpty()) {
            return false;
        }

        boolean found = false;
        for (Object object : causes) {
            if (!(object instanceof PrimedTNT) && !(object instanceof Vehicle)) {
                found = true;
                break;
            }
        }

        return found;
    }

    @Nullable
    public Object getRootCause() {
        if (!causes.isEmpty()) {
            return causes.get(0);
        }

        return null;
    }

    @Nullable
    public Player getFirstPlayer() {
        for (Object object : causes) {
            if (object instanceof Player) {
                return (Player) object;
            }
        }

        return null;
    }

    @Nullable
    public Entity getFirstEntity() {
        for (Object object : causes) {
            if (object instanceof Entity) {
                return (Entity) object;
            }
        }

        return null;
    }

    @Nullable
    public Entity getFirstNonPlayerEntity() {
        for (Object object : causes) {
            if (object instanceof Entity && !(object instanceof Player)) {
                return (Entity) object;
            }
        }

        return null;
    }

    @Nullable
    public Location getFirstBlock() {
        for (Object object : causes) {
            if (object instanceof Location) {
                return (Location) object;
            }
        }

        return null;
    }

    /**
     * Find the first type matching one in the given array.
     *
     * @param types an array of types
     * @return a found type or null
     */
    @Nullable
    public EntityType find(EntityType... types) {
        for (Object object : causes) {
            if (object instanceof Entity) {
                for (EntityType type : types) {
                    if (((Entity) object).getType() == type) {
                        return type;
                    }
                }
            }
        }

        return null;
    }

    @Override
    public String toString() {
        return Joiner.on(" | ").join(causes);
    }

    /**
     * Create a new instance with the given objects as the cause,
     * where the first-most object is the initial initiator and those
     * following it are controlled by the previous entry.
     *
     * @param cause an array of causing objects
     * @return a cause
     */
    public static Cause create(@Nullable Object... cause) {
        if (cause != null) {
            Builder builder = new Builder(cause.length);
            builder.addAll(cause);
            return builder.build();
        } else {
            return UNKNOWN;
        }
    }

    /**
     * Create a new instance that indicates that the cause is not known.
     *
     * @return a cause
     */
    public static Cause unknown() {
        return UNKNOWN;
    }

    /**
     * Add a parent cause to a {@code DataSerializable} object.
     *
     * <p>Note that {@code target} cannot be an instance of
     * {@link Block} because {@link #create(Object...)} will not bother
     * checking for such data on blocks (because it is relatively costly
     * to do so).</p>
     *
     * @param target the target
     * @param parent the parent cause
     * @throws IllegalArgumentException thrown if {@code target} is an instance of {@link Block}
     */
    public static void trackParentCause(DataSerializable target, Object parent) {
        if (target instanceof Location) {
            throw new IllegalArgumentException("Can't track causes on Blocks because Cause doesn't check block metadata");
        }

        HGMetadata.put(target, CAUSE_KEY, parent);
    }

    /**
     * Remove a parent cause from a {@code DataSerializable} object.
     *
     * @param target the target
     */
    public static void untrackParentCause(DataSerializable target) {
        HGMetadata.remove(target, CAUSE_KEY);
    }

    /**
     * Builds causes.
     */
    private static final class Builder {
        private final List<Object> causes;
        private final Set<Object> seen = Sets.newHashSet();
        private boolean indirect;

        private Builder(int expectedSize) {
            this.causes = new ArrayList<>(expectedSize);
        }

        private void addAll(@Nullable Object... element) {
            if (element != null) {
                for (Object o : element) {
                    if (o == null || seen.contains(o)) {
                        continue;
                    }

                    seen.add(o);

                    if (o instanceof PrimedTNT) {
                        addAll(((PrimedTNT) o).getSource());
                    } else if (o instanceof Projectile) {
                        addAll(((Projectile) o).getShooter());
                    }  else if (o instanceof Vehicle) {
                        ((Vehicle) o).getPassengers().forEach(this::addAll);
                    } else if (o instanceof AreaEffectCloud) {
                        indirect = true;
                        addAll(((AreaEffectCloud) o).getSource());
                    } else if (o instanceof Creature && ((Creature) o).getTarget() != null) {
                        indirect = true;
                        addAll(((Creature) o).getTarget());
                    } else if (o instanceof Tameable) {
                        indirect = true;
                        addAll(((Tameable) o).getOwner());
                    }

                    // Add manually tracked parent causes
                    Object source = o;
                    int index = causes.size();
                    while (source instanceof DataSerializable && !(source instanceof Block)) {
                        source = HGMetadata.getIfPresent((DataSerializable) source, CAUSE_KEY, Object.class);
                        if (source != null) {
                            causes.add(index, source);
                            seen.add(source);
                        }
                    }

                    causes.add(o);
                }
            }
        }

        public Cause build() {
            return new Cause(causes, indirect);
        }
    }

}
