package controller.cache;

import java.util.List;

public interface Cache<T> {
    void addData(T key, List<T> data);
    List<T> getData(T key);
    void removeData(T key);
    void clearData();
    boolean containsKey(T key);
}
