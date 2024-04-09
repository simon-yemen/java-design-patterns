---
title: Abstract Document
category: Structural
language: en
tag:
    - Abstraction
    - Extensibility
    - Decoupling
---

## Intent

The Abstract Document design pattern is a structural design pattern that aims to provide a consistent way to handle hierarchical and tree-like data structures by defining a common interface for various document types. It separates the core document structure from specific data formats, enabling dynamic updates and simplified maintenance.
抽象文档设计模式是一种结构化的设计模式，通过为各种文档类型定义通用接口，旨在提供一种一致的方式来处理分层和树形数据结构。它将核心文档结构从特定的数据格式中分离出来，支持动态更新和简化维护。
## Explanation

The Abstract Document pattern enables handling additional, non-static properties. This pattern uses concept of traits to enable type safety and separate properties of different classes into set of interfaces.
抽象文档模式支持处理额外的非静态属性。这种模式使用特征的概念来实现类型安全，并将不同类的属性分离到一组接口中。

Real world example

> Consider a car that consists of multiple parts. However, we don't know if the specific car really has all the parts, or just some of them. Our cars are dynamic and extremely flexible.
> 考虑一辆由多个部件组成的汽车。然而，我们不知道特定的汽车是否真的拥有所有的部件，还是只有其中的一部分。我们的汽车是动态的，非常灵活。
In plain words

> Abstract Document pattern allows attaching properties to objects without them knowing about it.
> 抽象文档模式允许在对象不知情的情况下为其添加属性。

Wikipedia says

> An object-oriented structural design pattern for organizing objects in loosely typed key-value stores and exposing the data using typed views. The purpose of the pattern is to achieve a high degree of flexibility between components in a strongly typed language where new properties can be added to the object-tree on the fly, without losing the support of type-safety. The pattern makes use of traits to separate different properties of a class into different interfaces.
> 抽象文档模式允许在对象不知道的情况下将属性附加到对象上，itAn面向对象的结构化设计模式将对象组织在松散类型的键值存储中，并使用类型化的视图公开数据。该模式的目的是在强类型语言中实现组件之间的高度灵活性，在这种语言中，新属性可以动态地添加到对象树中，而不会失去对类型安全的支持。该模式利用traits将类的不同属性分离到不同的接口中。

**Programmatic Example**

Let's first define the base classes `Document` and `AbstractDocument`. They basically make the object hold a property map and any amount of child objects.

```java
public interface Document {

    Void put(String key, Object value);

    Object get(String key);

    <T> Stream<T> children(String key, Function<Map<String, Object>, T> constructor);
}

public abstract class AbstractDocument implements Document {

    private final Map<String, Object> properties;

    protected AbstractDocument(Map<String, Object> properties) {
        Objects.requireNonNull(properties, "properties map is required");
        this.properties = properties;
    }

    @Override
    public Void put(String key, Object value) {
        properties.put(key, value);
        return null;
    }

    @Override
    public Object get(String key) {
        return properties.get(key);
    }

    @Override
    public <T> Stream<T> children(String key, Function<Map<String, Object>, T> constructor) {
        return Stream.ofNullable(get(key))
                .filter(Objects::nonNull)
                .map(el -> (List<Map<String, Object>>) el)
                .findAny()
                .stream()
                .flatMap(Collection::stream)
                .map(constructor);
    }
  ...
}
```

Next we define an enum `Property` and a set of interfaces for type, price, model and parts. This allows us to create static looking interface to our `Car` class.

```java
public enum Property {

    PARTS, TYPE, PRICE, MODEL
}

public interface HasType extends Document {

    default Optional<String> getType() {
        return Optional.ofNullable((String) get(Property.TYPE.toString()));
    }
}

public interface HasPrice extends Document {

    default Optional<Number> getPrice() {
        return Optional.ofNullable((Number) get(Property.PRICE.toString()));
    }
}

public interface HasModel extends Document {

    default Optional<String> getModel() {
        return Optional.ofNullable((String) get(Property.MODEL.toString()));
    }
}

public interface HasParts extends Document {

    default Stream<Part> getParts() {
        return children(Property.PARTS.toString(), Part::new);
    }
}
```

Now we are ready to introduce the `Car`.

```java
public class Car extends AbstractDocument implements HasModel, HasPrice, HasParts {

    public Car(Map<String, Object> properties) {
        super(properties);
    }
}
```

And finally here's how we construct and use the `Car` in a full example.

```java
    LOGGER.info("Constructing parts and car");

        var wheelProperties=Map.of(
        Property.TYPE.toString(),"wheel",
        Property.MODEL.toString(),"15C",
        Property.PRICE.toString(),100L);

        var doorProperties=Map.of(
        Property.TYPE.toString(),"door",
        Property.MODEL.toString(),"Lambo",
        Property.PRICE.toString(),300L);

        var carProperties=Map.of(
        Property.MODEL.toString(),"300SL",
        Property.PRICE.toString(),10000L,
        Property.PARTS.toString(),List.of(wheelProperties,doorProperties));

        var car=new Car(carProperties);

        LOGGER.info("Here is our car:");
        LOGGER.info("-> model: {}",car.getModel().orElseThrow());
        LOGGER.info("-> price: {}",car.getPrice().orElseThrow());
        LOGGER.info("-> parts: ");
        car.getParts().forEach(p->LOGGER.info("\t{}/{}/{}",
        p.getType().orElse(null),
        p.getModel().orElse(null),
        p.getPrice().orElse(null))
        );

// Constructing parts and car
// Here is our car:
// model: 300SL
// price: 10000
// parts: 
// wheel/15C/100
// door/Lambo/300
```

## Class diagram

![alt text](./etc/abstract-document.png "Abstract Document Traits and Domain")

## Applicability

This pattern is particularly useful in scenarios where you have different types of documents that share some common attributes or behaviors, but also have unique attributes or behaviors specific to their individual types. Here are some scenarios where the Abstract Document design pattern can be applicable:
这种模式特别适用于这样的场景:不同类型的文档共享一些公共属性或行为，但也有特定于它们各自类型的独特属性或行为。下面是可以应用抽象文档设计模式的一些场景。

* Content Management Systems (CMS): In a CMS, you might have various types of content such as articles, images, videos, etc. Each type of content could have shared attributes like creation date, author, and tags, while also having specific attributes like image dimensions for images or video duration for videos.
  内容管理系统(CMS):在CMS中，您可能有各种类型的内容，如文章、图像、视频等。每种类型的内容可以具有共享属性，如创建日期、作者和标签，同时也具有特定属性，如图像的图像尺寸或视频的时长。

* File Systems: If you're designing a file system where different types of files need to be managed, such as documents, images, audio files, and directories, the Abstract Document pattern can help provide a consistent way to access attributes like file size, creation date, etc., while allowing for specific attributes like image resolution or audio duration.
  文件系统:如果你正在设计一个文件系统，其中需要管理不同类型的文件，如文档、图像、音频文件和目录，抽象文档模式可以帮助提供一种一致的方式来访问文件大小、创建日期等属性，同时允许图像分辨率或音频时长等特定属性。

* E-commerce Systems: An e-commerce platform might have different product types such as physical products, digital downloads, and subscriptions. Each type could share common attributes like name, price, and description, while having unique attributes like shipping weight for physical products or download link for digital products.
  电子商务系统:电子商务平台可能有不同的产品类型，如实体产品、数字下载和订阅。每种类型都可以共享共同的属性，如名称、价格和描述，同时具有独特的属性，如实体产品的运输重量或数字产品的下载链接。

* Medical Records Systems: In healthcare, patient records might include various types of data such as demographics, medical history, test results, and prescriptions. The Abstract Document pattern can help manage shared attributes like patient ID and date of birth, while accommodating specialized attributes like test results or prescribed medications.
  医疗记录系统:在医疗保健中，患者记录可能包括各种类型的数据，如人口统计数据、病史、测试结果和处方。抽象文档模式可以帮助管理共享属性，如患者ID和出生日期，同时容纳专门属性，如测试结果或处方药物。

* Configuration Management: When dealing with configuration settings for software applications, there can be different types of configuration elements, each with its own set of attributes. The Abstract Document pattern can be used to manage these configuration elements while ensuring a consistent way to access and manipulate their attributes.
  配置管理:当处理软件应用程序的配置设置时，可以有不同类型的配置元素，每个都有自己的一组属性。抽象文档模式可以用来管理这些配置元素，同时确保以一致的方式访问和操作它们的属性。

* Educational Platforms: Educational systems might have various types of learning materials such as text-based content, videos, quizzes, and assignments. Common attributes like title, author, and publication date can be shared, while unique attributes like video duration or assignment due dates can be specific to each type.
  教育平台:教育系统可能有各种类型的学习材料，如文本内容、视频、小测验和作业。公共属性(如标题、作者和出版日期)可以共享，而独特属性(如视频时长或作业交付日期)可以特定于每种类型。

* Project Management Tools: In project management applications, you could have different types of tasks like to-do items, milestones, and issues. The Abstract Document pattern could be used to handle general attributes like task name and assignee, while allowing for specific attributes like milestone date or issue priority.
  项目管理工具:在项目管理应用程序中，你可以有不同类型的任务，如待办事项、里程碑和问题。抽象文档模式可用于处理一般属性，如任务名称和受让人，同时允许特定属性，如里程碑日期或问题优先级。

* Documents have diverse and evolving attribute structures.
  文档具有多样且不断变化的属性结构。

* Dynamically adding new properties is a common requirement.
  动态添加新属性是一个常见的需求。

* Decoupling data access from specific formats is crucial.
  将数据访问与特定格式解耦至关重要。

* Maintainability and flexibility are critical for the codebase.
  可维护性和灵活性对代码库至关重要。

The key idea behind the Abstract Document design pattern is to provide a flexible and extensible way to manage different types of documents or entities with shared and distinct attributes. By defining a common interface and implementing it across various document types, you can achieve a more organized and consistent approach to handling complex data structures.
可维护性和灵活性是代码库的关键。抽象文档设计模式背后的关键思想是提供一种灵活和可扩展的方式来管理具有共享和不同属性的不同类型的文档或实体。通过定义一个公共接口并跨各种文档类型实现它，您可以实现一种更有组织和一致的方法来处理复杂的数据结构。

## Consequences

Benefits

* Flexibility: Accommodates varied document structures and properties.

* Extensibility: Dynamically add new attributes without breaking existing code.

* Maintainability: Promotes clean and adaptable code due to separation of concerns.

* Reusability: Typed views enable code reuse for accessing specific attribute types.

Trade-offs

* Complexity: Requires defining interfaces and views, adding implementation overhead.

* Performance: Might introduce slight performance overhead compared to direct data access.

## Credits

* [Wikipedia: Abstract Document Pattern](https://en.wikipedia.org/wiki/Abstract_Document_Pattern)
* [Martin Fowler: Dealing with properties](http://martinfowler.com/apsupp/properties.pdf)
* [Pattern-Oriented Software Architecture Volume 4: A Pattern Language for Distributed Computing (v. 4)](https://amzn.to/49zRP4R)
