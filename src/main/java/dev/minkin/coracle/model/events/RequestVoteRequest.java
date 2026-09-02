package dev.minkin.coracle.model.events;

public record RequestVoteRequest(
        Integer term,
        Integer candidateId,
        Integer lastLogIndex,
        Integer lastLogTerm) implements Event {}