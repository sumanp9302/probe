package com.kata.probe.domain;
import java.util.HashSet;
import java.util.Set;

public class Grid {
    private final int width;
    private final int height;
    private final Set<Coordinate> obstacles = new HashSet<>();

    public Grid(int width, int height) {
        if (width <= 0 || height <= 0) throw new IllegalArgumentException("Invalid grid size");
        this.width = width; this.height = height;
    }

    public boolean isWithinBounds(Coordinate c) {
        return c.x() >= 0 && c.x() < width && c.y() >= 0 && c.y() < height;
    }

    public boolean isObstacle(Coordinate c) { return obstacles.contains(c); }

    public void addObstacle(Coordinate c) { obstacles.add(c); }
}
