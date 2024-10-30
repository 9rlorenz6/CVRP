package uebung_1;

public class Neighbor {
    private Node node;
    private int distance;
    private int demand = 0;

    public int getDemand() {
        return demand;
    }

    public void setDemand(int demand) {
        this.demand = demand;
    }

    public int getDistance() {
        return distance;
    }

    public Node getNode() {
        return node;
    }

    public void setNode(Node node) {
        this.node = node;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public Neighbor(Node node, int distance, int demand) {
        this.node = node;
        this.distance = distance;
        this.demand = demand;
    }

}
