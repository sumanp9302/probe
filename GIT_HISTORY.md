### 6b0089d - chore: bootstrap Spring Boot 3 + Java 21 skeleton (pom + app)
Date: 2025-11-27
Author: sumanp


### a0f4e5d - test: GridTest defines in-bounds and out-of-bounds behaviour (RED)
Date: 2025-11-27
Author: sumanp


### a6626e9 - feat: implement Grid with bounds and obstacle support (GREEN)
Date: 2025-11-27
Author: sumanp


### 9bf4d9d - test: ProbeTest describes movement, obstacle blocking, and turns (RED)
Date: 2025-11-27
Author: sumanp


### 7044b79 - Git Ignore
Date: 2025-11-27
Author: sumanp


### 5d6d18d - feat: implement Direction and Probe to satisfy movement & turns (GREEN)
Date: 2025-11-27
Author: sumanp


### d75849b - Git ignore
Date: 2025-11-27
Author: sumanp


### 938bf52 - test: CommandInterpreterTest captures executed/blocked/invalid metrics (RED)
Date: 2025-11-27
Author: sumanp


### 09e006b - feat: implement CommandInterpreter with minimal metrics tracking (GREEN)
Date: 2025-11-27
Author: sumanp


### 62d96ce - test: ProbeControllerTest for 200 OK, 400 validation, and 422 domain errors (RED)
Date: 2025-11-27
Author: sumanp


### 4cfe0ab - feat: implement DTOs and ProbeController for /api/probe/run (GREEN)
Date: 2025-11-27
Author: sumanp


### 8f8db3a - feat: implement DTOs and ProbeController for /api/probe/run (GREEN)
Date: 2025-11-27
Author: sumanp


### e74f606 - feat: TDD implementation — STEP 1 (RED) Grid bounds → STEP 8 (GREEN) DTOs + Controller
Date: 2025-11-27
Author: sumanp9302


### 1bd8df7 - feat: add GlobalExceptionHandler mapping 400 (validation) and 422 (domain) (GREEN)
Date: 2025-11-27
Author: sumanp


### 31a4bcd - feat: ProbeController for /api/probe/run
Date: 2025-11-27
Author: sumanp


### ad399b5 - feat: add DTOs, ProbeController for /api/probe/run, and GlobalExceptionHandler (400/422)
Date: 2025-11-27
Author: sumanp9302


### ded0170 - test: add malformed JSON controller test -> expect 400 VALIDATION_ERROR "Malformed JSON request" (RED)
Date: 2025-11-27
Author: sumanp


### e4badc9 - feat(error): map HttpMessageNotReadableException to 400 with "Malformed JSON request" (GREEN)
Date: 2025-11-27
Author: sumanp


### 122be0d - test: add controller test for invalid direction -> expect 400 VALIDATION_ERROR "Invalid direction value" (RED)
Date: 2025-11-27
Author: sumanp


### d7e2427 - feat(validation): pre-validate RunRequest.direction; return 400 VALIDATION_ERROR on invalid value (GREEN)
Date: 2025-11-27
Author: sumanp


### d57b98d - feat(validation): route invalid direction to GlobalExceptionHandler using InvalidDirectionException (GREEN) | refactor(controller): keep controller thin by throwing InvalidDirectionException; unify method signature to ResponseEntity<?>
Date: 2025-11-27
Author: sumanp


### efdb3f6 - Final README.md and GIT history
Date: 2025-11-27
Author: suman


### 7934546 - Add Robust Request Validation & Error Handling for Malformed JSON and Invalid Direction Inputs
Date: 2025-11-28
Author: sumanp9302


### 4777300 - Final GIT history
Date: 2025-11-28
Author: suman


### 763a568 - Merge remote-tracking branch 'origin/develop' into develop
Date: 2025-11-28
Author: suman


### 857a711 - Final GIT history
Date: 2025-11-28
Author: suman


### f171adc - Merge pull request #4 from sumanp9302/develop
Date: 2025-11-28
Author: Suman Pattnaik


### 5310a56 - Package restructure
Date: 2025-11-28
Author: sumanp


### 2074c34 - Merge remote-tracking branch 'origin/develop' into develop
Date: 2025-11-28
Author: sumanp


### e28cc5e - Merge pull request #5 from sumanp9302/develop
Date: 2025-11-28
Author: Suman Pattnaik


### 7a6ed9a - target impl
Date: 2025-12-08
Author: suman


### 492b744 - Delete file
Date: 2025-12-08
Author: sumanp


### e67ff31 - Restructuring as per Microservice approach
Date: 2025-12-08
Author: sumanp


### dd880f1 - test(service): describe probe run summary for mixed commands and Refactoring of code
Date: 2025-12-08
Author: sumanp


### 7e08ba9 - feat(service): implement ProbeService and run summary models
Date: 2025-12-08
Author: sumanp


### f79feec - test(api): define /api/probe/run contract and start-on-obstacle behaviour
Date: 2025-12-08
Author: sumanp


### 8089292 - feat(api): expose /api/probe/run endpoint using ProbeService
Date: 2025-12-08
Author: sumanp


### 13a4deb - test(api): cover malformed JSON and invalid direction scenarios
Date: 2025-12-08
Author: sumanp


### cf6aa67 - feat(error): add global error handling for validation and parsing failures
Date: 2025-12-08
Author: sumanp


### 11bbb0d - README.md final changes
Date: 2025-12-08
Author: sumanp


### 45479ee - Probe Application: Complete TDD Implementation + Microservice-Oriented Refactor for Clarity and Maintainability
Date: 2025-12-08
Author: Suman Pattnaik


### 18c1650 - test: add failing tests for new stateful probe API (/v1/probe)
Date: 2025-12-14
Author: suman


### 675142e - feat: add in-memory ProbeRepository for stateful storage
Date: 2025-12-14
Author: suman


### a92487e - feat: add ProbeAggregate to encapsulate probe, grid, summary, visited path
Date: 2025-12-14
Author: suman


### 99a0ecf - feat: implement ProbeStateService with create and get operations
Date: 2025-12-14
Author: suman


### ab82839 - feat: implement stateful probe create/get endpoints in /v1/probe
Date: 2025-12-14
Author: suman


### 6f02579 - refactor: introduce command pattern and factory for probe movement
Date: 2025-12-14
Author: suman


### 21dce44 - feat: implement /v1/probe/{id}/commands endpoint using command pattern
Date: 2025-12-14
Author: suman


### 84132ae - feat: add ApiError model and ProbeNotFoundException handling
Date: 2025-12-14
Author: suman


### cfd33bd - fix: correct create response, update tests, and register missing Spring beans
Date: 2025-12-15
Author: suman


### 898f9a7 - chore: add structured logging to stateful probe controller and service
Date: 2025-12-15
Author: suman


### f0cf504 - refactor: convert Grid to immutable record and introduce ObstacleMap; fix start-on-obstacle validation
Date: 2025-12-15
Author: suman


### dc7992a - test: add ProbeService tests for invalid/blocked commands and align with RunResponse fields
Date: 2025-12-15
Author: suman


### a1dabf2 - Stateful Probe API Implementation, Command Execution, Immutable Grid Refactor & Domain Cleanup
Date: 2025-12-15
Author: Suman Pattnaik


### 5654ef5 - Final README.md
Date: 2025-12-15
Author: suman


### 51570ea - Merge remote-tracking branch 'origin/master'
Date: 2025-12-15
Author: suman


### 852b13d - Final GIT HISTORY
Date: 2025-12-15
Author: suman

