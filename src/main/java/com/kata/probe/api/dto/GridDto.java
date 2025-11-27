package com.kata.probe.api.dto;

import jakarta.validation.constraints.Min;

public class GridDto {
    @Min(1)
    public int width;
    @Min(1)
    public int height;
}
