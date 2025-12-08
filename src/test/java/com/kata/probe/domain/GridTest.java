package com.kata.probe.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class GridTest {
    @Test void within_bounds() {
        Grid grid = new Grid(3, 3);
        assertTrue(grid.isWithinBounds(new Coordinate(0,0)));
        assertTrue(grid.isWithinBounds(new Coordinate(2,2)));
    }
    @Test void out_of_bounds() {
        Grid grid = new Grid(3, 3);
        assertFalse(grid.isWithinBounds(new Coordinate(-1,0)));
        assertFalse(grid.isWithinBounds(new Coordinate(3,0)));
        assertFalse(grid.isWithinBounds(new Coordinate(0,3)));
    }
}