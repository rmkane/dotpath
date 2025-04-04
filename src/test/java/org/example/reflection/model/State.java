package org.example.reflection.model;

import java.util.HashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class State {
    private String player;
    private Point position;
    private int count;
    private double value;

    @Builder.Default
    private Map<String, Object> properties = new HashMap<>();
}
