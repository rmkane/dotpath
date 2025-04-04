/**
 * Public API for the DotPath library, providing dot-notation access to object properties.
 *
 * <p>The main entry point is {@link com.github.rmkane.dotpath.api.DotPath}, which provides
 * static methods for getting and setting values using dot-notation paths:
 *
 * <h2>Basic Usage</h2>
 * <pre>
 * // Get a nested property value
 * String city = DotPath.get(person, "address.city");
 *
 * // Set a nested property value
 * DotPath.set(person, "address.city", "New York");
 *
 * // Convert and set a string value
 * DotPath.setFromString(person, "age", "30");
 * </pre>
 *
 * <h2>Working with Maps</h2>
 * <pre>
 * Map&lt;String, Object&gt; data = new HashMap&lt;&gt;();
 * DotPath.set(data, "user.name", "John");
 * DotPath.set(data, "user.age", 30);
 * </pre>
 *
 * <h2>Type Safety</h2>
 * <pre>
 * // Type-safe property access
 * Integer age = DotPath.get(person, "age");
 * Address address = DotPath.get(person, "address");
 *
 * // Property copying with type validation
 * DotPath.copy(source, target, "address.city");
 * </pre>
 *
 * <h2>Error Handling</h2>
 * <pre>
 * try {
 *     DotPath.set(person, "invalid.path", "value");
 * } catch (DotPathException e) {
 *     // Handle invalid path, type mismatch, or access error
 * }
 * </pre>
 *
 * <p>All operations that can fail will throw {@link com.github.rmkane.dotpath.api.DotPathException}
 * with a descriptive message about what went wrong.
 *
 * @see com.github.rmkane.dotpath.api.DotPath
 * @see com.github.rmkane.dotpath.api.DotPathException
 */
package com.github.rmkane.dotpath.api;
