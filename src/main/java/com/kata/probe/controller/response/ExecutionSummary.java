package com.kata.probe.controller.response;

public class ExecutionSummary {
    public int executed;
    public int blocked;
    public int invalid;

    public ExecutionSummary() {}

    public ExecutionSummary(int executed, int blocked, int invalid) {
        this.executed = executed;
        this.blocked = blocked;
        this.invalid = invalid;
    }
}
