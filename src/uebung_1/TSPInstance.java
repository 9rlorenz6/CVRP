package uebung_1;

import java.util.ArrayList;

public class TSPInstance {
    private ArrayList<Route> routes;

    public ArrayList<Route> getRoutes() {
        return routes;
    }

    public void setRoutes(ArrayList<Route> routes) {
        this.routes = routes;
    }

    public TSPInstance(ArrayList<Route> routes) {
        this.routes = routes;
    }   
    
}
