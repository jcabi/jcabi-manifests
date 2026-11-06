/*
 * SPDX-FileCopyrightText: Copyright (c) 2012-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.manifests;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link ClasspathMfs}.
 *
 * @since 2.1.0
 */
final class ClasspathMfsTest {

    @Test
    void findsManifestsWithoutContextClassLoader() throws Exception {
        final Thread thread = Thread.currentThread();
        final ClassLoader original = thread.getContextClassLoader();
        thread.setContextClassLoader(null);
        try {
            final MfMap manifests = new Manifests();
            manifests.append(new ClasspathMfs());
            MatcherAssert.assertThat(
                "A null context loader must still locate classpath manifests",
                manifests.get("Built-By"),
                Matchers.notNullValue()
            );
        } finally {
            thread.setContextClassLoader(original);
        }
    }
}
