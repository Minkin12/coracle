package dev.minkin.coracle.model.actions;

import dev.minkin.coracle.model.PersistentState;

public record Persist(PersistentState persistentState) implements Action { }
