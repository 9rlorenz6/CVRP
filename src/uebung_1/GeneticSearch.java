package uebung_1;

import java.util.ArrayList;

public class GeneticSearch {

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
        ArrayList<Route> routes = new ArrayList<Route>(); // Routen für jede Instanz
        int route_Counter = 1;
        Neighbor next;
        Route currentRoute = new Route(capacity);
        Node current = Cvrp_ls.getNodeById(nodes, 1); // Startpunkt bei Depot
        // System.out.println("Depot\naktuelle Route Nr. 1");
        
        // Loop für Instanzenerzeugung
        while (amount > 0) {
            // Loop für Routen der konkreten Instanz
            while (current.getClosestDemandingNeighbor() != null) {
                StringBuilder output_final = new StringBuilder();

                // zum Start der Route einen zufälligen neuen Startpunkt erzeugen
                // dadurch sollten die Elterninstanzen recht gut (im Sinne von Nicht-Optimal)
                // durchmischt sein
                if (currentRoute.getCapacity() == capacity) {
                    // Zufallszahl in Range der Anzahl noch zu beliefernder Nachbarn
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
                currentRoute.addCost(next.getDistance());
                next.getNode().reduceDemand(currentRoute.getCapacity());
                currentRoute.reduceCapacity(nodeDemand);

                // Verfolgungsausgabe
                output_final.append("\t" + "Nachbar ID: " + next.getNode().getId() + "\t");
                output_final.append("\t" + "Distanz: " + next.getDistance() + "\t" + "Bedarf: " + nodeDemand);
                output_final.append("\t" + "verbleibender Bedarf: " + next.getNode().getDemand() + "\t"
                        + "verbleibende Kapazität " + currentRoute.getCapacity());

                // Route ist beendet, Wege zurück zu Depot und neue Route starten
                if (currentRoute.getCapacity() == 0) {
                    // straight zurück zum Depot
                    currentRoute.addCost(next.getNode().getNeighborById(1).getDistance());
                    routes.add(currentRoute); // Route abspeichern
                    currentRoute = new Route(capacity); // neue Route erstellen
                    route_Counter++; // Zähler
                    current = nodes.get(0);// Nächste Suche von Depot aus, da neue Route
                    output_final.append("\n" + "Rückkehr zu Depot ID: " + current.getId() + "\t");
                    output_final.append("\naktuelle Route Nr. " + route_Counter);
                }
                // Route kann noch weiter gehen, da Kapazität noch nicht erschöpft
                // Belieferungszähler um 1 erhöhen
                else {
                    current = next.getNode();
                }
                // System.out.println(output_final.toString());
            }
            // letzte Route mit Restkapazität darf nicht fehlen
            routes.add(currentRoute);
        }
        parents.add(new TSPInstance(routes));
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
