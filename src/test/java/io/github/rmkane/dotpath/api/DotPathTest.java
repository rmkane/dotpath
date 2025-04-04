package io.github.rmkane.dotpath.api;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import io.github.rmkane.dotpath.model.Point;
import io.github.rmkane.dotpath.model.State;

class DotPathTest {
    @Test
    void testGenericGetAndSet() throws Exception {
        // Create a State object with all properties set using builder pattern
        Point position = Point.builder().x(10).y(20).build();

        Map<String, Object> properties = new HashMap<>();
        properties.put("level", 5);
        properties.put("score", 1000);
        properties.put("achievements", Arrays.asList("first_blood", "headshot"));

        State state = State.builder()
                .count(42)
                .value(3.14)
                .player("Player1")
                .position(position)
                .properties(properties)
                .build();

        // Test getting primitive types
        Integer count = DotPath.<Integer>get(state, "count");
        assertEquals(42, count);

        Double value = DotPath.<Double>get(state, "value");
        assertEquals(3.14, value);

        // Test getting String
        String player = DotPath.<String>get(state, "player");
        assertEquals("Player1", player);

        // Test getting nested object properties
        Point pos = DotPath.<Point>get(state, "position");
        assertEquals(10, pos.getX());
        assertEquals(20, pos.getY());

        // Test getting nested object's properties directly
        Integer posX = DotPath.<Integer>get(state, "position.x");
        assertEquals(10, posX);

        Integer posY = DotPath.<Integer>get(state, "position.y");
        assertEquals(20, posY);

        // Test getting from properties map
        Integer level = DotPath.<Integer>get(state, "properties.level");
        assertEquals(5, level);

        Integer score = DotPath.<Integer>get(state, "properties.score");
        assertEquals(1000, score);

        List<String> achievements = DotPath.<List<String>>get(state, "properties.achievements");
        assertEquals(Arrays.asList("first_blood", "headshot"), achievements);

        // Test setting primitive types
        DotPath.set(state, "count", 100);
        assertEquals(100, state.getCount());

        DotPath.set(state, "value", 6.28);
        assertEquals(6.28, state.getValue());

        // Test setting String
        DotPath.set(state, "player", "Player2");
        assertEquals("Player2", state.getPlayer());

        // Test setting nested object
        Point newPos = Point.builder().x(30).y(40).build();
        DotPath.set(state, "position", newPos);
        assertEquals(30, state.getPosition().getX());
        assertEquals(40, state.getPosition().getY());

        // Test setting nested object's properties directly
        DotPath.set(state, "position.x", 50);
        assertEquals(50, state.getPosition().getX());

        DotPath.set(state, "position.y", 60);
        assertEquals(60, state.getPosition().getY());

        // Test setting properties in map
        DotPath.set(state, "properties.level", 10);
        assertEquals(10, state.getProperties().get("level"));

        DotPath.set(state, "properties.score", 2000);
        assertEquals(2000, state.getProperties().get("score"));

        DotPath.set(state, "properties.achievements", Arrays.asList("victory", "mvp"));
        assertEquals(Arrays.asList("victory", "mvp"), state.getProperties().get("achievements"));
    }

    @Test
    void testGenericCopy() throws Exception {
        // Create source State with all properties using builder pattern
        Point position = Point.builder().x(10).y(20).build();

        Map<String, Object> properties = new HashMap<>();
        properties.put("level", 5);
        properties.put("score", 1000);

        State source = State.builder()
                .count(42)
                .value(3.14)
                .player("Player1")
                .position(position)
                .properties(properties)
                .build();

        // Create target State
        State target = State.builder().build();

        // Copy primitive types
        DotPath.copy(source, target, "count");
        assertEquals(42, target.getCount());

        DotPath.copy(source, target, "value");
        assertEquals(3.14, target.getValue());

        // Copy String
        DotPath.copy(source, target, "player");
        assertEquals("Player1", target.getPlayer());

        // Copy nested object
        DotPath.copy(source, target, "position");
        assertEquals(10, target.getPosition().getX());
        assertEquals(20, target.getPosition().getY());

        // Copy nested object's properties directly
        DotPath.copy(source, target, "position.x");
        assertEquals(10, target.getPosition().getX());

        DotPath.copy(source, target, "position.y");
        assertEquals(20, target.getPosition().getY());

        // Copy properties from map
        DotPath.copy(source, target, "properties.level");
        assertEquals(5, target.getProperties().get("level"));

        DotPath.copy(source, target, "properties.score");
        assertEquals(1000, target.getProperties().get("score"));

        // Test type incompatibility with completely different types
        String incompatibleTarget = "This is a String, not a State";

        // Try to copy from State to String
        DotPathException exception = assertThrows(DotPathException.class, () -> {
            DotPath.copy(source, incompatibleTarget, "count");
        });

        String stateClassName = State.class.getCanonicalName();

        assertTrue(exception
                .getMessage()
                .contains(
                        "Source type %s and target type java.lang.String are incompatible".formatted(stateClassName)));

        // Try to copy from String to State
        exception = assertThrows(DotPathException.class, () -> {
            DotPath.copy(incompatibleTarget, source, "player");
        });
        assertTrue(exception
                .getMessage()
                .contains(
                        "Source type java.lang.String and target type %s are incompatible".formatted(stateClassName)));
    }

    @Test
    void testSetFromString() throws Exception {
        // Initialize State with a Point object
        State state = State.builder().position(Point.builder().build()).build();

        // Test setting primitive types from strings
        DotPath.setFromString(state, "count", "42");
        assertEquals(42, state.getCount());

        DotPath.setFromString(state, "value", "3.14");
        assertEquals(3.14, state.getValue());

        // Test setting String
        DotPath.setFromString(state, "player", "Player1");
        assertEquals("Player1", state.getPlayer());

        // Test setting nested object's properties from strings
        DotPath.setFromString(state, "position.x", "10");
        assertEquals(10, state.getPosition().getX());

        DotPath.setFromString(state, "position.y", "20");
        assertEquals(20, state.getPosition().getY());

        // Test setting properties in map
        DotPath.setFromString(state, "properties.level", "5");
        assertEquals(5, state.getProperties().get("level"));

        DotPath.setFromString(state, "properties.score", "1000");
        assertEquals(1000, state.getProperties().get("score"));
    }

    @Test
    void testMapOperations() throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put("count", 42);
        map.put("value", 3.14);
        map.put("player", "Player1");
        map.put("level", 5);
        map.put("score", 1000);
        map.put("achievements", Arrays.asList("first_blood", "headshot"));

        // Test getting from map
        Integer count = DotPath.<Integer>get(map, "count");
        assertEquals(42, count);

        Double value = DotPath.<Double>get(map, "value");
        assertEquals(3.14, value);

        String player = DotPath.<String>get(map, "player");
        assertEquals("Player1", player);

        Integer level = DotPath.<Integer>get(map, "level");
        assertEquals(5, level);

        Integer score = DotPath.<Integer>get(map, "score");
        assertEquals(1000, score);

        List<String> achievements = DotPath.<List<String>>get(map, "achievements");
        assertEquals(Arrays.asList("first_blood", "headshot"), achievements);

        // Test setting in map
        DotPath.set(map, "count", 100);
        assertEquals(100, map.get("count"));

        DotPath.set(map, "player", "Player2");
        assertEquals("Player2", map.get("player"));

        DotPath.set(map, "level", 10);
        assertEquals(10, map.get("level"));

        DotPath.set(map, "score", 2000);
        assertEquals(2000, map.get("score"));

        DotPath.set(map, "achievements", Arrays.asList("victory", "mvp"));
        assertEquals(Arrays.asList("victory", "mvp"), map.get("achievements"));

        // Test nested map operations
        Map<String, Object> nestedMap = new HashMap<>();
        nestedMap.put("inner", "value");
        map.put("nested", nestedMap);

        String innerValue = DotPath.<String>get(map, "nested.inner");
        assertEquals("value", innerValue);

        DotPath.set(map, "nested.inner", "new value");
        assertEquals("new value", ((Map<?, ?>) map.get("nested")).get("inner"));
    }

    @Test
    void testNullIntermediateObjects() throws Exception {
        // Create a State with null position
        State state = State.builder().build();

        // Setting position.x should automatically create the Point object
        DotPath.set(state, "position.x", 10);
        assertNotNull(state.getPosition());
        assertEquals(10, state.getPosition().getX());

        // Setting position.y should use the existing Point object
        DotPath.set(state, "position.y", 20);
        assertEquals(20, state.getPosition().getY());

        // Getting position.x should work with the created Point
        Integer x = DotPath.get(state, "position.x");
        assertEquals(10, x);

        // Setting from string should also work
        DotPath.setFromString(state, "position.x", "30");
        assertEquals(30, state.getPosition().getX());
    }
}
