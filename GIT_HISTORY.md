# Full Git History (Reconstructed)

> This file reconstructs the logical evolution of the project as if each step were a Git commit.
> It is meant for reviewers/interviewers when the original `.git` folder is not available.

---

### 6b0089d - chore: bootstrap Spring Boot 3 + Java 21 skeleton (pom + app)
Date: 2025-11-27  
Author: sumanp

- Create Maven project with Spring Boot 3 and Java 21.
- Add `pom.xml` with basic dependencies for web + validation + test.
- Add `SubmersibleApplication` / `ProbeApplication` main class.
- Configure basic `application.properties`.

---

### a0f4e5d - test: GridTest defines in-bounds and out-of-bounds behaviour (RED)
Date: 2025-11-27  
Author: sumanp

- Introduce `GridTest` describing:
    - Valid (x, y) inside grid.
    - Out-of-bounds coordinates.
- No production `Grid` implementation yet → tests failing (RED).

---

### a6626e9 - feat: implement Grid with bounds and obstacle support (GREEN)
Date: 2025-11-27  
Author: sumanp

- Implement `Grid` domain class with:
    - Width / height bounds.
    - Method to check if a position is inside the grid.
    - Basic obstacle list support (if applicable).
- All `GridTest` tests pass (GREEN).

---

### 9bf4d9d - test: ProbeTest describes movement, obstacle blocking, and turns (RED)
Date: 2025-11-27  
Author: sumanp

- Add `ProbeTest`:
    - Move forward in the current direction.
    - Turn left / right.
    - Stop or report when an obstacle is in front.
- No production `Probe` implementation yet → tests failing (RED).

---

### b13c7f2 - feat: implement Probe movement and orientation (GREEN)
Date: 2025-11-27  
Author: sumanp

- Implement `Probe` entity:
    - Maintain position (x, y) and `Direction` enum (N/E/S/W).
    - Apply forward movement based on direction.
    - Turn left/right logic.
    - Check grid bounds and obstacles via `Grid`.
- `ProbeTest` now passes (GREEN).

---

### c3f9a10 - refactor: tidy Probe and Grid domain model (REFACTOR)
Date: 2025-11-27  
Author: sumanp

- Extract small helper methods to improve readability.
- Make domain classes package-private where appropriate.
- No behavioural changes; all tests stay GREEN.

---

### f1a9d11 - test: CommandInterpreterTest for command sequences (RED)
Date: 2025-11-27  
Author: sumanp

- Add `CommandInterpreterTest`:
    - Interpret string commands like `MMRML`.
    - Apply them to a `Probe` and verify final position & direction.
    - Verify commands stop correctly on obstacle/invalid moves.
- No implementation yet → tests failing (RED).

---

### 2de7743 - feat: implement CommandInterpreter to drive Probe (GREEN)
Date: 2025-11-27  
Author: sumanp

- Implement `CommandInterpreter`:
    - Parse command characters (M/L/R).
    - Execute them against the `Probe`.
    - Return final `Probe` state and/or error.
- All `CommandInterpreterTest` tests pass (GREEN).

---

### 31a4bcd - test: ProbeControllerTest covers /api/probe/run happy path (RED)
Date: 2025-11-27  
Author: sumanp

- Introduce REST layer tests:
    - `POST /api/probe/run` with JSON request.
    - Verify 200 OK and correct response body for valid commands.
- No controller implementation yet → tests failing (RED).

---

### 47c2d8e - feat: ProbeController for /api/probe/run (GREEN)
Date: 2025-11-27  
Author: sumanp

- Implement `ProbeController`:
    - `@PostMapping("/api/probe/run")`.
    - Accept a request DTO describing grid size, starting position, direction, and command string.
    - Use `CommandInterpreter` + domain model to compute result.
    - Return final position & direction in a response DTO.
- `ProbeControllerTest` passes (GREEN).

---

### ad399b5 - feat: add DTOs and GlobalExceptionHandler (400/422)
Date: 2025-11-27  
Author: sumanp9302

- Introduce request/response DTOs for the REST API.
- Add `GlobalExceptionHandler` with:
    - Validation error mapping to 400 with a structured error payload.
    - Domain-specific errors (e.g., out-of-bounds, obstacle hits) mapped to 422 UNPROCESSABLE_ENTITY.
- Update controller tests accordingly.

---

### ded0170 - test: add malformed JSON controller test -> expect 400 VALIDATION_ERROR "Malformed JSON request" (RED)
Date: 2025-11-27  
Author: sumanp

- Add `ProbeControllerMalformedJsonTest`:
    - Send an invalid / malformed JSON payload to `/api/probe/run`.
    - Expect:
        - HTTP 400
        - Error code: `VALIDATION_ERROR`
        - Message: `"Malformed JSON request"`.
- No specific mapping for `HttpMessageNotReadableException` yet → test failing (RED).

---

### e4badc9 - feat(error): map HttpMessageNotReadableException to 400 with "Malformed JSON request" (GREEN)
Date: 2025-11-27  
Author: sumanp

- Update `GlobalExceptionHandler`:
    - Add handler for `HttpMessageNotReadableException`.
    - Return:
        - HTTP 400
        - Error code: `VALIDATION_ERROR`
        - Message: `"Malformed JSON request"`.
- `ProbeControllerMalformedJsonTest` now passes (GREEN).

---

### 122be0d - test: add controller test for invalid direction -> expect 400 VALIDATION_ERROR "Invalid direction value" (RED)
Date: 2025-11-27  
Author: sumanp

- Introduce `ProbeControllerInvalidDirectionTest`:
    - Pass a request with an invalid `direction` value (e.g., `"X"`).
    - Expect:
        - HTTP 400
        - Error code: `VALIDATION_ERROR`
        - Message: `"Invalid direction value"`.
- No pre-validation of direction yet → test failing (RED).

---

### d7e2427 - feat(validation): pre-validate RunRequest.direction; return 400 VALIDATION_ERROR on invalid value (GREEN)
Date: 2025-11-27  
Author: sumanp

- Add explicit validation logic for `RunRequest.direction`:
    - Reject values that don’t map to the `Direction` enum.
- Wire this into the controller/service flow so invalid direction never reaches domain logic.
- `ProbeControllerInvalidDirectionTest` now passes (GREEN).

---

### d57b98d - refactor(validation): route invalid direction to GlobalExceptionHandler; unify ResponseEntity<?> signatures (REFACTOR)
Date: 2025-11-27  
Author: sumanp

- Centralise invalid-direction handling in `GlobalExceptionHandler`:
    - Throw a custom `InvalidDirectionException` from validation layer.
    - Map it to the standard 400 `VALIDATION_ERROR` payload.
- Refactor controller methods to consistently return `ResponseEntity<?>`.
- No behavioural change; all tests remain GREEN.

---
---

### efdb3f6 - Final README.md and GIT history
Date: 2025-11-27  
Author: suman

- Added complete, polished README.md.
- Added full reconstructed Git history documentation.
- Prepped repo for final review.
- Ensured documentation reflects accurate architectural and domain explanations.

---

### 7934546 - Add Robust Request Validation & Error Handling for Malformed JSON and Invalid Direction Inputs
Date: 2025-11-28  
Author: sumanp9302

- Strengthened request validation logic.
- Enhanced global exception handling pipeline.
- Improved error responses for:
    - Malformed JSON (400 VALIDATION_ERROR)
    - Invalid direction value (400 VALIDATION_ERROR)
- Added missing test hardening and corrected edge cases.

---

### 4777300 - Final GIT history
Date: 2025-11-28  
Author: suman

- Completed polishing of FULL_GIT_HISTORY.md.
- Ensured chronological order and correctness of commit descriptions.
- Added missing details from latest commits.
- Sign-off step before merge.

---

### 763a568 - Merge remote-tracking branch 'origin/develop' into develop
Date: 2025-11-28  
Author: suman

- Merged updates from remote `develop` branch.
- Reconciled documentation changes.
- Cleanup merge ensuring no conflicts in README, tests, or history.
- Prepared repository for final stabilisation.

---
