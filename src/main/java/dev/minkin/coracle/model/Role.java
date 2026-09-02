package dev.minkin.coracle.model;

public enum Role {
    LEADER("Leader"),
    CANDIDATE("Candidate"),
    FOLLOWER("Follower");

    private final String value;

    Role(String value) {
        this.value = value;
    }
    public String getValue() {
        return value;
    }
}
