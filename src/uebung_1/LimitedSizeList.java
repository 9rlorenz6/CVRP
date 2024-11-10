package uebung_1;

import java.util.LinkedList;

public class LimitedSizeList {
    private final int maxSize;
    private final LinkedList<TSPInstance> list;

    public LimitedSizeList(int maxSize) {
        this.maxSize = maxSize;
        this.list = new LinkedList<TSPInstance>();
    }

    public void add(TSPInstance instance) {
        // Überprüfen, ob die maximale Größe erreicht wurde
        if (list.size() >= maxSize) {
            list.removeFirst(); // Entfernt das älteste Element
        }
        list.addLast(instance); // Fügt das neue Element am Ende hinzu
    }

    public TSPInstance get(int index) {
        return list.get(index);
    }

    public int size() {
        return list.size();
    }

    public LinkedList<TSPInstance> getList() {
        return list;
    }
    public TSPInstance getLowerBest(){
        TSPInstance lowerBest = null;
        for (TSPInstance tspInstance : list) {
            if(lowerBest == null || tspInstance.getTotalCost() > lowerBest.getTotalCost()){
                lowerBest = tspInstance;
            }
        }
            return lowerBest;
    }
    public TSPInstance getUpperBest(){
        TSPInstance upperBest = null;
        for (TSPInstance tspInstance : list) {
            if(upperBest == null || tspInstance.getTotalCost() < upperBest.getTotalCost()){
                upperBest = tspInstance;
            }
        }
            return upperBest;
    }
    @Override
    public String toString() {
        StringBuilder string = new StringBuilder();
        for (TSPInstance t : list) {
            string.append(t.toString());
        }
        return string.toString();
    }

    public boolean notContaining(int id) {
        for (TSPInstance tspInstance : list) {
            if(tspInstance.getId() == id){
                return true;
            }
        }
        return false;
    }

}
