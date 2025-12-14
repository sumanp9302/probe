package com.kata.probe.controller.v1;

import com.kata.probe.controller.request.v1.ApplyCommandsRequest;
import com.kata.probe.controller.request.v1.CreateProbeRequest;
import com.kata.probe.controller.response.v1.CreateProbeResponse;
import com.kata.probe.controller.response.v1.ProbeStateResponse;
import com.kata.probe.service.ProbeStateService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/v1/probe")
public class ProbeStateController {

    private static final Logger log = LoggerFactory.getLogger(ProbeStateController.class);

    private final ProbeStateService service;

    public ProbeStateController(ProbeStateService service) {
        this.service = service;
    }

    @PostMapping
    public CreateProbeResponse create(@RequestBody CreateProbeRequest req) {
        log.debug("Creating probe: {}", req);

        UUID id = service.create(
                req.gridWidth,
                req.gridHeight,
                req.start,
                req.direction
        );

        return new CreateProbeResponse(id);
    }

    @GetMapping("/{id}")
    public ProbeStateResponse get(@PathVariable UUID id) {
        log.debug("Fetching probe with id {}", id);

        var agg = service.get(id);

        return new ProbeStateResponse(
                agg.getProbe().getPosition(),
                agg.getProbe().getDirection(),
                agg.getVisited(),
                agg.getSummary()
        );
    }

    @PostMapping("/{id}/commands")
    public ProbeStateResponse apply(
            @PathVariable UUID id,
            @RequestBody ApplyCommandsRequest req
    ) {
        log.debug("Applying commands {} to probe {}", req.commands, id);

        var agg = service.apply(id, req.commands);

        return new ProbeStateResponse(
                agg.getProbe().getPosition(),
                agg.getProbe().getDirection(),
                agg.getVisited(),
                agg.getSummary()
        );
    }
}
