package com.kata.probe.controller.response.v1;

import com.kata.probe.domain.*;
import com.kata.probe.controller.response.ExecutionSummary;
import java.util.List;

public class ProbeStateResponse {
    public Coordinate position;
    public Direction direction;
    public List<Coordinate> visited;
    public ExecutionSummary summary;

    public ProbeStateResponse(Coordinate p, Direction d, List<Coordinate> v, ExecutionSummary s) {
        this.position = p;
        this.direction = d;
        this.visited = v;
        this.summary = s;
    }
}
