package com.kata.probe.domain;

public record Grid(int width, int height) {

    public boolean isWithinBounds(Coordinate c) {
        return c.x() >= 0 && c.x() < width &&
                c.y() >= 0 && c.y() < height;
    }
}
