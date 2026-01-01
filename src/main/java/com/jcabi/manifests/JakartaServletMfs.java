/*
 * SPDX-FileCopyrightText: Copyright (c) 2012-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.manifests;

import com.jcabi.log.Logger;
import jakarta.servlet.ServletContext;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Manifests in servlet context.
 *
 * Append attributes from the web application {@code MANIFEST.MF}.
 *
 * <p>You can use this class in your own
 * {@link jakarta.servlet.Filter} or
 * {@link jakarta.servlet.ServletContextListener},
 * in order to inject {@code MANIFEST.MF} attributes to the class:
 *
 * <pre> Manifests.append(new JakartaServletMfs(context));</pre>
 *
 * <p>The class is thread-safe.
 *
 * @since 1.0
 */
public final class JakartaServletMfs implements Mfs {

    /**
     * Servlet context.
     */
    private final transient ServletContext ctx;

    /**
     * Ctor.
     * @param context Context
     */
    public JakartaServletMfs(final ServletContext context) {
        this.ctx = context;
    }

    @Override
    public Collection<InputStream> fetch() throws IOException {
        final URL main = this.ctx.getResource("/META-INF/MANIFEST.MF");
        final Collection<InputStream> streams = new ArrayList<>(1);
        if (main == null) {
            Logger.warn(this, "MANIFEST.MF not found in WAR package");
        } else {
            streams.add(main.openStream());
        }
        return streams;
    }

}
