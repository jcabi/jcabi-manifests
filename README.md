<img alt="logo" src="https://www.jcabi.com/logo-square.svg" width="64px" height="64px" />

[![EO principles respected here](https://www.elegantobjects.org/badge.svg)](https://www.elegantobjects.org)
[![DevOps By Rultor.com](http://www.rultor.com/b/jcabi/jcabi-manifests)](http://www.rultor.com/p/jcabi/jcabi-manifests)

[![mvn](https://github.com/jcabi/jcabi-manifests/actions/workflows/mvn.yml/badge.svg)](https://github.com/jcabi/jcabi-manifests/actions/workflows/mvn.yml)
[![PDD status](http://www.0pdd.com/svg?name=jcabi/jcabi-manifests)](http://www.0pdd.com/p?name=jcabi/jcabi-manifests)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.jcabi/jcabi-manifests/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.jcabi/jcabi-manifests)
[![Javadoc](https://javadoc.io/badge/com.jcabi/jcabi-manifests.svg)](http://www.javadoc.io/doc/com.jcabi/jcabi-manifests)

More details are here: [manifests.jcabi.com](http://manifests.jcabi.com/index.html).
Also, read this blog post: [How to Read MANIFEST.MF Files](http://www.yegor256.com/2014/07/03/how-to-read-manifest-mf.html).

Manipulations with `MANIFEST.MF` files in Java made easy:

```java
import com.jcabi.manifests.Manifests;
public class Main {
  public static void main(String[] args) {
    String version = Manifests.read("JCabi-Version");
    System.out.println("version is " + version);
  }
}
```

## How to contribute?

Fork the repository, make changes, submit a pull request.
We promise to review your changes same day and apply to
the `master` branch, if they look correct.

Please run Maven build before submitting a pull request:

```
$ mvn clean install -Pqulice
```

