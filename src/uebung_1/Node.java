package uebung_1;

import java.util.ArrayList;
import java.util.Comparator;

public class Node implements Comparator<Node> {
    private ArrayList<Neighbor> neighbors = new ArrayList<Neighbor>();
    private int id;
    private int demand = 0;
    private int coordX;
    private int coordY;

    public Neighbor getNeighborById(int id){
        Neighbor n = null;
        for (int i = 0; i < neighbors.size(); i++) {
            if(neighbors.get(i).getNode().getId() == id){
                n = neighbors.get(i);
            }
        }
        return n;
    }
    public void setDemand(int demand) {
        this.demand = demand;
    }

    public int getDemand() {
        return demand;
    }

    public void reduceDemand(int demand) {
        if (demand > this.demand) {
            this.demand = 0;
        } else {
            this.demand -= demand;
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Node(int id, int coordX, int coordY) {
        this.id = id;
        this.coordX = coordX;
        this.coordY = coordY;
    }

    @Override
    public int compare(Node node1, Node node2) {
        return 0;
    }

    public Neighbor getClosestDemandingNeighbor() {
        int bestIndex = 0;
        int shortestDistance = neighbors.get(0).getDistance(); // Start mit erstem Nachbar
        int i = 1;
        if (neighbors.size() == 0){
            return null;
        }
        while (i < neighbors.size()) {
            Neighbor nextNode = neighbors.get(i);
            if (neighbors.get(i).getNode().getDemand() == 0) { // voll versorgte Nachbarn überspringen
                i++;
                continue;
            }
            if (nextNode.getDistance() < shortestDistance) {
                bestIndex = i; // Nachbar mit meistem Bedarf merken
                shortestDistance = nextNode.getDistance(); // neuer Vergleichspunkt
            }
            i++;
        }
        return neighbors.get(bestIndex);
    }

    public ArrayList<Neighbor> getNeighbors() {
        return neighbors;
    }

    public void addNeighbor(Neighbor nodeInfo) {
        neighbors.add(nodeInfo);
    }

    public int getCoordX() {
        return coordX;
    }

    public void setCoordX(int coordX) {
        this.coordX = coordX;
    }

    public int getCoordY() {
        return coordY;
    }

    public void setCoordY(int coordY) {
        this.coordY = coordY;
    }
}
