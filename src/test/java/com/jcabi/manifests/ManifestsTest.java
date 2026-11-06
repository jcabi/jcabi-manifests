/*
 * SPDX-FileCopyrightText: Copyright (c) 2012-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.manifests;

import com.jcabi.log.Logger;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

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
            () -> new Manifests().append(
                new FilesMfs(
                    new File(
                        Thread.currentThread().getContextClassLoader()
                            .getResource("META-INF/MANIFEST_INVALID.MF").getFile()
                    )
                )
            ),
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
            Logger.format("%s: %s%n", name, value).getBytes(StandardCharsets.UTF_8)
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
            "fails to read from stream",
            manifests.get("From-File"),
            Matchers.equalTo("some test attribute")
        );
    }

    @Test
    void appendToSingleton() throws Exception {
        Manifests.singleton().append(new StringMfs(String.format("foo: bar%n")));
        MatcherAssert.assertThat(
            "fails to work as expected",
            Manifests.read("foo"),
            Matchers.equalTo("bar")
        );
    }

    @Test
    void readsAttributeIgnoringCaseOfItsName() throws Exception {
        final Manifests manifests = new Manifests();
        manifests.append(new StringMfs(String.format("Scm-Revision: abc%n")));
        MatcherAssert.assertThat(
            "fails to find attribute by name in different case",
            manifests.get("SCM-Revision"),
            Matchers.equalTo("abc")
        );
    }

    @Test
    void closesEveryStreamAfterParsingFails() throws Exception {
        try (
            InputStream broken = ManifestsTest.stream("invalid header%n%n");
            InputStream valid = ManifestsTest.stream("Build-Version: available%n%n")
        ) {
            Assertions.assertThrows(
                IOException.class,
                () -> new Manifests().append(new StreamsMfs(Arrays.asList(broken, valid))),
                "A malformed manifest must still fail"
            );
            Mockito.verify(broken).close();
            Mockito.verify(valid).close();
        }
    }

    @Test
    void preservesParsingFailureWhenClosingFails() throws Exception {
        try (
            InputStream broken = ManifestsTest.stream("bad header%n%n");
            InputStream failing = ManifestsTest.stream("Build-Name: failing%n%n");
            InputStream last = ManifestsTest.stream("Build-Name: last%n%n")
        ) {
            final IOException failure = new IOException("Cannot close stream");
            Mockito.doThrow(failure).when(failing).close();
            MatcherAssert.assertThat(
                "The close failure must be suppressed on the parsing error",
                Assertions.assertThrows(
                    IOException.class,
                    () -> new Manifests().append(
                        new StreamsMfs(Arrays.asList(broken, failing, last))
                    ),
                    "The parsing error must propagate"
                ).getSuppressed(),
                Matchers.arrayContaining(failure)
            );
            Mockito.verify(last).close();
            Mockito.doNothing().when(failing).close();
        }
    }

    @Test
    void closesEveryStreamExactlyOnce() throws Exception {
        try (
            InputStream first = ManifestsTest.stream("Build-Name: first%n%n");
            InputStream second = ManifestsTest.stream("Build-Name: second%n%n")
        ) {
            final MfMap manifests = new Manifests();
            manifests.append(new StreamsMfs(Arrays.asList(first, second)));
            MatcherAssert.assertThat(
                "The first attribute must win",
                manifests.get("Build-Name"), Matchers.equalTo("first")
            );
            Mockito.verify(first).close();
            Mockito.verify(second).close();
        }
    }

    @Test
    void preservesFirstValueDuringConcurrentAppends() throws Exception {
        final ConcurrentNavigableMap<String, String> attributes = Mockito.spy(
            new ConcurrentSkipListMap<>(String.CASE_INSENSITIVE_ORDER)
        );
        final CyclicBarrier checked = new CyclicBarrier(2);
        Mockito.doAnswer(
            call -> {
                final Object present = call.callRealMethod();
                checked.await(10L, TimeUnit.SECONDS);
                return present;
            }
        ).when(attributes).containsKey(ArgumentMatchers.anyString());
        Mockito.doAnswer(
            call -> {
                final Object previous = call.callRealMethod();
                Assertions.assertNull(previous, "An appended value must never be replaced");
                return previous;
            }
        ).when(attributes).put(ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
        final MfMap manifests = new Manifests(attributes);
        final FutureTask<Void> first = new FutureTask<>(
            () -> {
                manifests.append(new StringMfs(String.format("Build-Name: first%n")));
                return null;
            }
        );
        final FutureTask<Void> second = new FutureTask<>(
            () -> {
                manifests.append(new StringMfs(String.format("build-name: second%n")));
                return null;
            }
        );
        new Thread(first).start();
        new Thread(second).start();
        first.get(10L, TimeUnit.SECONDS);
        second.get(10L, TimeUnit.SECONDS);
        MatcherAssert.assertThat(
            "Concurrent appends must store one value for case-insensitive duplicate keys",
            manifests.size(), Matchers.equalTo(1)
        );
    }

    private static InputStream stream(final String text) {
        return Mockito.spy(
            new ByteArrayInputStream(String.format(text).getBytes(StandardCharsets.UTF_8))
        );
    }
}
