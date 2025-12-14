package com.kata.probe.domain;

import com.kata.probe.controller.response.ExecutionSummary;
import java.util.ArrayList;
import java.util.List;

public class ProbeAggregate {

    private final Grid grid;
    private Probe probe;
    private ExecutionSummary summary;
    private final List<Coordinate> visited = new ArrayList<>();

    public ProbeAggregate(Grid grid, Probe probe) {
        this.grid = grid;
        this.probe = probe;
        this.summary = new ExecutionSummary(0,0,0);
        this.visited.addAll(probe.getVisited());
    }

    public Grid getGrid() { return grid; }
    public Probe getProbe() { return probe; }
    public ExecutionSummary getSummary() { return summary; }
    public List<Coordinate> getVisited() { return List.copyOf(visited); }

    public void update(Probe probe, ExecutionSummary summary) {
        this.probe = probe;
        this.summary = summary;
        visited.clear();
        visited.addAll(probe.getVisited());
    }
}
