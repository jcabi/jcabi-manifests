/*
 * SPDX-FileCopyrightText: Copyright (c) 2012-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.manifests;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;

/**
 * Manifests in classpath.
 *
 * @since 1.0
 */
public final class ClasspathMfs implements Mfs {

    @Override
    public Collection<InputStream> fetch() throws IOException {
        final Enumeration<URL> resources = Thread.currentThread()
            .getContextClassLoader()
            .getResources("META-INF/MANIFEST.MF");
        final Collection<InputStream> streams = new ArrayList<>(1);
        while (resources.hasMoreElements()) {
            streams.add(resources.nextElement().openStream());
        }
        return streams;
    }

}
