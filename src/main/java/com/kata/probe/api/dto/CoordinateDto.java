package com.kata.probe.api.dto;

import jakarta.validation.constraints.Min;

public class CoordinateDto {
    @Min(0)
    public int x;
    @Min(0)
    public int y;
}
