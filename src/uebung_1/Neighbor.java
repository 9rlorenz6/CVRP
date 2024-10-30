package uebung_1;

public class Neighbor {
    private int id;
    private int distance;
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public int getDistance() {
        return distance;
    }
    public void setDistance(int distance) {
        this.distance = distance;
    }
    public Neighbor(int id, int distance) {
        this.id = id;
        this.distance = distance;
    }
    
}
