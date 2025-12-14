package com.kata.probe.controller.v1;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kata.probe.controller.request.v1.CreateProbeRequest;
import com.kata.probe.domain.Coordinate;
import com.kata.probe.domain.Direction;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest @AutoConfigureMockMvc
class ProbeStateControllerCreateTest {

    @Autowired
    MockMvc mvc;
    @Autowired ObjectMapper mapper;

    @Test
    void create_probe_returns_id() throws Exception {
        CreateProbeRequest req = new CreateProbeRequest();
        req.gridWidth = 5;
        req.gridHeight = 5;
        req.start = new Coordinate(0,0);
        req.direction = Direction.NORTH;

        mvc.perform(post("/v1/probe")
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(result -> UUID.fromString(result.getResponse().getContentAsString()));
    }
}
