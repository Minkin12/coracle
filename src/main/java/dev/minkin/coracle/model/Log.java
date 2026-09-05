package dev.minkin.coracle.model;

import java.util.ArrayList;
import java.util.List;


public class Log {
    List<Entry> entries;

    public Log(List<Entry> entries) {
        this.entries = entries;
    }

    public Log(){
        this.entries = new ArrayList<>(List.of(new Entry(0,"")));
    }

    public List<Entry> getEntries() {
        return entries;
    }

    public Integer lastIndex() {
        return entries.size() - 1;
    }

    public Integer lastTerm() {
        return entries.get(lastIndex()).term();
    }

    public Integer termAt(Integer index) {
        return entries.get(index).term();
    }

    public Entry entryAt(Integer index) {
        return entries.get(index);
    }

    public Boolean hasIndex(Integer index) {
        return index >= 0 && index < entries.size();
    }

    public Integer size() {
        return entries.size();
    }

    public void append(Entry entry) {
        entries.add(entry);
    }

    public void append(List<Entry> entries) {
        this.entries.addAll(new ArrayList<>(entries));
    }

    public void truncateFrom(Integer index) {
        if (index < entries.size()) {
            entries = new ArrayList<>(entries.subList(0, index-1));
        }
    }

    public void reconcile(Integer prevLogIndex, List<Entry> newEntries) {
        if (newEntries == null || newEntries.isEmpty()) {
            return;
        }
        for (int i = 0; i < newEntries.size(); i++) {
            int logIndex = prevLogIndex + 1 + i;
            if (logIndex < entries.size()) {
                Entry existingEntry = entries.get(logIndex);
                Entry newEntry = newEntries.get(i);
                if (!existingEntry.term().equals(newEntry.term())) {
                    truncateFrom(logIndex);
                    append(newEntries.subList(i, newEntries.size()));
                    break;
                }
            } else {
                append(newEntries.subList(i, newEntries.size()));
                break;
            }
        }

    }

}
