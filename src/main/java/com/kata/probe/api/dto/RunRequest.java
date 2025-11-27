package com.kata.probe.api.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public class RunRequest {
    @NotNull @Valid public GridDto grid;
    @NotNull @Valid public CoordinateDto start;
    @NotNull public String direction;      // Accepted: "NORTH" | "EAST" | "SOUTH" | "WEST"
    @NotNull public List<String> commands;
    public List<@Valid CoordinateDto> obstacles = List.of();
}
