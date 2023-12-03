# CS-E4110 Concurrent Programming Assignment

In this assignment, you are tasked with writing a small network game, where multiple players co-operate to correctly guess a word. The application design utilizes the _reactor pattern_ for separating concurrency intrinsic to network programming from application logic.

The reactor is a mechanism for changing an inherently concurrent problem, such as responding to multiple network clients, into an asynchronous but sequential problem. All the necessary threading and locking is implemented inside the reactor and the application built on top of the reactor can be written as sequentially executed event handlers.

For [background reading](https://doi.org/10.1145/226239.226255) on design patterns and the reactor pattern:

> Schmidt, Douglas C.. "Using Design Patterns to Develop Reusable Object-Oriented Communication Software". *Commun. ACM* 38, no.10 (1995): 65–74.

## Tasks

The assignment is split into three separate tasks: A, B and C. These tasks must be implemented in order, as Task B depends on Task A, and Task C depends on Task B.

The tasks are described on separate pages:

 - [Task-A: Blocking Queue](task-a.md)
 - [Task-B: Reactor Pattern](task-b.md)
 - [Task-C: The Hangman Game](task-c.md)

These tasks share a [common API](common-api.md) for general concepts.

Each task is submitted invidually according to the schedule defined in MyCourses and A+.

## Grading

Each of the three tasks is graded separately. A maximum of 100 points is available for each task. The grading is performed manually by course staff, as comprehensive automated testing of concurrent programs is challenging and remains an active field of academic research.

## Project Template

To start working on the assignment, download the provided project template and SBT build file from A+.

### What Is Included

The project template contains class stubs for the classes you must implement, as well as the common API definitions. Also, the project template provides additional utility classes you may freely use as a part of your solution.

### Testing

The project template provides some unit tests to help you check, does your solution conform to the assignment API. However, passing all of the tests **does not** imply that your solution is fully correct!

> Tip: You may write your own tests to supplement the ones included in the project template.

## General Requirements

Your code must work exactly as described in the assignment specification and in the task descriptions. Serious deviations may cause you to fail the assignment! 

The general requirements presented here apply to all three sub-tasks. Additionally, each task defines requirements that apply to that task.

### Concurrency 

The concurrent aspects of assignment code should be implemented using JVM monitors (intrinsic locks, `synchronized`, `notify()`, `notifyAll()`, `wait()`) and threads.

Perfectly correct concurrency is required. Design your code before writing it. Consider:
 - safety, nothing bad will ever happen
 - liveness, something good will eventually happen
 - memory consistency, reads and writes from different threads are well ordered

Efficient use of concurrency is encouraged. Do not add synchronization to parts of your program that does not require safe concurrent access, as synchronization has an effect on performance.

> Tip: Prioritize correctness over efficiency. Correctness is required to pass the assignment, efficiency is a secondary grading criteria.

For this assignments, polling or busy-waiting are not considered proper concurrency mechanisms. Solutions based on polling or busy-waiting are and will be rejected.

### API Conformance

Your solution must conform exactly to the API defined in this specification.

Your implementations for the tasks, especially B and C, must work with any other compliant implementation. Do not add custom methods or variables, that are referenced across the task boundaries.

> Example: Your implementation of the Reactor from Task B must work with any API compliant event queue implementation from Task A, even if the other event queue implementations differ internally.

> Example: You may add `synchronized` the keyword to method signatures, as it is only syntax sugar equal to wrapping the entire method body in a `this.synchronized{ ... }` block.

### Type Safety

Scala's type system is powerful but complex. You may use casts (i.e. `asInstanceOf[sometype]`) as a part of your solution, if your casts are safe. Document your reasoning as comments.

> Tip: Scala documentation on [variances](https://docs.scala-lang.org/tour/variances.html)

## Error Handling

The APIs defined for each task do not strictly define an error handling strategy. You must extend this specification and implement reasonable error handling to make your implementation more robust.

> Example: The specification expects user input in Task C to be correct (i.e. a guess is a single lower-case alphabetical character). You should  implement logic to deal with non-conformant user input, such as upper-case input, multi-character input etc. The specification does not define how these cases should be handled, so you are free to design a reasonable custom error handling. For example, ignoring extra characters and converting upper-case input to lower-case are valid options.

You must make sure that your implementation handles error cases, which would break any requirements defined in this specification.

> Example: You must prevent behaviour, that would cause your Reactor to leak threads and prevent proper shutdown. 

Document your choices and reasoning! Possible error cases that are not covered by either code or documentation are treated as errors.

### Constraits

The assignment uses Scala 2.13.6, defined in the template SBT build file. A JDK version 11 or above is recommended.

Using unsafe deprecated (i.e. marked as `Deprecated` in the relevant documentation) methods, such as `java.lang.Thread.destroy()`, will lead to rejection.

Please remove any debugging code, such as `println()` or unused code, before submitting your solution.

### External Libraries

Using external libraries that are not a part of the Scala Standard Library or the Java Platform SE is not allowed. 

Using specific parts of the Standard Library are prohibited. The following packages or their sub-packages included in the Scala Standard Library may not be used for this assignment:

 - `scala.concurrent`
 - `java.util.concurrent`
 - `java.nio`

> Tip: You may write your own implementations of classes found in these packages, using the JVM monitor primitives.

### Style Requirements

The program should be written in clear and understandable Scala. The [Scala Style Guide](https://docs.scala-lang.org/style/) provides guidance. Course staff will read your code during grading: exceedingly unclear, cryptic or obfuscated code will be rejected.

Code comments, variable and method names should be written in clear technical English. Code comments should focus on *why* your code works, and why it is *correctly synchronized* as defined by the Java Memory Model. 

You can assume that the readers are well familiar with the assignment instructions and API, there is no need to repeat these requirements. Informative comments are a part of the grading criteria: 10 points out of a 100 for each task. Good comments also help course staff grade the submissions more accurately. 

> Example: if a typo or index-off-by-one causes a deadlock, but the commments explain why the code should not deadlock, it is clear that the solution had the correct idea and the typo mistake can be graded as a minor issue, instead of major concurrency design problem.
