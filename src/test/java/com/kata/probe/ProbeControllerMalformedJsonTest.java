
package com.kata.probe;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * TDD (RED): malformed JSON must be handled and return 400 with a unified error payload.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
class ProbeControllerMalformedJsonTest {

    @Autowired MockMvc mvc;

    @Test
    @DisplayName("Malformed JSON → 400 VALIDATION_ERROR: 'Malformed JSON request'")
    void malformed_json_400() throws Exception {
        // Broken JSON payload: missing closing brace and invalid token
        String malformed = """
          { "grid": { "width": 5, "height": 5 },
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
