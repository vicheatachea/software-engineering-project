package controller.cache;

import dto.Event;
import java.time.LocalDate;
import java.util.List;
import java.util.TreeMap;

public class EventCache {
    private static EventCache instance;
    private final TreeMap<LocalDate, List<Event>> eventCache;

    private EventCache() {
        eventCache = new TreeMap<>();
    }

    public static synchronized EventCache getInstance() {
        if (instance == null) {
            instance = new EventCache();
        }
        return instance;
    }

    public void addEvents(LocalDate key, List<Event> events) {
        eventCache.put(key, events);
    }

    public List<Event> getEvents(LocalDate key) {
        return eventCache.get(key);
    }

    public void removeEvents(LocalDate key) {
        eventCache.remove(key);
    }

    public void clearEvents() {
        eventCache.clear();
    }
}
