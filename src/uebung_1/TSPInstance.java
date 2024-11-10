package uebung_1;

import java.util.ArrayList;

public class TSPInstance {
    private ArrayList<Route> routes;
    private int dimension;
    private int[] permutation;
    private int totalCost;
    private int capacity;
    private int parent1 = 0;
    private int parent2 = 0;
    private int id = 0;

    // Elternkonstruktor mit Routen, Permutation bestimmen
    public TSPInstance(ArrayList<Route> routes, int dimension, int capacity, int id) {
        this.routes = routes;
        this.dimension = dimension;
        this.capacity = capacity;
        this.permutation = buildPerm(routes); // ignoriert Rückkehr zu Depot, da mehrmals vorkommend
        this.totalCost = calcTotalCosts(routes); // berechnet Kosten inklusive Depot-Fahrten, da sonst ignoriert
        this.id = id;

    }

    // Kinderkonstruktor mit Permutation -> Routen und Kosten rückwärts erzeugen
    public TSPInstance(ArrayList<Node> nodes, int[] permutation, int capacity, int parent1, int parent2, int id) {
        this.permutation = permutation;
        this.capacity = capacity;
        this.dimension = permutation.length;
        this.parent1 = parent1;
        this.parent2 = parent2;
        this.id = id;
        this.routes = buildRoutesFromPermutation(nodes, permutation, capacity);
        this.totalCost = calcTotalCosts(this.routes);

    }

    public int getParent1() {
        return parent1;
    }

    public int getParent2() {
        return parent2;
    }

    public int getId() {
        return id;
    }

    public ArrayList<Route> getRoutes() {
        return routes;
    }

    public int getTotalCost() {
        return totalCost;
    }

    public int getCapacity() {
        return capacity;
    }

    public int[] getPermutation() {
        return permutation;
    }

    public int getDimension() {
        return dimension;
    }

    public void setDimension(int dimension) {
        this.dimension = dimension;
    }

    /**
     * [3,2,5,1,6] -> Gehe von 1 zu 6 und schaue, wann wegen Kapazität zu 1
     * zurückgekehrt werden muss um die Kosten zu berechnen
     * 
     * @param nodes
     * @param permutation
     * @param capacity
     * @return
     */
    private ArrayList<Route> buildRoutesFromPermutation(ArrayList<Node> nodes, int[] permutation, int capacity) {
        ArrayList<Route> routes = new ArrayList<Route>();
        Route route = new Route(capacity);
        int routeCounter = 1;
        int start = findStartInPerm(permutation, 1);// Start bei Depot
        int nextEntry = ((start + 1) % permutation.length); // Wenn Depot letzter Punkt, step = perm[0] wegen Modulo
        // System.out.println("Route: "+routeCounter);
        Node current = Cvrp_ls.getNodeById(nodes, permutation[start]); // ist das Depot am Anfang
        Neighbor next = current.getNeighborById(permutation[nextEntry]);

        while (permutation[nextEntry] != 1) {

            if (next == null) {
                break;
            }
            if (route.getCapacity() >= next.getNode().getDemand()) {
                route.addCheckpoint(next);
                route.addCost(next.getDistance());
                route.reduceCapacity(next.getNode().getDemand());
               // System.out.println("\tSchritt zu: " + next.getNode().getId());
                current = next.getNode();   
                nextEntry = (nextEntry + 1) % permutation.length;          //nächster Permutationseintrag möglich
                next = current.getNeighborById(permutation[nextEntry]);
            } else {
                route.addCheckpoint(current.getNeighborById(1));// Depot ist das nächste Ziel
                route.addCost(current.getNeighborById(1).getDistance());
                current = current.getNeighborById(1).getNode();
              //  System.out.println("\tSchritt zu: " + current.getId());
                routes.add(route);
                routeCounter++;
                route = new Route(capacity);
                next = current.getNeighborById(permutation[nextEntry]);    //vom Depot zum nächsten Eintrag
             //   System.out.println("Route: " + routeCounter);
                continue;
            }

        }
        routes.add(route);
        return routes;
    }

    private int findStartInPerm(int[] permutation, int id) {
        int i = 0;
        while (i < permutation.length) {
            if (permutation[i] != id) {
                i++;
                continue;
            } else {
                break;
            }
        }
        return i;
    }

    /**
     * Die Reihenfolge nach ID wird berechnet: [1]->[22] [22]->[31]... Wird zwischen
     * 22 und 31 das Depot angefahren, wird dies im Parameter {@link #totalCost}
     * berücksichtigt mit der Methode {@link #calcRealCosts}
     * 
     * @param routes Belieferungsfahrten
     * @return Permutationsmatrix ohne Nachladeberücksichtigung
     */
    private int[] buildPerm(ArrayList<Route> routes) {
        int[] permutation = new int[this.dimension];
        int lastRoutePos = 0;
        for (Route route : routes) {
            ArrayList<Neighbor> checkpoints = route.getCheckpoints();
            for (int i = 0; i < checkpoints.size(); i++) {
                // wenn 3 Checkpoints schon drin, bei [3] weitermachen
                permutation[i + lastRoutePos] = checkpoints.get(i).getNode().getId();
            }
            lastRoutePos += checkpoints.size();
        }
        return permutation;
    }

    private int calcTotalCosts(ArrayList<Route> routes) {
        int realCost = 0;
        for (int i = 0; i < routes.size(); i++) {
            realCost += routes.get(i).getCost();
        }
        return realCost;
    }

    public String permToString() {
        StringBuilder result = new StringBuilder();
        result.append("Permutation:\n");
        for (int i = 0; i < permutation.length - 1; i++) {
            result.append("\t[" + permutation[i] + "] -> " + "[" + permutation[i + 1] + "]\n");
        }
        return result.toString();
    }

    public String toString() {
        StringBuilder instanceDetails = new StringBuilder();
        instanceDetails.append("Instanz- ID: " + this.id + "\t\tEltern: " + parent1 + " + " + parent2 + "\t")
                .append("\tKosten: " + totalCost)
                .append("\tAnzahl Routen: " + routes.size() + "\n");

        return instanceDetails.toString();
    }
}
