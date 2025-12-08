package com.kata.probe.service;

import com.kata.probe.controller.request.RunRequest;
import com.kata.probe.controller.response.RunResponse;
import com.kata.probe.domain.Coordinate;
import com.kata.probe.domain.Direction;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ProbeServiceTest {

    private final ProbeService probeService = new ProbeService();

    @Test
    void countsInvalidAndBlockedCommandsInSummary() {
        RunRequest request = new RunRequest();
        request.gridWidth = 2;
        request.gridHeight = 2;
        request.start = new Coordinate(1, 1);
        request.direction = Direction.NORTH;
        request.commands = Arrays.asList("F", "X", null, "F", "B");
        request.obstacles = List.of(new Coordinate(0, 1));

        RunResponse response = probeService.run(request);

        assertEquals(1, response.executionSummary.executed);
        assertEquals(2, response.executionSummary.blocked);
        assertEquals(2, response.executionSummary.invalid);
        assertEquals(new Coordinate(1, 0), response.finalPosition);
    }

}
