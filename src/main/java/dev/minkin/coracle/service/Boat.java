package dev.minkin.coracle.service;

import dev.minkin.coracle.model.*;
import dev.minkin.coracle.model.actions.*;
import dev.minkin.coracle.model.events.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
    private Log log;

    // Volatile state
    private Integer commitIndex;
    private Integer lastApplied;

    // Volatile state for candidate
    private Set<Integer> voters;

    // Volatile state for leader
    private Map<Integer, Integer> nextIndex;
    private Map<Integer, Integer> matchIndex;


    List<Action> process(Event event) {
        List<Action> intentions = new ArrayList<>();
        switch (event) {
            case AppendEntriesRequest appendEntriesRequest:
                if (appendEntriesRequest.leaderTerm() < currentTerm) {
                    intentions.add(new Send(new AppendEntriesResponse(currentTerm, false, id), appendEntriesRequest.leaderId()));
                    break;
                } else if (appendEntriesRequest.leaderTerm() > currentTerm) {
                    currentTerm = appendEntriesRequest.leaderTerm();
                    role = Role.FOLLOWER;
                    votedFor = null;
                    intentions.add(new Persist(new PersistentState(currentTerm, null, log)));
                } else {
                    if (role == Role.CANDIDATE) {
                        role = Role.FOLLOWER;
                    }
                }
                intentions.add(new ResetTimer(TimerType.ELECTION));
                if (appendEntriesRequest.prevLogIndex() > log.lastIndex()) {
                    intentions.add(new Send(new AppendEntriesResponse(currentTerm, false, id), appendEntriesRequest.leaderId()));
                    break;

                }
                if (!log.entryAt(appendEntriesRequest.prevLogIndex()).term().equals(appendEntriesRequest.prevLogTerm())) {
                    intentions.add(new Send(new AppendEntriesResponse(currentTerm, false, id), appendEntriesRequest.leaderId()));
                    break;
                }
               log.reconcile(appendEntriesRequest.prevLogIndex(), appendEntriesRequest.batch());
                if (appendEntriesRequest.leaderCommit() > commitIndex) {
                    commitIndex = Math.min(appendEntriesRequest.leaderCommit(), appendEntriesRequest.prevLogIndex() + appendEntriesRequest.batch().size());
                }
                intentions.add(new Persist(new PersistentState(currentTerm, votedFor, log)));
                intentions.add(new Send(new AppendEntriesResponse(currentTerm, true, id), appendEntriesRequest.leaderId()));

                break;
            case AppendEntriesResponse appendEntriesResponse:
                break;
            case ClientSubmission clientSubmission:
                break;
            case ElectionTimeout electionTimeout:
                currentTerm++;
                role = Role.CANDIDATE;
                votedFor = id;
                voters.clear();
                voters.add(id);
                intentions.add(new Persist(new PersistentState(currentTerm, votedFor, log)));
                intentions.add(new ResetTimer(TimerType.ELECTION));
                for (Integer i : otherBoats) {
                    intentions.add(new Send(new RequestVoteRequest(currentTerm, id, log.lastIndex(), log.lastTerm()), i));
                }
                break;
            case Heartbeat heartbeat:
                if (role.equals(Role.LEADER)) {
                    for (Integer i : otherBoats) {
                        intentions.add(new Send(buildAppendEntriesRequest(i), i));
                    }
                }
                break;
            case RequestVoteRequest requestVoteRequest:
                Entry lastLogEntry = log.entryAt(log.lastIndex());
                if (currentTerm > requestVoteRequest.currentTerm()) {
                    intentions.add(new Send(new RequestVoteResponse(currentTerm, false, id), requestVoteRequest.candidateId()));
                    break;
                } else if (currentTerm < requestVoteRequest.currentTerm()) {
                    currentTerm = requestVoteRequest.currentTerm();
                    role = Role.FOLLOWER;
                    votedFor = null;
                    intentions.add(new Persist(new PersistentState(currentTerm, null, log)));
                }
                boolean canVote = votedFor == null || votedFor.equals(requestVoteRequest.candidateId());
                boolean logIsCurrent = lastLogEntry.term() < requestVoteRequest.lastLogTerm() || (lastLogEntry.term().equals(requestVoteRequest.lastLogTerm()) && log.lastIndex() <= requestVoteRequest.lastLogIndex());
                if (canVote && logIsCurrent) {
                    votedFor = requestVoteRequest.candidateId();
                    intentions.add(new Persist(new PersistentState(currentTerm, votedFor, log)));
                    intentions.add(new Send(new RequestVoteResponse(currentTerm, true, id), requestVoteRequest.candidateId()));
                    intentions.add(new ResetTimer(TimerType.ELECTION));

                } else {
                    intentions.add(new Send(new RequestVoteResponse(currentTerm, false, id), requestVoteRequest.candidateId()));
                }
                break;
            case RequestVoteResponse requestVoteResponse:
                if (!role.equals(Role.CANDIDATE) || !currentTerm.equals(requestVoteResponse.currentTerm())) {
                    //no op
                    break;
                }
                voters.add(id);
                if (requestVoteResponse.voteGranted()) {
                    voters.add(requestVoteResponse.voterId());
                }
                if (voters.size() > (otherBoats.size() + 1) / 2) {
                    role = Role.LEADER;
                    intentions.add(new ResetTimer(TimerType.HEARTBEAT));
                    intentions.add(new ClearTimer(TimerType.ELECTION));
                    for (Integer i : otherBoats) {
                        nextIndex.put(i, log.size());
                        matchIndex.put(i, 0);
                        intentions.add(new Send(buildAppendEntriesRequest(i), i));
                    }
                }
                break;
        }
        if (commitIndex > lastApplied) {
            intentions.add(new Apply(commitIndex));
            lastApplied = commitIndex;
        }
        return intentions;
    }



    private AppendEntriesRequest buildAppendEntriesRequest(Integer boatId) {
        Integer next = nextIndex.get(boatId);
        return AppendEntriesRequest.builder()
                .leaderTerm(currentTerm)
                .leaderId(id)
                .prevLogIndex(next - 1)
                .prevLogTerm(log.entryAt(next - 1).term())
                .batch(new ArrayList<>(log.getEntries().subList(next, log.size())))
                .leaderCommit(commitIndex)
                .build();
    }


}
