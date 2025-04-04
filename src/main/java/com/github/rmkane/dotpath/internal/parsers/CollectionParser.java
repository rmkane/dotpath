package com.github.rmkane.dotpath.internal.parsers;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Utility class for parsing strings into various collection types.
 * Provides methods to convert comma-separated strings into appropriate collections.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CollectionParser {
    /**
     * Parses a comma-separated string into a List of strings.
     * Each element is trimmed to remove leading and trailing whitespace.
     *
     * <p>Example:
     * <pre>
     * parseList("a, b, c") returns ["a", "b", "c"]
     * parseList("1,2,3")   returns ["1", "2", "3"]
     * parseList("")        returns an empty list
     * </pre>
     *
     * @param str The comma-separated string to parse
     * @return A List containing the parsed and trimmed elements
     */
    public static List<String> parseList(String str) {
        if (str == null || str.trim().isEmpty()) {
            return List.of();
        }
        return Arrays.stream(str.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }
}
