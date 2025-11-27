package com.kata.probe.service;

import com.kata.probe.domain.Probe;

import java.util.List;

public class CommandInterpreter {

    public static class Result {
        public int executed;
        public int blocked;
        public int invalid;
    }

    public Result execute(Probe probe, List<String> commands) {
        Result r = new Result();
        if (commands == null) return r;

        for (String c : commands) {
            if (c == null) {
                r.invalid++;
                continue;
            }
            switch (c) {
                case "L" -> {
                    probe.turnLeft();
                    r.executed++;
                }
                case "R" -> {
                    probe.turnRight();
                    r.executed++;
                }
                case "F" -> {
                    if (probe.moveForward()) r.executed++;
                    else r.blocked++;
                }
                case "B" -> {
                    if (probe.moveBackward()) r.executed++;
                    else r.blocked++;
                }
                default -> r.invalid++;
            }
        }
        return r;
    }
}
