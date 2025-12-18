# Probe Kata (Spring Boot)

A Spring Boot application implementing a **stateful 2D probe navigation system** for controlling a remotely operated probe on a grid.

This project was implemented strictly according to the provided **requirements** and **client feedback**, with emphasis on:

* Clean object‑oriented design
* Test‑driven development
* Clear REST API contracts
* Extensibility via the Command pattern
* Minimal but correct state management

---

## 📚 Table of Contents

* Overview
* Architecture
* Project Structure
* Domain Model
* Command Pattern
* Stateful API
* Error Handling
* Validation Rules
* Build & Run
* Testing

---

## 🔎 Overview

The system models a probe that:

* Operates on a bounded 2D grid
* Has a position and facing direction
* Accepts movement and rotation commands
* Avoids obstacles and grid boundaries
* Tracks all visited coordinates

The probe lifecycle is **stateful**:

1. Create a probe
2. Apply commands incrementally
3. Query current probe state at any time

---

## 🧠 Architecture

Key architectural decisions:

* **Thin controllers** (no business logic)
* **Domain‑driven design**
* **Aggregate root** for state management
* **In‑memory repository** (as requested)
* **Centralized exception handling**

No persistence, security, or async processing is introduced, as those were **explicitly out of scope**.

---

## 🧱 Project Structure

```
com.kata.probe
├─ ProbeApplication.java
├─ controller
│  └─ v1
│     └─ ProbeStateController.java
├─ domain
│  ├─ Coordinate.java
│  ├─ Direction.java
│  ├─ Grid.java            # Immutable record
│  ├─ ObstacleMap.java
│  ├─ Probe.java
│  ├─ ProbeAggregate.java  # Aggregate root
│  └─ commands
│     ├─ Command.java
│     ├─ ForwardCommand.java
│     ├─ BackwardCommand.java
│     ├─ TurnLeftCommand.java
│     ├─ TurnRightCommand.java
│     ├─ InvalidCommand.java
│     └─ CommandFactory.java
├─ service
│  └─ ProbeStateService.java
├─ repository
│  └─ ProbeRepository.java
└─ exception
   ├─ ApiError.java
   ├─ ProbeNotFoundException.java
   └─ GlobalExceptionHandler.java
```

---

## 🧩 Domain Model

### Coordinate

Immutable value object representing a grid position.

### Direction

Enum representing orientation (`NORTH`, `EAST`, `SOUTH`, `WEST`) with left/right rotation logic.

### Grid

Immutable record defining grid boundaries and containment checks.

### ObstacleMap

Encapsulates obstacle positions and lookup logic.

### Probe

Encapsulates probe behavior:

* Move forward / backward
* Turn left / right
* Track visited coordinates

Movement is blocked if:

* The next position is outside the grid
* The next position contains an obstacle

### ProbeAggregate

Aggregate root holding:

* Probe instance
* Grid reference
* Last execution summary

All state changes occur through this aggregate.

---

## 🎮 Command Pattern

Movement and rotation are implemented using the **Command pattern**.

Each command encapsulates one behavior and operates on the `ProbeAggregate`.

Benefits:

* No switch/if chains
* Easy to add new commands
* Clear separation of concerns
* Improved testability

---

## 🌐 Stateful REST API

### Create Probe

`POST /v1/probe`

Creates a new probe session and returns a generated probe ID.

---

### Apply Commands

`POST /v1/probe/{id}/commands`

Applies a list of commands to an existing probe.

---

### Get Probe State

`GET /v1/probe/{id}`

Returns:

* Current position
* Direction
* Visited coordinates
* Execution summary

---

## 🛑 Error Handling

Errors are handled centrally using `GlobalExceptionHandler`.

| HTTP Status | Reason                            |
| ----------- | --------------------------------- |
| 400         | Invalid input / malformed request |
| 404         | Probe not found                   |
| 422         | Domain validation failure         |
| 500         | Unexpected server error           |

All error responses follow a consistent `ApiError` structure.

---

## ✅ Validation Rules

* Grid dimensions must be positive
* Start position must be within grid
* Start position cannot be an obstacle
* Commands must be non‑empty
* Invalid commands are handled gracefully

---

## ⚒️ Build & Run

```bash
mvn clean package
java -jar target/*.jar
```

---

## 🧪 Testing

The project includes:

* **Domain unit tests** (movement, rotation, boundaries)
* **Service tests** for state updates
* **Controller integration tests** using MockMvc
* **Error‑handling tests**

Tests reflect a **TDD‑style development approach** (RED → GREEN → REFACTOR).

---

## 📌 Notes

* Storage is intentionally in‑memory
* The design is extensible but not over‑engineered
* The implementation strictly follows the provided requirements and feedback

---

## 📜 License

MIT (educational/demo use)

---

## 🧪 Postman Collection + REST Client Snippets (ALL Scenarios)

### 📦 Postman Collection (Import as Raw JSON)

Use this collection to validate **all happy paths, validation failures, and edge cases** for the stateful probe API.

```json
{
  "info": {
    "name": "Probe Kata – Stateful API",
    "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
  },
  "variable": [
    { "key": "baseUrl", "value": "http://localhost:8080" },
    { "key": "probeId", "value": "" }
  ],
  "item": [
    {
      "name": "Create Probe",
      "request": {
        "method": "POST",
        "header": [{ "key": "Content-Type", "value": "application/json" }],
        "url": "{{baseUrl}}/v1/probe",
        "body": {
          "mode": "raw",
          "raw": "{ \"gridWidth\": 5, \"gridHeight\": 5, \"start\": { \"x\": 0, \"y\": 0 }, \"direction\": \"NORTH\" }"
        }
      },
      "event": [{
        "listen": "test",
        "script": {
          "exec": [
            "pm.collectionVariables.set('probeId', pm.response.json().id);"
          ]
        }
      }]
    },
    {
      "name": "Get Probe State",
      "request": {
        "method": "GET",
        "url": "{{baseUrl}}/v1/probe/{{probeId}}"
      }
    },
    {
      "name": "Apply Commands (Happy Path)",
      "request": {
        "method": "POST",
        "header": [{ "key": "Content-Type", "value": "application/json" }],
        "url": "{{baseUrl}}/v1/probe/{{probeId}}/commands",
        "body": {
          "mode": "raw",
          "raw": "[\"F\", \"R\", \"F\"]"
        }
      }
    },
    {
      "name": "Apply Invalid Commands",
      "request": {
        "method": "POST",
        "header": [{ "key": "Content-Type", "value": "application/json" }],
        "url": "{{baseUrl}}/v1/probe/{{probeId}}/commands",
        "body": {
          "mode": "raw",
          "raw": "[\"X\", \"\", null]"
        }
      }
    },
    {
      "name": "Probe Not Found",
      "request": {
        "method": "GET",
        "url": "{{baseUrl}}/v1/probe/ffffffff-ffff-ffff-ffff-ffffffffffff"
      }
    }
  ]
}
```

---

### 🧪 VS Code REST Client (`.http`) Snippets

```http
@baseUrl = http://localhost:8080

### Create Probe
POST {{baseUrl}}/v1/probe
Content-Type: application/json

{
  "gridWidth": 5,
  "gridHeight": 5,
  "start": { "x": 0, "y": 0 },
  "direction": "NORTH"
}

### Apply Commands
POST {{baseUrl}}/v1/probe/{{probeId}}/commands
Content-Type: application/json

["F", "R", "F"]

### Get Probe State
GET {{baseUrl}}/v1/probe/{{probeId}}

### Probe Not Found
GET {{baseUrl}}/v1/probe/ffffffff-ffff-ffff-ffff-ffffffffffff
```
