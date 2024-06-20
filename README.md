# Run Catching: Functional Exception Handling and Retries in Java

This project provides a concise and expressive way to handle exceptions and perform repeatable operations in Java through a functional approach. 
It consists of three main classes: `Try`, `Repeat`, and `Retry`, which offer versatile methods to wrap operations that may throw exceptions and to execute operations repeatedly under certain conditions.

## Main Classes

### Try:
The `Try` class offers a functional implementation of the `try-catch` block, allowing for more fluent and expressive handling of exceptions and composition of potentially failing operations.

#### Key Features
- **`runCatching`**: Wrap operations that may throw exceptions and return a `Try` indicating success or failure.
- **`runCatchingOrElse`**: Execute an operation and return a default value in case of an exception.
- **`runCatchingOrThrow`**: Execute an operation and throw a specific user-provided exception in case of failure.
- **`onSuccess` and `onFailure`**: Handle the result of successful or failed operations via callback functions.
- **Safe access methods**: Retrieve the resulting value or handle the failure with methods like `get`, `getOrNull`, `getOrDefault`, `getOrElse`, and `getOrThrow`.

#### Example
```java
String result = Try.runCatching(() -> someMethodThatMightThrow())
        .onSuccess(value -> System.out.println("Successful operation: " + value))
        .onFailure(exception -> System.err.println("Failed operation: " + exception.getMessage()))
        .getOrDefault("Default Value");
```

### Repeat:
The `Repeat` class allows executing an operation repeatedly under specified conditions and handling failures during these repetitions.

#### Key Features
- **`of`**: Create a `Repeat` instance by defining the number of repetitions and the function to execute.
- **`repeatWhile`**: Specify a condition to determine if the operation should be repeated.
- **`delay`**: Introduce a delay between repetitions.
- **`orElse`**: Provide an alternative value if all attempts fail.

#### Example
```java
String result = Repeat.of(5, attempt -> someMethodThatMightThrow())
        .repeatWhile((value, exception) -> exception != null)
        .delay(1000)
        .orElse(() -> "Default value")
        .run();
```

### Retry:
The `Retry` class is a simplified version of `Repeat` focused on re-attempting a failed operation a specified number of retries with optional delay and other parameters.

#### Key Features
- **`of`**: Create a `Retry` instance by specifying the number of retries and the function to execute.
- **`onSuccess`**: Define a success handler for the successful retry.
- **`onFailure`**: Define an error handler for exceptions during retries.
- **`orElse`**: Provide a fallback value if all retries fail.
- **`delay`**: Introduce a delay between retries.

#### Example
```java
String result = Retry.of(3, () - return someMethodThatMightThrow())
        .delay(500)
        .onSuccess(value -> System.out.println("Successful operation. result: " + value))
        .onFailure(ex -> System.err.println("Retry failed: " + ex.getMessage()))
        .orElse(() -> "Default value")
        .run();
```

