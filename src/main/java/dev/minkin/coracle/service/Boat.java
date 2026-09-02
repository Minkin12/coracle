package dev.minkin.coracle.service;

import dev.minkin.coracle.model.*;
import dev.minkin.coracle.model.actions.Action;
import dev.minkin.coracle.model.actions.Apply;
import dev.minkin.coracle.model.actions.Send;
import dev.minkin.coracle.model.events.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Builder
@AllArgsConstructor
public class Boat {
    private Integer id;
    private Role role;
    private List<Integer> otherBoats;

    // Persistent state
    private Integer currentTerm;
    private Integer votedFor;
    private List<Entry> entries;

    // Volatile state
    private Integer commitIndex;
    private Integer lastApplied;

    // Volatile state for leader
    private List<Integer> nextIndex;
    private List<Integer> matchIndex;

    List<Action> process(Event event) {
        List<Action> intentions = new ArrayList<>();
        switch (event) {

            case AppendEntriesRequest appendEntriesRequest:
                if (currentTerm < appendEntriesRequest.candidateTerm()) {
                    currentTerm = appendEntriesRequest.candidateTerm();
                    role = Role.FOLLOWER;
                }
                break;
            case AppendEntriesResponse appendEntriesResponse:
                break;
            case ClientSubmission clientSubmission:
                break;
            case ElectionTimeout electionTimeout:
                break;
            case Heartbeat heartbeat:
                break;
            case RequestVoteRequest requestVoteRequest:
                Entry lastLogEntry = entries.getLast();
                if (currentTerm > requestVoteRequest.term()) {
                    intentions.add(new Send(new RequestVoteResponse(currentTerm, false)));
                } else {
                    currentTerm = requestVoteRequest.term();
                    role = Role.FOLLOWER;
                    votedFor = null;
                }


                if ((votedFor == null || votedFor.equals(requestVoteRequest.candidateId())) && (requestVoteRequest.lastLogTerm().equals(lastLogEntry.term()) && entries.size() <= requestVoteRequest.lastLogIndex())) {
                    intentions.add(new Send(new RequestVoteResponse(currentTerm, true)));
                } else {
                    intentions.add(new Send(new RequestVoteResponse(currentTerm, false)));
                }
                break;
            case RequestVoteResponse requestVoteResponse:
                break;
        }
        if (commitIndex > lastApplied) {
           intentions.add(new Apply(commitIndex));
           lastApplied = commitIndex;
        }
        return intentions;
    }


}
