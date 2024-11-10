package uebung_1;

import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.table.TableRowSorter;

public class GeneticSearch {
    public static final int parentsDefault = 10;
    public static double sizeOfSides = 0.3; // Linke und Rechte Seite sollen Platz für Mitte lassen
    private static final double leastFitnessFactor = 1.1; 

    /**
     * Kann für mehrere Kinder genutzt werden, da Austauschbereiche jedes mal
     * zufällig gewürfelt werden (auch bei gleichen Eltern)
     * 
     * @param parent1 Elternteil 1
     * @param parent2 Elternteil 2
     * @return einzelnes Kind dieser Kombination
     */
    public static TSPInstance combineGenetics(ArrayList<Node> nodes, TSPInstance parent1, TSPInstance parent2, int childId) {
        int[] newPerm = new int[parent1.getDimension()];
        int left = (int) (newPerm.length * sizeOfSides);
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
        int[] repairedPerm = GeneticSearch.repairCrossover(parent1, parent2, newPerm, left, right);
        if(repairedPerm.length == 0){
            return null;
        }
        TSPInstance child = new TSPInstance(nodes, repairedPerm, parent1.getCapacity(), parent1.getId(),
                parent2.getId(), childId);
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
        // System.out.println("Depot\naktuelle Route Nr. 1");
        // Loop für Instanzenerzeugung
        while (parentId <= amount) {
            Route currentRoute = new Route(capacity);
            Node current = Cvrp_ls.getNodeById(nodes, 1); // Startpunkt bei Depot
            ArrayList<Route> routes = new ArrayList<Route>(); // Routen für jede Instanz
            // Loop für Routen der konkreten Instanz
            while (current.getClosestDemandingNeighbor() != null) {
                StringBuilder output_final = new StringBuilder();

                /**
                 * zum Start der Route einen zufälligen neuen Startpunkt erzeugen dadurch
                 * sollten die Elterninstanzen recht gut (im Sinne von Nicht-Optimal)
                 * durchmischt sein
                 */
                if (currentRoute.getCapacity() == capacity) {
                    // Zufallszahl aus Menge der Anzahl noch zu beliefernder Nachbarn
                    // daraus wird ein zufälliger Nachbar gewählt
                    int randomNeighbor = (int) (Math.random() * getUnclearedNeighbors(current).size());
                    next = current.getNeighbors().get(randomNeighbor); // hier mit Index, da Listengröße gearbeitet wird
                    if (next.getNode().isCleared()) {
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
                 * Route wird beendet, Wege zurück zu Depot und neue Route starten wichtig,
                 * damit jeder Knoten echt nur 1 Mal angesteuert wird
                 */
                if (currentRoute.getCapacity() < nodeDemand) {
                    // straight zurück zum Depot
                    currentRoute.addCost(next.getNode().getNeighborById(1).getDistance());
                    routes.add(currentRoute); // Route abspeichern
                    currentRoute = new Route(capacity); // neue Route erstellen
                    route_Counter++; // Zähler
                    current = nodes.get(0);// Nächste Suche von Depot aus, da neue Route
                    // output_final.append("\n" + "Rückkehr zu Depot ID: " + current.getId() + "\t" + "Distanz: "
                    //         + next.getNode().getNeighborById(1).getDistance());
                    // output_final.append("\naktuelle Route Nr. " + route_Counter);
                    // System.out.println(output_final.toString());
                    continue;
                }
                currentRoute.addCost(next.getDistance());
                next.getNode().setCleared(true);
                currentRoute.reduceCapacity(nodeDemand);
                currentRoute.addCheckpoint(next);

                // Verfolgungsausgabe
                // output_final.append("\tNachbar ID: " + next.getNode().getId() + "\t");
                // output_final.append("\tDistanz: " + next.getDistance() + "\t" + "Bedarf:" + nodeDemand);
                // output_final.append("\tverbleibende Kapazität " + currentRoute.getCapacity());
                // System.out.println(output_final.toString());

                // Route kann noch weiter gehen, da Kapazität noch nicht erschöpft
                // Belieferungszähler um 1 erhöhen
                current = next.getNode();
            }
            // letzte Route mit Restkapazität darf nicht fehlen
            routes.add(currentRoute);
            parents.add(new TSPInstance(routes, nodes.size(), capacity, parentId));
            parentId++;
            route_Counter = 1;
            for (int i = 1; i < nodes.size(); i++) {
                nodes.get(i).setCleared(false); // Node-Belieferung zurücksetzen
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

    public static LimitedSizeList findGeneticSetWithTime(ArrayList<Node> nodes, int capacity,
            long maxRuntimeMillis) {
        // Elterninstanzen
        ArrayList<TSPInstance> parents = findStartInstances(nodes, capacity, parentsDefault);
        StringBuilder parentInfo = new StringBuilder();
        for (TSPInstance tspInstance : parents) {
            parentInfo.append("\nElternID: " + tspInstance.getId())
                    .append("\tGesamtdistanz: " + tspInstance.getTotalCost())
                    .append("\tAnzahl Routen: " + tspInstance.getRoutes().size());
        }
        int childId = parents.size() + 1;
        System.out.println(parentInfo.toString());
        // Laufzeitmessung starten
        long startTime = System.currentTimeMillis();

        // alle Eltern ein Mal durchtesten (1,2 | 1,3 | 1,4 | 2,3 | 2,4 | 3,4)
        int parentBase = 0;
        ArrayList<TSPInstance> nextGeneration = new ArrayList<TSPInstance>();
        LimitedSizeList mostFit = new LimitedSizeList(5);
        while (true) {
            long elapsedTime = System.currentTimeMillis() - startTime;
            long remainingTime = maxRuntimeMillis - elapsedTime;
            System.out.println("Restzeit: " + remainingTime +"\n");
            if(remainingTime <= 0){
                break;
            }

            for (int parentRun = parentBase + 1; parentRun < parents.size(); parentRun++) {
                TSPInstance child = combineGenetics(nodes, parents.get(parentBase), parents.get(parentRun), childId);
                System.out.println(child.toString());
                nextGeneration.add(child);
                if (child.getTotalCost() <= (int) (GeneticSearch.getFittest(parents)*leastFitnessFactor)) {
                    mostFit.add(child);
                }
                if (parentRun + 1 == parents.size()) {
                    parents = nextGeneration;
                    nextGeneration = new ArrayList<TSPInstance>();
                    childId = parents.size() + 1;
                }
                childId++;
            }
            parentBase++;
        }
        return mostFit;
    }

    private static int getFittest(ArrayList<TSPInstance> parents) {
        int lowestCost = -1;
        for (TSPInstance tspInstance : parents) {
            if (lowestCost == -1 || tspInstance.getTotalCost() < lowestCost) {
                lowestCost = tspInstance.getTotalCost();
            }
        }
        return lowestCost;
    }

    public static int[] repairCrossover(TSPInstance parent1, TSPInstance parent2, int[] permutation, int leftBound,
            int rightBound) {
        int[] newPerm = Arrays.copyOf(permutation, permutation.length);

        int progress = 0; // Zählzeiger, wird bei Duplikat auf 0 zurückgesetzt
        // linke Seite reparieren
        while (progress < newPerm.length - 1) {
            // Permutation Suchobjekt Ab hier
            int foundHere = valueFoundTwice(newPerm, newPerm[progress], progress + 1);
            if (foundHere != -1) {
                // Position aus Mittelsegment des 1. Elternteils zum Reparieren
                newPerm[progress] = parent1.getPermutation()[foundHere];
                progress = 0; // zurück zum Anfang, neu suchen auf Dopplungen
                continue;
            }
            progress++;
            if (progress == leftBound) {// zu rechter Seite springen
                progress = rightBound;
            }
        }
        return newPerm;
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
    private static int valueFoundTwice(int[] permutation, int id, int start) {
        for (int i = start; i < permutation.length; i++) {
            if (permutation[i] == id) {
                return i; // Methodenabbruch mit TRUE da Eintrag gefunden wurde
            }
        }
        return -1; // in Schleife nichts gefunden, daher FALSE
    }

    /**
     * Indexbestimmung des Objekts in Struktur
     * 
     * @param permutation Struktur(Elternstruktur zum Reparieren)
     * @param id          Suchobjekt
     * @return
     */
    private static int getIndexOfId(int[] permutation, int id, int startpoint) {
        int index = startpoint;
        while (index < permutation.length) {
            if (permutation[index] == id) {
                break;
            }
            index++;
        }
        return index;
    }
}
