<img src="http://img.jcabi.com/logo-square.png" width="64px" height="64px" />

[![EO principles respected here](https://www.elegantobjects.org/badge.svg)](https://www.elegantobjects.org)
[![Managed by Zerocracy](https://www.0crat.com/badge/C3RUBL5H9.svg)](https://www.0crat.com/p/C3RUBL5H9)
[![DevOps By Rultor.com](http://www.rultor.com/b/jcabi/jcabi-manifests)](http://www.rultor.com/p/jcabi/jcabi-manifests)

[![Build Status](https://travis-ci.org/jcabi/jcabi-manifests.svg?branch=master)](https://travis-ci.org/jcabi/jcabi-manifests)
[![PDD status](http://www.0pdd.com/svg?name=jcabi/jcabi-manifests)](http://www.0pdd.com/p?name=jcabi/jcabi-manifests)
[![Build status](https://ci.appveyor.com/api/projects/status/p49ene126ubcl6va/branch/master?svg=true)](https://ci.appveyor.com/project/yegor256/jcabi-manifests/branch/master)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.jcabi/jcabi-manifests/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.jcabi/jcabi-manifests)
[![Javadoc](https://javadoc.io/badge/com.jcabi/jcabi-manifests.svg)](http://www.javadoc.io/doc/com.jcabi/jcabi-manifests)
[![Dependencies](https://www.versioneye.com/user/projects/561ac3d0a193340f3200104f/badge.svg?style=flat)](https://www.versioneye.com/user/projects/561ac3d0a193340f3200104f)

[![jpeek report](http://i.jpeek.org/com.jcabi/jcabi-manifests/badge.svg)](http://i.jpeek.org/com.jcabi/jcabi-manifests/)

More details are here: [manifests.jcabi.com](http://manifests.jcabi.com/index.html).
Also, read this blog post: [How to Read MANIFEST.MF Files](http://www.yegor256.com/2014/07/03/how-to-read-manifest-mf.html).

Manipulations with MANIFEST.MF files made easy:

```java
import com.jcabi.manifests.Manifests;
public class Main {
  public static void main(String[] args) {
    String version = Manifests.read("JCabi-Version");
    System.out.println("version is " + version);
  }
}
```

## Questions?

If you have any questions about the framework, or something doesn't work as expected,
please [submit an issue here](https://github.com/jcabi/jcabi-manifests/issues/new).
If you want to discuss, please use our [Google Group](https://groups.google.com/forum/#!forum/jcabi).

## How to contribute?

Fork the repository, make changes, submit a pull request.
We promise to review your changes same day and apply to
the `master` branch, if they look correct.

Please run Maven build before submitting a pull request:

```
$ mvn clean install -Pqulice
```
