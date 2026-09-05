package dev.minkin.coracle.model;

public enum TimerType {
    ELECTION("Election"),
    HEARTBEAT("Heartbeat");
    private final String value;
    TimerType(String value) {
        this.value = value;
    }
}
