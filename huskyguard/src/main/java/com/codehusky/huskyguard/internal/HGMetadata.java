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

package com.codehusky.huskyguard.internal;

import org.spongepowered.api.data.DataSerializable;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;

/**
 * Utility methods for dealing with metadata on entities.
 *
 * <p>WorldGuard is placed as the owner of all values.</p>
 */
public final class HGMetadata {

    private HGMetadata() {
    }
    private static HashMap<DataSerializable,HashMap<String, Object>> metadata = new HashMap<>();
    /**
     * Add some metadata to a target.
     *
     * @param target the target
     * @param key the key
     * @param value the value
     */
    public static void put(DataSerializable target, String key, Object value) {
        //target.setMetadata(key, new FixedMetadataValue(HuskyGuardPlugin.inst(), value));
        if(metadata.containsKey(target)){
            metadata.get(target).put(key,value);
        }else{
            HashMap<String, Object> t = new HashMap<>();
            t.put(key, value);
            metadata.put(target, t);
        }
    }

    /**
     * Get the (first) metadata value on the given target that has the given
     * key and is of the given class type.
     *
     * @param target the target
     * @param key the key
     * @param expected the type of the value
     * @param <T> the type of the value
     * @return a value, or {@code null} if one does not exists
     */
    @Nullable
    @SuppressWarnings("unchecked")
    public static <T> T getIfPresent(DataSerializable target, String key, Class<T> expected) {
        //List<MetadataValue> values = target.getMetadata(key);
        //HuskyGuardPlugin owner = HuskyGuardPlugin.inst();
        /*for (MetadataValue value : values) {
            if (value.getOwningPlugin() == owner) {
                Object v = value.value();
                if (expected.isInstance(v)) {
                    return (T) v;
                }
            }
        }*/

        if(metadata.containsKey(target)){
            HashMap<String, Object> v = metadata.get(target);
            if(v.containsKey(key)){
                Object r = v.get(key);
                if(expected.isInstance(r)){
                    return (T) v;
                }
            }
        }

        return null;
    }

    /**
     * Removes metadata from the target.
     *
     * @param target the target
     * @param key the key
     */
    public static void remove(DataSerializable target, String key) {
        if(metadata.containsKey(target)){
            metadata.get(target).remove(key);
            if(metadata.get(target).size() == 0){
                metadata.remove(target);
            }
        }
        //target.removeMetadata(key, HuskyGuardPlugin.inst());
    }
}
