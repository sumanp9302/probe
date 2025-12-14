package com.kata.probe.repository;

import com.kata.probe.domain.ProbeAggregate;
import java.util.*;

public class ProbeRepository {

    private final Map<UUID, ProbeAggregate> store = new HashMap<>();

    public UUID save(ProbeAggregate agg) {
        UUID id = UUID.randomUUID();
        store.put(id, agg);
        return id;
    }

    public Optional<ProbeAggregate> find(UUID id) {
        return Optional.ofNullable(store.get(id));
    }

    public void update(UUID id, ProbeAggregate agg) {
        store.put(id, agg);
    }
}
