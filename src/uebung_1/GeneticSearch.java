package uebung_1;

import java.util.ArrayList;

public class GeneticSearch {
    public static double sizeOfSides = 0.3; // Linke und Rechte Seite sollen Platz für Mitte lassen

    /**
     * Kann für mehrere Kinder genutzt werden, da Austauschbereiche jedes mal
     * zufällig gewürfelt werden
     * (auch bei gleichen Eltern)
     * 
     * @param parent1 Elternteil 1
     * @param parent2 Elternteil 2
     * @return einzelnes Kind dieser Kombination
     */
    public static TSPInstance combineGenetics(ArrayList<Node> nodes, TSPInstance parent1, TSPInstance parent2) {
        int[][] newPerm = new int[parent1.getDimension()][2];
        int left = (int) (Math.random() * newPerm.length * sizeOfSides);
        int right = (int) (parent1.getDimension() - Math.random() * newPerm.length * sizeOfSides);

        for (int i = 0; i < left; i++) {
            newPerm[i][1] = parent1.getPermutation()[i][1]; // Links von P1 verwenden
        }
        for (int i = left; i < right; i++) {
            newPerm[i][1] = parent2.getPermutation()[i][1]; // Mitte von P2 verwenden
        }
        for (int i = right; i < newPerm.length; i++) {
            newPerm[i][1] = parent1.getPermutation()[i][1]; // Rechts von P1 verwenden
        }
        TSPInstance child = new TSPInstance(nodes, newPerm, parent1.getCapacity());
        child.repairCrossover(parent1, parent2, newPerm);
        return child;

    }

    /**
     * Im Groben übernommen von "findGreedySet", angepasst für Genetische lokale
     * Suche
     * 
     * @param nodes    Knotenliste des TSP
     * @param capacity Routenkapazität
     * @param amount   Anzahl der Eltern, die zu erzeugen sind
     * @return Liste mit Eltern-Instanzen für genetische Kombinationen
     */
    public static ArrayList<TSPInstance> findStartInstances(ArrayList<Node> nodes, int capacity, int amount) {
        ArrayList<TSPInstance> parents = new ArrayList<>(); // Elterninstanzen
        int route_Counter = 1;
        Neighbor next;
        Route currentRoute = new Route(capacity);
        Node current = Cvrp_ls.getNodeById(nodes, 1); // Startpunkt bei Depot
        // System.out.println("Depot\naktuelle Route Nr. 1");
        
        // Loop für Instanzenerzeugung
        while (amount > 0) {
            ArrayList<Route> routes = new ArrayList<Route>(); // Routen für jede Instanz
            // Loop für Routen der konkreten Instanz
            while (current.getClosestDemandingNeighbor() != null) {
                StringBuilder output_final = new StringBuilder();

                /** zum Start der Route einen zufälligen neuen Startpunkt erzeugen
                    dadurch sollten die Elterninstanzen recht gut (im Sinne von Nicht-Optimal)
                    durchmischt sein
                */ 
                if (currentRoute.getCapacity() == capacity) {
                    // Zufallszahl aus Menge der Anzahl noch zu beliefernder Nachbarn
                    // daraus wird ein zufälliger Nachbar gewählt
                    int randomNeighbor = (int) (Math.random() * getDemandingNeighbors(current).size());
                    next = current.getNeighbors().get(randomNeighbor); // hier mit Index, da Listengröße gearbeitet wird
                }
                // während der Route den nächstgelegenen Nachbarn mit Bedarf suchen
                else {
                    next = current.getClosestDemandingNeighbor();
                }
                // Abarbeitung der Bedarfe und Kosten
                int nodeDemand = next.getNode().getDemand();

                /* Route wird beendet, Wege zurück zu Depot und neue Route starten
                 * wichtig, damit jeder Knoten echt nur 1 Mal angesteuert wird
                 */ 
                if(currentRoute.getCapacity() < nodeDemand){
                    // straight zurück zum Depot
                    currentRoute.addCost(next.getNode().getNeighborById(1).getDistance());
                    routes.add(currentRoute); // Route abspeichern
                    currentRoute = new Route(capacity); // neue Route erstellen
                    route_Counter++; // Zähler
                    current = nodes.get(0);// Nächste Suche von Depot aus, da neue Route
                    output_final.append("\n" + "Rückkehr zu Depot ID: " + current.getId() + "\t");
                    output_final.append("\naktuelle Route Nr. " + route_Counter);
                    continue;
                }
                currentRoute.addCost(next.getDistance());
                next.getNode().reduceDemand(currentRoute.getCapacity());
                currentRoute.reduceCapacity(nodeDemand);
                currentRoute.addCheckpoint(next);

                // Verfolgungsausgabe
                output_final.append("\t" + "Nachbar ID: " + next.getNode().getId() + "\t");
                output_final.append("\t" + "Distanz: " + next.getDistance() + "\t" + "Bedarf: " + nodeDemand);
                output_final.append("\t" + "verbleibender Bedarf: " + next.getNode().getDemand() + "\t"
                        + "verbleibende Kapazität " + currentRoute.getCapacity());

                // Route kann noch weiter gehen, da Kapazität noch nicht erschöpft
                // Belieferungszähler um 1 erhöhen
                    current = next.getNode();
            }
            // letzte Route mit Restkapazität darf nicht fehlen
            routes.add(currentRoute);
            parents.add(new TSPInstance(routes, nodes.size(), capacity));
        }
        return parents;
    }

    /**
     * gibt alle Nachbarn zurück, die noch zu beliefern sind
     * 
     * @param node Der Knoten, von dem aus der nächste Nachbar ausfindig gemacht
     *             werden soll
     * @return Liste aller Nachbarn die noch zu beliefern sind
     */
    private static ArrayList<Neighbor> getDemandingNeighbors(Node node) {
        ArrayList<Neighbor> demandingNeighbors = new ArrayList<Neighbor>();
        for (Neighbor neighbor : node.getNeighbors()) {
            if (neighbor.getNode().getDemand() != 0) {
                demandingNeighbors.add(neighbor);
            }
        }
        return demandingNeighbors;
    }
}
