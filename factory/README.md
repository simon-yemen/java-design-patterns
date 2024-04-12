---
title: Factory
category: Creational
language: en
tag:
 - Gang of Four
---

## Also known as

* Simple Factory
* Static Factory Method

## Intent

Providing a static method encapsulated in a class called the factory, to hide the implementation 
logic and make client code focus on usage rather than initializing new objects.
提供一个封装在名为工厂的类中的静态方法，以隐藏实现逻辑，并使客户端代码专注于使用，而不是初始化新对象。

## Explanation

Real-world example

> Imagine an alchemist who is about to manufacture coins. The alchemist must be able to create both 
> gold and copper coins and switching between them must be possible without modifying the existing 
> source code. The factory pattern makes it possible by providing a static construction method which 
> can be called with relevant parameters.
> 想象一下，一个炼金术士将要制造硬币。炼金术士必须能够创造黄金和铜币之间的切换必须是可能的，而不修改现有的源代码。工厂模式通过提供一个可以使用相关参数调用的静态构造方法来实现这一点。


Wikipedia says

> Factory is an object for creating other objects – formally a factory is a function or method that 
> returns objects of a varying prototype or class.
> 工厂是用于创建其他对象的对象——形式上，工厂是一个函数或方法，它返回不同原型或类的对象。


**Programmatic Example**

We have an interface `Coin` and two implementations `GoldCoin` and `CopperCoin`.

```java
public interface Coin {
  String getDescription();
}

public class GoldCoin implements Coin {

  static final String DESCRIPTION = "This is a gold coin.";

  @Override
  public String getDescription() {
    return DESCRIPTION;
  }
}

public class CopperCoin implements Coin {
   
  static final String DESCRIPTION = "This is a copper coin.";

  @Override
  public String getDescription() {
    return DESCRIPTION;
  }
}
```

Enumeration below represents types of coins that we support (`GoldCoin` and `CopperCoin`).

```java
@RequiredArgsConstructor
@Getter
public enum CoinType {

  COPPER(CopperCoin::new),
  GOLD(GoldCoin::new);

  private final Supplier<Coin> constructor;
}
```

Then we have the static method `getCoin` to create coin objects encapsulated in the factory class 
`CoinFactory`.

```java
public class CoinFactory {

  public static Coin getCoin(CoinType type) {
    return type.getConstructor().get();
  }
}
```

Now on the client code we can create different types of coins using the factory class.

```java
LOGGER.info("The alchemist begins his work.");
var coin1 = CoinFactory.getCoin(CoinType.COPPER);
var coin2 = CoinFactory.getCoin(CoinType.GOLD);
LOGGER.info(coin1.getDescription());
LOGGER.info(coin2.getDescription());
```

Program output:

```java
The alchemist begins his work.
This is a copper coin.
This is a gold coin.
```

## Class Diagram

![alt text](./etc/factory.urm.png "Factory pattern class diagram")

## Applicability

Use the factory pattern when you only care about the creation of an object, not how to create 
and manage it.
当你只关心对象的创建，而不关心如何创建和管理它时，可以使用工厂模式。


Pros

* Allows keeping all objects creation in one place and avoid of spreading 'new' keyword across codebase.允许将所有对象创建在一个地方，并避免在代码库中传播` new `关键字。
* Allows to write loosely coupled code. Some of its main advantages include better testability, easy-to-understand code, swappable components, scalability and isolated features.允许编写松散耦合的代码。它的一些主要优点包括更好的可测试性，易于理解的代码，可切换的组件，可扩展性和隔离特性。

Cons

* The code becomes more complicated than it should be. 

## Known uses

* [java.util.Calendar#getInstance()](https://docs.oracle.com/javase/8/docs/api/java/util/Calendar.html#getInstance--)
* [java.util.ResourceBundle#getBundle()](https://docs.oracle.com/javase/8/docs/api/java/util/ResourceBundle.html#getBundle-java.lang.String-)
* [java.text.NumberFormat#getInstance()](https://docs.oracle.com/javase/8/docs/api/java/text/NumberFormat.html#getInstance--)
* [java.nio.charset.Charset#forName()](https://docs.oracle.com/javase/8/docs/api/java/nio/charset/Charset.html#forName-java.lang.String-)
* [java.net.URLStreamHandlerFactory#createURLStreamHandler(String)](https://docs.oracle.com/javase/8/docs/api/java/net/URLStreamHandlerFactory.html) (returns different singleton objects, depending on a protocol)
* [java.util.EnumSet#of()](https://docs.oracle.com/javase/8/docs/api/java/util/EnumSet.html#of(E))
* [javax.xml.bind.JAXBContext#createMarshaller()](https://docs.oracle.com/javase/8/docs/api/javax/xml/bind/JAXBContext.html#createMarshaller--) and other similar methods.

## Related patterns

* [Factory Method](https://java-design-patterns.com/patterns/factory-method/)
* [Factory Kit](https://java-design-patterns.com/patterns/factory-kit/)
* [Abstract Factory](https://java-design-patterns.com/patterns/abstract-factory/)
