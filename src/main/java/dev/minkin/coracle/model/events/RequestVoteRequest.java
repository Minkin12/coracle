package dev.minkin.coracle.model.events;

public record RequestVoteRequest(
        Integer currentTerm,
        Integer candidateId,
        Integer lastLogIndex,
        Integer lastLogTerm) implements Event {}