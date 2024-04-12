---
title: Active Object
category: Concurrency
language: en
tag:
    - Performance
---

## Intent

The Active Object design pattern provides a safe and reliable way to implement asynchronous behavior in concurrent systems. It achieves this by encapsulating tasks within objects that have their own thread and message queue. This separation keeps the main thread responsive and avoids issues like direct thread manipulation or shared state access.
主动对象设计模式提供了一种安全可靠的方法来实现并发系统中的异步行为。它通过将任务封装在具有自己的线程和消息队列的对象中来实现这一点。这种分离保持了主线程的响应性，并避免了直接操作线程或共享状态访问等问题。

## Explanation

The class that implements the active object pattern will contain a self-synchronization mechanism without using 'synchronized' methods.
实现活动对象模式的类将包含一个不使用` synchronized `方法的自同步机制。

Real-world example

> The Orcs are known for their wildness and untameable soul. It seems like they have their own thread of control based on previous behavior.
> 兽人以野性和不可驯服的灵魂而闻名。根据之前的行为，他们似乎有自己的控制线索。

To implement a creature that has its own thread of control mechanism and expose its API only and not the execution itself, we can use the Active Object pattern.
要实现一个有自己的线程控制机制的生物，并且只暴露它的API而不暴露执行本身，我们可以使用活动对象模式。

**Programmatic Example**

```java
public abstract class ActiveCreature {
    private final Logger logger = LoggerFactory.getLogger(ActiveCreature.class.getName());

    private BlockingQueue<Runnable> requests;

    private String name;

    private Thread thread;

    public ActiveCreature(String name) {
        this.name = name;
        this.requests = new LinkedBlockingQueue<Runnable>();
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        requests.take().run();
                    } catch (InterruptedException e) {
                        logger.error(e.getMessage());
                    }
                }
            }
        }
        );
        thread.start();
    }

    public void eat() throws InterruptedException {
        requests.put(new Runnable() {
                         @Override
                         public void run() {
                             logger.info("{} is eating!", name());
                             logger.info("{} has finished eating!", name());
                         }
                     }
        );
    }

    public void roam() throws InterruptedException {
        requests.put(new Runnable() {
                         @Override
                         public void run() {
                             logger.info("{} has started to roam the wastelands.", name());
                         }
                     }
        );
    }

    public String name() {
        return this.name;
    }
}
```

We can see that any class that will extend the ActiveCreature class will have its own thread of control to invoke and execute methods.
我们可以看到，任何扩展ActiveCreature类的类都会有自己的控制线程来调用和执行方法。

For example, the Orc class:

```java
public class Orc extends ActiveCreature {

    public Orc(String name) {
        super(name);
    }

}
```

Now, we can create multiple creatures such as Orcs, tell them to eat and roam, and they will execute it on their own thread of control:

```java
  public static void main(String[]args){
        var app=new App();
        app.run();
        }

@Override
public void run(){
        ActiveCreature creature;
        try{
        for(int i=0;i<creatures;i++){
        creature=new Orc(Orc.class.getSimpleName().toString()+i);
        creature.eat();
        creature.roam();
        }
        Thread.sleep(1000);
        }catch(InterruptedException e){
        logger.error(e.getMessage());
        }
        Runtime.getRuntime().exit(1);
        }
```

## Class diagram

![alt text](./etc/active-object.urm.png "Active Object class diagram")

## Applicability

* When you need to perform long-running operations without blocking the main thread.
* When you need to interact with external resources asynchronously.
* When you want to improve the responsiveness of your application.
* When you need to manage concurrent tasks in a modular and maintainable way.

## Tutorials

* [Android and Java Concurrency: The Active Object Pattern](https://www.youtube.com/watch?v=Cd8t2u5Qmvc)

## Consequences

Benefits

* Improves responsiveness of the main thread.
* Encapsulates concurrency concerns within objects.
* Promotes better code organization and maintainability.
* Provides thread safety and avoids shared state access problems.

Trade-offs

* Introduces additional overhead due to message passing and thread management.
* May not be suitable for all types of concurrency problems.

## Related patterns

* Observer
* Reactor
* Producer-consumer
* Thread pool

## Credits

* [Design Patterns: Elements of Reusable Object Software](https://amzn.to/3HYqrBE)
* [Concurrent Programming in Java: Design Principles and Patterns](https://amzn.to/498SRVq)
* [Learning Concurrent Programming in Scala](https://amzn.to/3UE07nV)
* [Pattern Languages of Program Design 3](https://amzn.to/3OI1j61)
