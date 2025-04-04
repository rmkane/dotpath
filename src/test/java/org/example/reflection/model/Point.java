package org.example.reflection.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@lombok.Builder
public class Point {
    private int x, y;
}
