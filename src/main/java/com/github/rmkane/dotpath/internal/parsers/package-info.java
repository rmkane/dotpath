/**
 * Internal parsers for converting string values into various data types.
 *
 * <p>This package contains utility classes for parsing strings into different data types:
 * <ul>
 *   <li>{@link com.github.rmkane.dotpath.internal.parsers.TimeParser} - Parses date and time strings using ISO formats
 *   <li>{@link com.github.rmkane.dotpath.internal.parsers.CollectionParser} - Parses strings into collections
 * </ul>
 *
 * <p>These parsers are used internally by the type conversion system to support
 * converting string values to various types when using dot notation paths.
 *
 * <p>Example usage:
 * <pre>
 * // Parse a date
 * LocalDate date = TimeParser.parseLocalDate("2024-04-04");
 *
 * // Parse a comma-separated list
 * List<String> items = CollectionParser.parseList("a, b, c");
 * </pre>
 *
 * <p><strong>Note:</strong> This is an internal API and should not be used directly by client code.
 * The API may change without notice.
 *
 * @see com.github.rmkane.dotpath.internal.TypeResolver
 */
package com.github.rmkane.dotpath.internal.parsers;
