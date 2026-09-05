package dev.minkin.coracle;

import dev.minkin.coracle.model.events.Event;
import dev.minkin.coracle.model.*;
import dev.minkin.coracle.service.Boat;

import java.util.*;

public class Fleet {
    PriorityQueue<Event> eventQueue = new PriorityQueue<>();
    Map<Integer, Integer> electionTimeoutMap = new HashMap<>();
    Map<Integer, Boat> boats = new HashMap<>();
    Map<Integer, Log> stateMachines = new HashMap<>();
    // simulated persistence
    Map<Integer, PersistentState> persistentStates = new HashMap<>();
    // simulated clock
    private long clock;


    Random random = new Random(5);

    // cluster is created but not started
    public Fleet(Integer numberOfBoats) {

        List<Integer> ids = new ArrayList<>();

        for (int i = 0; i < numberOfBoats; i++) {
            ids.add(i);

            boats.put(i, Boat.builder()
                    .id(i)
                    .role(Role.FOLLOWER)
                    .otherBoats(new ArrayList<>())
                    .currentTerm(0)
                    .votedFor(-1)
                    .log(new Log())
                    .build());
            electionTimeoutMap.put(i, random.nextInt(150, 300));
        }
        for (int i = 0; i < ids.size(); i++) {
            Boat boat = boats.get(i);
            boat.getOtherBoats().addAll(ids);
            boat.getOtherBoats().removeIf(s -> s.equals(boat.getId()));
        }

    }

    // start the nodes in the cluster
    void start() {
        for (int i = 0; i < boats.size(); i++) {
            Boat boat = boats.get(i);
            PersistentState currentBoatState = persistentStates.get(i);
            boat.setCurrentTerm(currentBoatState.currentTerm());
            boat.setVotedFor(currentBoatState.votedFor());
            boat.setLog(new Log(currentBoatState.log().getEntries()));
        }


    }

    void startBoat(Integer id) {
        if (boats.containsKey(id)) {
            Boat boat = boats.get(id);
            PersistentState currentBoatState = persistentStates.get(id);
            boat.setCurrentTerm(currentBoatState.currentTerm());
            boat.setVotedFor(currentBoatState.votedFor());
            boat.setLog(new Log(currentBoatState.log().getEntries()));
        } else {
            List<Integer> ids = new ArrayList<>(boats.keySet().stream().toList());
            ids.remove(id);
            boats.put(id, Boat.builder()
                    .id(id)
                    .role(Role.FOLLOWER)
                    .otherBoats(ids)
                    .currentTerm(0)
                    .votedFor(-1)
                    .log(new Log())
                    .build());
            electionTimeoutMap.put(id, random.nextInt(150, 300));
        }
    }

    void restartBoat(Integer id) {
       startBoat(id);
    }

    void step() {

    }

    void killBoat(Integer id) {
        boats.remove(id);
        electionTimeoutMap.remove(id);
    }

    void submit(String command) {

    }

    void partition() {

    }
}
