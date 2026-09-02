package dev.minkin.coracle.model.actions;

import dev.minkin.coracle.model.events.Event;

public record Send(Event event) implements Action {}
