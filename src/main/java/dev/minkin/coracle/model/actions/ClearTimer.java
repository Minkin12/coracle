package dev.minkin.coracle.model.actions;

import dev.minkin.coracle.model.TimerType;

public record ClearTimer(TimerType timerType) implements Action { }
