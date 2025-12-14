package com.kata.probe.domain.commands;

public class CommandFactory {

    public static Command from(String raw) {
        if (raw == null || raw.isBlank()) return new InvalidCommand();

        String cmd = raw.trim().toUpperCase();
        return switch (cmd) {
            case "F" -> new ForwardCommand();
            case "B" -> new BackwardCommand();
            case "L" -> new TurnLeftCommand();
            case "R" -> new TurnRightCommand();
            default -> new InvalidCommand();
        };
    }
}
