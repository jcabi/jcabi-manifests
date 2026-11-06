/*
 * SPDX-FileCopyrightText: Copyright (c) 2012-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.manifests;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Iterator;

/**
 * Streams owned by one manifest operation.
 *
 * @since 2.1.0
 */
final class CloseableStreams implements Closeable {

    /**
     * Owned streams.
     */
    private final Collection<InputStream> streams;

    /**
     * Ctor.
     *
     * @param list Streams to close together
     */
    CloseableStreams(final Collection<InputStream> list) {
        this.streams = list;
    }

    @Override
    public void close() throws IOException {
        IOException failure = null;
        final Iterator<InputStream> iterator = this.streams.iterator();
        while (iterator.hasNext()) {
            try {
                iterator.next().close();
            } catch (final IOException ex) {
                if (failure == null) {
                    failure = ex;
                } else if (!failure.equals(ex)) {
                    failure.addSuppressed(ex);
                }
            }
        }
        if (failure != null) {
            throw failure;
        }
    }

    /**
     * Get the owned streams.
     *
     * @return Streams
     */
    Collection<InputStream> all() {
        return this.streams;
    }
}
