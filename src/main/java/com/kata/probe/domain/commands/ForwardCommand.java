package com.kata.probe.domain.commands;

import com.kata.probe.domain.Probe;

public class ForwardCommand implements Command {
    @Override
    public boolean execute(Probe probe) {
        return probe.moveForward();
    }
}
