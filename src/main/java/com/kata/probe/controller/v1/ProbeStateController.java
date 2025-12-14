package com.kata.probe.controller.v1;

import com.kata.probe.controller.request.v1.ApplyCommandsRequest;
import com.kata.probe.controller.request.v1.CreateProbeRequest;
import com.kata.probe.controller.response.v1.ProbeStateResponse;
import com.kata.probe.service.ProbeStateService;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/v1/probe")
public class ProbeStateController {

    private final ProbeStateService service;

    public ProbeStateController(ProbeStateService service) {
        this.service = service;
    }

    @PostMapping
    public UUID create(@RequestBody CreateProbeRequest req) {
        return service.create(req.gridWidth, req.gridHeight, req.start, req.direction);
    }

    @GetMapping("/{id}")
    public ProbeStateResponse get(@PathVariable UUID id) {
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
            @RequestBody ApplyCommandsRequest req) {

        var agg = service.apply(id, req.commands);

        return new ProbeStateResponse(
                agg.getProbe().getPosition(),
                agg.getProbe().getDirection(),
                agg.getVisited(),
                agg.getSummary()
        );
    }

}
