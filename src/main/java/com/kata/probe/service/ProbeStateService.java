package com.kata.probe.service;

import com.kata.probe.controller.response.ExecutionSummary;
import com.kata.probe.domain.*;
import com.kata.probe.domain.commands.CommandFactory;
import com.kata.probe.domain.commands.InvalidCommand;
import com.kata.probe.exception.ProbeNotFoundException;
import com.kata.probe.repository.ProbeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
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

    public ProbeAggregate apply(UUID id, List<String> commands) {
        var agg = get(id);
        Probe probe = agg.getProbe();

        int executed = 0, invalid = 0, blocked = 0;

        for (String raw : commands) {
            var cmd = CommandFactory.from(raw);
            boolean success = cmd.execute(probe);

            if (cmd instanceof InvalidCommand) invalid++;
            else if (success) executed++;
            else blocked++;
        }

        ExecutionSummary summary = new ExecutionSummary(executed, blocked, invalid);
        agg.update(probe, summary);
        repo.update(id, agg);
        return agg;
    }

}
