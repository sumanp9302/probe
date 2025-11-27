package com.kata.probe;

import com.kata.probe.domain.*;
import com.kata.probe.service.CommandInterpreter;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CommandInterpreterTest {
    @Test void invalid_and_blocked_are_counted() {
        Grid g = new Grid(2,2);
        g.addObstacle(new Coordinate(0,1));
        Probe p = new Probe(new Coordinate(0,0), Direction.NORTH, g);

        var r = new CommandInterpreter().execute(p, List.of("F","X",null,"F","B"));
        // F -> blocked (obstacle), X -> invalid, null -> invalid, F -> blocked, B -> executed
        assertEquals(1, r.executed);
        assertEquals(2, r.blocked);
        assertEquals(2, r.invalid);
    }
}