# DotPath

A Java library for accessing nested properties using dot notation (e.g., "user.address.street"). DotPath makes it easy
to get, set, and copy deeply nested properties in Java objects and maps.

## Features

- Access nested properties using dot notation (e.g., "user.address.street")
- Get/set values with type safety
- Support for nested objects and maps
- Automatic creation of intermediate objects
- String value conversion for primitive types
- Property copying between objects

## Installation

Add the following Maven dependency to your `pom.xml`:

```xml

<dependency>
    <groupId>io.github.rmkane</groupId>
    <artifactId>dotpath</artifactId>
    <version>0.1.0-SNAPSHOT</version>
</dependency>
```

## Requirements

- Java 21 or higher
- Maven 3.x

## Usage

### Basic Operations

```java
// Create a test object
Point position = Point.builder().x(10).y(20).build();
State state = State.builder()
        .count(42)
        .position(position)
        .build();

// Get values using dot notation
Integer count = DotPath.get(state, "count");     // 42
Integer x = DotPath.get(state, "position.x");    // 10

// Set values using dot notation
DotPath.

set(state, "count",100);                         // state.count = 100
DotPath.

set(state, "position.x",30);                     // state.position.x = 30
```

### String Conversion and Property Copying

```java
// Set values from strings (with automatic type conversion)
DotPath.setFromString(state, "count","200");     // state.count = 200
DotPath.

setFromString(state, "position.y","40");         // state.position.y = 40

// Copy properties between objects
State target = State.builder().build();
DotPath.

copy(source, target, "position.x");              // Copies x coordinate
```

### Map Support

The library provides seamless support for working with Maps:

```java
Map<String, Object> map = new HashMap<>();
map.

put("level",5);
map.

put("nested",new HashMap<>());

// Get/set values in maps
        DotPath.

set(map, "level",10);                            // map.level = 10
DotPath.

set(map, "nested.value","test");                 // Creates nested structure

Integer level = DotPath.get(map, "level");       // 10
String value = DotPath.get(map, "nested.value"); // "test"
```

## Development

### Package Structure

```
io.github.rmkane.dotpath
├── api                          # Public API classes
│   ├── DotPath.java               # API logic
│   └── DotPathException.java      # Exception
└── internal                     # Internal implementation
    ├── operations/                # Property and map operations
    └── traversal/                 # Path traversal logic
```

### Building and Testing

Use Make commands for common development tasks:

```bash
# Clean and build the project
make clean compile

# Run the test suite
make test

# Format the code
make format

# Set up git hooks (automatically run by 'make all')
make setup-hooks

# Generate API documentation
make docs

# View documentation in browser
make open-docs
```

The project uses git hooks to ensure code quality:

- Pre-commit hook: Runs `spotless:check` to verify code formatting before each commit
- Hooks are automatically installed when running `make all` or `make setup-hooks`

### Documentation

The generated documentation will be available at:

```
target/reports/apidocs/index.html
```

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
