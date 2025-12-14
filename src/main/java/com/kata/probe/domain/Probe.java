package com.kata.probe.domain;

import java.util.ArrayList;
import java.util.List;

public class Probe {
    private Coordinate position;
    private Direction direction;
    private final Grid grid;
    private final ObstacleMap obstacleMap;
    private final List<Coordinate> visited = new ArrayList<>();

    public Probe(Coordinate start, Direction direction, Grid grid, ObstacleMap obstacleMap) {
        if (!grid.isWithinBounds(start)) {
            throw new IllegalArgumentException("Start position out of bounds: " + start);
        }
        this.position = start;
        this.direction = direction;
        this.grid = grid;
        this.obstacleMap = obstacleMap;
        this.visited.add(start);
    }


    private Coordinate nextForward() {
        return new Coordinate(
                position.x() + direction.dxForward(),
                position.y() + direction.dyForward()
        );
    }

    private Coordinate nextBackward() {
        return new Coordinate(
                position.x() - direction.dxForward(),
                position.y() - direction.dyForward()
        );
    }

    public boolean moveForward() {
        return applyMove(nextForward());
    }

    public boolean moveBackward() {
        return applyMove(nextBackward());
    }

    public void turnLeft()  { direction = direction.left(); }
    public void turnRight() { direction = direction.right(); }

    private boolean applyMove(Coordinate next) {
        if (!grid.isWithinBounds(next)) return false;       // blocked: out of bounds
        if (obstacleMap.hasObstacle(next)) return false;    // blocked: obstacle
        this.position = next;
        visited.add(next);
        return true;
    }


    public Coordinate getPosition() { return position; }
    public Direction getDirection() { return direction; }
    public List<Coordinate> getVisited() { return List.copyOf(visited); }
}
