package uebung_1;

import java.util.ArrayList;
import java.util.Comparator;

public class Node implements Comparator<Node> {
    private ArrayList<Neighbor> neighbors = new ArrayList<Neighbor>();
    private int id;
    private int demand = 0;

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

    private int coordX;
    private int coordY;
    private int distance;

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
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
        int biggestDemand = 0;
        int i = 0;
        int bestIndex = neighbors.get(0).getNode().getId(); // Start mit erstem Nachbar
        while (i < neighbors.size()) {
            int nDemand = neighbors.get(i).getDemand();
            if (nDemand == 0) { // voll versorgte Nachbarn überspringen
                i++;
                continue;
            } else if (nDemand > biggestDemand) {
                bestIndex = i; // Nachbar mit meistem Bedarf merken
                biggestDemand = nDemand; // neuer Vergleichspunkt
            }
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
