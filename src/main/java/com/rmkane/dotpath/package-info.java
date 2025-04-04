/**
 * A reflection utility library that provides easy-to-use property access and manipulation
 * using dot-notation paths. This library simplifies working with nested objects and properties
 * through reflection.
 *
 * <p>The main entry point is {@link com.rmkane.dotpath.api.PropertyPathUtils}, which provides
 * methods for getting and setting values using property paths.</p>
 *
 * <p>Example usage:</p>
 * <pre>
 * Person person = new Person();
 * PropertyPathUtils.set(person, "address.street", "123 Main St");
 * String street = PropertyPathUtils.get(person, "address.street");
 * </pre>
 *
 * @see com.rmkane.dotpath.api.PropertyPathUtils
 */
package com.rmkane.dotpath;
