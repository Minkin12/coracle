package dev.minkin.coracle.model.actions;

public sealed interface Action permits
        Send,
        Persist,
        Apply,
        ResetTimer
{}
