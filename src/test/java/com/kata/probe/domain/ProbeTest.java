package com.kata.probe.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProbeTest {
    @Test void forward_move_is_recorded() {
        Grid grid = new Grid(3,3);
        Probe probe = new Probe(new Coordinate(0,0), Direction.NORTH, grid);
        assertTrue(probe.moveForward());
        assertEquals(new Coordinate(0,1), probe.getPosition());
        assertEquals(2, probe.getVisited().size()); // start + after move
    }
    @Test void move_into_obstacle_is_blocked() {
        Grid grid = new Grid(3,3);
        grid.addObstacle(new Coordinate(0,1));
        Probe probe = new Probe(new Coordinate(0,0), Direction.NORTH, grid);
        assertFalse(probe.moveForward());
        assertEquals(new Coordinate(0,0), probe.getPosition());
        assertEquals(1, probe.getVisited().size());
    }
    @Test void turn_updates_direction() {
        Probe probe = new Probe(new Coordinate(1,1), Direction.NORTH, new Grid(3,3));
        probe.turnLeft();  assertEquals(Direction.WEST, probe.getDirection());
        probe.turnRight(); assertEquals(Direction.NORTH, probe.getDirection());
    }
}
