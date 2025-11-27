
package com.kata.probe.api.controller;

import com.kata.probe.api.dto.*;
import com.kata.probe.domain.*;
import com.kata.probe.service.CommandInterpreter;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/probe")
public class ProbeController {


    @PostMapping("/run")
    public ResponseEntity<?> run(@Valid @RequestBody RunRequest req) {
        // 1) Direction guard -> 400 if invalid
        Direction dir;
        try {
            dir = Direction.valueOf(req.direction);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(
                    Map.of("error", Map.of("code", "VALIDATION_ERROR", "message", "Invalid direction value"))
            );
        }

        // 2) Grid + obstacles
        Grid grid = new Grid(req.grid.width, req.grid.height);
        if (req.obstacles != null) {
            req.obstacles.forEach(o -> grid.addObstacle(new Coordinate(o.x, o.y)));
        }

        // 3) Probe
        Probe probe = new Probe(new Coordinate(req.start.x, req.start.y), dir, grid);

        // 4) Execute
        CommandInterpreter.Result result = new CommandInterpreter().execute(probe, req.commands);

        // 5) Map OK response
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
