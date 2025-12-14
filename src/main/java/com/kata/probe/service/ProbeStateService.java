package com.kata.probe.service;

import com.kata.probe.domain.*;
import com.kata.probe.exception.ProbeNotFoundException;
import com.kata.probe.repository.ProbeRepository;

import java.util.UUID;

public class ProbeStateService {

    private final ProbeRepository repo;

    public ProbeStateService(ProbeRepository repo) {
        this.repo = repo;
    }

    public UUID create(int width, int height, Coordinate start, Direction dir) {
        Grid grid = new Grid(width, height);
        Probe probe = new Probe(start, dir, grid);
        ProbeAggregate agg = new ProbeAggregate(grid, probe);
        return repo.save(agg);
    }

    public ProbeAggregate get(UUID id) {
        return repo.find(id).orElseThrow(() -> new ProbeNotFoundException(id));
    }
}
