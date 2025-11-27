
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
 * TDD (RED): invalid direction value must yield 400 with a unified error payload.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
class ProbeControllerInvalidDirectionTest {

    @Autowired MockMvc mvc;

    @Test
    @DisplayName("Invalid direction -> 400 VALIDATION_ERROR: 'Invalid direction value'")
    void invalid_direction_400() throws Exception {
        String body = """
          {
            "grid": { "width": 5, "height": 5 },
            "start": { "x": 0, "y": 0 },
            "direction": "NORTHEAST",
            "commands": ["F"]
          }
        """;

        mvc.perform(post("/api/probe/run")
                        .contentType(APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.error.message").value("Invalid direction value"));
    }
}
