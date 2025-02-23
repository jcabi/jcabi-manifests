/*
 * SPDX-FileCopyrightText: Copyright (c) 2012-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.manifests;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

/**
 * Manifests.
 *
 * @since 1.0
 */
public interface Mfs {

    /**
     * Find and fetch them all.
     * @return Iterator of found resources
     * @throws IOException If fails
     */
    Collection<InputStream> fetch() throws IOException;

}
