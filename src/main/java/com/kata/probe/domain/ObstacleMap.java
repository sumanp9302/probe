package com.kata.probe.domain;

import java.util.HashSet;
import java.util.Set;

public class ObstacleMap {

    private final Set<Coordinate> obstacles = new HashSet<>();

    public void addObstacle(Coordinate coordinate) {
        obstacles.add(coordinate);
    }

    public boolean hasObstacle(Coordinate coordinate) {
        return obstacles.contains(coordinate);
    }
}
