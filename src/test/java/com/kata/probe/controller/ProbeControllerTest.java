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

    @Autowired MockMvc mvc;

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
