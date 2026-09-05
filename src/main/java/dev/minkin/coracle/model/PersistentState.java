package dev.minkin.coracle.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Builder
public record PersistentState(
        Integer currentTerm,
        Integer votedFor,
        Log log){ }
