package org.example.reflection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.example.reflection.model.Point;
import org.example.reflection.model.State;
import org.junit.jupiter.api.Test;

class PropertyPathUtilsTest {
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
        Integer count = PropertyPathUtils.<Integer>get(state, "count");
        assertEquals(42, count);

        Double value = PropertyPathUtils.<Double>get(state, "value");
        assertEquals(3.14, value);

        // Test getting String
        String player = PropertyPathUtils.<String>get(state, "player");
        assertEquals("Player1", player);

        // Test getting nested object
        Point pos = PropertyPathUtils.<Point>get(state, "position");
        assertEquals(10, pos.getX());
        assertEquals(20, pos.getY());

        // Test getting from properties map
        Integer level = PropertyPathUtils.<Integer>get(state, "properties.level");
        assertEquals(5, level);

        Integer score = PropertyPathUtils.<Integer>get(state, "properties.score");
        assertEquals(1000, score);

        List<String> achievements = PropertyPathUtils.<List<String>>get(state, "properties.achievements");
        assertEquals(Arrays.asList("first_blood", "headshot"), achievements);

        // Test setting primitive types
        PropertyPathUtils.set(state, "count", 100);
        assertEquals(100, state.getCount());

        PropertyPathUtils.set(state, "value", 6.28);
        assertEquals(6.28, state.getValue());

        // Test setting String
        PropertyPathUtils.set(state, "player", "Player2");
        assertEquals("Player2", state.getPlayer());

        // Test setting nested object
        Point newPos = Point.builder().x(30).y(40).build();
        PropertyPathUtils.set(state, "position", newPos);
        assertEquals(30, state.getPosition().getX());
        assertEquals(40, state.getPosition().getY());

        // Test setting properties in map
        PropertyPathUtils.set(state, "properties.level", 10);
        assertEquals(10, state.getProperties().get("level"));

        PropertyPathUtils.set(state, "properties.score", 2000);
        assertEquals(2000, state.getProperties().get("score"));

        PropertyPathUtils.set(state, "properties.achievements", Arrays.asList("victory", "mvp"));
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
        PropertyPathUtils.copy(source, target, "count");
        assertEquals(42, target.getCount());

        PropertyPathUtils.copy(source, target, "value");
        assertEquals(3.14, target.getValue());

        // Copy String
        PropertyPathUtils.copy(source, target, "player");
        assertEquals("Player1", target.getPlayer());

        // Copy nested object
        PropertyPathUtils.copy(source, target, "position");
        assertEquals(10, target.getPosition().getX());
        assertEquals(20, target.getPosition().getY());

        // Copy properties from map
        PropertyPathUtils.copy(source, target, "properties.level");
        assertEquals(5, target.getProperties().get("level"));

        PropertyPathUtils.copy(source, target, "properties.score");
        assertEquals(1000, target.getProperties().get("score"));

        // Test type incompatibility with completely different types
        String incompatibleTarget = "This is a String, not a State";

        // Try to copy from State to String
        ReflectionException exception = assertThrows(ReflectionException.class, () -> {
            PropertyPathUtils.copy(source, incompatibleTarget, "count");
        });
        assertTrue(
                exception
                        .getMessage()
                        .contains(
                                "Source type org.example.reflection.model.State and target type java.lang.String are incompatible"));

        // Try to copy from String to State
        exception = assertThrows(ReflectionException.class, () -> {
            PropertyPathUtils.copy(incompatibleTarget, source, "player");
        });
        assertTrue(
                exception
                        .getMessage()
                        .contains(
                                "Source type java.lang.String and target type org.example.reflection.model.State are incompatible"));
    }

    @Test
    void testSetFromString() throws Exception {
        State state = State.builder().build();

        // Test setting primitive types from strings
        PropertyPathUtils.setFromString(state, "count", "42");
        assertEquals(42, state.getCount());

        PropertyPathUtils.setFromString(state, "value", "3.14");
        assertEquals(3.14, state.getValue());

        // Test setting String
        PropertyPathUtils.setFromString(state, "player", "Player1");
        assertEquals("Player1", state.getPlayer());

        // Test setting properties in map
        PropertyPathUtils.setFromString(state, "properties.level", "5");
        assertEquals(5, state.getProperties().get("level"));

        PropertyPathUtils.setFromString(state, "properties.score", "1000");
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
        Integer count = PropertyPathUtils.<Integer>get(map, "count");
        assertEquals(42, count);

        Double value = PropertyPathUtils.<Double>get(map, "value");
        assertEquals(3.14, value);

        String player = PropertyPathUtils.<String>get(map, "player");
        assertEquals("Player1", player);

        Integer level = PropertyPathUtils.<Integer>get(map, "level");
        assertEquals(5, level);

        Integer score = PropertyPathUtils.<Integer>get(map, "score");
        assertEquals(1000, score);

        List<String> achievements = PropertyPathUtils.<List<String>>get(map, "achievements");
        assertEquals(Arrays.asList("first_blood", "headshot"), achievements);

        // Test setting in map
        PropertyPathUtils.set(map, "count", 100);
        assertEquals(100, map.get("count"));

        PropertyPathUtils.set(map, "player", "Player2");
        assertEquals("Player2", map.get("player"));

        PropertyPathUtils.set(map, "level", 10);
        assertEquals(10, map.get("level"));

        PropertyPathUtils.set(map, "score", 2000);
        assertEquals(2000, map.get("score"));

        PropertyPathUtils.set(map, "achievements", Arrays.asList("victory", "mvp"));
        assertEquals(Arrays.asList("victory", "mvp"), map.get("achievements"));
    }
}
