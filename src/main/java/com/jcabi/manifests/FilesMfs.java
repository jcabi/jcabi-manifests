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
