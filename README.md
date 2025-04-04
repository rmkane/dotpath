# Java Reflection Utils

A Java library that provides utility methods for working with Java reflection. It includes methods for getting and setting field values, invoking methods, and more.

## Features

- Get and set field values using dot-notation paths (e.g., "user.address.street")
- Copy values between objects
- Convert string values to appropriate types
- Handle nested objects and maps
- Support for primitive types, objects, and collections
- Automatic type resolution and conversion

## Getting Started

### Prerequisites

- Java 21 or higher
- Maven 3.6 or higher

### Installation

Add the following Maven dependency to your `pom.xml` file:

```xml
<dependency>
    <groupId>org.example.reflection</groupId>
    <artifactId>java-reflection-utils</artifactId>
    <version>1.0.0</version>
</dependency>
```

### Usage Examples

```java
// Create a sample object
State state = State.builder()
    .count(42)
    .value(3.14)
    .player("Player1")
    .position(Point.builder().x(10).y(20).build())
    .build();

// Get a value using dot notation
int count = ReflectionUtils.get(state, "count");
Point position = ReflectionUtils.get(state, "position");

// Set a value
ReflectionUtils.set(state, "count", 100);
ReflectionUtils.set(state, "position.x", 30);

// Copy values between objects
State newState = new State();
ReflectionUtils.copy(state, newState, "count");
ReflectionUtils.copy(state, newState, "position");

// Set values from strings
ReflectionUtils.setFromString(state, "count", "50");
ReflectionUtils.setFromString(state, "value", "2.718");
```

## Project Structure

```
src/
├── main/
│   └── java/
│       └── org/
│           └── example/
│               └── reflection/
│                   └── ReflectionUtils.java
└── test/
    └── java/
        └── org/
            └── example/
                └── reflection/
                    ├── model/
                    │   ├── Point.java
                    │   └── State.java
                    └── ReflectionUtilsTest.java
```

## Development

### Building the Project

```bash
mvn clean install
```

### Running Tests

```bash
mvn test
```

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
