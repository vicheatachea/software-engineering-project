package controller.cache;

import java.util.List;
import java.util.TreeMap;

public class GeneralCache<T> {
    private static GeneralCache instance;
    private final TreeMap<String, List<T>> dataCache;

    private GeneralCache() {
        dataCache = new TreeMap<>();
    }

    public static synchronized GeneralCache getInstance() {
        if (instance == null) {
            instance = new GeneralCache();
        }
        return instance;
    }

    public void addData(String key, List<T> data) {
        dataCache.put(key, data);
    }

    public List<T> getData(String key) {
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
