package uebung_1;

import java.util.ArrayList;

public class Route {
    private ArrayList<Node> checkpoints;
    private int capacity = 0;
    private int cost = 0;
    public int getCost() {
        return cost;
    }
    public String toString(){
        String result = "Gesamtdistanz " + cost;
        return result;
    }
    public void addCost(int cost) {
        this.cost += cost;
    }

    public int getCapacity() {
        return capacity;
    }

    public Route(int capacity) {
        this.capacity = capacity;
    }
    public void reduceCapacity(int demand) {
        if(demand > this.capacity){
            this.capacity = 0;
        }else{
            this.capacity -= demand;
        }
        
    }
    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public ArrayList<Node> getCheckpoints() {
        return checkpoints;
    }

    public void setCheckpoints(ArrayList<Node> checkpoints) {
        this.checkpoints = checkpoints;
    }

    public Route(ArrayList<Node> checkpoints) {
        this.checkpoints = checkpoints;
    }
}
