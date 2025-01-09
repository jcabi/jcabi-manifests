/*
 * Copyright (c) 2012-2025, jcabi.com
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met: 1) Redistributions of source code must retain the above
 * copyright notice, this list of conditions and the following
 * disclaimer. 2) Redistributions in binary form must reproduce the above
 * copyright notice, this list of conditions and the following
 * disclaimer in the documentation and/or other materials provided
 * with the distribution. 3) Neither the name of the jcabi.com nor
 * the names of its contributors may be used to endorse or promote
 * products derived from this software without specific prior written
 * permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT
 * NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 * THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
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
     * @return True if attributes map contains the given key and false otherwise
     * @since 2.0
     */
    boolean containsKey(String key);

    /**
     * Check if attributes map contains the given value.
     * @param value Attribute value
     * @return True if attributes map contains the given value and false otherwise
     * @since 2.0
     */
    boolean containsValue(String value);

    /**
     * Get attribute value by its key.
     * @param key Attribute name
     * @return Value of the attribute and null if attribute not found
     */
    String get(String key);

    /**
     * Get a copy of attributes map.
     * @return Copy of attributes map
     * @since 2.0
     */
    Map<String, String> getAsMap();

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
