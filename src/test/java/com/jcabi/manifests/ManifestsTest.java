/*
 * Copyright (c) 2012-2025, jcabi.com
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met: 1) Redistributions of source code must retain the above
 * copyright notice, this list of conditions and the following
 * disclaimer. 2) Redistributions in binary form must reproduce the above
 * copyright notice, this list of conditions and the following
 * disclaimer in the documentation and/or other materials provided
 * with the distribution. 3) Neither the name of the jcabi.com nor
 * the names of its contributors may be used to endorse or promote
 * products derived from this software without specific prior written
 * permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT
 * NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 * THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
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
            Manifests.read("Built-By"),
            Matchers.notNullValue()
        );
    }

    @Test
    void throwsExceptionWhenAttributeIsEmpty() {
        Assertions.assertThrows(
            IllegalArgumentException.class,
            () -> Manifests.read("Jcabi-Test-Empty-Attribute")
        );
    }

    @Test
    void throwsExceptionIfAttributeIsMissed() {
        Assertions.assertThrows(
            IllegalArgumentException.class,
            () -> Manifests.read("absent-property")
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
            }
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
            manifests.containsKey(name) && manifests.get(name).equals(value)
        );
    }

    @Test
    void appendsAttributesFromInputStream() throws Exception {
        final MfMap manifests = new Manifests();
        manifests.append(
            new StreamsMfs(this.getClass().getResourceAsStream("test.mf"))
        );
        MatcherAssert.assertThat(
            manifests.get("From-File"),
            Matchers.equalTo("some test attribute")
        );
    }

    @Test
    void appendToSingleton() throws Exception {
        Manifests.singleton().append(new StringMfs("foo: bar\n"));
        MatcherAssert.assertThat(
            Manifests.read("foo"),
            Matchers.equalTo("bar")
        );
    }

}
