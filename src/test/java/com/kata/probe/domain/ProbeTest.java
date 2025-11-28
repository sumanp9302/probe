package com.kata.probe.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProbeTest {
    @Test void forward_move_is_recorded() {
        Grid g = new Grid(3,3);
        Probe p = new Probe(new Coordinate(0,0), Direction.NORTH, g);
        assertTrue(p.moveForward());
        assertEquals(new Coordinate(0,1), p.getPosition());
        assertEquals(2, p.getVisited().size()); // start + after move
    }
    @Test void move_into_obstacle_is_blocked() {
        Grid g = new Grid(3,3);
        g.addObstacle(new Coordinate(0,1));
        Probe p = new Probe(new Coordinate(0,0), Direction.NORTH, g);
        assertFalse(p.moveForward());
        assertEquals(new Coordinate(0,0), p.getPosition());
        assertEquals(1, p.getVisited().size());
    }
    @Test void turn_updates_direction() {
        Probe p = new Probe(new Coordinate(1,1), Direction.NORTH, new Grid(3,3));
        p.turnLeft();  assertEquals(Direction.WEST, p.getDirection());
        p.turnRight(); assertEquals(Direction.NORTH, p.getDirection());
    }
}
