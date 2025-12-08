
# Probe Kata (Spring Boot)

A small Spring Boot service that simulates a **probe** moving on a 2D grid with optional **obstacles**.  
It exposes a single HTTP endpoint to execute movement commands and returns the final state, the visited path, and an execution summary.

---

## Table of Contents

- Architecture & Project Structure
- Domain Model
- Service Layer
- HTTP API
  - Endpoint
  - Request
  - Response
  - Error Handling
  - Examples
- Build & Run
- Testing
- TDD Walkthrough
- Notes & Constraints
- License

---

## Architecture & Project Structure

```
com.kata.probe
├─ ProbeApplication.java
├─ domain
│  ├─ Coordinate.java
│  ├─ Direction.java
│  ├─ Grid.java
│  └─ Probe.java
├─ controller
│  ├─ ProbeController.java
│  ├─ request
│  │  └─ RunRequest.java
│  └─ response
│     ├─ RunResponse.java
│     └─ ExecutionSummary.java
├─ service
│  └─ ProbeService.java
└─ exception
   └─ GlobalExceptionHandler.java
```

This layout separates **domain** (pure logic) from the **service** orchestration and the **controller** (HTTP boundary), with a dedicated **global exception handler** for consistent API errors.

---

## Domain Model

- **Coordinate**: `record Coordinate(int x, int y)`
- **Direction**: `NORTH`, `EAST`, `SOUTH`, `WEST` with helpers:
  - `left()`, `right()` to rotate
  - `dxForward()`, `dyForward()` to compute deltas for forward/backward moves
- **Grid**: width/height (must be > 0), maintains a set of **obstacles** and validates **bounds**.
- **Probe**: tracks `position`, `direction`, connected `grid`, and `visited` coordinates.
  - `moveForward()`, `moveBackward()` apply movement if **within bounds** and **not an obstacle**
  - `turnLeft()`, `turnRight()` rotate direction
  - `getVisited()` returns an immutable copy of the path (including start).

---

## Service Layer

**ProbeService** builds the `Grid`, loads obstacles, constructs the `Probe`, executes commands, and assembles the `RunResponse`.  
It counts **executed**, **blocked**, and **invalid** commands via `ExecutionSummary`.  
Supported commands:
- `F` (forward), `B` (backward) — counted as **blocked** if the move would exit bounds or hit an obstacle
- `L` (turn left), `R` (turn right) — always succeed
- Any other/`null`/whitespace → **invalid**.

---

## HTTP API

### Endpoint

`POST /api/probe/run` — Execute a batch of commands on a grid.

### Request

```json
{
  "gridWidth": 5,
  "gridHeight": 5,
  "start": { "x": 0, "y": 0 },
  "direction": "NORTH",
  "commands": ["F", "R", "F", "B", "L"],
  "obstacles": [{ "x": 2, "y": 1 }]
}
```

- `gridWidth`, `gridHeight`: integers ≥ 1
- `start`: starting coordinate (must be **within bounds** and **not an obstacle**)
- `direction`: one of `NORTH|EAST|SOUTH|WEST`
- `commands`: **non-empty** list of strings
- `obstacles`: optional list of coordinates (default empty)

### Response

```json
{
  "finalPosition": { "x": 1, "y": 1 },
  "finalDirection": "EAST",
  "visitedPath": [
    { "x": 0, "y": 0 },
    { "x": 0, "y": 1 },
    { "x": 1, "y": 1 }
  ],
  "executionSummary": {
    "executed": 3,
    "blocked": 0,
    "invalid": 0
  }
}
```

Contains the end state, the full path (including start), and a counts summary.  
The sample above corresponds to commands `["F","R","F"]` on a 3×3 grid starting at (0,0) facing `NORTH`.

### Error Handling

Errors are normalized by **GlobalExceptionHandler**:
- **400 Bad Request** → `VALIDATION_ERROR` with message:
  - `"Malformed JSON request"` when the JSON cannot be parsed (including invalid enum value like `"NORTHEAST"` for `direction`).
  - `"Request validation failed"` when bean validation fails (e.g., empty `commands`).
- **422 Unprocessable Entity** → `VALIDATION_ERROR` with the underlying message (e.g., `"Start is an obstacle"` or `"Start out of bounds"`).

---

## Examples

### Happy Path

```bash
curl -s -X POST http://localhost:8080/api/probe/run \
  -H 'Content-Type: application/json' \
  -d '{
    "gridWidth": 3,
    "gridHeight": 3,
    "start": { "x": 0, "y": 0 },
    "direction": "NORTH",
    "commands": ["F", "R", "F"],
    "obstacles": []
  }'
```

Expected highlights:
- `finalPosition`: `{ "x": 1, "y": 1 }`
- `finalDirection`: `"EAST"`
- `visitedPath.length`: `3`
- `executionSummary.executed`: `3`

### Start on Obstacle

```bash
curl -s -X POST http://localhost:8080/api/probe/run \
  -H 'Content-Type: application/json' \
  -d '{
    "gridWidth": 5,
    "gridHeight": 5,
    "start": { "x": 2, "y": 1 },
    "direction": "NORTH",
    "commands": ["F"],
    "obstacles": [{ "x": 2, "y": 1 }]
  }'
```

Returns **422** with:
```json
{
  "error": {
    "code": "VALIDATION_ERROR",
    "message": "Start is an obstacle"
  }
}
```

### Malformed JSON / Invalid Direction

- Malformed JSON body → **400** with `"Malformed JSON request"`
- `"direction": "NORTHEAST"` → **400** with `"Malformed JSON request"` (enum parse failure)

---

## Build & Run

> Requires Java 17+ and Maven (or Gradle, if configured).

```bash
# from project root
mvn clean package
java -jar target/*.jar
# Service starts on http://localhost:8080
```

Spring Boot autoconfigures the web layer; no additional setup is needed.

---

## Testing

JUnit tests cover domain logic, service summary accounting, controller contract, and error paths.

```bash
mvn test
```

Coverage includes:
- **Domain**: bounds, obstacles, movement & rotation behavior
- **Service**: executed/blocked/invalid command counting and final state
- **Controller**: `/api/probe/run` JSON contract & happy path
- **Error handling**: malformed JSON, invalid `direction`, start-on-obstacle scenarios

---

## TDD Walkthrough

The project was built via incremental **TDD** phases:

1. **Phase 1 — Domain (Grid + Probe)**
  - RED: tests for grid bounds, obstacles, and probe movement
  - GREEN: minimal domain implementation to satisfy tests

2. **Phase 2 — Service Layer**
  - RED: test describing execution summary of mixed commands
  - GREEN: implement `RunRequest`, `RunResponse`, `ExecutionSummary`, and `ProbeService`

3. **Phase 3 — HTTP API (Controller + Error Handling)**
  - RED: controller tests for happy path and start-on-obstacle
  - GREEN: controller implementation
  - RED: malformed JSON & invalid direction tests
  - GREEN: global exception handler to unify errors
  - Optional refactor and cleanup with all tests passing

---

## Notes & Constraints

- Grid size must be **positive** (`width > 0`, `height > 0`).
- Start must be **within bounds** and **not** an obstacle.
- `visitedPath` includes the **start** coordinate and every successful move.
- Invalid commands don’t change state and are counted in the summary.
- Blocked moves (out-of-bounds/into obstacle) don’t change state but are counted as **blocked**.

---

## License

This kata-style project is intended for learning and experimentation.  
Choose a license appropriate for your use (e.g., MIT). *(Placeholder — add your actual license file.)*

---

# Postman Collection

> Import the JSON into Postman (**File → Import → Raw Text**) and set the `baseUrl` variable (defaults to `http://localhost:8080`).

```json
{
  "info": {
    "name": "Probe Kata API",
    "_postman_id": "c6fb7da2-7f8e-4c5a-9c8d-000000000001",
    "description": "Postman collection to exercise the /api/probe/run endpoint.",
    "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
  },
  "item": [
    {
      "name": "Happy Path — 3x3, NORTH, F R F",
      "request": {
        "method": "POST",
        "header": [
          { "key": "Content-Type", "value": "application/json" }
        ],
        "url": { "raw": "{{baseUrl}}/api/probe/run", "host": ["{{baseUrl}}"], "path": ["api", "probe", "run"] },
        "body": {
          "mode": "raw",
          "raw": "{\"gridWidth\": 3, \"gridHeight\": 3, \"start\": {\"x\": 0, \"y\": 0}, \"direction\": \"NORTH\", \"commands\": [\"F\",\"R\",\"F\"], \"obstacles\": []}"
        }
      },
      "event": [
        {
          "listen": "test",
          "script": {
            "type": "text/javascript",
            "exec": [
              "pm.test('Status is 200', function () { pm.response.to.have.status(200); });",
              "pm.test('Executed == 3', function () { pm.expect(pm.response.json().executionSummary.executed).to.eql(3); });",
              "pm.test('FinalDirection == EAST', function () { pm.expect(pm.response.json().finalDirection).to.eql('EAST'); });"
            ]
          }
        }
      ]
    },
    {
      "name": "Start on Obstacle — 5x5, 422",
      "request": {
        "method": "POST",
        "header": [
          { "key": "Content-Type", "value": "application/json" }
        ],
        "url": { "raw": "{{baseUrl}}/api/probe/run", "host": ["{{baseUrl}}"], "path": ["api", "probe", "run"] },
        "body": {
          "mode": "raw",
          "raw": "{\"gridWidth\": 5, \"gridHeight\": 5, \"start\": {\"x\": 2, \"y\": 1}, \"direction\": \"NORTH\", \"commands\": [\"F\"], \"obstacles\": [{\"x\": 2, \"y\": 1}]}"
        }
      },
      "event": [
        {
          "listen": "test",
          "script": {
            "type": "text/javascript",
            "exec": [
              "pm.test('Status is 422', function () { pm.response.to.have.status(422); });",
              "pm.test('Error code VALIDATION_ERROR', function () { pm.expect(pm.response.json().error.code).to.eql('VALIDATION_ERROR'); });",
              "pm.test('Message is Start is an obstacle', function () { pm.expect(pm.response.json().error.message).to.eql('Start is an obstacle'); });"
            ]
          }
        }
      ]
    },
    {
      "name": "Invalid Direction — 400",
      "request": {
        "method": "POST",
        "header": [
          { "key": "Content-Type", "value": "application/json" }
        ],
        "url": { "raw": "{{baseUrl}}/api/probe/run", "host": ["{{baseUrl}}"], "path": ["api", "probe", "run"] },
        "body": {
          "mode": "raw",
          "raw": "{\"gridWidth\": 5, \"gridHeight\": 5, \"start\": {\"x\": 0, \"y\": 0}, \"direction\": \"NORTHEAST\", \"commands\": [\"F\"], \"obstacles\": []}"
        }
      },
      "event": [
        {
          "listen": "test",
          "script": {
            "type": "text/javascript",
            "exec": [
              "pm.test('Status is 400', function () { pm.response.to.have.status(400); });",
              "pm.test('Error code VALIDATION_ERROR', function () { pm.expect(pm.response.json().error.code).to.eql('VALIDATION_ERROR'); });",
              "pm.test('Message is Malformed JSON request', function () { pm.expect(pm.response.json().error.message).to.eql('Malformed JSON request'); });"
            ]
          }
        }
      ]
    },
    {
      "name": "Malformed JSON — 400",
      "request": {
        "method": "POST",
        "header": [
          { "key": "Content-Type", "value": "application/json" }
        ],
        "url": { "raw": "{{baseUrl}}/api/probe/run", "host": ["{{baseUrl}}"], "path": ["api", "probe", "run"] },
        "body": {
          "mode": "raw",
          "raw": "{\n  \"gridWidth\": 5,\n  \"gridHeight\": 5,\n  \"start\": { \"x\": 0, \"y\": 0 },\n  \"direction\": \"NORTH\",\n  \"commands\": [\"F\", \"R\", \"F\"],\n  \"obstacles\": [{ \"x\": 2, \"y\": 1 }]\n"
        }
      },
      "event": [
        {
          "listen": "test",
          "script": {
            "type": "text/javascript",
            "exec": [
              "pm.test('Status is 400', function () { pm.response.to.have.status(400); });",
              "pm.test('Error code VALIDATION_ERROR', function () { pm.expect(pm.response.json().error.code).to.eql('VALIDATION_ERROR'); });",
              "pm.test('Message is Malformed JSON request', function () { pm.expect(pm.response.json().error.message).to.eql('Malformed JSON request'); });"
            ]
          }
        }
      ]
    }
  ],
  "event": [],
  "variable": [
    { "key": "baseUrl", "value": "http://localhost:8080" }
  ]
}
```

---

# REST Client Snippet (`.http` for VS Code)

> Save as `probe-kata.http` and execute requests directly in VS Code (extension: `humao.rest-client`).

```http
### Probe Kata API — REST Client Snippets
# Use with VS Code REST Client extension (humao.rest-client)

@baseUrl = http://localhost:8080

### Happy Path — 3x3, NORTH, F R F
POST {{baseUrl}}/api/probe/run
Content-Type: application/json

{
  "gridWidth": 3,
  "gridHeight": 3,
  "start": { "x": 0, "y": 0 },
  "direction": "NORTH",
  "commands": ["F", "R", "F"],
  "obstacles": []
}

### Start on Obstacle — 5x5, 422
POST {{baseUrl}}/api/probe/run
Content-Type: application/json

{
  "gridWidth": 5,
  "gridHeight": 5,
  "start": { "x": 2, "y": 1 },
  "direction": "NORTH",
  "commands": ["F"],
  "obstacles": [{ "x": 2, "y": 1 }]
}

### Invalid Direction — 400
POST {{baseUrl}}/api/probe/run
Content-Type: application/json

{
  "gridWidth": 5,
  "gridHeight": 5,
  "start": { "x": 0, "y": 0 },
  "direction": "NORTHEAST",
  "commands": ["F"],
  "obstacles": []
}

### Malformed JSON — 400 (missing closing brace)
POST {{baseUrl}}/api/probe/run
Content-Type: application/json

{
  "gridWidth": 5,
  "gridHeight": 5,
  "start": { "x": 0, "y": 0 },
  "direction": "NORTH",
  "commands": ["F", "R", "F"],
