package com.kata.probe.controller;

import com.kata.probe.controller.request.RunRequest;
import com.kata.probe.controller.response.RunResponse;
import com.kata.probe.service.ProbeService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/probe")
public class ProbeController {

    private final ProbeService probeService;

    public ProbeController(ProbeService probeService) {
        this.probeService = probeService;
    }

    @PostMapping("/run")
    public ResponseEntity<RunResponse> run(@Valid @RequestBody RunRequest request) {
        return ResponseEntity.ok(probeService.run(request));
    }
}