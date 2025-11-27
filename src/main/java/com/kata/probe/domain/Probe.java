package com.kata.probe.domain;

import java.util.ArrayList;
import java.util.List;

public class Probe {
    private Coordinate position;
    private Direction direction;
    private final Grid grid;
    private final List<Coordinate> visited = new ArrayList<>();

    public Probe(Coordinate start, Direction direction, Grid grid) {
        if (!grid.isWithinBounds(start)) throw new IllegalArgumentException("Start out of bounds");
        if (grid.isObstacle(start)) throw new IllegalArgumentException("Start is an obstacle");
        this.position = start;
        this.direction = direction;
        this.grid = grid;
        visited.add(start);
    }

    public boolean moveForward() {
        Coordinate next = new Coordinate(
                position.x() + direction.dxForward(),
                position.y() + direction.dyForward()
        );
        return applyMove(next);
    }

    public boolean moveBackward() {
        Coordinate next = new Coordinate(
                position.x() - direction.dxForward(),
                position.y() - direction.dyForward()
        );
        return applyMove(next);
    }

    public void turnLeft()  { direction = direction.left(); }
    public void turnRight() { direction = direction.right(); }

    private boolean applyMove(Coordinate next) {
        if (!grid.isWithinBounds(next)) return false;      // blocked: out of bounds
        if (grid.isObstacle(next)) return false;           // blocked: obstacle
        this.position = next;
        visited.add(next);
        return true;
    }

    public Coordinate getPosition() { return position; }
    public Direction getDirection() { return direction; }
    public List<Coordinate> getVisited() { return List.copyOf(visited); }
}