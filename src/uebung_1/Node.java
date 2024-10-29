package uebung_1;
import java.util.ArrayList;
import java.util.Comparator;

public class Node implements Comparator<Node> {
    private ArrayList<Node> neighbors = new ArrayList<Node>();
    private int id;
    private boolean visited = false;
    private int demand = 0;
    private int coordX;
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    private int  coordY;
    private int distance;
    private Node nextNeighbor;
    
 
    public Node(int id, int coordX, int coordY) {
        this.id = id;
        this.coordX = coordX;
        this.coordY = coordY;
    }
    public Node closestNeighor(){
        return nextNeighbor;
    }
    @Override
    public int compare(Node node1, Node node2) { 
        return 0;
    }
    public ArrayList<Node> getNeighbors() {
        return neighbors;
    }
    public void setNeighbors(ArrayList<Node> neighbors) {
        this.neighbors = neighbors;
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
