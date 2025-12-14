package com.kata.probe.domain.commands;

import com.kata.probe.domain.Probe;

public class TurnLeftCommand implements Command {
    @Override
    public boolean execute(Probe probe) {
        probe.turnLeft();
        return true;
    }
}
