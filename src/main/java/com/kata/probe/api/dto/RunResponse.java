package com.kata.probe.api.dto;

import java.util.List;

public class RunResponse {
    public FinalStateDto finalState;
    public List<CoordinateDto> visited;
    public ExecutionDto execution;

    public RunResponse(FinalStateDto fs, List<CoordinateDto> visited, ExecutionDto ex) {
        this.finalState = fs;
        this.visited = visited;
        this.execution = ex;
    }
}