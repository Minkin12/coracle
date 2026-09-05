package dev.minkin.coracle.model.events;

public record AppendEntriesResponse(
        Integer term,
        Boolean success,
        Integer followerId) implements Event {}
