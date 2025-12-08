package com.kata.probe.service;

import com.kata.probe.controller.request.RunRequest;
import com.kata.probe.controller.response.ExecutionSummary;
import com.kata.probe.controller.response.RunResponse;
import com.kata.probe.domain.Coordinate;
import com.kata.probe.domain.Grid;
import com.kata.probe.domain.Probe;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProbeService {

    public RunResponse run(RunRequest request){
        Grid grid = new Grid(request.gridWidth, request.gridHeight);

        for(Coordinate obstacles : request.obstacles){
            grid.addObstacle(obstacles);
        }

        Probe probe = new Probe(request.start, request.direction, grid);

        ExecutionSummary summary = executeCommands(probe, request.commands);
        return new RunResponse(
                probe.getPosition(),
                probe.getDirection(),
                probe.getVisited(),
                summary
        );

    }

    private ExecutionSummary executeCommands(Probe probe, List<String> commands) {
        int executed = 0, blocked = 0, invalid = 0;

        for (String raw : commands) {
            if (raw == null) {
                invalid++;
                continue;
            }

            String cmd = raw.trim().toUpperCase();

            switch (cmd) {
                case "L" -> {
                    probe.turnLeft();
                    executed++;
                }
                case "R" -> {
                    probe.turnRight();
                    executed++;
                }
                case "F" -> {
                    if (probe.moveForward()) executed++;
                    else blocked++;
                }
                case "B" -> {
                    if (probe.moveBackward()) executed++;
                    else blocked++;
                }
                default -> invalid++;
            }
        }
        return new ExecutionSummary(executed, blocked, invalid);
    }
}
