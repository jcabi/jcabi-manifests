/*
 * SPDX-FileCopyrightText: Copyright (c) 2012-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.manifests;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;

/**
 * Manifests in a UTF-8 string.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 1.3
 */
public final class StringMfs implements Mfs {

    /**
     * The string.
     */
    private final transient String source;

    /**
     * Ctor.
     * @param src The source
     */
    public StringMfs(final String src) {
        this.source = src;
    }

    @Override
    public Collection<InputStream> fetch() {
        return Collections.singleton(
            new ByteArrayInputStream(this.source.getBytes(StandardCharsets.UTF_8))
        );
    }

}
