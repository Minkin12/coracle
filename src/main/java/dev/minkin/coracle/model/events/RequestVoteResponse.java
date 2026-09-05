package dev.minkin.coracle.model.events;

public record RequestVoteResponse(
        Integer currentTerm,
        Boolean voteGranted,
        Integer voterId) implements Event {}
