---
title: Strategy
category: Behavioral
language: en
tag:
 - Gang of Four
---

## Also known as

Policy

## Intent

Define a family of algorithms, encapsulate each one, and make them interchangeable. Strategy lets 
the algorithm vary independently of the clients that use it.
定义一系列算法，封装每个算法，并使它们可以互换。策略让算法独立于使用它的客户端而变化。

## Explanation

Real-world example

> Slaying dragons is a dangerous job. With experience, it becomes easier. Veteran 
> dragonslayers have developed different fighting strategies against different types of dragons.         
> 屠龙是一项危险的工作。有了经验，就会变得更容易。经验丰富的屠龙者针对不同类型的龙制定了不同的战斗策略。

In plain words

> Strategy pattern allows choosing the best-suited algorithm at runtime.   

Wikipedia says

> In computer programming, the strategy pattern (also known as the policy pattern) is a behavioral 
> software design pattern that enables selecting an algorithm at runtime.
> 在计算机编程中，策略模式(也称为策略模式)是一种行为软件设计模式，允许在运行时选择算法。

**Programmatic Example**

Let's first introduce the dragon-slaying strategy interface and its implementations.

```java
@FunctionalInterface
public interface DragonSlayingStrategy {

  void execute();
}

@Slf4j
public class MeleeStrategy implements DragonSlayingStrategy {

  @Override
  public void execute() {
    LOGGER.info("With your Excalibur you sever the dragon's head!");
  }
}

@Slf4j
public class ProjectileStrategy implements DragonSlayingStrategy {

  @Override
  public void execute() {
    LOGGER.info("You shoot the dragon with the magical crossbow and it falls dead on the ground!");
  }
}

@Slf4j
public class SpellStrategy implements DragonSlayingStrategy {

  @Override
  public void execute() {
    LOGGER.info("You cast the spell of disintegration and the dragon vaporizes in a pile of dust!");
  }
}
```

And here is the mighty dragonslayer, who can pick his fighting strategy based on the 
opponent.
这是强大的屠龙者，他可以根据对手选择自己的战斗策略。

```java
public class DragonSlayer {

  private DragonSlayingStrategy strategy;

  public DragonSlayer(DragonSlayingStrategy strategy) {
    this.strategy = strategy;
  }

  public void changeStrategy(DragonSlayingStrategy strategy) {
    this.strategy = strategy;
  }

  public void goToBattle() {
    strategy.execute();
  }
}
```

Finally, here's the dragonslayer in action.

```java
    LOGGER.info("Green dragon spotted ahead!");
    var dragonSlayer = new DragonSlayer(new MeleeStrategy());
    dragonSlayer.goToBattle();
    LOGGER.info("Red dragon emerges.");
    dragonSlayer.changeStrategy(new ProjectileStrategy());
    dragonSlayer.goToBattle();
    LOGGER.info("Black dragon lands before you.");
    dragonSlayer.changeStrategy(new SpellStrategy());
    dragonSlayer.goToBattle();
```

Program output:

```
    Green dragon spotted ahead!
    With your Excalibur you sever the dragon's head!
    Red dragon emerges.
    You shoot the dragon with the magical crossbow and it falls dead on the ground!
    Black dragon lands before you.
    You cast the spell of disintegration and the dragon vaporizes in a pile of dust!    
```

What's more, the lambda expressions in Java 8 provides another approach for the implementation:
此外，Java 8中的lambda表达式还提供了另一种实现方式:

```java
public class LambdaStrategy {

  private static final Logger LOGGER = LoggerFactory.getLogger(LambdaStrategy.class);

  public enum Strategy implements DragonSlayingStrategy {
    MeleeStrategy(() -> LOGGER.info(
        "With your Excalibur you severe the dragon's head!")),
    ProjectileStrategy(() -> LOGGER.info(
        "You shoot the dragon with the magical crossbow and it falls dead on the ground!")),
    SpellStrategy(() -> LOGGER.info(
        "You cast the spell of disintegration and the dragon vaporizes in a pile of dust!"));

    private final DragonSlayingStrategy dragonSlayingStrategy;

    Strategy(DragonSlayingStrategy dragonSlayingStrategy) {
      this.dragonSlayingStrategy = dragonSlayingStrategy;
    }

    @Override
    public void execute() {
      dragonSlayingStrategy.execute();
    }
  }
}
```

And here's the dragonslayer in action.

```java
    LOGGER.info("Green dragon spotted ahead!");
    dragonSlayer.changeStrategy(LambdaStrategy.Strategy.MeleeStrategy);
    dragonSlayer.goToBattle();
    LOGGER.info("Red dragon emerges.");
    dragonSlayer.changeStrategy(LambdaStrategy.Strategy.ProjectileStrategy);
    dragonSlayer.goToBattle();
    LOGGER.info("Black dragon lands before you.");
    dragonSlayer.changeStrategy(LambdaStrategy.Strategy.SpellStrategy);
    dragonSlayer.goToBattle();
```

The program output is the same as the above one.

## Class diagram

![alt text](./etc/strategy_urm.png "Strategy")

## Applicability

Use the Strategy pattern when

* Many related classes differ only in their behavior. Strategies provide a way to configure a class either one of many behaviors
许多相关的类只在行为上有所不同。策略提供了一种从多种行为中配置类的方法
* You need different variants of an algorithm. for example, you might define algorithms reflecting different space/time trade-offs. Strategies can be used when these variants are implemented as a class hierarchy of algorithms
  你需要一个算法的不同变体。例如，您可以定义反映不同空间/时间权衡的算法。当这些变体被实现为算法的类层次结构时，可以使用策略
* An algorithm uses data that clients shouldn't know about. Use the Strategy pattern to avoid exposing complex algorithm-specific data structures
  算法使用了客户端不应该知道的数据。使用策略模式可以避免暴露复杂的特定于算法的数据结构
* A class defines many behaviors, and these appear as multiple conditional statements in its operations. Instead of many conditionals, move the related conditional branches into their own Strategy class
  类定义了很多行为，这些行为在操作中表现为多个条件语句。将相关的条件分支移到它们自己的策略类中，而不是使用很多条件分支

## Tutorial 

* [Strategy Pattern Tutorial](https://www.journaldev.com/1754/strategy-design-pattern-in-java-example-tutorial)

## Credits

* [Design Patterns: Elements of Reusable Object-Oriented Software](https://www.amazon.com/gp/product/0201633612/ref=as_li_tl?ie=UTF8&camp=1789&creative=9325&creativeASIN=0201633612&linkCode=as2&tag=javadesignpat-20&linkId=675d49790ce11db99d90bde47f1aeb59)
* [Functional Programming in Java: Harnessing the Power of Java 8 Lambda Expressions](https://www.amazon.com/gp/product/1937785467/ref=as_li_tl?ie=UTF8&camp=1789&creative=9325&creativeASIN=1937785467&linkCode=as2&tag=javadesignpat-20&linkId=7e4e2fb7a141631491534255252fd08b)
* [Head First Design Patterns: A Brain-Friendly Guide](https://www.amazon.com/gp/product/0596007124/ref=as_li_tl?ie=UTF8&camp=1789&creative=9325&creativeASIN=0596007124&linkCode=as2&tag=javadesignpat-20&linkId=6b8b6eea86021af6c8e3cd3fc382cb5b)
* [Refactoring to Patterns](https://www.amazon.com/gp/product/0321213351/ref=as_li_tl?ie=UTF8&camp=1789&creative=9325&creativeASIN=0321213351&linkCode=as2&tag=javadesignpat-20&linkId=2a76fcb387234bc71b1c61150b3cc3a7)
