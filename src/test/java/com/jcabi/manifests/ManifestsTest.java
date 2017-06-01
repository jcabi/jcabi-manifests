/**
 * Copyright (c) 2012-2017, jcabi.com
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
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Test case for {@link Manifests}.
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @since 0.7
 */
public final class ManifestsTest {

    /**
     * Manifests can read a single attribute, which always exist in MANIFEST.MF.
     * @throws Exception If something goes wrong
     */
    @Test
    public void readsSingleExistingAttribute() throws Exception {
        MatcherAssert.assertThat(
            Manifests.read("JCabi-Version"),
            Matchers.notNullValue()
        );
    }

    /**
     * Manifests can throw an exception if an attribute is empty.
     * @throws Exception If something goes wrong
     */
    @Test(expected = IllegalArgumentException.class)
    public void throwsExceptionWhenAttributeIsEmpty() throws Exception {
        Manifests.read("Jcabi-Test-Empty-Attribute");
    }

    /**
     * Manifests can throw an exception when attribute is absent.
     * @throws Exception If something goes wrong
     */
    @Test(expected = IllegalArgumentException.class)
    public void throwsExceptionIfAttributeIsMissed() throws Exception {
        Manifests.read("absent-property");
    }

    /**
     * Manifests can throw an exception loading file with empty attribute.
     * @throws Exception If something goes wrong
     */
    @Test(expected = IOException.class)
    public void throwsExceptionWhenNoAttributes() throws Exception {
        final File file = new File(
            Thread.currentThread().getContextClassLoader()
                .getResource("META-INF/MANIFEST_INVALID.MF").getFile()
        );
        final Manifests mfs = new Manifests();
        mfs.append(new FilesMfs(file));
    }

    /**
     * Manifests can append attributes from file.
     * @throws Exception If something goes wrong
     */
    @Test
    public void appendsAttributesFromFile() throws Exception {
        final String name = "Test-Attribute-From-File";
        final String value = "some text value of attribute";
        final File file = File.createTempFile("test-", ".MF");
        FileUtils.writeStringToFile(
            file,
            Logger.format("%s: %s\n", name, value)
        );
        final Manifests mfs = new Manifests();
        mfs.append(new FilesMfs(file));
        MatcherAssert.assertThat(
            "loaded from file",
            mfs.containsKey(name) && mfs.get(name).equals(value)
        );
    }

    /**
     * Manifests can append input stream.
     * @throws Exception If something goes wrong
     * @since 0.8
     */
    @Test
    public void appendsAttributesFromInputStream() throws Exception {
        final Manifests mfs = new Manifests();
        mfs.append(
            new StreamsMfs(this.getClass().getResourceAsStream("test.mf"))
        );
        MatcherAssert.assertThat(
            mfs.get("From-File"),
            Matchers.equalTo("some test attribute")
        );
    }

    /**
     * Manifests can get the first value when multiple manifests with the same
     * attribute have been appended.
     * @throws Exception If something goes wrong
     */
    @Test
    public void getsFirstValueAcrossMultipleManifests() throws Exception {
        final String name = "Attr";
        final Manifests mfs = new Manifests();
        mfs.append(ManifestsTest.manifestStream("Attr: a\n"));
        mfs.append(ManifestsTest.manifestStream("Attr: b\n"));
        MatcherAssert.assertThat(
            "The value is from the first manifest",
            mfs.containsKey(name) && "a".equals(mfs.get(name))
        );
    }

    /**
     * Manifests can get all values of an attribute across multiple appended
     * manifests.
     * @throws Exception If something goes wrong
     */
    @Test
    public void listsAllValuesOfSameKey() throws Exception {
        final Manifests mfs = new Manifests();
        mfs.append(ManifestsTest.manifestStream("Multi-Value-Attr: 1\n"));
        mfs.append(ManifestsTest.manifestStream("Multi-Value-Attr: 2\n"));
        MatcherAssert.assertThat(
            mfs.allValues("Multi-Value-Attr"),
            Matchers.contains("1", "2")
        );
    }

    /**
     * Manifest can get all values of two attributes across multiple appended
     * manifests.
     * @throws Exception If something goes wrong
     */
    @Test
    public void listsAllValuesOfSameKeyAcrossMultipleManifests()
        throws Exception {
        final Manifests mfs = new Manifests();
        mfs.append(ManifestsTest.manifestStream("Attr1: 1a\n"));
        mfs.append(ManifestsTest.manifestStream("Attr1: 1b\nAttr2: 2a\n"));
        mfs.append(ManifestsTest.manifestStream("Attr1: 1c\n"));
        mfs.append(ManifestsTest.manifestStream("Attr2: 2b\n"));
        MatcherAssert.assertThat(
            mfs.allValues("Attr1"),
            Matchers.contains("1a", "1b", "1c")
        );
        MatcherAssert.assertThat(
            mfs.allValues("Attr2"),
            Matchers.contains("2a", "2b")
        );
    }

    /**
     * Manifest can return a list of all values that will not get mutated by
     * further append operations.
     * @throws Exception If something goes wrong
     */
    @Test
    public void returnsIndependentListOfAllValues() throws Exception {
        final String attr = "Attr3";
        final String first = "first";
        final String second = "second";
        final Manifests mfs = new Manifests();
        mfs.append(ManifestsTest.manifestStream("Attr3: first\n"));
        final List<String> list = mfs.allValues(attr);
        mfs.append(ManifestsTest.manifestStream("Attr3: second\n"));
        MatcherAssert.assertThat(
            list, Matchers.contains(first)
        );
        MatcherAssert.assertThat(
            mfs.allValues(attr),
            Matchers.contains(first, second)
        );
    }

    /**
     * A new in memory Manifest containing the supplied string as content.
     * @param content The content of the manifest
     * @return A Mfs Manifest
     */
    private static Mfs manifestStream(final String content) {
        return new StreamsMfs(
            new ByteArrayInputStream(
                content.getBytes(StandardCharsets.UTF_8)
            )
        );
    }

}
