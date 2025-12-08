package com.kata.probe.controller.response;

import com.kata.probe.domain.Coordinate;
import com.kata.probe.domain.Direction;

import java.util.List;

public class RunResponse {

    public Coordinate finalPosition;
    public Direction finalDirection;
    public List<Coordinate> visitedPath;
    public ExecutionSummary executionSummary;

    public RunResponse(Coordinate finalPosition,
                       Direction finalDirection,
                       List<Coordinate> visitedPath,
                       ExecutionSummary executionSummary) {
        this.finalPosition = finalPosition;
        this.finalDirection = finalDirection;
        this.visitedPath = visitedPath;
        this.executionSummary = executionSummary;
    }
}