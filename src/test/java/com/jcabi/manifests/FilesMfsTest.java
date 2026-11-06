/*
 * SPDX-FileCopyrightText: Copyright (c) 2012-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.manifests;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.util.Arrays;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mockito;

/**
 * Test case for {@link FilesMfs}.
 *
 * @since 2.1.0
 */
final class FilesMfsTest {

    @Test
    void closesEarlierStreamsWhenOpeningFails(@TempDir final Path directory) throws Exception {
        try (
            InputStream stream = Mockito.mock(InputStream.class)
        ) {
            Assertions.assertThrows(
                FileNotFoundException.class,
                () -> new FilesMfs(
                    Arrays.asList(
                        FilesMfsTest.file(stream), directory.resolve("missing.mf").toFile()
                    )
                ).fetch(),
                "The missing file must fail to open"
            );
            Mockito.verify(stream).close();
        }
    }

    @Test
    void preservesOpeningFailureWhenClosingFails(@TempDir final Path directory) throws Exception {
        try (
            InputStream first = Mockito.mock(InputStream.class);
            InputStream second = Mockito.mock(InputStream.class)
        ) {
            final IOException closing = new IOException("Cannot close manifest");
            Mockito.doThrow(closing).when(first).close();
            MatcherAssert.assertThat(
                "The close error must be suppressed on the opening error",
                Assertions.assertThrows(
                    FileNotFoundException.class,
                    () -> new FilesMfs(
                        Arrays.asList(
                            FilesMfsTest.file(first), FilesMfsTest.file(second),
                            directory.resolve("absent.mf").toFile()
                        )
                    ).fetch(),
                    "The opening failure must propagate"
                ).getSuppressed(),
                Matchers.arrayContaining(closing)
            );
            Mockito.verify(second).close();
            Mockito.doNothing().when(first).close();
        }
    }

    @Test
    void leavesSuccessfulStreamsOpen() throws Exception {
        try (
            InputStream stream = Mockito.mock(InputStream.class)
        ) {
            MatcherAssert.assertThat(
                "The caller must receive the opened stream",
                new FilesMfs(FilesMfsTest.file(stream)).fetch(), Matchers.contains(stream)
            );
            Mockito.verify(stream, Mockito.never()).close();
        }
    }

    private static File file(final InputStream stream) throws Exception {
        final File file = Mockito.mock(File.class);
        final URI uri = Mockito.mock(URI.class);
        final URL url = Mockito.mock(URL.class);
        Mockito.when(file.toURI()).thenReturn(uri);
        Mockito.when(uri.toURL()).thenReturn(url);
        Mockito.when(url.openStream()).thenReturn(stream);
        return file;
    }
}
