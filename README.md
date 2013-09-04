More details are here: [www.jcabi.com/jcabi-manifests](http://www.jcabi.com/jcabi-manifests/index.html)

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

You need just this dependency:

```xml
<dependency>
  <groupId>com.jcabi</groupId>
  <artifactId>jcabi-manifests</artifactId>
  <version>1.0</version>
</dependency>
```

