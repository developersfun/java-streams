# Java Streams API Demo

This project is a simple Java application designed to demonstrate various features of the Java Stream API, from its introduction in Java 8 through to newer additions in Java 16. The code provides clear, commented examples for a wide range of stream operations, making it an excellent resource for learning and reference.

The main demonstration is contained within the `StreamsDemoTest.java` file.

## How to Run

1.  Clone the repository or open the project in your favorite IDE (e.g., IntelliJ IDEA, Eclipse, VS Code).
2.  Navigate to the `StreamsDemoTest.java` file located at `src/test/java/com/tdf/StreamsDemoTest.java`.
3.  Run the `main` method within the `StreamsDemoTest` class.
4.  The output will be printed to the console, showing the results for each demonstrated stream operation.

---

## Stream Operations Quick Reference

| Java Version | Function(s)                               | Key Use Case                                      |
| :----------: | ----------------------------------------- | ------------------------------------------------- |
|   **Java 8**   | `filter`, `map`, `sorted`, `collect`      | The foundational pipeline for data transformation.|
|   **Java 8**   | `forEach`                                 | Performing terminal actions on each element.      |
|   **Java 8**   | `reduce`                                  | Aggregating a stream into a single result.        |
|   **Java 8**   | `anyMatch`, `allMatch`, `noneMatch`       | Short-circuiting checks against a predicate.      |
|   **Java 8**   | `findFirst`, `findAny`                    | Retrieving a single element from the stream.      |
|   **Java 8**   | `distinct`                                | Removing duplicate elements.                      |
|   **Java 8**   | `limit`, `skip`                           | Truncating or paginating the stream.              |
|   **Java 8**   | `groupingBy`, `counting`                  | Data categorization and aggregation.              |
|   **Java 9**   | `takeWhile`                               | Taking elements as long as a condition is met.    |
|   **Java 9**   | `dropWhile`                               | Discarding elements as long as a condition is met.|
|   **Java 9**   | `ofNullable`                              | Safely creating a stream from a nullable object.  |
|  **Java 10**   | `toUnmodifiableList`                      | Collecting elements into an unmodifiable list.    |
|  **Java 12**   | `teeing`                                  | Performing two collections and merging the results.|
|  **Java 16**   | `mapMulti`                                | A flexible, performant alternative to `flatMap`.  |
|  **Java 16**   | `toList`                                  | Shorthand for collecting into an unmodifiable list.|

---

## Detailed Examples

### Java 8

#### `filter`, `map`, `sorted`, `collect`
This is the most common stream pipeline, used for complex data processing tasks where you need to select, order, and transform elements into a new collection.

| Category        | Description                                                                                                                              |
| --------------- | ---------------------------------------------------------------------------------------------------------------------------------------- |
| **When to use** | Ideal for data processing where you need to select, order, and transform elements into a new collection.                                 |
| **When to avoid** | For very simple loops without complex transformations, a traditional `for` loop might be slightly more performant. Avoid overly complex logic inside lambdas. |

```java
// Objective: Get names of employees with salary > 25000, sorted by salary in descending order.
List<String> highSalaryEmployeeNames = employeeList.stream()
        .filter(e -> e.salary() > 25000)
        .sorted(Comparator.comparingDouble(Employee::salary).reversed())
        .map(Employee::name)
        .collect(Collectors.toList());
// Output: High Salary Employees: [Nine, Two, Ten, Six, Eight]
```

#### `forEach`
Performs a terminal action on each element of a stream, typically for side-effects like printing or logging.

| Category        | Description                                                                                                                              |
| --------------- | ---------------------------------------------------------------------------------------------------------------------------------------- |
| **When to use** | When you need to "do something" with each element at the end of a stream pipeline and don't need to create a new collection.             |
| **When to avoid** | Do not use to modify a shared state or collection from a parallel stream. It is a terminal operation, so no other operations can be chained after it. |

```java
// Objective: Print the names of all employees in the "BE" department.
employeeList.stream()
        .filter(e -> e.department().equals("BE"))
        .forEach(e -> System.out.println("BE Employee: " + e.name()));
// Output:
// BE Employee: Three
// BE Employee: Four
// BE Employee: Five
```

#### `reduce`
Combines all elements of a stream into a single result by repeatedly applying a binary operator.

| Category        | Description                                                                                                                              |
| --------------- | ---------------------------------------------------------------------------------------------------------------------------------------- |
| **When to use** | Perfect for aggregation operations like summing numbers, concatenating strings, or finding the min/max element.                          |
| **When to avoid** | For simply collecting elements into a list, use `collect(Collectors.toList())`. For primitive streams, `sum()`, `max()`, `min()` are more direct. |

```java
// Objective: Calculate the total salary of all employees.
Optional<Double> totalSalary = employeeList.stream()
        .map(Employee::salary)
        .reduce(Double::sum);
// Output: Total Salary: 320000.0
```

#### `anyMatch`, `allMatch`, `noneMatch`
Short-circuiting terminal operations to check if stream elements match a predicate.

| Category        | Description                                                                                                                              |
| --------------- | ---------------------------------------------------------------------------------------------------------------------------------------- |
| **When to use** | When you need a boolean answer about the stream's contents. They are very efficient as they stop processing as soon as the result is determined. |
| **When to avoid** | If you need to know *how many* elements match (use `filter().count()`) or which elements match (use `filter().collect()`).                |

```java
// Objective: Check if any employee is in "IT", if all salaries are > 10000, and if there are no "Intern" employees.
boolean anyIT = employeeList.stream().anyMatch(e -> e.department().equals("IT")); // true
boolean allHighSalaries = employeeList.stream().allMatch(e -> e.salary() > 10000); // true
boolean noInterns = employeeList.stream().noneMatch(e -> e.department().equals("Intern")); // true
```

#### `groupingBy`, `counting`
A powerful collector used to group elements of a stream into a `Map`, often combined with downstream collectors to perform aggregations on each group.

| Category        | Description                                                                                                                              |
| --------------- | ---------------------------------------------------------------------------------------------------------------------------------------- |
| **When to use** | Whenever you need to categorize or partition data. It is the direct equivalent of SQL's `GROUP BY` clause.                               |
| **When to avoid** | For very complex, multi-level grouping, the collector logic can become deeply nested and hard to read.                                 |

```java
// Objective: Count the number of employees in each department.
Map<String, Long> departmentFrequency = employeeList.stream()
        .collect(Collectors.groupingBy(Employee::department, Collectors.counting()));
// Output: {ANDR=1, BE=3, DEVOPS=1, FE=1, HR=1, IOS=1, IT=2}
```

---
### Java 9

#### `takeWhile`
Returns the longest prefix of elements from a sorted stream that match a given predicate.

| Category        | Description                                                                                                                              |
| --------------- | ---------------------------------------------------------------------------------------------------------------------------------------- |
| **When to use** | On a sorted stream, when you want to take elements from the beginning until a condition is no longer met.                                 |
| **When to avoid** | On an unordered stream, the result is non-deterministic. Use `filter()` to get all matching elements regardless of position.             |

```java
// Objective: Get employees with salary less than 20000 from a sorted list.
List<Employee> takeWhile = employeeList.stream()
        .sorted(Comparator.comparingDouble(Employee::salary))
        .takeWhile(e -> e.salary() < 20000)
        .collect(Collectors.toList());
// Output: TakeWhile salary < 20000: [Four, One, Three]
```

#### `dropWhile`
Discards elements from the beginning of a sorted stream as long as they match a predicate, then returns the rest.

| Category        | Description                                                                                                                              |
| --------------- | ---------------------------------------------------------------------------------------------------------------------------------------- |
| **When to use** | On a sorted stream, to skip an initial set of elements that meet a certain condition.                                                    |
| **When to avoid** | Not very useful on unordered streams.                                                                                                    |

```java
// Objective: Get employees with salary >= 20000 from a sorted list.
List<Employee> dropWhile = employeeList.stream()
        .sorted(Comparator.comparingDouble(Employee::salary))
        .dropWhile(e -> e.salary() < 20000)
        .collect(Collectors.toList());
// Output: DropWhile salary < 20000: [Five, Seven, Eight, Six, Ten, Two, Nine]
```

---
### Java 10

#### `toUnmodifiableList`
A collector that gathers stream elements into a truly unmodifiable `List`.

| Category        | Description                                                                                                                              |
| --------------- | ---------------------------------------------------------------------------------------------------------------------------------------- |
| **When to use** | When you want to return a read-only list from a method, preventing any client code from modifying it. Excellent for defensive copying.    |
| **When to avoid** | If the collection needs to be modified after creation, use `collect(Collectors.toList())` or `collect(Collectors.toCollection(ArrayList::new))`. |

```java
// Objective: Collect employee names into an unmodifiable list.
List<String> unmodifiableNames = employeeList.stream()
        .map(Employee::name)
        .collect(Collectors.toUnmodifiableList());
// The list cannot be modified. Throws UnsupportedOperationException on add/remove.
```

---
### Java 12

#### `teeing`
A collector that takes two other collectors and a merger function, processing the stream with both simultaneously and then merging their results.

| Category        | Description                                                                                                                              |
| --------------- | ---------------------------------------------------------------------------------------------------------------------------------------- |
| **When to use** | When you need to perform two different aggregations on the same stream in a single pass (e.g., calculating sum and count to find an average). |
| **When to avoid** | If the logic is simple, performing two separate stream operations might be more readable.                                                |

```java
// Objective: Calculate the average salary of all employees using two downstream collectors in one pass.
Map<String, Double> teeingResult = employeeList.stream().collect(
        Collectors.teeing(
                Collectors.summingDouble(Employee::salary), // Collector 1: sum
                Collectors.counting(),                     // Collector 2: count
                (sum, count) -> Map.of("AVERAGE_SALARY", sum / count) // Merger
        )
);
// Output: Teeing for Average Salary: {AVERAGE_SALARY=32000.0}
```

---
### Java 16

#### `mapMulti`
A more flexible and often more performant version of `flatMap` that replaces each element of a stream with zero, one, or more elements by passing them to a consumer.

| Category        | Description                                                                                                                              |
| --------------- | ---------------------------------------------------------------------------------------------------------------------------------------- |
| **When to use** | When mapping one element to many, especially with imperative logic (e.g., if-statements), as it avoids creating intermediate streams.      |
| **When to avoid** | For simple one-to-one transformations, `map` is simpler. For simple one-to-many transformations, `flatMap` can be more readable.         |

```java
// Objective: Transform each employee into multiple elements (name and department) in the stream.
List<String> mapMultiResult = employeeList.stream()
        .<String>mapMulti((employee, downstream) -> {
            downstream.accept(employee.name());
            downstream.accept(employee.department());
        })
        .collect(Collectors.toList());
// Output: [One, IT, Two, IT, Three, BE, ...]
```

#### `toList`
A convenient shorthand for `collect(Collectors.toList())` that returns an unmodifiable list.

| Category        | Description                                                                                                                              |
| --------------- | ---------------------------------------------------------------------------------------------------------------------------------------- |
| **When to use** | In almost all cases where you would have used `collect(Collectors.toList())`. It's shorter and more readable.                             |
| **When to avoid** | The list returned is unmodifiable. If you need a mutable list, you must use `collect(Collectors.toCollection(ArrayList::new))`.          |

```java
// Objective: Filter employees and collect them into a new List using the convenient toList() method.
List<Employee> toListResult = employeeList.stream()
        .filter(e -> e.salary() > 60000)
        .toList(); // Returns an unmodifiable list
// Output: toList result: [Nine]
```