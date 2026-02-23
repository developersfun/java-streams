package com.tdf;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StreamsDemoTest {

    private static List<Employee> employeeList = List.of(
            new Employee(1, "One", "IT", 16000.0),
            new Employee(2, "Two", "IT", 60000.0),
            new Employee(3, "Three", "BE", 19000.0),
            new Employee(4, "Four", "BE", 11000.0),
            new Employee(5, "Five", "BE", 22000.0),
            new Employee(6, "Six", "IOS", 38000.0),
            new Employee(7, "Seven", "HR", 22000.0),
            new Employee(8, "Eight", "FE", 26000.0),
            new Employee(9, "Nine", "ANDR", 66000.0),
            new Employee(10, "Ten", "DEVOPS", 39000.0)
    );

    public static void main(String[] args) {

        //<editor-fold desc="Java 8">
        System.out.println("--- Java 8 ---");

        /**
         * Usecase: To perform a multi-step transformation on a stream of data. This is the most common stream pipeline.
         * `filter` selects elements based on a condition.
         * `sorted` orders elements based on a comparator.
         * `map` transforms elements from one type to another.
         * `collect` gathers the results into a collection.
         *
         * When to use: Ideal for data processing tasks where you need to select, order, and transform elements into a new collection.
         * It's highly readable and expressive for complex data manipulation.
         *
         * When to avoid: For very simple loops without complex transformations, a traditional `for` loop might be slightly more performant,
         * though often at the cost of readability. Avoid putting overly complex, multi-line logic inside lambdas.
         *
         * Best example: Filtering a list of products by category, sorting them by price, and collecting their names into a new list.
         */
        List<String> highSalaryEmployeeNames = employeeList.stream()
                .filter(e -> e.salary() > 25000)
                .sorted(Comparator.comparingDouble(Employee::salary).reversed())
                .map(Employee::name)
                .collect(Collectors.toList());
        System.out.println("High Salary Employees: " + highSalaryEmployeeNames);

        /**
         * Usecase: To perform a terminal action on each element of a stream, typically for side-effects like printing or logging.
         *
         * When to use: When you need to "do something" with each element at the end of a stream pipeline and you don't need to
         * create a new collection from the results.
         *
         * When to avoid: Do not use `forEach` to modify a shared state or collection, especially from a parallel stream, as it can
         * lead to race conditions. It is a terminal operation, so no other stream operations can be chained after it.
         *
         * Best example: Printing the name of each employee who meets certain criteria after a filter operation.
         */
        employeeList.stream()
                .filter(e -> e.department().equals("BE"))
                .forEach(e -> System.out.println("BE Employee: " + e.name()));

        /**
         * Usecase: To combine all elements of a stream into a single result by repeatedly applying a binary operator.
         *
         * When to use: Perfect for aggregation operations like summing numbers, concatenating strings, or finding the min/max element.
         *
         * When to avoid: For simply collecting elements into a list, use `collect(Collectors.toList())`. For primitive streams,
         * the dedicated `sum()`, `max()`, `min()` methods are more direct and often more readable.
         *
         * Best example: Calculating the total salary of all employees or finding the product of a stream of numbers.
         */
        Optional<Double> totalSalary = employeeList.stream()
                .map(Employee::salary)
                .reduce(Double::sum);
        System.out.println("Total Salary: " + totalSalary.orElse(0.0));

        /**
         * Usecase: Short-circuiting terminal operations to check if stream elements match a predicate.
         * `anyMatch`: Checks if at least one element matches.
         * `allMatch`: Checks if all elements match.
         * `noneMatch`: Checks if no elements match.
         *
         * When to use: When you need a boolean answer about the stream's contents. They are very efficient as they stop
         * processing as soon as the result is determined.
         *
         * When to avoid: If you need to know *how many* elements match (use `filter().count()`) or which elements match
         * (use `filter().collect()`).
         *
         * Best example: Checking if a list of transactions contains any fraudulent ones (`anyMatch`).
         */
        boolean anyIT = employeeList.stream().anyMatch(e -> e.department().equals("IT"));
        System.out.println("Any IT employee? " + anyIT);
        boolean allHighSalaries = employeeList.stream().allMatch(e -> e.salary() > 10000);
        System.out.println("All salaries > 10000? " + allHighSalaries);
        boolean noInterns = employeeList.stream().noneMatch(e -> e.department().equals("Intern"));
        System.out.println("No interns? " + noInterns);

        /**
         * Usecase: To find an element from a stream. `findFirst` gets the first element in encounter order, while `findAny`
         * is non-deterministic and may return any element (offering better performance in parallel streams).
         *
         * When to use: When you only need one example element that matches a criteria (often used after `filter`). Use `findFirst`
         * when order matters. Use `findAny` with parallel streams when any element will do.
         *
         * When to avoid: If you need all elements that match a predicate, use `filter().collect()` instead.
         *
         * Best example: Finding the first user in a list who has administrative privileges.
         */
        Optional<Employee> firstEmployee = employeeList.stream().findFirst();
        System.out.println("First Employee: " + firstEmployee.get().name());
        Optional<Employee> anyEmployee = employeeList.stream().findAny();
        System.out.println("Any Employee: " + anyEmployee.get().name());

        /**
         * Usecase: To remove duplicate elements from a stream based on their `equals()` method.
         *
         * When to use: When you need a collection of unique values from a source that may contain duplicates.
         *
         * When to avoid: `distinct()` is a stateful operation that maintains a buffer of seen elements. This can consume
         * significant memory on very large streams, especially with objects that have complex equality logic.
         *
         * Best example: Getting a list of unique departments from a list of employees.
         */
        List<String> distinctDepartments = employeeList.stream()
                .map(Employee::department)
                .distinct()
                .collect(Collectors.toList());
        System.out.println("Distinct Departments: " + distinctDepartments);

        /**
         * Usecase: `limit(n)` truncates the stream to `n` elements. `skip(n)` discards the first `n` elements.
         *
         * When to use: Excellent for pagination logic (e.g., `skip(10).limit(10)` to get the second page of 10 items).
         * Also useful for getting a subset of elements from the start of a stream.
         *
         * When to avoid: On unsorted streams, the elements returned can be arbitrary. These methods are most predictable
         * and useful when combined with a `sorted()` operation.
         *
         * Best example: Implementing pagination for a search result list displayed to a user.
         */
        List<Employee> firstThree = employeeList.stream().limit(3).collect(Collectors.toList());
        System.out.println("First 3 employees: " + firstThree.size());
        List<Employee> skipThree = employeeList.stream().skip(3).collect(Collectors.toList());
        System.out.println("Skipped 3 employees: " + skipThree.size());

        /**
         * Usecase: `groupingBy` is a powerful collector used to group elements of a stream into a `Map`. It's often
         * combined with downstream collectors like `counting` to perform aggregations on each group.
         *
         * When to use: Whenever you need to categorize or partition data. It is the direct equivalent of SQL's `GROUP BY` clause.
         *
         * When to avoid: While powerful, for very complex, multi-level grouping and aggregation, the collector logic can
         * become deeply nested and hard to read.
         *
         * Best example: Grouping employees by department and counting how many are in each.
         */
        Map<String, Long> departmentFrequency = employeeList.stream()
                .collect(Collectors.groupingBy(Employee::department, Collectors.counting()));
        System.out.println("Department Frequency: " + departmentFrequency);
        //</editor-fold>

        //<editor-fold desc="Java 9">
        System.out.println("\n--- Java 9 ---");

        /**
         * Usecase: Returns the longest prefix of elements from a stream that match a given predicate. It stops processing
         * the stream as soon as an element does not match the predicate.
         *
         * When to use: On an ordered or sorted stream, when you want to take elements from the beginning until a condition is no longer met.
         *
         * When to avoid: On an unordered stream, the result is non-deterministic and often not useful. If you want all elements
         * that match the predicate regardless of their position, use `filter()` instead.
         *
         * Best example: Processing a sorted stream of log entries and taking all entries until the first "ERROR" is found.
         */
        List<Employee> takeWhile = employeeList.stream()
                .sorted(Comparator.comparingDouble(Employee::salary))
                .takeWhile(e -> e.salary() < 20000)
                .collect(Collectors.toList());
        System.out.println("TakeWhile salary < 20000: " + takeWhile.stream().map(Employee::name).collect(Collectors.toList()));

        /**
         * Usecase: The opposite of `takeWhile`. It discards elements from the beginning of the stream as long as they match a
         * predicate, then returns the rest of the stream.
         *
         * When to use: On an ordered or sorted stream, to skip an initial set of elements that meet a certain condition.
         *
         * When to avoid: Like `takeWhile`, it is not very useful on unordered streams.
         *
         * Best example: In a sorted list of numbers, skipping all initial negative numbers and processing only the positive ones.
         */
        List<Employee> dropWhile = employeeList.stream()
                .sorted(Comparator.comparingDouble(Employee::salary))
                .dropWhile(e -> e.salary() < 20000)
                .collect(Collectors.toList());
        System.out.println("DropWhile salary < 20000: " + dropWhile.stream().map(Employee::name).collect(Collectors.toList()));

        /**
         * Usecase: Creates a stream containing a single element if the provided element is non-null, or an empty stream if it is null.
         *
         * When to use: To safely convert a potentially null object into a stream without causing a `NullPointerException`. This is
         * useful for integrating with legacy code or APIs that might return null.
         *
         * When to avoid: If you are certain the object is not null, `Stream.of(object)` is more direct. To filter nulls from a
         * larger stream, use `filter(Objects::nonNull)` instead.
         *
         * Best example: `Stream.ofNullable(map.get("key"))` which produces a stream of one or zero elements safely.
         */
        Stream<Employee> nullableStream = Stream.ofNullable(employeeList.get(0));
        System.out.println("ofNullable count: " + nullableStream.count());
        Stream<Employee> nullStream = Stream.ofNullable(null);
        System.out.println("ofNullable (null) count: " + nullStream.count());
        //</editor-fold>

        //<editor-fold desc="Java 10">
        System.out.println("\n--- Java 10 ---");

        /**
         * Usecase: A collector that gathers stream elements into a truly unmodifiable `List`.
         *
         * When to use: When you want to return a list from a method that you want to be read-only, preventing any client code
         * from adding, removing, or changing its elements. This is excellent for defensive copying and creating immutable data structures.
         *
         * When to avoid: If the collection needs to be modified after it's created, use `collect(Collectors.toList())` or
         * `collect(Collectors.toCollection(ArrayList::new))` instead.
         *
         * Best example: Returning a list of supported currencies or default application settings from a service class.
         */
        List<String> unmodifiableNames = employeeList.stream()
                .map(Employee::name)
                .collect(Collectors.toUnmodifiableList());
        System.out.println("Unmodifiable list created, class: " + unmodifiableNames.getClass());
        //</editor-fold>

        //<editor-fold desc="Java 12">
        System.out.println("\n--- Java 12 ---");

        /**
         * Usecase: A collector that takes two other collectors and a merger function. It processes the stream with both collectors
         * simultaneously and then merges their results using the provided function.
         *
         * When to use: When you need to perform two different aggregations on the same stream in a single pass, such as calculating
         * both the sum and the count to derive an average.
         *
         * When to avoid: If the logic is simple, performing two separate stream operations might be more readable. `teeing` can
         * make the code dense if overused or nested.
         *
         * Best example: Calculating both the minimum and maximum salary in a single pass over the employee stream.
         */
        Map<String, Double> teeingResult = employeeList.stream().collect(
                Collectors.teeing(
                        Collectors.summingDouble(Employee::salary),
                        Collectors.counting(),
                        (sum, count) -> Map.of("AVERAGE_SALARY", sum / count)
                )
        );
        System.out.println("Teeing for Average Salary: " + teeingResult);
        //</editor-fold>

        //<editor-fold desc="Java 16">
        System.out.println("\n--- Java 16 ---");

        /**
         * Usecase: A more flexible and often more performant version of `flatMap`. It replaces each element of a stream with
         * zero, one, or more elements by passing them to a consumer.
         *
         * When to use: When mapping one element to many, especially when the logic is imperative (e.g., involves if-statements)
         * or when performance is critical, as it avoids creating intermediate streams for each element.
         *
         * When to avoid: For simple one-to-one transformations, `map` is simpler. For simple one-to-many transformations where you
         * can easily return a stream (e.g., `list.stream()`), `flatMap` can be more readable.
         *
         * Best example: For each employee, produce their name and department as separate strings in the output stream.
         */
        List<String> mapMultiResult = employeeList.stream()
                .<String>mapMulti((employee, downstream) -> {
                    downstream.accept(employee.name());
                    downstream.accept(employee.department());
                })
                .collect(Collectors.toList());
        System.out.println("mapMulti result: " + mapMultiResult);

        /**
         * Usecase: A convenient shorthand for `collect(Collectors.toList())`.
         *
         * When to use: In almost all cases where you would have used `collect(Collectors.toList())`. It's shorter and more readable.
         *
         * When to avoid: The list returned by `toList()` is unmodifiable. If you explicitly need a mutable list (e.g., an `ArrayList`
         * that you intend to modify later), you must use `collect(Collectors.toCollection(ArrayList::new))`.
         *
         * Best example: Any time you need to collect stream results into a general-purpose list.
         */
        List<Employee> toListResult = employeeList.stream()
                .filter(e -> e.salary() > 60000)
                .toList();
        System.out.println("toList result: " + toListResult.stream().map(Employee::name).collect(Collectors.toList()));
        //</editor-fold>
    }
}

record Employee(int id, String name, String department, Double salary){}