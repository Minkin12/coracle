package dev.minkin.coracle.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PersistentState {
    private Integer currentTerm;
    private Integer votedFor;
    private List<Entry> entries;
}
