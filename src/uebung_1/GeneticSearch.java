package uebung_1;

import java.util.ArrayList;

public class GeneticSearch {
    public static final int parentsDefault = 10;
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
        int[] newPerm = new int[parent1.getDimension()];
        int left = (int) (Math.random() * newPerm.length * sizeOfSides);
        int right = (int) (newPerm.length - left);

        for (int i = 0; i < left; i++) {
            newPerm[i] = parent1.getPermutation()[i]; // Links von P1 verwenden
        }
        for (int i = left; i < right; i++) {
            newPerm[i] = parent2.getPermutation()[i]; // Mitte von P2 verwenden
        }
        for (int i = right; i < newPerm.length; i++) {
            newPerm[i] = parent1.getPermutation()[i]; // Rechts von P1 verwenden
        }
        TSPInstance child = new TSPInstance(nodes,
                newPerm,
                parent1.getCapacity(),
                parent1.getId(),
                parent2.getId());
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
        ArrayList<TSPInstance> parents = new ArrayList<TSPInstance>(); // Elterninstanzen
        int route_Counter = 1;
        Neighbor next;
        int parentId = 1;
        Route currentRoute = new Route(capacity);
        Node current = Cvrp_ls.getNodeById(nodes, 1); // Startpunkt bei Depot
        // System.out.println("Depot\naktuelle Route Nr. 1");

        // Loop für Instanzenerzeugung
        while (parentId <= amount) {
            ArrayList<Route> routes = new ArrayList<Route>(); // Routen für jede Instanz
            // Loop für Routen der konkreten Instanz
            while (current.getClosestDemandingNeighbor() != null) {
                StringBuilder output_final = new StringBuilder();

                /**
                 * zum Start der Route einen zufälligen neuen Startpunkt erzeugen
                 * dadurch sollten die Elterninstanzen recht gut (im Sinne von Nicht-Optimal)
                 * durchmischt sein
                 */
                if (currentRoute.getCapacity() == capacity) {
                    // Zufallszahl aus Menge der Anzahl noch zu beliefernder Nachbarn
                    // daraus wird ein zufälliger Nachbar gewählt
                    int randomNeighbor = (int) (Math.random() * getUnclearedNeighbors(current).size());
                    next = current.getNeighbors().get(randomNeighbor); // hier mit Index, da Listengröße gearbeitet wird
                    if (next.getNode().getDemand() == 0) {
                        next = getUnclearedNeighbors(current).get(0);
                    }
                }
                // während der Route den nächstgelegenen Nachbarn mit Bedarf suchen
                else {
                    next = current.getClosestDemandingNeighbor();
                }
                // Abarbeitung der Bedarfe und Kosten
                int nodeDemand = next.getNode().getDemand();

                /*
                 * Route wird beendet, Wege zurück zu Depot und neue Route starten
                 * wichtig, damit jeder Knoten echt nur 1 Mal angesteuert wird
                 */
                if (currentRoute.getCapacity() < nodeDemand) {
                    // straight zurück zum Depot
                    currentRoute.addCost(next.getNode().getNeighborById(1).getDistance());
                    routes.add(currentRoute); // Route abspeichern
                    currentRoute = new Route(capacity); // neue Route erstellen
                    route_Counter++; // Zähler
                    current = nodes.get(0);// Nächste Suche von Depot aus, da neue Route
                    output_final.append("\n" + "Rückkehr zu Depot ID: " + current.getId() + "\t"
                                             + "Distanz: " + next.getNode().getNeighborById(1).getDistance());
                    output_final.append("\naktuelle Route Nr. " + route_Counter);
                    System.out.println(output_final.toString());
                    continue;
                }
                currentRoute.addCost(next.getDistance());
                next.getNode().setCleared(true);
                currentRoute.reduceCapacity(nodeDemand);
                currentRoute.addCheckpoint(next);

                // Verfolgungsausgabe
                output_final.append("\tNachbar ID: " + next.getNode().getId() + "\t");
                output_final.append("\tDistanz: " + next.getDistance() + "\t" + "Bedarf:" + nodeDemand);
                output_final.append("\tverbleibende Kapazität " + currentRoute.getCapacity());
                System.out.println(output_final.toString());
                // Route kann noch weiter gehen, da Kapazität noch nicht erschöpft
                // Belieferungszähler um 1 erhöhen
                current = next.getNode();
            }
            // letzte Route mit Restkapazität darf nicht fehlen
            routes.add(currentRoute);
            parents.add(new TSPInstance(routes, nodes.size(), capacity, parentId));
            parentId++;
            for (int i = 1; i < nodes.size(); i++) {
                nodes.get(i).setCleared(false); //Node-Belieferung zurücksetzen
            }
                
            
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
    private static ArrayList<Neighbor> getUnclearedNeighbors(Node node) {
        ArrayList<Neighbor> demandingNeighbors = new ArrayList<Neighbor>();
        for (Neighbor neighbor : node.getNeighbors()) {
            if (!neighbor.getNode().isCleared()) {
                demandingNeighbors.add(neighbor);
            }
        }
        return demandingNeighbors;
    }

    public static LimitedSizeList<TSPInstance> findGeneticSetWithTime(ArrayList<Node> nodes,
            int capacity,
            long maxRuntimeMillis) {
        // Elterninstanzen
        ArrayList<TSPInstance> parents = findStartInstances(nodes, capacity, parentsDefault);
        StringBuilder parentInfo = new StringBuilder();
        for (TSPInstance tspInstance : parents) {
            parentInfo.append("\nElternID: " + tspInstance.getId())
                    .append("\tGesamtdistanz: " + tspInstance.getTotalCost())
                    .append("\tAnzahl Routen: " + tspInstance.getRoutes().size()+"\n");
        }
        System.out.println(parentInfo.toString());
        // Laufzeitmessung starten
        long startTime = System.currentTimeMillis();

        // alle Eltern ein Mal durchtesten (1,2 | 1,3 | 1,4 | 2,3 | 2,4 | 3,4)
        int parentBase = 0;
        ArrayList<TSPInstance> nextGeneration = new ArrayList<TSPInstance>();
        LimitedSizeList<TSPInstance> mostFit = new LimitedSizeList<TSPInstance>(5);

        while (System.currentTimeMillis() - startTime < maxRuntimeMillis) {
            for (int parentRun = parentBase + 1; parentRun < parents.size(); parentRun++) {
                TSPInstance child = combineGenetics(nodes, parents.get(parentBase), parents.get(parentRun));
                nextGeneration.add(child);
                if (child.getTotalCost() <= GeneticSearch.getFittest(parents)) {
                    mostFit.add(child);
                }
                if (parentRun + 1 == parents.size()) {
                    parents = nextGeneration;
                }
            }
            parentBase++;
        }
        return mostFit;
    }

    private static int getFittest(ArrayList<TSPInstance> parents) {
        int lowestCost = -1;
        for (TSPInstance tspInstance : parents) {
            if (lowestCost == -1
                    || tspInstance.getTotalCost() < lowestCost) {
                lowestCost = tspInstance.getTotalCost();
            }
        }
        return lowestCost;
    }
}
