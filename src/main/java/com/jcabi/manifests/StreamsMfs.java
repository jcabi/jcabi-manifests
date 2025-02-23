/*
 * SPDX-FileCopyrightText: Copyright (c) 2012-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.manifests;

import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;

/**
 * Manifests in streams.
 *
 * Append attributes from {@code MANIFEST.MF} file:
 *
 * <pre> Manifests.append(
 *   new StreamsMfs(this.getClass().getResourceAsStream("MANIFEST.MF"))
 * );</pre>
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 1.1
 */
public final class StreamsMfs implements Mfs {

    /**
     * Streams.
     */
    private final transient Collection<InputStream> streams;

    /**
     * Ctor.
     * @param stream Stream
     */
    public StreamsMfs(final InputStream stream) {
        this(Collections.singleton(stream));
    }

    /**
     * Ctor.
     * @param list Files
     */
    public StreamsMfs(final Collection<InputStream> list) {
        this.streams = Collections.unmodifiableCollection(list);
    }

    @Override
    public Collection<InputStream> fetch() {
        return Collections.unmodifiableCollection(this.streams);
    }

}
