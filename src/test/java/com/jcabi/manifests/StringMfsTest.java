/*
 * SPDX-FileCopyrightText: Copyright (c) 2012-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.manifests;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.apache.commons.io.IOUtils;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link StringMfs}.
 *
 * @since 2.0.0
 */
final class StringMfsTest {

    @Test
    void turnsStringIntoMfs() throws IOException {
        final InputStream input = new StringMfs("Foo: bar").fetch().iterator().next();
        MatcherAssert.assertThat(
            IOUtils.toString(input, StandardCharsets.UTF_8),
            Matchers.containsString("Foo:")
        );
    }

}
