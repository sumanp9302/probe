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