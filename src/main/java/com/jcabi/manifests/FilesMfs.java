/*
 * SPDX-FileCopyrightText: Copyright (c) 2012-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.manifests;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

/**
 * Manifests in files.
 *
 * <p>Append attributes from {@code MANIFEST.MF} file:</p>
 *
 * <pre> Manifests.singleton().append(new FilesMfs(new File("MANIFEST.MF")));</pre>
 *
 * <p>The class is immutable and thread-safe.</p>
 *
 * @since 1.1
 */
public final class FilesMfs implements Mfs {

    /**
     * Files.
     */
    private final transient Collection<File> files;

    /**
     * Ctor.
     *
     * @param file File
     */
    public FilesMfs(final File file) {
        this(Collections.singleton(file));
    }

    /**
     * Ctor.
     *
     * @param list Files
     */
    public FilesMfs(final Collection<File> list) {
        this.files = new ArrayList<>(list);
    }

    @Override
    public Collection<InputStream> fetch() throws IOException {
        final Collection<InputStream> streams = new ArrayList<>(1);
        try {
            for (final File file : this.files) {
                streams.add(file.toURI().toURL().openStream());
            }
        } catch (final IOException failure) {
            final Iterator<InputStream> iterator = streams.iterator();
            while (iterator.hasNext()) {
                try {
                    iterator.next().close();
                } catch (final IOException closing) {
                    if (!closing.equals(failure)) {
                        failure.addSuppressed(closing);
                    }
                }
            }
            throw failure;
        }
        return streams;
    }
}
