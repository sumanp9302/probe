# Probe Kata (Spring Boot)

A Spring Boot application implementing a fully tested **2D probe navigation system**.  
It supports:

- A **stateless API** for one-shot command execution
- A **stateful API** for persistent probe sessions
- A clean **domain-driven** architecture
- A fully implemented **Command Pattern**
- Comprehensive **validation** and **error handling**
- A complete **TDD evolution**, built from failing tests → functionality → refactor
- Full **movement tracking**, **obstacle detection**, and **grid boundary rules**

This project demonstrates production-grade design, readability, correctness, and extensibility.

---

# 📚 Table of Contents

- Architecture & Design Philosophy
- Project Structure
- Domain Model (Deep Explanation)
- Command Pattern (Design Breakdown)
- Stateless Service (`ProbeService`)
- Stateful Service (`ProbeStateService` + Aggregate + Repository)
- HTTP API
    - Stateless Endpoint
    - Stateful Endpoints
    - Request & Response Contracts
    - Error Handling Catalog
    - Behavioral Edge Cases
- Validation Rules (Matrix)
- Examples (cURL + Postman + REST Client)
- Build & Run
- Testing
    - Domain
    - Service
    - Controller
    - Error Handling
- TDD Walkthrough (Commit-by-Commit Explanation)
- Performance Notes
- Future Enhancements
- FAQ
- License

---

# 🧠 Architecture & Design Philosophy

This project is built around several core principles:

### **1. Separation of Concerns**
- Domain logic lives in `/domain`
- Controllers contain **zero business logic**
- Services orchestrate operations
- Repository abstracts persistence
- Errors are globally normalized

### **2. Immutability**
- `Grid` is an immutable `record`
- `Coordinate` is immutable
- `ObstacleMap` encapsulates state cleanly
- Probe’s visited path is returned as an immutable copy

Immutability reduces bugs and makes reasoning easier.

### **3. Explicit, Testable Behavior**
Every rule is backed by test cases:

- Boundary checks
- Obstacle blocking
- Direction rotation
- Invalid commands
- Stateful persistence
- Request validation

### **4. Command Pattern for Movement**
Clean, extensible, open-for-extension commands:

```
Command
  ├── ForwardCommand
  ├── BackwardCommand
  ├── LeftCommand
  ├── RightCommand
  └── InvalidCommand
```

Adding new commands (e.g., "Jump", "Teleport", "Scan") requires **zero changes** to Probe or controllers.

### **5. TDD Development Workflow**
The entire project evolved through:

```
RED → GREEN → REFACTOR
```

Every commit increased correctness and confidence.

---

# 🧱 Project Structure

```
com.kata.probe
├─ ProbeApplication.java
├─ domain
│  ├─ Coordinate.java
│  ├─ Direction.java
│  ├─ Grid.java                  # Immutable Java record
│  ├─ ObstacleMap.java
│  ├─ Probe.java
│  ├─ ProbeAggregate.java        # Stateful wrapper
│  └─ commands
│     ├─ Command.java
│     ├─ ForwardCommand.java
│     ├─ BackwardCommand.java
│     ├─ LeftCommand.java
│     ├─ RightCommand.java
│     ├─ InvalidCommand.java
│     └─ CommandFactory.java
├─ controller
│  ├─ ProbeController.java       # Stateless
│  ├─ v1
│  │  └─ ProbeStateController.java
│  ├─ request
│  │  └─ RunRequest.java
│  └─ response
│     ├─ RunResponse.java
│     └─ ExecutionSummary.java
├─ repository
│  └─ ProbeRepository.java       # In-memory DB
├─ service
│  ├─ ProbeService.java          # Stateless core logic
│  └─ ProbeStateService.java     # Stateful probe lifecycle
└─ exception
   ├─ ApiError.java
   ├─ ProbeNotFoundException.java
   └─ GlobalExceptionHandler.java
```

---

# 🧩 Domain Model (Deep Explanation)

### **Coordinate**
Simple immutable value object:
```
Coordinate(x, y)
```
Used everywhere to represent positions.

---

### **Direction**
Represents orientation:

```
NORTH → EAST → SOUTH → WEST
```

Supports:

- `left()`
- `right()`
- `dxForward()`
- `dyForward()`

Examples:

| Direction | Fwd dx | Fwd dy |
|----------|--------|---------|
| NORTH    | 0      | +1      |
| SOUTH    | 0      | -1      |
| EAST     | +1     | 0       |
| WEST     | -1     | 0       |

---

### **Grid (record)**
Immutable:

```java
public record Grid(int width, int height) {
    public boolean isWithinBounds(Coordinate c) {  }
}
```

Why immutable?

- No accidental mutations
- Eliminates shared-state bugs
- Safe to reuse the same grid across stateful and stateless probes

---

### **ObstacleMap**
Encapsulates obstacles and their lookup:

- Uses `Set<Coordinate>` internally
- Prevents accidental duplicates
- Lookup is constant-time

---

### **Probe**
Holds:

- `position`
- `direction`
- `grid` reference
- `obstacleMap` reference
- `visitedPath` (grows over time)

Behavior:

- `moveForward()`
- `moveBackward()`
- `turnLeft()`
- `turnRight()`
- `getVisited()` returns immutable list

Movement is only allowed when **within bounds** and **not an obstacle**.

---

### **ProbeAggregate (Stateful Only)**

Wraps:

- `grid`
- `probe`
- `lastExecutionSummary`

This enables stateful session lifecycle:
- create → retrieve → update → persist

---

# 🎮 Command Pattern

The Probe executes commands through polymorphism.

```
         ┌─────────────────────────┐
         │        Command          │
         ├─────────────────────────┤
         │ boolean execute(probe)  │
         └─────────────────────────┘
    ┌──────────┬─────────────┬──────────────┬───────────────┐
    ▼          ▼             ▼              ▼
 Forward   Backward       Left           Right        Invalid
Command    Command       Command        Command      Command
```

### Benefits:
- No `switch-case` in ProbeService or Probe
- New commands require **no modification** to Probe
- Cleaner, more testable code

---

# 🛰️ Stateless Service Layer — ProbeService

Responsible for:

- Constructing Grid
- Loading obstacles into ObstacleMap
- Creating Probe
- Executing commands end-to-end
- Producing a `RunResponse`

Invalid/blocked/invalid commands are distinguished.

---

# 🗂️ Stateful Service Layer — ProbeStateService

Provides persistent Probe sessions:

1. **create()**  
   Builds grid, probe, aggregate, saves into repository.

2. **get()**  
   Loads existing probe state.

3. **apply()**  
   Parses commands via CommandFactory.  
   Updates Probe, recalculates summary.  
   Saves updated aggregate.

Stateful API does **not** support obstacles (client requirement).

---

# 🌐 HTTP API

## 1. Stateless Endpoint

### **POST /api/probe/run**

Executes all commands immediately.

---

## 2. Stateful Endpoints

### **POST /v1/probe**
Creates a persistent probe session.

### **GET /v1/probe/{id}**
Returns current state.

### **POST /v1/probe/{id}/commands**
Applies commands incrementally.

---

# 📄 Request & Response Contracts

All responses return:

- Final position
- Final direction
- Visited path (immutable)
- Execution summary

---

# 🛑 Error Handling Catalog

Handled centrally by `GlobalExceptionHandler`.

| Status | Code               | Meaning |
|--------|---------------------|---------|
| **400** | VALIDATION_ERROR   | malformed JSON, invalid enum, empty commands |
| **422** | VALIDATION_ERROR   | domain validation (start on obstacle, OOB) |
| **404** | NOT_FOUND          | probe ID not found |
| **500** | INTERNAL_ERROR     | unexpected failure |

Examples:

### Start on obstacle
```json
{
  "error": {
    "code": "VALIDATION_ERROR",
    "message": "Start is an obstacle"
  }
}
```

### Invalid direction
```json
{
  "error": {
    "code": "VALIDATION_ERROR",
    "message": "Malformed JSON request"
  }
}
```

---

# 🧮 Validation Rules (Matrix)

| Rule | Condition | Source |
|------|-----------|--------|
| Grid size must be > 0 | width < 1 or height < 1 | Controller validation |
| Start must be within grid | x < 0 OR x ≥ width | Domain |
| Start cannot be an obstacle | start ∈ obstacles | Domain |
| Commands cannot be empty | commands.size == 0 | Bean validation |
| Invalid commands | null, blank, or unknown symbol | CommandFactory |
| Movement blocked | out-of-bounds OR obstacle | Probe |

---

# 🧪 Example — Stateless Happy Path

See README sample above (kept concise here).

---

# ⚒️ Build & Run

```
mvn clean package
java -jar target/*.jar
```

---

# 🧪 Testing Overview

### Domain tests
- Movement
- Rotation
- Out-of-bounds
- Obstacle blocking
- Path tracking

### Service tests
- Summary calculation
- Invalid + blocked command handling

### Controller tests
- Versioned APIs
- JSON contract
- Validation
- Error handling

### Stateful tests
- Repository behavior
- Aggregate updating
- Commands applied sequentially

---

# 🧪 TDD Walkthrough (Commit-By-Commit Summary)

1. **Add failing tests** for stateful API
2. Add minimal **ProbeRepository**
3. Add **ProbeAggregate**
4. Add **ProbeStateService** (create, get)
5. Add **ProbeStateController**
6. Add **Command Pattern** classes
7. Implement `/v1/probe/{id}/commands`
8. Add **ApiError**, exceptions, and handler
9. Add **logging**
10. Convert `Grid` → **record**
11. Refactor **Probe internals**
12. Add more validation tests + fixes to satisfy all edge cases

---

# 🚀 Performance Notes

- Obstacle lookup is O(1) due to `HashSet`
- Movement operations are constant-time
- Stateful storage is in-memory (O(1) access)
- Grid record avoids mutation overhead

For large grids, system remains performant.

---

# 🔮 Future Enhancements

- Persistent storage (Redis / SQL)
- Bulk command streaming
- Undo/redo (Memento Pattern)
- Multi-probe simulation
- Diagonal movement
- Probe sensors (“scan ahead”)
- WebSocket for live movement visualization

---

# ❓ FAQ

### **Is this production-ready?**
Architecturally yes. Persistence layer would need upgrading.

### **Why use a Grid record?**
Immutability eliminates entire classes of bugs.

### **Can we add new commands?**
Yes — zero modification required to Probe or service layer.

### **Why separate obstacles into ObstacleMap?**
Better SRP: Grid handles geometry, ObstacleMap handles state.

---

# 📜 License

MIT (recommended).  
This project is for educational and demo purposes.

---

# Postman Collection + REST Client Snippets (ALL Scenarios)

---

# 🧪 POSTMAN COLLECTION (Import as Raw Text)

```json
{
  "info": {
    "name": "Probe Kata API — Full Test Suite",
    "_postman_id": "11111111-2222-3333-4444-555555555555",
    "description": "Complete Postman test suite covering stateless & stateful probe APIs.",
    "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
  },
  "variable": [
    { "key": "baseUrl", "value": "http://localhost:8080" },
    { "key": "probeId", "value": "" }
  ],
  "item": [

    {
      "name": "Stateless — Happy Path (F, R, F)",
      "request": {
        "method": "POST",
        "header": [{ "key": "Content-Type", "value": "application/json" }],
        "url": {
          "raw": "{{baseUrl}}/api/probe/run",
          "host": ["{{baseUrl}}"],
          "path": ["api", "probe", "run"]
        },
        "body": {
          "mode": "raw",
          "raw": "{\"gridWidth\": 3, \"gridHeight\": 3, \"start\": {\"x\": 0, \"y\": 0}, \"direction\": \"NORTH\", \"commands\": [\"F\",\"R\",\"F\"], \"obstacles\": []}"
        }
      }
    },

    {
      "name": "Stateless — Start on Obstacle (422)",
      "request": {
        "method": "POST",
        "header": [{ "key": "Content-Type", "value": "application/json" }],
        "url": {
          "raw": "{{baseUrl}}/api/probe/run",
          "host": ["{{baseUrl}}"],
          "path": ["api", "probe", "run"]
        },
        "body": {
          "mode": "raw",
          "raw": "{\"gridWidth\": 5, \"gridHeight\": 5, \"start\": {\"x\": 2, \"y\": 1}, \"direction\": \"NORTH\", \"commands\": [\"F\"], \"obstacles\": [{\"x\": 2, \"y\": 1}]}"
        }
      }
    },

    {
      "name": "Stateless — Invalid Direction (400)",
      "request": {
        "method": "POST",
        "header": [{ "key": "Content-Type", "value": "application/json" }],
        "url": {
          "raw": "{{baseUrl}}/api/probe/run",
          "host": ["{{baseUrl}}"],
          "path": ["api", "probe", "run"]
        },
        "body": {
          "mode": "raw",
          "raw": "{\"gridWidth\": 5, \"gridHeight\": 5, \"start\": {\"x\": 0, \"y\": 0}, \"direction\": \"NORTHEAST\", \"commands\": [\"F\"], \"obstacles\": []}"
        }
      }
    },

    {
      "name": "Stateless — Malformed JSON (400)",
      "request": {
        "method": "POST",
        "header": [{ "key": "Content-Type", "value": "application/json" }],
        "url": {
          "raw": "{{baseUrl}}/api/probe/run",
          "host": ["{{baseUrl}}"],
          "path": ["api", "probe", "run"]
        },
        "body": {
          "mode": "raw",
          "raw": "{ \"gridWidth\": 5,"
        }
      }
    },

    {
      "name": "Stateless — Invalid Commands (X, '', null, blank)",
      "request": {
        "method": "POST",
        "header": [{ "key": "Content-Type", "value": "application/json" }],
        "url": {
          "raw": "{{baseUrl}}/api/probe/run",
          "host": ["{{baseUrl}}"],
          "path": ["api", "probe", "run"]
        },
        "body": {
          "mode": "raw",
          "raw": "{\"gridWidth\": 5, \"gridHeight\": 5, \"start\": {\"x\": 2, \"y\": 2}, \"direction\": \"NORTH\", \"commands\": [\"X\", \"\", \" \", null], \"obstacles\": []}"
        }
      }
    },

    {
      "name": "Stateless — Blocked Movement by Obstacle",
      "request": {
        "method": "POST",
        "header": [{ "key": "Content-Type", "value": "application/json" }],
        "url": {
          "raw": "{{baseUrl}}/api/probe/run",
          "host": ["{{baseUrl}}"],
          "path": ["api","probe","run"]
        },
        "body": {
          "mode": "raw",
          "raw": "{\"gridWidth\":3,\"gridHeight\":3,\"start\":{\"x\":0,\"y\":0},\"direction\":\"NORTH\",\"commands\":[\"F\"],\"obstacles\":[{\"x\":0,\"y\":1}]}"
        }
      }
    },

    {
      "name": "Stateless — Blocked (Out of Bounds)",
      "request": {
        "method": "POST",
        "header": [{ "key": "Content-Type", "value": "application/json" }],
        "url": {
          "raw": "{{baseUrl}}/api/probe/run",
          "host": ["{{baseUrl}}"],
          "path": ["api","probe","run"]
        },
        "body": {
          "mode": "raw",
          "raw": "{\"gridWidth\":2,\"gridHeight\":2,\"start\":{\"x\":0,\"y\":0},\"direction\":\"SOUTH\",\"commands\":[\"F\"],\"obstacles\":[]}"
        }
      }
    },

    {
      "name": "Stateless — Mixed Commands (executed/blocked/invalid)",
      "request": {
        "method": "POST",
        "header": [{ "key": "Content-Type", "value": "application/json" }],
        "url": {
          "raw": "{{baseUrl}}/api/probe/run",
          "host": ["{{baseUrl}}"],
          "path": ["api","probe","run"]
        },
        "body": {
          "mode": "raw",
          "raw": "{\"gridWidth\":2,\"gridHeight\":2,\"start\":{\"x\":1,\"y\":1},\"direction\":\"NORTH\",\"commands\":[\"F\",\"X\",\"B\",null,\"R\"],\"obstacles\":[{\"x\":1,\"y\":2}]}"
        }
      }
    },

    {
      "name": "Stateless — Empty Commands (400)",
      "request": {
        "method": "POST",
        "header": [{ "key": "Content-Type", "value": "application/json" }],
        "url": {
          "raw": "{{baseUrl}}/api/probe/run",
          "host": ["{{baseUrl}}"],
          "path": ["api","probe","run"]
        },
        "body": {
          "mode": "raw",
          "raw": "{\"gridWidth\":5,\"gridHeight\":5,\"start\":{\"x\":0,\"y\":0},\"direction\":\"NORTH\",\"commands\":[],\"obstacles\":[]}"
        }
      }
    },

    {
      "name": "Stateful — Create Probe",
      "event": [
        {
          "listen": "test",
          "script": {
            "exec": [
              "let id = pm.response.json().id;",
              "pm.collectionVariables.set('probeId', id);",
              "console.log('Probe ID saved:', id);"
            ]
          }
        }
      ],
      "request": {
        "method": "POST",
        "header": [{ "key": "Content-Type", "value": "application/json" }],
        "url": {
          "raw": "{{baseUrl}}/v1/probe",
          "host": ["{{baseUrl}}"],
          "path": ["v1","probe"]
        },
        "body": {
          "mode": "raw",
          "raw": "{\"gridWidth\":5,\"gridHeight\":5,\"start\":{\"x\":0,\"y\":0},\"direction\":\"NORTH\"}"
        }
      }
    },

    {
      "name": "Stateful — Get Probe State",
      "request": {
        "method": "GET",
        "url": {
          "raw": "{{baseUrl}}/v1/probe/{{probeId}}",
          "host": ["{{baseUrl}}"],
          "path": ["v1","probe","{{probeId}}"]
        }
      }
    },

    {
      "name": "Stateful — Apply Commands (Happy Path)",
      "request": {
        "method": "POST",
        "header": [{ "key": "Content-Type","value":"application/json" }],
        "url": {
          "raw": "{{baseUrl}}/v1/probe/{{probeId}}/commands",
          "host": ["{{baseUrl}}"],
          "path": ["v1","probe","{{probeId}}","commands"]
        },
        "body": {
          "mode": "raw",
          "raw": "[\"F\",\"R\",\"F\"]"
        }
      }
    },

    {
      "name": "Stateful — Apply Invalid Commands",
      "request": {
        "method": "POST",
        "header": [{ "key": "Content-Type","value":"application/json" }],
        "url": {
          "raw": "{{baseUrl}}/v1/probe/{{probeId}}/commands",
          "host": ["{{baseUrl}}"],
          "path": ["v1","probe","{{probeId}}","commands"]
        },
        "body": {
          "mode": "raw",
          "raw": "[\"X\", \"\", \" \", null]"
        }
      }
    },

    {
      "name": "Stateful — Apply Blocked Commands",
      "request": {
        "method": "POST",
        "header": [{ "key": "Content-Type","value":"application/json" }],
        "url": {
          "raw": "{{baseUrl}}/v1/probe/{{probeId}}/commands",
          "host": ["{{baseUrl}}"],
          "path": ["v1","probe","{{probeId}}","commands"]
        },
        "body": {
          "mode": "raw",
          "raw": "[\"F\"]"
        }
      }
    },

    {
      "name": "Stateful — Probe Not Found (404)",
      "request": {
        "method": "GET",
        "url": {
          "raw": "{{baseUrl}}/v1/probe/ffffffff-ffff-ffff-ffff-ffffffffffff",
          "host": ["{{baseUrl}}"],
          "path": ["v1","probe","ffffffff-ffff-ffff-ffff-ffffffffffff"]
        }
      }
    }
  ]
}
```

---

# 🧪 VS CODE REST CLIENT SNIPPETS (`.http`)

```http
@baseUrl = http://localhost:8080

############################################################
# STATELESS API
############################################################

### Stateless — Happy Path
POST {{baseUrl}}/api/probe/run
Content-Type: application/json

{
  "gridWidth": 3,
  "gridHeight": 3,
  "start": { "x": 0, "y": 0 },
  "direction": "NORTH",
  "commands": ["F","R","F"],
  "obstacles": []
}

### Stateful — Create Probe
POST {{baseUrl}}/v1/probe
Content-Type: application/json

{
  "gridWidth": 5,
  "gridHeight": 5,
  "start": { "x": 0, "y": 0 },
  "direction": "NORTH"
}

### Stateful — Apply Commands
POST {{baseUrl}}/v1/probe/{{probeId}}/commands
Content-Type: application/json

["F","R","F"]

### Stateful — Get Probe
GET {{baseUrl}}/v1/probe/{{probeId}}
```

