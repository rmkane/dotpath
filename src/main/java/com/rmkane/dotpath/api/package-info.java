/**
 * Public API for the DotPath library, providing dot-notation access to object properties.
 *
 * <p>The main entry point is {@link com.rmkane.dotpath.api.PropertyPathUtils}, which provides
 * static methods for getting and setting values using dot-notation paths:
 *
 * <h2>Basic Usage</h2>
 * <pre>
 * // Get a nested property value
 * String city = PropertyPathUtils.get(person, "address.city");
 *
 * // Set a nested property value
 * PropertyPathUtils.set(person, "address.city", "New York");
 *
 * // Convert and set a string value
 * PropertyPathUtils.setFromString(person, "age", "30");
 * </pre>
 *
 * <h2>Working with Maps</h2>
 * <pre>
 * Map&lt;String, Object&gt; data = new HashMap&lt;&gt;();
 * PropertyPathUtils.set(data, "user.name", "John");
 * PropertyPathUtils.set(data, "user.age", 30);
 * </pre>
 *
 * <h2>Type Safety</h2>
 * <pre>
 * // Type-safe property access
 * Integer age = PropertyPathUtils.get(person, "age");
 * Address address = PropertyPathUtils.get(person, "address");
 *
 * // Property copying with type validation
 * PropertyPathUtils.copy(source, target, "address.city");
 * </pre>
 *
 * <h2>Error Handling</h2>
 * <pre>
 * try {
 *     PropertyPathUtils.set(person, "invalid.path", "value");
 * } catch (DotPathException e) {
 *     // Handle invalid path, type mismatch, or access error
 * }
 * </pre>
 *
 * <p>All operations that can fail will throw {@link com.rmkane.dotpath.api.DotPathException}
 * with a descriptive message about what went wrong.
 *
 * @see com.rmkane.dotpath.api.PropertyPathUtils
 * @see com.rmkane.dotpath.api.DotPathException
 */
package com.rmkane.dotpath.api;
