---
title: Builder
category: Creational
language: en
tag:
    - Gang of Four
---

## Intent 目的

Separate the construction of a complex object from its representation so that the same construction process can create different representations.

将复杂对象的构造与其表示分离，以便相同的构造过程可以创建不同的表示。

## Explanation 解释

Real-world example 现实世界的例子

> Imagine a character generator for a role-playing game. The easiest option is to let the computer create the character for you. If you want to manually select the character details like profession, gender, hair color, etc. the character generation becomes a step-by-step process that completes when all the selections are ready.

> 想象一下角色扮演游戏的角色生成器。最简单的选择是让电脑为你创建角色。如果你想手动选择角色细节，如职业，性别，头发颜色等，角色生成成为一个循序渐进的过程，当所有的选择都准备好了。

In plain words 简单的说

> Allows you to create different flavors of an object while avoiding constructor pollution. Useful when there could be several flavors of an object. Or when there are a lot of steps involved in creation of an object.

> 允许您创建不同风格的对象，同时避免构造函数污染。当一个对象可能有几种口味时非常有用。或者当有很多步骤涉及到创建一个对象。

Wikipedia says

> The builder pattern is an object creation software design pattern with the intentions of finding a solution to the telescoping constructor antipattern.

> 构建器模式是一种对象创建软件设计模式，其目的是找到伸缩构造器反模式的解决方案。

Having said that let me add a bit about what telescoping constructor antipattern is. At one point or the other, we have all seen a constructor like below:

```java
public Hero(Profession profession,String name,HairType hairType,HairColor hairColor,Armor armor,Weapon weapon){
        }
```

As you can see the number of constructor parameters can quickly get out of hand, and it may become difficult to understand the arrangement of parameters. Plus this parameter list could keep on growing if you would want to add more options in the future. This is called telescoping constructor antipattern.

正如您所看到的，构造函数参数的数量很快就会失控，而且很难理解参数的排列。此外，如果您将来想要添加更多选项，则此参数列表可以继续增长。这被称为伸缩构造函数反模式。

**Programmatic Example**

The sane alternative is to use the Builder pattern. First of all, we have our hero that we want to create:

同样的替代方法是使用Builder模式。首先，我们有了想要创建的英雄:

```java
public final class Hero {
    private final Profession profession;
    private final String name;
    private final HairType hairType;
    private final HairColor hairColor;
    private final Armor armor; // 装甲
    private final Weapon weapon;

    private Hero(Builder builder) {
        this.profession = builder.profession;
        this.name = builder.name;
        this.hairColor = builder.hairColor;
        this.hairType = builder.hairType;
        this.weapon = builder.weapon;
        this.armor = builder.armor;
    }
}
```

Then we have the builder:

```java
  public static class Builder {
    private final Profession profession;
    private final String name;
    private HairType hairType;
    private HairColor hairColor;
    private Armor armor;
    private Weapon weapon;

    public Builder(Profession profession, String name) {
        if (profession == null || name == null) {
            throw new IllegalArgumentException("profession and name can not be null");
        }
        this.profession = profession;
        this.name = name;
    }

    public Builder withHairType(HairType hairType) {
        this.hairType = hairType;
        return this;
    }

    public Builder withHairColor(HairColor hairColor) {
        this.hairColor = hairColor;
        return this;
    }

    public Builder withArmor(Armor armor) {
        this.armor = armor;
        return this;
    }

    public Builder withWeapon(Weapon weapon) {
        this.weapon = weapon;
        return this;
    }

    public Hero build() {
        return new Hero(this);
    }
}
```

Then it can be used as:

```java
var mage=new Hero.Builder(Profession.MAGE,"Riobard").withHairColor(HairColor.BLACK).withWeapon(Weapon.DAGGER).build();
```

## Class diagram

![alt text](./etc/builder.urm.png "Builder class diagram")

## Applicability 适用性

Use the Builder pattern when

* The algorithm for creating a complex object should be independent of the parts that make up the object and how they're assembled
* The construction process must allow different representations for the object that's constructed
* It's particularly useful when a product requires a lot of steps to be created and when these steps need to be executed in a specific sequence

## Known Uses

* Java.lang.StringBuilder
* Java.nio.ByteBuffer as well as similar buffers such as FloatBuffer, IntBuffer, and others
* javax.swing.GroupLayout.Group#addComponent()

## Consequences

Benefits:

* More control over the construction process compared to other creational patterns
* Supports constructing objects step-by-step, defer construction steps or run steps recursively
* Can construct objects that require a complex assembly of sub-objects. The final product is detached from the parts that make it up, as well as their assembly process
* Single Responsibility Principle. You can isolate complex construction code from the business logic of the product

Trade-offs:

* The overall complexity of the code can increase since the pattern requires creating multiple new classes

## Tutorials

* [Refactoring Guru](https://refactoring.guru/design-patterns/builder)
* [Oracle Blog](https://blogs.oracle.com/javamagazine/post/exploring-joshua-blochs-builder-design-pattern-in-java)
* [Journal Dev](https://www.journaldev.com/1425/builder-design-pattern-in-java)

## Known uses

* [java.lang.StringBuilder](http://docs.oracle.com/javase/8/docs/api/java/lang/StringBuilder.html)
* [java.nio.ByteBuffer](http://docs.oracle.com/javase/8/docs/api/java/nio/ByteBuffer.html#put-byte-) as well as similar buffers such as FloatBuffer, IntBuffer and so on.
* [java.lang.StringBuffer](http://docs.oracle.com/javase/8/docs/api/java/lang/StringBuffer.html#append-boolean-)
* All implementations of [java.lang.Appendable](http://docs.oracle.com/javase/8/docs/api/java/lang/Appendable.html)
* [Apache Camel builders](https://github.com/apache/camel/tree/0e195428ee04531be27a0b659005e3aa8d159d23/camel-core/src/main/java/org/apache/camel/builder)
* [Apache Commons Option.Builder](https://commons.apache.org/proper/commons-cli/apidocs/org/apache/commons/cli/Option.Builder.html)

## Related patterns

* [Step Builder](https://java-design-patterns.com/patterns/step-builder/) is a variation of the Builder pattern that generates a complex object using a step-by-step approach. The Step Builder pattern is a good choice when you need to build an object with a large number of optional parameters, and you want to avoid the telescoping constructor antipattern.

## Credits

* [Design Patterns: Elements of Reusable Object-Oriented Software](https://www.amazon.com/gp/product/0201633612/ref=as_li_tl?ie=UTF8&camp=1789&creative=9325&creativeASIN=0201633612&linkCode=as2&tag=javadesignpat-20&linkId=675d49790ce11db99d90bde47f1aeb59)
* [Effective Java](https://www.amazon.com/gp/product/0134685997/ref=as_li_tl?ie=UTF8&camp=1789&creative=9325&creativeASIN=0134685997&linkCode=as2&tag=javadesignpat-20&linkId=4e349f4b3ff8c50123f8147c828e53eb)
* [Head First Design Patterns: A Brain-Friendly Guide](https://www.amazon.com/gp/product/0596007124/ref=as_li_tl?ie=UTF8&camp=1789&creative=9325&creativeASIN=0596007124&linkCode=as2&tag=javadesignpat-20&linkId=6b8b6eea86021af6c8e3cd3fc382cb5b)
* [Refactoring to Patterns](https://www.amazon.com/gp/product/0321213351/ref=as_li_tl?ie=UTF8&camp=1789&creative=9325&creativeASIN=0321213351&linkCode=as2&tag=javadesignpat-20&linkId=2a76fcb387234bc71b1c61150b3cc3a7)
