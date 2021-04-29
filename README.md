# datasource
Support master/slave switchable datasources.
configuration sample:
```yaml
spring:
  data-source: # master datasource
    url: jdbc:mysql://...
    ...
    ...
  data-source-slave: # slave datasource
    url: jdbc:mysql://...
    ...
    ...
```
Annotate @ReadOnly before any class/method, then it will use slave connection. This annotation will be ignored if current thread is in an active transaction. So don't worry about the side effect.  
code sample 1:
```java
import nameless.common.datasource.aop.ReadOnly;
// ...
@ReadOnly
public class Sample1 {
  public void daoMethod() {
    // ...
  }
}
```
code sample 2:
```java
import nameless.common.datasource.aop.ReadOnly;
// ...
public class Sample2 {
  @ReadOnly
  public void method1() {
    // ...
  }
  
  /**
   * here @ReadOnly will be ignored. master connection will be selected
   */
  @ReadOnly
  @Transactional
  public void method1() {
  
}  
```
