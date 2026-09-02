package dev.minkin.coracle.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
public record Entry(
        Integer term,
        String command) {}
