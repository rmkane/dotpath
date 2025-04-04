package org.example.reflection;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.example.reflection.model.Point;
import org.example.reflection.model.State;
import org.junit.jupiter.api.Test;

class ReflectionUtilsTest {
    @Test
    void testGenericGetAndSet() throws Exception {
        // Create a State object with all properties set using builder pattern
        Point position = Point.builder()
                .x(10)
                .y(20)
                .build();
                
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
        Integer count = ReflectionUtils.<Integer>get(state, "count");
        assertEquals(42, count);

        Double value = ReflectionUtils.<Double>get(state, "value");
        assertEquals(3.14, value);
        
        // Test getting String
        String player = ReflectionUtils.<String>get(state, "player");
        assertEquals("Player1", player);
        
        // Test getting nested object
        Point pos = ReflectionUtils.<Point>get(state, "position");
        assertEquals(10, pos.getX());
        assertEquals(20, pos.getY());
        
        // Test getting from properties map
        Integer level = ReflectionUtils.<Integer>get(state, "properties.level");
        assertEquals(5, level);
        
        Integer score = ReflectionUtils.<Integer>get(state, "properties.score");
        assertEquals(1000, score);
        
        List<String> achievements = ReflectionUtils.<List<String>>get(state, "properties.achievements");
        assertEquals(Arrays.asList("first_blood", "headshot"), achievements);

        // Test setting primitive types
        ReflectionUtils.set(state, "count", 100);
        assertEquals(100, state.getCount());

        ReflectionUtils.set(state, "value", 6.28);
        assertEquals(6.28, state.getValue());
        
        // Test setting String
        ReflectionUtils.set(state, "player", "Player2");
        assertEquals("Player2", state.getPlayer());
        
        // Test setting nested object
        Point newPos = Point.builder()
                .x(30)
                .y(40)
                .build();
        ReflectionUtils.set(state, "position", newPos);
        assertEquals(30, state.getPosition().getX());
        assertEquals(40, state.getPosition().getY());
        
        // Test setting properties in map
        ReflectionUtils.set(state, "properties.level", 10);
        assertEquals(10, state.getProperties().get("level"));
        
        ReflectionUtils.set(state, "properties.score", 2000);
        assertEquals(2000, state.getProperties().get("score"));
        
        ReflectionUtils.set(state, "properties.achievements", Arrays.asList("victory", "mvp"));
        assertEquals(Arrays.asList("victory", "mvp"), state.getProperties().get("achievements"));
    }

    @Test
    void testGenericCopy() throws Exception {
        // Create source State with all properties using builder pattern
        Point position = Point.builder()
                .x(10)
                .y(20)
                .build();
                
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
        ReflectionUtils.copy(source, target, "count");
        assertEquals(42, target.getCount());

        ReflectionUtils.copy(source, target, "value");
        assertEquals(3.14, target.getValue());
        
        // Copy String
        ReflectionUtils.copy(source, target, "player");
        assertEquals("Player1", target.getPlayer());
        
        // Copy nested object
        ReflectionUtils.copy(source, target, "position");
        assertEquals(10, target.getPosition().getX());
        assertEquals(20, target.getPosition().getY());
        
        // Copy properties from map
        ReflectionUtils.copy(source, target, "properties.level");
        assertEquals(5, target.getProperties().get("level"));
        
        ReflectionUtils.copy(source, target, "properties.score");
        assertEquals(1000, target.getProperties().get("score"));
    }

    @Test
    void testSetFromString() throws Exception {
        State state = State.builder().build();
        
        // Test setting primitive types from strings
        ReflectionUtils.setFromString(state, "count", "42");
        assertEquals(42, state.getCount());
        
        ReflectionUtils.setFromString(state, "value", "3.14");
        assertEquals(3.14, state.getValue());
        
        // Test setting String
        ReflectionUtils.setFromString(state, "player", "Player1");
        assertEquals("Player1", state.getPlayer());
        
        // Test setting properties in map
        ReflectionUtils.setFromString(state, "properties.level", "5");
        assertEquals(5, state.getProperties().get("level"));
        
        ReflectionUtils.setFromString(state, "properties.score", "1000");
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
        Integer count = ReflectionUtils.<Integer>get(map, "count");
        assertEquals(42, count);

        Double value = ReflectionUtils.<Double>get(map, "value");
        assertEquals(3.14, value);
        
        String player = ReflectionUtils.<String>get(map, "player");
        assertEquals("Player1", player);
        
        Integer level = ReflectionUtils.<Integer>get(map, "level");
        assertEquals(5, level);
        
        Integer score = ReflectionUtils.<Integer>get(map, "score");
        assertEquals(1000, score);
        
        List<String> achievements = ReflectionUtils.<List<String>>get(map, "achievements");
        assertEquals(Arrays.asList("first_blood", "headshot"), achievements);

        // Test setting in map
        ReflectionUtils.set(map, "count", 100);
        assertEquals(100, map.get("count"));
        
        ReflectionUtils.set(map, "player", "Player2");
        assertEquals("Player2", map.get("player"));
        
        ReflectionUtils.set(map, "level", 10);
        assertEquals(10, map.get("level"));
        
        ReflectionUtils.set(map, "score", 2000);
        assertEquals(2000, map.get("score"));
        
        ReflectionUtils.set(map, "achievements", Arrays.asList("victory", "mvp"));
        assertEquals(Arrays.asList("victory", "mvp"), map.get("achievements"));
    }
} 