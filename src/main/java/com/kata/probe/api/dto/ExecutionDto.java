package com.kata.probe.api.dto;

public class ExecutionDto {
    public int executed;
    public int blocked;
    public int invalid;

    public ExecutionDto(int executed, int blocked, int invalid) {
        this.executed = executed;
        this.blocked = blocked;
        this.invalid = invalid;
    }
}
