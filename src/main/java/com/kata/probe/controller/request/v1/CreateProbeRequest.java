package com.kata.probe.controller.request.v1;

import com.kata.probe.domain.Coordinate;
import com.kata.probe.domain.Direction;

public class CreateProbeRequest {
    public int gridWidth;
    public int gridHeight;
    public Coordinate start;
    public Direction direction;
}
