Project structure (target)

We’re aiming for:

com.kata.probe
├─ ProbeApplication.java        // (already exists)
├─ domain
│   ├─ Coordinate.java
│   ├─ Direction.java
│   ├─ Grid.java
│   └─ Probe.java
├─ controller
│   ├─ ProbeController.java
│   ├─ request
│   │   └─ RunRequest.java
│   └─ response
│       ├─ RunResponse.java
│       └─ ExecutionSummary.java
├─ service
│   └─ ProbeService.java
└─ exception
└─ GlobalExceptionHandler.java

Below is the full TDD evolution to reach the above structure.

===========================================================
PHASE 1 — DOMAIN (Grid + Probe)
===========================================================

STEP 1 — RED: create tests only (these fail initially)

src/test/java/com/kata/probe/domain/GridTest.java

package com.kata.probe.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GridTest {

    @Test
    void within_bounds() {
        Grid grid = new Grid(3, 3);
        assertTrue(grid.isWithinBounds(new Coordinate(0, 0)));
        assertTrue(grid.isWithinBounds(new Coordinate(2, 2)));
    }

    @Test
    void out_of_bounds() {
        Grid grid = new Grid(3, 3);
        assertFalse(grid.isWithinBounds(new Coordinate(-1, 0)));
        assertFalse(grid.isWithinBounds(new Coordinate(3, 0)));
        assertFalse(grid.isWithinBounds(new Coordinate(0, 3)));
    }
}


src/test/java/com/kata/probe/domain/ProbeTest.java

package com.kata.probe.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProbeTest {

    @Test
    void forward_move_is_recorded() {
        Grid grid = new Grid(3, 3);
        Probe probe = new Probe(new Coordinate(0, 0), Direction.NORTH, grid);

        assertTrue(probe.moveForward());
        assertEquals(new Coordinate(0, 1), probe.getPosition());
        assertEquals(2, probe.getVisited().size());
    }

    @Test
    void move_into_obstacle_is_blocked() {
        Grid grid = new Grid(3, 3);
        grid.addObstacle(new Coordinate(0, 1));
        Probe probe = new Probe(new Coordinate(0, 0), Direction.NORTH, grid);

        assertFalse(probe.moveForward());
        assertEquals(new Coordinate(0, 0), probe.getPosition());
        assertEquals(1, probe.getVisited().size());
    }

    @Test
    void turn_updates_direction() {
        Probe probe = new Probe(new Coordinate(1, 1), Direction.NORTH, new Grid(3, 3));

        probe.turnLeft();
        assertEquals(Direction.WEST, probe.getDirection());

        probe.turnRight();
        assertEquals(Direction.NORTH, probe.getDirection());
    }
}

Commit:
git add src/test/java/com/kata/probe/domain
git commit -m "test(domain): define grid bounds and probe movement behaviour"


STEP 2 — GREEN: implement minimal domain to satisfy tests

src/main/java/com/kata/probe/domain/Coordinate.java

package com.kata.probe.domain;

public record Coordinate(int x, int y) {}


src/main/java/com/kata/probe/domain/Direction.java

package com.kata.probe.domain;

public enum Direction {
NORTH, EAST, SOUTH, WEST;

    public Direction left() {
        return switch (this) {
            case NORTH -> WEST;
            case WEST -> SOUTH;
            case SOUTH -> EAST;
            case EAST -> NORTH;
        };
    }

    public Direction right() {
        return switch (this) {
            case NORTH -> EAST;
            case EAST -> SOUTH;
            case SOUTH -> WEST;
            case WEST -> NORTH;
        };
    }

    public int dxForward() {
        return switch (this) {
            case EAST -> 1;
            case WEST -> -1;
            default -> 0;
        };
    }

    public int dyForward() {
        return switch (this) {
            case NORTH -> 1;
            case SOUTH -> -1;
            default -> 0;
        };
    }
}


src/main/java/com/kata/probe/domain/Grid.java

package com.kata.probe.domain;

import java.util.HashSet;
import java.util.Set;

public class Grid {

    private final int width;
    private final int height;
    private final Set<Coordinate> obstacles = new HashSet<>();

    public Grid(int width, int height) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Invalid grid size");
        }
        this.width = width;
        this.height = height;
    }

    public boolean isWithinBounds(Coordinate c) {
        return c.x() >= 0 && c.x() < width
            && c.y() >= 0 && c.y() < height;
    }

    public boolean isObstacle(Coordinate c) {
        return obstacles.contains(c);
    }

    public void addObstacle(Coordinate c) {
        obstacles.add(c);
    }
}


src/main/java/com/kata/probe/domain/Probe.java

package com.kata.probe.domain;

import java.util.ArrayList;
import java.util.List;

public class Probe {

    private Coordinate position;
    private Direction direction;
    private final Grid grid;
    private final List<Coordinate> visited = new ArrayList<>();

    public Probe(Coordinate startPosition, Direction startDirection, Grid grid) {
        if (!grid.isWithinBounds(startPosition)) {
            throw new IllegalArgumentException("Start out of bounds");
        }
        if (grid.isObstacle(startPosition)) {
            throw new IllegalArgumentException("Start is an obstacle");
        }
        this.position = startPosition;
        this.direction = startDirection;
        this.grid = grid;
        this.visited.add(startPosition);
    }

    public boolean moveForward() {
        Coordinate next = new Coordinate(
            position.x() + direction.dxForward(),
            position.y() + direction.dyForward()
        );
        return applyMove(next);
    }

    public boolean moveBackward() {
        Coordinate next = new Coordinate(
            position.x() - direction.dxForward(),
            position.y() - direction.dyForward()
        );
        return applyMove(next);
    }

    public void turnLeft() {
        direction = direction.left();
    }

    public void turnRight() {
        direction = direction.right();
    }

    private boolean applyMove(Coordinate next) {
        if (!grid.isWithinBounds(next)) return false;
        if (grid.isObstacle(next)) return false;
        this.position = next;
        visited.add(next);
        return true;
    }

    public Coordinate getPosition() { return position; }
    public Direction getDirection() { return direction; }
    public List<Coordinate> getVisited() { return List.copyOf(visited); }
}

Commit:
git add src/main/java/com/kata/probe/domain
git commit -m "feat(domain): implement grid bounds, obstacles and probe movement"


===========================================================
PHASE 2 — SERVICE LAYER
===========================================================

STEP 3 — RED: service test

src/test/java/com/kata/probe/service/ProbeServiceTest.java

package com.kata.probe.service;

import com.kata.probe.controller.request.RunRequest;
import com.kata.probe.controller.response.RunResponse;
import com.kata.probe.domain.Coordinate;
import com.kata.probe.domain.Direction;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProbeServiceTest {

    private final ProbeService probeService = new ProbeService();

    @Test
    void invalid_and_blocked_commands_are_counted_in_summary() {
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

Commit:
git add src/test/java/com/kata/probe/service/ProbeServiceTest.java
git commit -m "test(service): describe probe run summary for mixed commands"


STEP 4 — GREEN: implement request, response, and service

src/main/java/com/kata/probe/controller/request/RunRequest.java

package com.kata.probe.controller.request;

import com.kata.probe.domain.Coordinate;
import com.kata.probe.domain.Direction;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class RunRequest {

    @Min(1)
    public int gridWidth;

    @Min(1)
    public int gridHeight;

    @NotNull
    public Coordinate start;

    @NotNull
    public Direction direction;

    @NotEmpty
    public List<String> commands;

    public List<Coordinate> obstacles = List.of();
}


src/main/java/com/kata/probe/controller/response/ExecutionSummary.java

package com.kata.probe.controller.response;

public class ExecutionSummary {

    public int executed;
    public int blocked;
    public int invalid;

    public ExecutionSummary() {}

    public ExecutionSummary(int executed, int blocked, int invalid) {
        this.executed = executed;
        this.blocked = blocked;
        this.invalid = invalid;
    }
}


src/main/java/com/kata/probe/controller/response/RunResponse.java

package com.kata.probe.controller.response;

import com.kata.probe.domain.Coordinate;
import com.kata.probe.domain.Direction;

import java.util.List;

public class RunResponse {

    public Coordinate finalPosition;
    public Direction finalDirection;
    public List<Coordinate> visitedPath;
    public ExecutionSummary executionSummary;

    public RunResponse(Coordinate finalPosition,
                       Direction finalDirection,
                       List<Coordinate> visitedPath,
                       ExecutionSummary executionSummary) {
        this.finalPosition = finalPosition;
        this.finalDirection = finalDirection;
        this.visitedPath = visitedPath;
        this.executionSummary = executionSummary;
    }
}


src/main/java/com/kata/probe/service/ProbeService.java

package com.kata.probe.service;

import com.kata.probe.controller.request.RunRequest;
import com.kata.probe.controller.response.ExecutionSummary;
import com.kata.probe.controller.response.RunResponse;
import com.kata.probe.domain.Coordinate;
import com.kata.probe.domain.Direction;
import com.kata.probe.domain.Grid;
import com.kata.probe.domain.Probe;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProbeService {

    public RunResponse run(RunRequest request) {
        Grid grid = new Grid(request.gridWidth, request.gridHeight);

        for (Coordinate obstacle : request.obstacles) {
            grid.addObstacle(obstacle);
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
            if (raw == null) { invalid++; continue; }

            String cmd = raw.trim().toUpperCase();

            switch (cmd) {
                case "L" -> { probe.turnLeft(); executed++; }
                case "R" -> { probe.turnRight(); executed++; }
                case "F" -> { if (probe.moveForward()) executed++; else blocked++; }
                case "B" -> { if (probe.moveBackward()) executed++; else blocked++; }
                default -> invalid++;
            }
        }

        return new ExecutionSummary(executed, blocked, invalid);
    }
}

Commit:
git add src/main/java/com/kata/probe/controller/request \
src/main/java/com/kata/probe/controller/response \
src/main/java/com/kata/probe/service
git commit -m "feat(service): implement ProbeService and run summary models"


===========================================================
PHASE 3 — HTTP API (Controller + Error Handling)
===========================================================

STEP 5 — RED: controller tests

src/test/java/com/kata/probe/controller/ProbeControllerTest.java

package com.kata.probe.controller;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
class ProbeControllerTest {

    @Autowired
    MockMvc mvc;

    @Test
    void basic_run_returns_200_with_final_state_and_summary() throws Exception {
        String body = """
          {
            "gridWidth": 3,
            "gridHeight": 3,
            "start": { "x": 0, "y": 0 },
            "direction": "NORTH",
            "commands": ["F", "R", "F"],
            "obstacles": []
          }
        """;

        mvc.perform(post("/api/probe/run")
                        .contentType(APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.finalPosition.x").value(1))
                .andExpect(jsonPath("$.finalPosition.y").value(1))
                .andExpect(jsonPath("$.finalDirection").value("EAST"))
                .andExpect(jsonPath("$.visitedPath.length()").value(3))
                .andExpect(jsonPath("$.executionSummary.executed").value(3));
    }

    @Test
    void start_on_obstacle_returns_422_with_validation_error() throws Exception {
        String body = """
          {
            "gridWidth": 5,
            "gridHeight": 5,
            "start": { "x": 2, "y": 1 },
            "direction": "NORTH",
            "commands": ["F"],
            "obstacles": [
              { "x": 2, "y": 1 }
            ]
          }
        """;

        mvc.perform(post("/api/probe/run")
                        .contentType(APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.error.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.error.message").value("Start is an obstacle"));
    }
}

Commit:
git add src/test/java/com/kata/probe/controller/ProbeControllerTest.java
git commit -m "test(api): define /api/probe/run contract and start-on-obstacle behaviour"


STEP 6 — GREEN (happy path only): controller implementation

src/main/java/com/kata/probe/controller/ProbeController.java

package com.kata.probe.controller;

import com.kata.probe.controller.request.RunRequest;
import com.kata.probe.controller.response.RunResponse;
import com.kata.probe.service.ProbeService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/probe")
public class ProbeController {

    private final ProbeService probeService;

    public ProbeController(ProbeService probeService) {
        this.probeService = probeService;
    }

    @PostMapping("/run")
    public ResponseEntity<RunResponse> run(@Valid @RequestBody RunRequest request) {
        return ResponseEntity.ok(probeService.run(request));
    }
}

Commit:
git add src/main/java/com/kata/probe/controller/ProbeController.java
git commit -m "feat(api): expose /api/probe/run endpoint using ProbeService"


STEP 7 — RED: malformed JSON & invalid direction tests

src/test/java/com/kata/probe/controller/ProbeControllerMalformedJsonTest.java

package com.kata.probe.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
class ProbeControllerMalformedJsonTest {

    @Autowired
    MockMvc mvc;

    @Test
    @DisplayName("malformed JSON yields 400 VALIDATION_ERROR with 'Malformed JSON request'")
    void malformed_json_returns_400() throws Exception {
        String malformed = """
          {
            "gridWidth": 5,
            "gridHeight": 5,
            "start": { "x": 0, "y": 0 },
            "direction": "NORTH",
            "commands": ["F", "R", "F"],
            "obstacles": [{ "x": 2, "y": 1 }]
        """;

        mvc.perform(post("/api/probe/run")
                        .contentType(APPLICATION_JSON)
                        .content(malformed))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.error.message").value("Malformed JSON request"));
    }
}


src/test/java/com/kata/probe/controller/ProbeControllerInvalidDirectionTest.java

package com.kata.probe.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
class ProbeControllerInvalidDirectionTest {

    @Autowired
    MockMvc mvc;

    @Test
    @DisplayName("invalid direction yields 400 VALIDATION_ERROR with 'Malformed JSON request'")
    void invalid_direction_returns_400() throws Exception {
        String body = """
          {
            "gridWidth": 5,
            "gridHeight": 5,
            "start": { "x": 0, "y": 0 },
            "direction": "NORTHEAST",
            "commands": ["F"],
            "obstacles": []
          }
        """;

        mvc.perform(post("/api/probe/run")
                        .contentType(APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.error.message").value("Malformed JSON request"));
    }
}

Commit:
git add src/test/java/com/kata/probe/controller/ProbeControllerMalformedJsonTest.java \
src/test/java/com/kata/probe/controller/ProbeControllerInvalidDirectionTest.java
git commit -m "test(api): cover malformed JSON and invalid direction scenarios"


STEP 8 — GREEN: global error handling

src/main/java/com/kata/probe/exception/GlobalExceptionHandler.java

package com.kata.probe.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        return ResponseEntity.badRequest().body(
                Map.of("error",
                        Map.of(
                                "code", "VALIDATION_ERROR",
                                "message", "Request validation failed"
                        )
                )
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleMalformedJson(HttpMessageNotReadableException ex) {
        return ResponseEntity.badRequest().body(
                Map.of("error",
                        Map.of(
                                "code", "VALIDATION_ERROR",
                                "message", "Malformed JSON request"
                        )
                )
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.unprocessableEntity().body(
                Map.of("error",
                        Map.of(
                                "code", "VALIDATION_ERROR",
                                "message", ex.getMessage()
                        )
                )
        );
    }
}

Commit:
git add src/main/java/com/kata/probe/exception/GlobalExceptionHandler.java
git commit -m "feat(error): add global error handling for validation and parsing failures"


STEP 9 — (Optional) REFACTOR

Clean small things, ensure all tests pass.

Commit:
git add src/main/java src/test/java
git commit -m "refactor: minor cleanups with all tests passing"


===========================================================
YES — THIS IS TRUE TDD
===========================================================

Because at every stage you followed:

1. Write failing test (RED)
2. Implement minimum code (GREEN)
3. Refactor cleanly while tests stay green

This final content is now in **one single copy-paste block** as you required.

