/**
 * Copyright (c) 2012-2015, jcabi.com
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
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import javax.servlet.ServletContext;

/**
 * Static reader of {@code META-INF/MANIFEST.MF} files.
 *
 * The class provides convenient methods to read
 * all {@code MANIFEST.MF} files available in classpath
 * and all attributes from them.
 *
 * <p>This mechanism may be very useful for transferring
 * information from continuous integration environment to the production
 * environment. For example, you want your site to show project version and
 * the date of {@code WAR} file packaging. First, you configure
 * {@code maven-war-plugin} to add this information to {@code MANIFEST.MF}:
 *
 * <pre> &lt;plugin>
 *  &lt;artifactId>maven-war-plugin&lt;/artifactId>
 *  &lt;configuration>
 *   &lt;archive>
 *    &lt;manifestEntries>
 *     &lt;Foo-Version>${project.version}&lt;/Foo-Version>
 *     &lt;Foo-Date>${maven.build.timestamp}&lt;/Foo-Date>
 *    &lt;/manifestEntries>
 *   &lt;/archive>
 *  &lt;/configuration>
 * &lt;/plugin></pre>
 *
 * <p>{@code maven-war-plugin} will add these attributes to your
 * {@code MANIFEST.MF} file and the
 * project will be deployed to the production environment. Then, you can read
 * these attributes where it's necessary (in one of your JAXB annotated objects,
 * for example) and show to users:
 *
 * <pre> import com.jcabi.manifests.Manifest;
 * import java.text.SimpleDateFormat;
 * import java.util.Date;
 * import java.util.Locale;
 * import javax.xml.bind.annotation.XmlElement;
 * import javax.xml.bind.annotation.XmlRootElement;
 * &#64;XmlRootElement
 * public final class Page {
 *   &#64;XmlElement
 *   public String version() {
 *     return Manifests.read("Foo-Version");
 *   }
 *   &#64;XmlElement
 *   public Date date() {
 *     return new SimpleDateFormat("yyyy.MM.dd", Locale.ENGLISH).parse(
 *       Manifests.read("Foo-Date");
 *     );
 *   }
 * }</pre>
 *
 * <p>If you want to add more manifests to the collection, use
 * its static instance:
 *
 * <pre>Manifests.DEFAULT.append(new FilesMfs(new File("MANIFEST.MF")));</pre>
 *
 * <p>You can also modify the map directly:
 *
 * <pre>Manifests.DEFAULT.put("Hello", "world");</pre>
 *
 * <p>The only dependency you need (check the latest version at
 * <a href="http://manifests.jcabi.com/">jcabi-manifests</a>):
 *
 * <pre> &lt;dependency>
 *  &lt;groupId>com.jcabi&lt;/groupId>
 *  &lt;artifactId>jcabi-manifests&lt;/artifactId>
 * &lt;/dependency></pre>
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @since 0.7
 * @see <a href="http://download.oracle.com/javase/1,5.0/docs/guide/jar/jar.html#JAR%20Manifest">JAR Manifest</a>
 * @see <a href="http://maven.apache.org/shared/maven-archiver/index.html">Maven Archiver</a>
 * @see <a href="http://manifests.jcabi.com/index.html">manifests.jcabi.com</a>
 * @see <a href="http://www.yegor256.com/2014/07/03/how-to-read-manifest-mf.html">How to Read MANIFEST.MF Files</a>
 */
@SuppressWarnings("PMD.TooManyMethods")
public final class Manifests implements MfMap {

    /**
     * Default singleton.
     */
    public static final MfMap DEFAULT = new Manifests();

    /**
     * Attributes retrieved.
     */
    private final transient Map<String, String> attributes;

    /**
     * All Attributes retrieved.
     * @todo #31:30min Decide how this multimap should behave during Map methods
     *  This class implements Map<String, String> and how allValues should
     *  behave after the many Map methods is unclear.
     */
    private final transient ConcurrentMap<String, List<String>> multimap =
        new ConcurrentHashMap<String, List<String>>();

    static {
        try {
            Manifests.DEFAULT.append(new ClasspathMfs());
        } catch (final IOException ex) {
            Logger.error(
                Manifests.class,
                "#load(): '%s' failed %[exception]s", ex
            );
        }
    }

    /**
     * Public ctor.
     * @since 1.0
     */
    public Manifests() {
        this(new HashMap<String, String>(0));
    }

    /**
     * Public ctor.
     * @param attrs Attributes to encapsulate
     * @since 1.0
     */
    public Manifests(final Map<String, String> attrs) {
        super();
        this.attributes = new ConcurrentHashMap<String, String>(attrs);
    }

    @Override
    public int size() {
        return this.attributes.size();
    }

    @Override
    public boolean isEmpty() {
        return this.attributes.isEmpty();
    }

    @Override
    public boolean containsKey(final Object key) {
        return this.attributes.containsKey(key);
    }

    @Override
    public boolean containsValue(final Object value) {
        return this.attributes.containsValue(value);
    }

    @Override
    public String get(final Object key) {
        return this.attributes.get(key);
    }

    @Override
    public List<String> allValues(final String key) {
        return new ArrayList<String>(this.multimap.get(key));
    }

    @Override
    public String put(final String key, final String value) {
        return this.attributes.put(key, value);
    }

    @Override
    public String remove(final Object key) {
        return this.attributes.remove(key);
    }

    @Override
    public void putAll(final Map<? extends String, ? extends String> attrs) {
        this.attributes.putAll(attrs);
    }

    @Override
    public void clear() {
        this.attributes.clear();
    }

    @Override
    public Set<String> keySet() {
        return this.attributes.keySet();
    }

    @Override
    public Collection<String> values() {
        return this.attributes.values();
    }

    @Override
    public Set<Map.Entry<String, String>> entrySet() {
        return this.attributes.entrySet();
    }

    @Override
    public MfMap append(final Mfs streams) throws IOException {
        final long start = System.currentTimeMillis();
        final Collection<InputStream> list = streams.fetch();
        int saved = 0;
        int ignored = 0;
        for (final InputStream stream : list) {
            for (final Map.Entry<String, String> attr
                : Manifests.load(stream).entrySet()) {
                if (this.attributes.containsKey(attr.getKey())) {
                    ++ignored;
                } else {
                    this.attributes.put(attr.getKey(), attr.getValue());
                    ++saved;
                }
                this.addToMultiMap(attr.getKey(), attr.getValue());
            }
        }
        Logger.info(
            this,
            // @checkstyle LineLength (1 line)
            "%d attributes loaded from %d stream(s) in %[ms]s, %d saved, %d ignored: %[list]s",
            this.attributes.size(), list.size(),
            System.currentTimeMillis() - start,
            saved, ignored,
            new TreeSet<String>(this.attributes.keySet())
        );
        return this;
    }

    /**
     * Read one attribute available in one of {@code MANIFEST.MF} files.
     *
     * <p>If such a attribute doesn't exist {@link IllegalArgumentException}
     * will be thrown. If you're not sure whether the attribute is present or
     * not use {@link #exists(String)} beforehand.
     *
     * <p>The method is thread-safe.
     *
     * @param name Name of the attribute
     * @return The value of the attribute retrieved
     */
    public static String read(final String name) {
        if (name == null) {
            throw new IllegalArgumentException("attribute can't be NULL");
        }
        if (name.isEmpty()) {
            throw new IllegalArgumentException("attribute can't be empty");
        }
        if (!Manifests.exists(name)) {
            throw new IllegalArgumentException(
                Logger.format(
                    // @checkstyle LineLength (1 line)
                    "Attribute '%s' not found in MANIFEST.MF file(s) among %d other attribute(s): %[list]s",
                    name,
                    Manifests.DEFAULT.size(),
                    new TreeSet<String>(Manifests.DEFAULT.keySet())
                )
            );
        }
        return Manifests.DEFAULT.get(name);
    }

    /**
     * Check whether attribute exists in any of {@code MANIFEST.MF} files.
     *
     * <p>Use this method before {@link #read(String)} to check whether an
     * attribute exists, in order to avoid a runtime exception.
     *
     * <p>The method is thread-safe.
     *
     * @param name Name of the attribute to check
     * @return Returns {@code TRUE} if it exists, {@code FALSE} otherwise
     */
    public static boolean exists(final String name) {
        if (name == null) {
            throw new IllegalArgumentException("attribute name can't be NULL");
        }
        if (name.isEmpty()) {
            throw new IllegalArgumentException("attribute name can't be empty");
        }
        return Manifests.DEFAULT.containsKey(name);
    }

    /**
     * Append attributes from the web application {@code MANIFEST.MF}.
     *
     * <p>The method is deprecated. Instead, use this code:
     *
     * <pre>Manifests.DEFAULT.append(new ServletMfs());</pre>
     *
     * @param ctx Servlet context
     * @see #Manifests()
     * @throws IOException If some I/O problem inside
     * @deprecated Use {@link #append(Mfs)} and {@link ServletMfs} instead
     */
    @Deprecated
    public static void append(final ServletContext ctx) throws IOException {
        Manifests.DEFAULT.append(new ServletMfs(ctx));
    }

    /**
     * Append attributes from the file.
     *
     * <p>The method is deprecated. Instead, use this code:
     *
     * <pre>Manifests.DEFAULT.append(new FilesMfs(file));</pre>
     *
     * @param file The file to load attributes from
     * @throws IOException If some I/O problem inside
     * @deprecated Use {@link #append(Mfs)} and {@link FilesMfs} instead
     */
    @Deprecated
    public static void append(final File file) throws IOException {
        if (file == null) {
            throw new IllegalArgumentException("file can't be NULL");
        }
        Manifests.DEFAULT.append(new FilesMfs(file));
    }

    /**
     * Append attributes from input stream.
     *
     * <p>The method is deprecated. Instead, use this code:
     *
     * <pre>Manifests.DEFAULT.append(new StreamsMfs(stream));</pre>
     *
     * @param stream Stream to use
     * @throws IOException If some I/O problem inside
     * @since 0.8
     * @deprecated Use {@link #append(Mfs)} and {@link StreamsMfs} instead
     */
    @Deprecated
    public static void append(final InputStream stream) throws IOException {
        if (stream == null) {
            throw new IllegalArgumentException("input stream can't be NULL");
        }
        Manifests.DEFAULT.append(new StreamsMfs(stream));
    }

    /**
     * Load attributes from input stream.
     *
     * <p>Inside the method we catch {@code RuntimeException} (which may look
     * suspicious) in order to protect our execution flow from expected (!)
     * exceptions from {@link Manifest#getMainAttributes()}. For some reason,
     * this JDK method doesn't throw checked exceptions if {@code MANIFEST.MF}
     * file format is broken. Instead, it throws a runtime exception (an
     * unchecked one), which we should catch in such an inconvenient way.
     *
     * @param stream Stream to load from
     * @return The attributes loaded
     * @throws IOException If some problem happens
     * @since 0.8
     */
    @SuppressWarnings("PMD.AvoidCatchingGenericException")
    private static Map<String, String> load(final InputStream stream)
        throws IOException {
        final ConcurrentMap<String, String> props =
            new ConcurrentHashMap<String, String>(0);
        try {
            final Manifest manifest = new Manifest(stream);
            final Attributes attrs = manifest.getMainAttributes();
            for (final Object key : attrs.keySet()) {
                final String value = attrs.getValue(
                    Attributes.Name.class.cast(key)
                );
                props.put(key.toString(), value);
            }
            Logger.debug(
                Manifests.class,
                "%d attribute(s) loaded %[list]s",
                props.size(), new TreeSet<String>(props.keySet())
            );
        // @checkstyle IllegalCatch (1 line)
        } catch (final RuntimeException ex) {
            Logger.error(Manifests.class, "#load(): failed %[exception]s", ex);
        } finally {
            stream.close();
        }
        return props;
    }

    /**
     * For a new key, adds a new list with one item otherwise adds the value to
     * the existing list for that key.
     * @param key The key for the attribute
     * @param value The value for the attribute
     */
    private void addToMultiMap(final String key, final String value) {
        this.multimap.putIfAbsent(
            key,
            new LinkedList<String>()
        );
        this.multimap.get(key).add(value);
    }
}
