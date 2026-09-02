package dev.minkin.coracle.model.events;

public sealed interface Event permits
        AppendEntriesRequest,
        AppendEntriesResponse,
        RequestVoteRequest,
        RequestVoteResponse,
        Heartbeat,
        ElectionTimeout,
        ClientSubmission
{}
