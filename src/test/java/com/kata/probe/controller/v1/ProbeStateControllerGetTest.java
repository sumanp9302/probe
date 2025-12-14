package com.kata.probe.controller.v1;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kata.probe.controller.request.v1.CreateProbeRequest;
import com.kata.probe.domain.Coordinate;
import com.kata.probe.domain.Direction;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest @AutoConfigureMockMvc
class ProbeStateControllerGetTest {

    @Autowired
    MockMvc mvc;
    @Autowired ObjectMapper mapper;

    @Test
    void get_probe_state() throws Exception {

        CreateProbeRequest req = new CreateProbeRequest();
        req.gridWidth = 5;
        req.gridHeight = 5;
        req.start = new Coordinate(1,1);
        req.direction = Direction.EAST;

        var result = mvc.perform(post("/v1/probe")
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode json = mapper.readTree(result.getResponse().getContentAsString());
        String id = json.get("id").asText();

        mvc.perform(get("/v1/probe/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.position.x").value(1))
                .andExpect(jsonPath("$.position.y").value(1))
                .andExpect(jsonPath("$.direction").value("EAST"))
                .andExpect(jsonPath("$.visited.length()").value(1));
    }
}
