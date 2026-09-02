package dev.minkin.coracle.model.events;

public record AppendEntriesRequest(
        Integer candidateTerm,
        Integer candidateId,
        Integer lastLogIndex) implements Event {}
