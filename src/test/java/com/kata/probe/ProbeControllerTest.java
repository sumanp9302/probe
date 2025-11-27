package com.kata.probe;

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

    @Test void happy_path_200_ok() throws Exception {
        String body = """
          { "grid":{"width":5,"height":5},
            "start":{"x":0,"y":0}, "direction":"NORTH",
            "commands":["F","R","F"],
            "obstacles":[{"x":2,"y":1}] }
        """;
        mvc.perform(post("/api/probe/run").contentType(APPLICATION_JSON).content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.finalState.direction").value("EAST"))
                .andExpect(jsonPath("$.visited.length()").value(3));
    }

    @Test void negative_coordinates_400() throws Exception {
        String body = """
          { "grid":{"width":5,"height":5},
            "start":{"x":-1,"y":0}, "direction":"NORTH",
            "commands":["F"] }
        """;
        mvc.perform(post("/api/probe/run").contentType(APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.code").value("VALIDATION_ERROR"));
    }

    @Test void start_on_obstacle_422() throws Exception {
        String body = """
          { "grid":{"width":5,"height":5},
            "start":{"x":2,"y":1}, "direction":"NORTH",
            "commands":["F"], "obstacles":[{"x":2,"y":1}] }
        """;
        mvc.perform(post("/api/probe/run").contentType(APPLICATION_JSON).content(body))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.error.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.error.message").value("Start is an obstacle"));
    }
}
