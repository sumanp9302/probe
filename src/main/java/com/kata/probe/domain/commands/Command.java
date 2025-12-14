package com.kata.probe.domain.commands;

import com.kata.probe.domain.Probe;

public interface Command {
    boolean execute(Probe probe);
}
