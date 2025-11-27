package com.kata.probe.api.controller;

import com.kata.probe.api.dto.*;
import com.kata.probe.domain.*;
import com.kata.probe.service.CommandInterpreter;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/probe")
public class ProbeController {

    @PostMapping("/run")
    public ResponseEntity<RunResponse> run(@Valid @RequestBody RunRequest req) {
        // Build grid
        Grid grid = new Grid(req.grid.width, req.grid.height);

        // Add obstacles
        if (req.obstacles != null) {
            req.obstacles.forEach(o -> grid.addObstacle(new Coordinate(o.x, o.y)));
        }

        // Build probe
        Direction dir = Direction.valueOf(req.direction);
        Probe probe = new Probe(new Coordinate(req.start.x, req.start.y), dir, grid);

        // Execute commands
        CommandInterpreter.Result result = new CommandInterpreter().execute(probe, req.commands);

        // Map response
        FinalStateDto fs = toFinalStateDto(probe);
        List<CoordinateDto> visited = toVisited(probe.getVisited());
        ExecutionDto ex = new ExecutionDto(result.executed, result.blocked, result.invalid);

        return ResponseEntity.ok(new RunResponse(fs, visited, ex));
    }

    private FinalStateDto toFinalStateDto(Probe probe) {
        return new FinalStateDto(
                probe.getPosition().x(),
                probe.getPosition().y(),
                probe.getDirection().name()
        );
    }

    private List<CoordinateDto> toVisited(List<Coordinate> path) {
        return path.stream()
                .map(c -> {
                    CoordinateDto d = new CoordinateDto();
                    d.x = c.x();
                    d.y = c.y();
                    return d;
                })
                .toList();
    }
}
