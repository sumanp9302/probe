package com.kata.probe.controller.request;

import com.kata.probe.domain.Coordinate;
import com.kata.probe.domain.Direction;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class RunRequest {

    @Min(1)
    public int gridWidth;

    @Min(1)
    public int gridHeight;

    @NotNull
    public Coordinate start;

    @NotNull
    public Direction direction;

    @NotNull
    public List<String> commands;

    @NotNull
    public List<Coordinate> obstacles;
}
