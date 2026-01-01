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

/**
 * Manifests in files.
 *
 * Append attributes from {@code MANIFEST.MF} file:
 *
 * <pre> Manifests.append(new FilesMfs(new File("MANIFEST.MF")));</pre>
 *
 * <p>The class is immutable and thread-safe.
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
     * @param file File
     */
    public FilesMfs(final File file) {
        this(Collections.singleton(file));
    }

    /**
     * Ctor.
     * @param list Files
     */
    public FilesMfs(final Collection<File> list) {
        this.files = Collections.unmodifiableCollection(list);
    }

    @Override
    public Collection<InputStream> fetch() throws IOException {
        final Collection<InputStream> streams = new ArrayList<>(1);
        for (final File file : this.files) {
            streams.add(file.toURI().toURL().openStream());
        }
        return streams;
    }

}
