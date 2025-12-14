package com.kata.probe.service;

import com.kata.probe.domain.*;
import com.kata.probe.exception.ProbeNotFoundException;
import com.kata.probe.repository.ProbeRepository;

import com.kata.probe.domain.commands.CommandFactory;
import com.kata.probe.domain.commands.InvalidCommand;
import com.kata.probe.controller.response.ExecutionSummary;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ProbeStateService {

    private static final Logger log = LoggerFactory.getLogger(ProbeStateService.class);

    private final ProbeRepository repo;

    public ProbeStateService(ProbeRepository repo) {
        this.repo = repo;
    }

    public UUID create(int width, int height, Coordinate start, Direction direction) {
        log.info("Creating new probe: grid={}x{}, start={}, direction={}",
                width, height, start, direction);

        Grid grid = new Grid(width, height);
        Probe probe = new Probe(start, direction, grid);
        ProbeAggregate agg = new ProbeAggregate(grid, probe);

        UUID id = repo.save(agg);

        log.debug("Probe created with ID={}", id);
        return id;
    }

    public ProbeAggregate get(UUID id) {
        log.debug("Fetching probe with ID={}", id);

        return repo.find(id)
                .orElseThrow(() -> {
                    log.warn("Probe not found: {}", id);
                    return new ProbeNotFoundException(id);
                });
    }

    public ProbeAggregate apply(UUID id, List<String> rawCommands) {
        log.info("Applying {} commands to probe ID={}", rawCommands.size(), id);

        var agg = get(id);
        Probe probe = agg.getProbe();

        int executed = 0;
        int blocked = 0;
        int invalid = 0;

        for (String raw : rawCommands) {
            var cmd = CommandFactory.from(raw);
            boolean success = cmd.execute(probe);

            if (cmd instanceof InvalidCommand) {
                invalid++;
            } else if (!success) {
                blocked++;
            } else {
                executed++;
            }
        }

        ExecutionSummary summary = new ExecutionSummary(executed, blocked, invalid);
        agg.update(probe, summary);
        repo.update(id, agg);

        log.debug("Command summary for probe ID={}: executed={}, blocked={}, invalid={}",
                id, executed, blocked, invalid);

        return agg;
    }
}
