/*
 * This project is licensed under the MIT license. Module model-view-viewmodel is using ZK framework licensed under LGPL (see lgpl-3.0.txt).
 *
 * The MIT License
 * Copyright © 2014-2022 Ilkka Seppälä
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.iluwatar.activeobject;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ActiveCreature class is the base of the active object example.
 * 主动对象设计模式
 *
 * @author Noam Greenshtain
 * Creature 生物
 */
public abstract class ActiveCreature {

  private static final Logger logger = LoggerFactory.getLogger(ActiveCreature.class.getName());

  private BlockingQueue<Runnable> requests;

  private String name;

  private Thread thread; // Thread of execution.


  /*
      `status`变量是用来表示`ActiveCreature`类中管理的线程执行状态的整数值。其作用主要有两个方面：

      1. 追踪线程执行状态：`status`变量用来记录线程执行的不同阶段或状态。在代码中并没有明确指定所有可能的状态含义，
          但从上下文来看，至少有两种状态：
        - 0：表示线程执行正常或者按照预期停止。
        - 非零值：一般用来表示线程在执行过程中遇到某种异常或错误情况。
      2. 控制线程的中断逻辑：在`kill(int status)`方法中，传入的`status`参数用于更新当前线程的状态，并随后调用`thread.interrupt()`来中断线程。
          在`catch (InterruptedException e)`块中，检查`status`的值来决定是否记录错误日志。
          这意味着，只有当线程不是因正常逻辑而被中断时（即`status`不是0），才会记录一条错误日志。
          简而言之，`status`字段作为一个状态标识，帮助程序员更好地理解并控制`ActiveCreature`类所管理的线程在其生命周期内的行为和异常处理。
          通过调用`kill`方法并传入不同的状态值，可以反映线程结束的原因，以及如何响应这种结束（比如记录特定的信息到日志）。
   */
  private int status; // status of the thread of execution.

  /**
   * Constructor and initialization.
   */
  protected ActiveCreature(String name) {
    this.name = name;
    this.status = 0;
    this.requests = new LinkedBlockingQueue<>();
    thread = new Thread(() -> {
      boolean infinite = true;
      while (infinite) {
        try {
          /*
              什么情况下会走到catch中：
              每次执行take方法都会检查中断位，如果已经被中断，则抛出InterruptedException异常，进入到catch中去

              take和poll的区别：
              take() 是用于阻塞式获取队列元素，适用于需要等待队列填充的情况；
              而 poll() 则提供了非阻塞式获取或定时等待获取的功能，适合那些不能或不需要长时间等待的场景。
           */
          requests.take().run();
        } catch (InterruptedException e) {
          // 当线程被打断时，检查选status是否为0，不等于意味着非正常打断，此时进行一些额外处理
          if (this.status != 0) {
            logger.error("Thread was interrupted. --> {}", e.getMessage());
          }
          infinite = false;
          Thread.currentThread().interrupt();
        }
      }
    });
    thread.start();
  }

  /**
   * Eats the porridge.吃燕麦粥
   * 调用主动对象的eat方法时会在requests中放入一个吃的任务，最终任务还是在该对象中的Thread执行
   *
   * @throws InterruptedException due to firing a new Runnable.
   */
  public void eat() throws InterruptedException {
    requests.put(() -> {
      logger.info("{} is eating!", name());
      logger.info("{} has finished eating!", name());
    });
  }

  /**
   * Roam the wastelands.漫步，同上
   *
   * @throws InterruptedException due to firing a new Runnable.
   */
  public void roam() throws InterruptedException {
    requests.put(() ->
        logger.info("{} has started to roam in the wastelands.", name())
    );
  }

  /**
   * Returns the name of the creature.
   *
   * @return the name of the creature.
   */
  public String name() {
    return this.name;
  }

  /**
   * Kills the thread of execution.
   *
   * @param status of the thread of execution. 0 == OK, the rest is logging an error.
   */
  public void kill(int status) {
    this.status = status;
    this.thread.interrupt();
  }

  /**
   * Returns the status of the thread of execution.
   *
   * @return the status of the thread of execution.
   */
  public int getStatus() {
    return this.status;
  }
}
