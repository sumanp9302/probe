package com.kata.probe.domain.commands;

import com.kata.probe.domain.Probe;

public class TurnRightCommand implements Command {
    @Override
    public boolean execute(Probe probe) {
        probe.turnRight();
        return true;
    }
}
