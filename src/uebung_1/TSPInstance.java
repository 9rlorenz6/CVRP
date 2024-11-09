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
    public TSPInstance(ArrayList<Node> nodes, int[] permutation, int capacity, int parent1, int parent2) {
        this.permutation = permutation;
        this.capacity = capacity;
        this.routes = buildRoutesFromPermutation(nodes, permutation, capacity);
        this.totalCost = calcTotalCosts(this.routes);
        this.parent1 = parent1;
        this.parent2 = parent2;
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
     * zurückgekehrt werden muss
     * um die Kosten zu berechnen
     * 
     * @param nodes
     * @param permutation
     * @param capacity
     * @return
     */
    private ArrayList<Route> buildRoutesFromPermutation(ArrayList<Node> nodes, int[] permutation, int capacity) {
        ArrayList<Route> routes = new ArrayList<Route>();
        Route route = new Route(capacity);
        int steps = 0;

        Node depot = Cvrp_ls.getNodeById(nodes, 1); // Start wie immer bei Depot
        while (steps < nodes.size() - 1) {
            Node next = Cvrp_ls.getNodeById(nodes, permutation[steps]);
            if (route.getCapacity() >= next.getDemand()) {
                route.addCheckpoint(new Neighbor(next,
                        next.getNeighborById(permutation[steps]).getDistance()));
            } else {// TODO-Richard: Rückwärts-Erstellung von Routen aus Permutation
                route.addCheckpoint(new Neighbor(depot,
                        depot.getNeighborById(permutation[steps]).getDistance()));
            }
        }
        return routes;
    }

    /**
     * Die Reihenfolge nach ID wird berechnet:
     * [1]->[22]
     * [22]->[31]...
     * Wird zwischen 22 und 31 das Depot angefahren, wird dies im Parameter
     * {@link #totalCost} berücksichtigt
     * mit der Methode {@link #calcRealCosts}
     * 
     * @param routes Belieferungsfahrten
     * @return Permutationsmatrix ohne Nachladeberücksichtigung
     */
    private int[]buildPerm(ArrayList<Route> routes) {
        int[] permutation = new int[this.dimension];
        int lastRoutePos = 0;
        for (Route route : routes) {
            ArrayList<Neighbor> checkpoints = route.getCheckpoints();
            for (int i = 0; i < checkpoints.size(); i++) {
                //wenn 3 Checkpoints schon drin, bei [3] weitermachen
                permutation[i+lastRoutePos] = checkpoints.get(i).getNode().getId();    
            }
            lastRoutePos+=checkpoints.size();
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
        for (int i = 0; i < permutation.length; i++) {
            result.append("[" + i + "] -> " + "[" + permutation[i] + "]\n");
        }
        return result.toString();
    }

    public void repairCrossover(TSPInstance parent1, TSPInstance parent2, int[] newPerm) {
        int progress = 0; // Zählzeiger, wird bei Duplikat auf 0 zurückgesetzt
        while (progress < newPerm.length) {
            // Permutation Suchobjekt Ab hier
            if (valueFoundTwice(newPerm, newPerm[progress], progress + 1)) {
                int positionInParent = getIndexOfId(parent2.getPermutation(), newPerm[progress]); // Position im 2.
                                                                                                     // Elternteil
                newPerm[progress] = parent2.getPermutation()[positionInParent]; // dessen Wert übernehmen
                progress = 0; // zurück zum Anfang, neu suchen auf Dopplungen
                continue; // Inkrementierung überspringen
            }
            progress++;
        }
        this.totalCost = calcTotalCosts(this.routes);
    }

    /**
     * Es wird geschaut, ob das gegebene Objekt in der Permutation nach dem
     * Startpunkt nochmal auftaucht
     * 
     * @param permutation
     * @param id          Suchobjekt
     * @param startpoint  Startpunkt
     * @return Ja oder Nein
     */
    private static boolean valueFoundTwice(int[] permutation, int id, int startpoint) {
        for (int i = startpoint; i < permutation.length; i++) {
            if (permutation[i] == id) {
                return true; // Methodenabbruch mit TRUE da Eintrag gefunden wurde
            }
        }
        return false; // in Schleife nichts gefunden, daher FALSE
    }

    /**
     * Indexbestimmung des Objekts in Struktur
     * 
     * @param permutation Struktur(Elternstruktur zum Reparieren)
     * @param id          Suchobjekt
     * @return
     */
    private static int getIndexOfId(int[] permutation, int id) {
        int index = 0;
        while (index < permutation.length) {
            if (permutation[index] == id) {
                break;
            }
            index++;
        }
        return index;
    }

    public String toString() {
        StringBuilder instanceDetails = new StringBuilder();
        instanceDetails.append("\n" + "Eltern: " + parent1 + " + " + parent2 + "\t")
                        .append("\tKosten: " + totalCost)
                        .append("\tAnzahl Routen: " + routes.size());

        return instanceDetails.toString();
    }
}
