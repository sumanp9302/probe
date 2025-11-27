package com.kata.probe;

import com.kata.probe.domain.*;
import com.kata.probe.service.CommandInterpreter;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CommandInterpreterTest {

    @Test
    void invalid_and_blocked_are_counted() {
        Grid g = new Grid(2,2);
        g.addObstacle(new Coordinate(0,1)); // irrelevant to our path but okay
        Probe p = new Probe(new Coordinate(1,1), Direction.NORTH, g);

        var r = new CommandInterpreter()
                .execute(p, Arrays.asList("F", "X", null, "F", "B"));

        assertEquals(1, r.executed);
        assertEquals(2, r.blocked);
        assertEquals(2, r.invalid);
    }

}