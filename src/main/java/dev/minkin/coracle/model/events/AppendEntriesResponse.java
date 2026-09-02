package dev.minkin.coracle.model.events;

public record AppendEntriesResponse(
        Integer term,
        Boolean voteGranted) implements Event {}
