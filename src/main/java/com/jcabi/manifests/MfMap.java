/*
 * SPDX-FileCopyrightText: Copyright (c) 2012-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.manifests;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

/**
 * Map of manifest attributes.
 *
 * @see Manifests
 * @since 1.1
 */
public interface MfMap {

    /**
     * Get size of attributes map.
     * @return Size of attributes map
     * @since 2.0
     */
    int size();

    /**
     * Check if attributes map is empty.
     * @return True if attributes map is empty and false otherwise
     * @since 2.0
     */
    boolean isEmpty();

    /**
     * Check if attributes map contains the given key.
     * @param key Attribute name
     * @return True if attributes map contains the given key, and false otherwise
     * @since 2.0
     */
    boolean containsKey(String key);

    /**
     * Check if attributes map contains the given value.
     * @param value Attribute value
     * @return True if attributes map contains the given value, and false otherwise
     * @since 2.0
     */
    boolean containsValue(String value);

    /**
     * Get attribute value by its key.
     * @param key Attribute name
     * @return Value of the attribute, and null if attribute not found
     */
    String get(String key);

    /**
     * Get a copy of attributes map.
     * @return Copy of attributes map
     * @since 2.0
     */
    Map<String, String> getAsMap();

    /**
     * Get a copy of attributes map, named per the original specification
     * of issue #38.
     *
     * <p>This is an alias for {@link #getAsMap()} that exposes the same
     * snapshot under the shorter, idiomatic name from the acceptance
     * criteria. Implementations may override it, but the default delegates
     * to {@link #getAsMap()} so existing implementations need no change.
     *
     * @return Copy of attributes map
     * @since 2.1
     */
    default Map<String, String> asMap() {
        return this.getAsMap();
    }

    /**
     * Get a copy of a set of attributes keys.
     * @return Copy of a set of attributes keys
     * @since 2.0
     */
    Set<String> keySet();

    /**
     * Append this collection of MANIFEST.MF files.
     *
     * This method changes the original instance.
     *
     * @param mfs Content to append
     * @throws IOException If fails on I/O problem
     * @since 2.0.0
     */
    void append(Mfs mfs) throws IOException;
}
