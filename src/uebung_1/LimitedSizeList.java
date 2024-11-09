package uebung_1;

import java.util.LinkedList;

public class LimitedSizeList<T> {
    private final int maxSize;
    private final LinkedList<T> list;

    public LimitedSizeList(int maxSize) {
        this.maxSize = maxSize;
        this.list = new LinkedList<>();
    }

    public void add(T element) {
        // Überprüfen, ob die maximale Größe erreicht wurde
        if (list.size() >= maxSize) {
            list.removeFirst(); // Entfernt das älteste Element
        }
        list.addLast(element); // Fügt das neue Element am Ende hinzu
    }

    public T get(int index) {
        return list.get(index);
    }

    public int size() {
        return list.size();
    }

    public LinkedList<T> getList() {
        return list;
    }

    @Override
    public String toString() {
        StringBuilder string = new StringBuilder();
        for (T t : list) {
            string.append(t.toString());
        }
        return string.toString();
    }

}
