
package com.kata.probe.controller;

import com.kata.probe.api.dto.*;
import com.kata.probe.domain.*;
import com.kata.probe.service.CommandInterpreter;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.kata.probe.exception.InvalidDirectionException;

import java.util.List;

@RestController
@RequestMapping("/api/probe")
public class ProbeController {



    @PostMapping("/run")
    public ResponseEntity<?> run(@Valid @RequestBody RunRequest req) {
        // Direction pre-validation -> throw and let GlobalExceptionHandler map to 400
        final Direction dir;
        try {
            dir = Direction.valueOf(req.direction);
        } catch (IllegalArgumentException ex) {
            throw new InvalidDirectionException("Invalid direction value");
        }

        // build grid, obstacles, probe, execute, map response (unchanged)
        Grid grid = new Grid(req.grid.width, req.grid.height);
        if (req.obstacles != null) {
            req.obstacles.forEach(o -> grid.addObstacle(new Coordinate(o.x, o.y)));
        }
        Probe probe = new Probe(new Coordinate(req.start.x, req.start.y), dir, grid);
        CommandInterpreter.Result result = new CommandInterpreter().execute(probe, req.commands);

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
