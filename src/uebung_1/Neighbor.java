package uebung_1;

public class Neighbor {
    private Node node;
    private int distance;

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

    public Neighbor(Node node, int distance) {
        this.node = node;
        this.distance = distance;
    }

}
