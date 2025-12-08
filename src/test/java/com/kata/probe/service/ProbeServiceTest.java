package com.kata.probe.service;

import com.kata.probe.controller.request.RunRequest;
import com.kata.probe.controller.response.RunResponse;
import com.kata.probe.domain.Coordinate;
import com.kata.probe.domain.Direction;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ProbeServiceTest {

    private final ProbeService probeService = new ProbeService();

    @Test
    void countsInvalidAndBlockedCommandsInSummary(){
        RunRequest request = new RunRequest();
        request.gridWidth = 3;
        request.gridHeight = 3;
        request.start = new Coordinate(1,1);
        request.commands = Arrays.asList("F", "X", null, "F", "B");
        request.obstacles = new Coordinate[]{ new Coordinate(1,2) }; // obstacle
        request.direction = Direction.NORTH;

        RunResponse response = probeService.runProbe(request);

        assertEquals(1, response.executionSummary.executed); // F blocked by obstacle, B blocked by out of bounds
        assertEquals(2, response.executionSummary.blocked);  // F blocked by obstacle, B blocked by out of bounds
        assertEquals(2, response.executionSummary.invalid);  // X and null
        assertEquals(new Coordinate(1,0), response.finalPosition);

    }

}
