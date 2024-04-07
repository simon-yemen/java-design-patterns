---
title: Filterer
language: en
category: Functional
tag:
 - Extensibility
---

## Name / classification

Filterer

## Intent

The intent of this design pattern is to introduce a functional interface that will add a 
functionality for container-like objects to easily return filtered versions of themselves.

此设计模式的目的是引入一个功能接口，该接口将为类似容器的对象添加一个功能，以便轻松返回它们自己的过滤版本。

Real world example

> We are designing a threat (malware) detection software which can analyze target systems for 
> threats that are present in it. In the design we have to take into consideration that new 
> Threat types can be added later. Additionally, there is a requirement that the threat detection 
> system can filter the detected threats based on different criteria (the target system acts as 
> container-like object for threats).

> 我们正在设计一种威胁(恶意软件)检测软件，它可以分析目标系统中存在的威胁。在设计中，我们必须考虑到新的威胁类型可以在以后添加。此外，还需要威胁检测系统能够根据不同的标准过滤检测到的威胁(目标系统充当类似容器的威胁对象)

In plain words

> Filterer pattern is a design pattern that helps container-like objects return filtered versions 
> of themselves. 
> 筛选器模式是一种设计模式，它帮助类容器对象返回自己的筛选版本。

**Programmatic Example**

To model the threat detection example presented above we introduce `Threat` and `ThreatAwareSystem` 
interfaces.

为了对上面给出的威胁检测示例进行建模，我们引入了threat和ThreatAwareSystem接口。

```java
public interface Threat {
  String name();
  int id();
  ThreatType type();
}

public interface ThreatAwareSystem {
  String systemId();
  List<? extends Threat> threats();
  Filterer<? extends ThreatAwareSystem, ? extends Threat> filtered();

}
```

Notice the `filtered` method that returns instance of `Filterer` interface which is defined as:

```java
@FunctionalInterface
public interface Filterer<G, E> {
  G by(Predicate<? super E> predicate);
}
```

It is used to fulfill the requirement for system to be able to filter itself based on threat 
properties. The container-like object (`ThreatAwareSystem` in our case) needs to have a method that 
returns an instance of `Filterer`. This helper interface gives ability to covariantly specify a 
lower bound of contravariant `Predicate` in the subinterfaces of interfaces representing the 
container-like objects.

它用于满足系统能够根据威胁属性对自身进行过滤的要求。容器类对象(在我们的例子中是ThreatAwareSystem)需要有一个返回filter实例的方法。这个助手接口提供了在表示容器类对象的接口的子接口中协变地指定逆变谓词的下界的能力。

In our example we will be able to pass a predicate that takes `? extends Threat` object and 
return `?  extends ThreatAwareSystem` from `Filtered::by` method. A simple implementation 
of `ThreatAwareSystem`:

在我们的示例中，我们将能够传递一个带?扩展威胁对象并返回?从Filtered::by方法扩展了ThreatAwareSystem。一个简单的ThreatAwareSystem实现:

```java
public class SimpleThreatAwareSystem implements ThreatAwareSystem {

  private final String systemId;
  private final ImmutableList<Threat> issues;

  public SimpleThreatAwareSystem(final String systemId, final List<Threat> issues) {
    this.systemId = systemId;
    this.issues = ImmutableList.copyOf(issues);
  }
  
  @Override
  public String systemId() {
    return systemId;
  }
  
  @Override
  public List<? extends Threat> threats() {
    return new ArrayList<>(issues);
  }

  @Override
  public Filterer<? extends ThreatAwareSystem, ? extends Threat> filtered() {
    return this::filteredGroup;
  }

  private ThreatAwareSystem filteredGroup(Predicate<? super Threat> predicate) {
    return new SimpleThreatAwareSystem(this.systemId, filteredItems(predicate));
  }

  private List<Threat> filteredItems(Predicate<? super Threat> predicate) {
    return this.issues.stream()
            .filter(predicate)
            .collect(Collectors.toList());
  }
}
```

The `filtered` method is overridden to filter the threats list by given predicate.

Now if we introduce a new subtype of `Threat` interface that adds probability with which given 
threat can appear:

`filtered`方法被重写为根据给定的条件过滤威胁列表。

现在，如果我们引入一个新的`Threat`接口子类型，它将增加给定的概率
威胁可能出现:

```java
public interface ProbableThreat extends Threat {
  double probability();
}
```

We can also introduce a new interface that represents a system that is aware of threats with their 
probabilities:
我们还可以引入一个新的接口，表示系统可以通过概率感知威胁:

````java
public interface ProbabilisticThreatAwareSystem extends ThreatAwareSystem {
  @Override
  List<? extends ProbableThreat> threats();

  @Override
  Filterer<? extends ProbabilisticThreatAwareSystem, ? extends ProbableThreat> filtered();
}
````

Notice how we override the `filtered` method in `ProbabilisticThreatAwareSystem` and specify 
different return covariant type by specifying different generic types. Our interfaces are clean and 
not cluttered by default implementations. We we will be able to filter 
`ProbabilisticThreatAwareSystem` by `ProbableThreat` properties:
请注意我们如何覆盖ProbabilisticThreatAwareSystem中的filtered方法，并通过指定不同的泛型类型来指定不同的返回协变类型。我们的接口是干净的，默认实现没有混乱。我们将能够通过ProbableThreat属性过滤ProbabilisticThreatAwareSystem:

```java
public class SimpleProbabilisticThreatAwareSystem implements ProbabilisticThreatAwareSystem {

  private final String systemId;
  private final ImmutableList<ProbableThreat> threats;

  public SimpleProbabilisticThreatAwareSystem(final String systemId, final List<ProbableThreat> threats) {
    this.systemId = systemId;
    this.threats = ImmutableList.copyOf(threats);
  }

  @Override
  public String systemId() {
    return systemId;
  }

  @Override
  public List<? extends ProbableThreat> threats() {
    return threats;
  }

  @Override
  public Filterer<? extends ProbabilisticThreatAwareSystem, ? extends ProbableThreat> filtered() {
    return this::filteredGroup;
  }

  private ProbabilisticThreatAwareSystem filteredGroup(final Predicate<? super ProbableThreat> predicate) {
    return new SimpleProbabilisticThreatAwareSystem(this.systemId, filteredItems(predicate));
  }

  private List<ProbableThreat> filteredItems(final Predicate<? super ProbableThreat> predicate) {
    return this.threats.stream()
            .filter(predicate)
            .collect(Collectors.toList());
  }
}
```

Now if we want filter `ThreatAwareSystem` by threat type we can do:
现在如果我们想通过威胁类型来过滤`ThreatAwareSystem`，我们可以这样做:

```java
Threat rootkit = new SimpleThreat(ThreatType.ROOTKIT, 1, "Simple-Rootkit");
Threat trojan = new SimpleThreat(ThreatType.TROJAN, 2, "Simple-Trojan");
List<Threat> threats = List.of(rootkit, trojan);

ThreatAwareSystem threatAwareSystem = new SimpleThreatAwareSystem("System-1", threats);

ThreatAwareSystem rootkitThreatAwareSystem = threatAwareSystem.filtered()
           .by(threat -> threat.type() == ThreatType.ROOTKIT);
```

Or if we want to filter `ProbabilisticThreatAwareSystem`:

```java
ProbableThreat malwareTroyan = new SimpleProbableThreat("Troyan-ArcBomb", 1, ThreatType.TROJAN, 0.99);
ProbableThreat rootkit = new SimpleProbableThreat("Rootkit-System", 2, ThreatType.ROOTKIT, 0.8);
List<ProbableThreat> probableThreats = List.of(malwareTroyan, rootkit);

ProbabilisticThreatAwareSystem simpleProbabilisticThreatAwareSystem =new SimpleProbabilisticThreatAwareSystem("System-1", probableThreats);

ProbabilisticThreatAwareSystem filtered = simpleProbabilisticThreatAwareSystem.filtered()
           .by(probableThreat -> Double.compare(probableThreat.probability(), 0.99) == 0);
```

## Class diagram

![Filterer](./etc/filterer.png "Filterer")

## Applicability

Pattern can be used when working with container-like objects that use subtyping, instead of 
parametrizing (generics) for extensible class structure. It enables you to easily extend filtering 
ability of container-like objects as business requirements change.
当处理使用子类型的容器类对象时，可以使用模式，而不是参数化(泛型)以实现可扩展的类结构。当业务需求发生变化时，它使您能够轻松扩展类容器对象的过滤能力。

## 泛型的上界和下界通配符
在Java泛型中，上界通配符`<? extends T>`和下界通配符`<? super T>`是用来限制泛型类型参数范围的特殊符号。具体来说：

- **上界通配符（Upper Bounds Wildcards）**：使用`extends`关键字，它指定了泛型类型参数必须是指定类型T或其子类。这意味着你可以从这样的集合中读取类型为T的对象，但不能写入任何除了null以外的元素，因为编译器无法确保你要写入的元素确实是允许的类型。例如，如果你有一个`List<? extends Number>`，你可以从中读取Number类型的对象，但你不能添加任何Number的子类型到这个列表中。
- **下界通配符（Lower Bounds Wildcards）**：使用`super`关键字，它指定了泛型类型参数必须是指定类型T或其父类。这意味着你可以向这样的集合中写入类型为T或其子类型的对象，但是不能从中读取元素，除非你将其转型为一个特定的类型。例如，如果你有一个`List<? super Integer>`，你可以向其中添加Integer或其子类型的对象，但是你不能直接从列表中读取元素，除非你将其转换为Integer或其他适当的类型。

总的来说，这两种通配符都是用来提供更灵活的泛型编程方式，它们允许你编写更加通用的代码，同时保持类型安全。在选择使用上界通配符还是下界通配符时，你应该根据实际的需求来决定。如果你需要从集合中读取数据，通常使用上界通配符；如果你需要向集合中写入数据，通常使用下界通配符。

## PECS原则
为了更好地理解为什么生产者使用 `extends` 而消费者使用 `super`，我们首先需要明确两个术语的定义：

- **生产者（Producer）**：指的是返回一个泛型类型实例的方法或代码块。例如，一个方法返回 `List<T>` 类型的对象时，它就是一个生产者，因为它“生产”了列表中的元素供其他代码使用。
- **消费者（Consumer）**：指的是接受一个泛型类型实例作为参数的方法或代码块。例如，一个方法接受 `List<T>` 类型的对象作为参数时，它就是一个消费者，因为它“消费”了传递给它的列表。

现在，让我们探讨为何在这两种场景下要分别使用 `extends` 和 `super`：

### 生产者使用 `extends`

当你有一个生产者方法，即一个返回泛型类型的方法，你通常希望这个方法尽可能通用，能够被更多的调用者使用。使用 `? extends T`（上界通配符）允许你返回 `T` 类型或 `T` 的任何子类型的集合。这样，方法的调用者可以安全地取出集合中的元素，因为无论集合中的具体元素是什么类型，它们都是 `T` 类型或其子类型。

```java
public List<? extends Number> getNumbers() {
    // 此方法可以返回List<Integer>, List<Double>, List<Number>等
}
```

在上面的例子中，`getNumbers` 方法是一个生产者方法，它可能返回 `Number` 类型或其任何子类型的列表。这允许该方法非常灵活，因为它可以用于多种不同的上下文。

### 消费者使用 `super`

当你有一个消费者方法，即一个接受泛型类型参数的方法，你通常希望这个方法能够处理 `T` 类型及其任何父类型的对象。使用 `? super T`（下界通配符）允许你向集合中添加 `T` 类型或其父类型的对象。这样，你可以确保你的代码可以处理更广泛的输入类型。

```java
public void processElements(List<? super Integer> elements) {
    // 此方法可以接受List<Integer>, List<Object>等
    elements.add(new Integer(1)); // 这里我们可以添加Integer类型的对象到列表中
}
```

在上面的例子中，`processElements` 方法是一个消费者方法，它可以接收 `Integer` 类型或其父类型的列表。这使得该方法能够处理更多类型的列表，并且能够向这些列表中添加 `Integer` 类型的对象。

### 总结

使用 `extends` 对于生产者来说，提供了一种方式来生成能够被广泛使用的泛型类型；而使用 `super` 对于消费者来说，提供了一种方式来接受并操作更广泛的泛型类型。这种设计符合 PECS（Producer-Extends, Consumer-Super）原则，有助于编写既灵活又类型安全的代码。

## Tutorials

* [Article about Filterer pattern posted on it's author's blog](https://blog.tlinkowski.pl/2018/filterer-pattern/)
* [Application of Filterer pattern in domain of text analysis](https://www.javacodegeeks.com/2019/02/filterer-pattern-10-steps.html)

## Known uses

One of the uses is present on the blog presented in 
[this](https://www.javacodegeeks.com/2019/02/filterer-pattern-10-steps.html) link. It presents how 
to use `Filterer` pattern to create text issue analyzer with support for test cases used for unit 
testing.

## Consequences

Pros:
 * You can easily introduce new subtypes for container-like objects and subtypes for objects that are contained within them and still be able to filter easily be new properties of those new subtypes.

Cons:
 * Covariant return types mixed with generics can be sometimes tricky

## Credits

* Author of the pattern : [Tomasz Linkowski](https://tlinkowski.pl/)
