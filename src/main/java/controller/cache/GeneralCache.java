package controller.cache;

import dto.Event;

import java.util.List;
import java.util.TreeMap;

public class GeneralCache {
    private static GeneralCache instance;
    private final TreeMap<String, List<Event>> dataCache;

    private GeneralCache() {
        dataCache = new TreeMap<>();
    }

    public static synchronized GeneralCache getInstance() {
        if (instance == null) {
            instance = new GeneralCache();
        }
        return instance;
    }

    public void addData(String key, List<Event> data) {
        dataCache.put(key, data);
    }

    public List<Event> getData(String key) {
        return dataCache.get(key);
    }

    public void removeData(String key) {
        dataCache.remove(key);
    }

    public void clearData() {
        dataCache.clear();
    }

    public boolean containsKey(String key) {
        return dataCache.containsKey(key);
    }
}
