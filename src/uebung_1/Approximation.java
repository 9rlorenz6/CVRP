package uebung_1;

import java.util.ArrayList;

public class Approximation {
    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public ArrayList<Node> getNodes() {
        return nodes;
    }

    public void setNodes(ArrayList<Node> nodes) {
        this.nodes = nodes;
    }

    public Node getDepot() {
        return depot;
    }

    public void setDepot(Node depot) {
        this.depot = depot;
    }

    public double getSeconds() {
        return seconds;
    }

    public void setSeconds(double seconds) {
        this.seconds = seconds;
    }

    public ArrayList<Route> getRoutes() {
        return routes;
    }

    public void setRoutes(ArrayList<Route> routes) {
        this.routes = routes;
    }

    private String mode;
    private ArrayList<Node> nodes;
    private Node depot;
    private double seconds;
    public ArrayList<Route> routes;

    public ArrayList<Route> approximate_with_cvrp(ArrayList<Node> nodes){
        //TODO: Instanziierten Algorithmus bauen
        ArrayList<Route> result = null;
        return result;
    }
}
