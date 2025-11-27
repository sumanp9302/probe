# Submersible Probe Control API (KATA)

## 🚀 Overview

This project is a minimal, test-driven REST API that controls a remotely operated submersible probe on a 2D grid representing the ocean floor.  
The probe starts at a given coordinate and direction, receives a sequence of commands (`F`, `B`, `L`, `R`), must stay within grid boundaries, avoid obstacles, and finally return its full execution summary including the final state and all visited coordinates.

The implementation is intentionally clean, simple, and object-oriented, reflecting KATA-style incremental development using TDD.  
All decision-making (movement, turns, obstacle detection, boundary conditions) lives in the domain layer, while the API layer handles validation and mapping only.

---

## 📦 Running the Application

### Prerequisites
- Java 21+
- Maven 3.9+

### Start the API
```
mvn spring-boot:run
```

Service starts on:
```
http://localhost:8080
```

### Run Tests
```
mvn clean test
```

---

## 🧭 API — `/api/probe/run` (POST)

### Example Request
```json
{
  "grid": { "width": 5, "height": 5 },
  "start": { "x": 1, "y": 1 },
  "direction": "NORTH",
  "commands": ["F", "R", "F", "L", "B"],
  "obstacles": [
    { "x": 1, "y": 3 },
    { "x": 2, "y": 2 }
  ]
}
```

### Command Meanings
- **F** → Move forward
- **B** → Move backward
- **L** → Turn left
- **R** → Turn right

### Boundary & Obstacle Behavior (Important)
- Moving **outside the grid** → the move is **blocked** (probe stays in place).
- Moving **into an obstacle** → the move is **blocked**.
- No errors are thrown for blocked movement.
- Execution summary counts:
    - `executed` → successful commands
    - `blocked` → movement commands blocked by grid/obstacles
    - `invalid` → unknown commands (e.g., `"X"` or `null`)

### Example Response
```json
{
  "finalState": {
    "x": 2,
    "y": 2,
    "direction": "NORTH"
  },
  "visited": [
    { "x": 1, "y": 1 },
    { "x": 1, "y": 2 },
    { "x": 2, "y": 2 }
  ],
  "execution": {
    "executed": 4,
    "blocked": 1,
    "invalid": 0
  }
}
```

---

## 🧪 Test-Driven Development (TDD)

This project was built using the RED → GREEN → REFACTOR TDD cycle:

1. Write a failing test for a behavior
2. Implement the minimal logic to make the test pass
3. Refactor safely
4. Repeat incrementally

A full chronological TDD commit history is available in:

```
FULL_GIT_HISTORY.md
```

This contains all RED and GREEN commits demonstrating incremental implementation.

---

## 🧱 Project Structure

```
src/
 ├── main/java/com/kata/probe
 │     ├── domain/         # Probe, Grid, Direction, Coordinate
 │     ├── service/        # CommandInterpreter
 │     ├── api/            # Controller, DTOs, Exception Handling
 │
 └── test/java/com/kata/probe
       ├── domain          # Unit tests for Grid, Probe, Direction
       ├── service         # Unit tests for CommandInterpreter
       ├── api             # Controller/validation/error handling tests
```

---

## ✅ KATA Requirements Coverage

- ✔ Defined grid with coordinates
- ✔ Starting position & direction
- ✔ Forward/backward movement
- ✔ Left/right turning
- ✔ Stay within grid
- ✔ Avoid obstacles
- ✔ Return full visited coordinate history
- ✔ REST API endpoint `/api/probe/run`
- ✔ Proper validation & exception handling
- ✔ Edge-case handling (malformed JSON, invalid direction, start on obstacle)
- ✔ Clean OO design
- ✔ TDD with visible commit history

---

## 🎯 Status

This implementation fully meets the requested **KATA-level minimal design**, with clean separation of concerns, complete test coverage, and a clear TDD commit trail.
