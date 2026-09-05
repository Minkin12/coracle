package dev.minkin.coracle.model.events;

import dev.minkin.coracle.model.Entry;
import lombok.Builder;

import java.util.List;

@Builder
public record AppendEntriesRequest(
        Integer leaderTerm,
        Integer leaderId,
        Integer prevLogIndex,
        Integer prevLogTerm,
        List<Entry> batch,
        Integer leaderCommit
) implements Event {}
