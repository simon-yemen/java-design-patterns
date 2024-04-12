---
title: Factory Kit
category: Creational
language: en
tag:
 - Extensibility
---
## Also Known As

Abstract-Factory

## Intent

Define a factory of immutable content with separated builder and factory interfaces.
使用分离的构建器和工厂接口定义一个不可变内容的工厂。

## Explanation

Real-world example

> Imagine a magical weapon factory that can create any type of weapon wished for. When the factory
> is unboxed, the master recites the weapon types needed to prepare it. After that, any of those
> weapon types can be summoned in an instant. 
> 想象一下，有一个神奇的武器工厂，可以制造任何想要的武器。当工厂被打开的时候，大师背诵武器类型需要准备它。之后，任何武器类型都可以在瞬间被召唤。

In plain words

> Factory kit is a configurable object builder, a factory to create factories.
> Factory kit是一个可配置的对象构建器，一个创建工厂的工厂。

**Programmatic Example**

Let's first define the simple `Weapon` hierarchy.

```java
public interface Weapon {
}

public enum WeaponType {
    SWORD,
    AXE,
    BOW,
    SPEAR
}

public class Sword implements Weapon {
    @Override
    public String toString() {
        return "Sword";
    }
}

// Axe, Bow, and Spear are defined similarly
```

Next, we define a functional interface that allows adding a builder with a name to the factory.
接下来，我们定义一个函数式接口，它允许向工厂添加一个带有名称的构建器。

```java
public interface Builder {
  void add(WeaponType name, Supplier<Weapon> supplier);
}
```

The meat of the example is the `WeaponFactory` interface that effectively implements the factory
kit pattern. The method `#factory` is used to configure the factory with the classes it needs to
be able to construct. The method `#create` is then used to create object instances.
这个例子的核心是有效地实现了factory kit模式的WeaponFactory接口。#factory方法用于使用它需要能够构造的类来配置工厂。#create方法用于创建对象实例。

```java
public interface WeaponFactory {

  static WeaponFactory factory(Consumer<Builder> consumer) {
      var map = new HashMap<WeaponType, Supplier<Weapon>>();
      consumer.accept(map::put);
      return name -> map.get(name).get();
  }
    
  Weapon create(WeaponType name);
}
```

Now, we can show how `WeaponFactory` can be used.

```java
var factory = WeaponFactory.factory(builder -> {
  builder.add(WeaponType.SWORD, Sword::new);
  builder.add(WeaponType.AXE, Axe::new);
  builder.add(WeaponType.SPEAR, Spear::new);
  builder.add(WeaponType.BOW, Bow::new);
});
var list = new ArrayList<Weapon>();
list.add(factory.create(WeaponType.AXE));
list.add(factory.create(WeaponType.SPEAR));
list.add(factory.create(WeaponType.SWORD));
list.add(factory.create(WeaponType.BOW));
list.stream().forEach(weapon -> LOGGER.info("{}", weapon.toString()));
```

Here is the console output when the example is run.

```
21:15:49.709 [main] INFO com.iluwatar.factorykit.App - Axe
21:15:49.713 [main] INFO com.iluwatar.factorykit.App - Spear
21:15:49.713 [main] INFO com.iluwatar.factorykit.App - Sword
21:15:49.713 [main] INFO com.iluwatar.factorykit.App - Bow
```

## Class diagram

![alt text](./etc/factory-kit.png "Factory Kit")

## Applicability

Use the Factory Kit pattern when

* The factory class can't anticipate the types of objects it must create
* A new instance of a custom builder is needed instead of a global one
* The types of objects that the factory can build need to be defined outside the class
* The builder and creator interfaces need to be separated
* Game developments and other applications that have user customisation

## Related patterns

* [Builder](https://java-design-patterns.com/patterns/builder/)
* [Factory](https://java-design-patterns.com/patterns/factory/)
* [Abstract-Factory](https://java-design-patterns.com/patterns/abstract-factory/)

## Tutorials

* [Factory kit implementation tutorial](https://diego-pacheco.medium.com/factory-kit-pattern-66d5ccb0c405)

## Credits

* [Design Pattern Reloaded by Remi Forax](https://www.youtube.com/watch?v=-k2X7guaArU)
