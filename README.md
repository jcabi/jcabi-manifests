<img src="http://img.jcabi.com/logo-square.png" width="64px" height="64px" />
 
[![Build Status](https://travis-ci.org/jcabi/jcabi-manifests.svg?branch=master)](https://travis-ci.org/jcabi/jcabi-manifests)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.jcabi/jcabi-manifests/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.jcabi/jcabi-manifests)

More details are here: [manifests.jcabi.com](http://manifests.jcabi.com/index.html)

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
