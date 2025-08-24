/*
 * SPDX-FileCopyrightText: Copyright (c) 2012-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.manifests;

import com.jcabi.log.Logger;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link Manifests}.
 *
 * @since 0.7
 */
final class ManifestsTest {

    @Test
    void readsSingleExistingAttribute() {
        MatcherAssert.assertThat(
            "fails to read one property",
            Manifests.read("Built-By"),
            Matchers.notNullValue()
        );
    }

    @Test
    void throwsExceptionWhenAttributeIsEmpty() {
        Assertions.assertThrows(
            IllegalArgumentException.class,
            () -> Manifests.read("Jcabi-Test-Empty-Attribute"),
            "doesn't throw on empty attribute"
        );
    }

    @Test
    void throwsExceptionIfAttributeIsMissed() {
        Assertions.assertThrows(
            IllegalArgumentException.class,
            () -> Manifests.read("absent-property"),
            "doesn't throw on absent property"
        );
    }

    @Test
    void throwsExceptionWhenNoAttributes() {
        Assertions.assertThrows(
            IOException.class,
            () -> {
                final File file = new File(
                    Thread.currentThread().getContextClassLoader()
                        .getResource("META-INF/MANIFEST_INVALID.MF").getFile()
                );
                final Manifests mfs = new Manifests();
                mfs.append(new FilesMfs(file));
            },
            "doesn't throw"
        );
    }

    @Test
    void appendsAttributesFromFile() throws Exception {
        final String name = "Test-Attribute-From-File";
        final String value = "some text value of attribute";
        final File file = File.createTempFile("test-", ".MF");
        Files.write(
            file.toPath(),
            Logger.format("%s: %s\n", name, value).getBytes(StandardCharsets.UTF_8)
        );
        final MfMap manifests = new Manifests();
        manifests.append(new FilesMfs(file));
        MatcherAssert.assertThat(
            "loaded from file",
            manifests.containsKey(name) && manifests.get(name).equals(value),
            Matchers.is(true)
        );
    }

    @Test
    void appendsAttributesFromInputStream() throws Exception {
        final MfMap manifests = new Manifests();
        manifests.append(
            new StreamsMfs(this.getClass().getResourceAsStream("test.mf"))
        );
        MatcherAssert.assertThat(
            "failes to read from stream",
            manifests.get("From-File"),
            Matchers.equalTo("some test attribute")
        );
    }

    @Test
    void appendToSingleton() throws Exception {
        Manifests.singleton().append(new StringMfs("foo: bar\n"));
        MatcherAssert.assertThat(
            "fails to work as expected",
            Manifests.read("foo"),
            Matchers.equalTo("bar")
        );
    }

}
