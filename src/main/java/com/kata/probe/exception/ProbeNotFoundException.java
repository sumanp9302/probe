package com.kata.probe.exception;

import java.util.UUID;

public class ProbeNotFoundException extends RuntimeException {
    public ProbeNotFoundException(UUID id) {
        super("Probe not found: " + id);
    }
}
