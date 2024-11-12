package uebung_1;

import java.util.ArrayList;
import java.util.Arrays;

public class GeneticSearch {

    public static double sizeOfSides = 0.3; // Linke und Rechte Seite sollen Platz für Mitte lassen
    private static final double initialParentSize = 0.1;
    private static final double topPercentile = 0.9;

    /**
     * Kann für mehrere Kinder genutzt werden, da Austauschbereiche jedes mal
     * zufällig gewürfelt werden (auch bei gleichen Eltern)
     * 
     * @param parent1 Elternteil 1
     * @param parent2 Elternteil 2
     * @return einzelnes Kind dieser Kombination
     */
    public static TSPInstance combineGenetics(ArrayList<Node> nodes, TSPInstance parent1, TSPInstance parent2,
            int childId) {
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

        // System.out.println("Kind: " + childId + "\tEltern: " + parent1.getId() + " +
        // " + parent2.getId());
        int[] repairedPerm = GeneticSearch.repairCrossover(parent1, parent2, newPerm, left, right);

        TSPInstance child = new TSPInstance(nodes, repairedPerm, parent1.getCapacity(), parent1.getId(),
                parent2.getId(), childId);
        if (child.getRoutes().size() == 1) {
            System.out.println();
        }
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
            currentRoute.addCheckpoint(new Neighbor(current, 0)); // erste Route startet bei Depot
            ArrayList<Route> routes = new ArrayList<Route>(); // Routen für jede Instanz
            // Loop für Routen der konkreten Instanz
            while (current.hasDemandingNeighbors() != false) {

                /**
                 * zum Start der Route einen zufälligen neuen Startpunkt erzeugen dadurch
                 * sollten die Elterninstanzen recht gut (im Sinne von Nicht-Optimal)
                 * durchmischt sein
                 */
                int randomNeighbor = (int) (Math.random() * getUnclearedNeighbors(current).size());
                // if (currentRoute.getCapacity() == capacity) {
                // Zufallszahl aus Menge der Anzahl noch zu beliefernder Nachbarn
                // daraus wird ein zufälliger Nachbar gewählt
                next = getUnclearedNeighbors(current).get(randomNeighbor); // hier mit Index, da Listengröße gearbeitet
                                                                           // wird
                if (next.getNode().isCleared()) {
                    next = getUnclearedNeighbors(current).get(0);
                }
                // }
                // während der Route den nächstgelegenen Nachbarn mit Bedarf suchen
                // else {
                // next = current.getClosestDemandingNeighbor();
                // }
                // Abarbeitung der Bedarfe und Kosten
                int nodeDemand = next.getNode().getDemand();

                /*
                 * Route wird beendet, Wege zurück zu Depot und neue Route starten wichtig,
                 * damit jeder Knoten echt nur 1 Mal angesteuert wird
                 */
                if (currentRoute.getCapacity() < nodeDemand) {
                    // straight zurück zum Depot
                    currentRoute.addCost(next.getNode().getNeighborById(1).getDistance());
                    currentRoute.addCheckpoint(next.getNode().getNeighborById(1));
                    routes.add(currentRoute); // Route abspeichern
                    currentRoute = new Route(capacity); // neue Route erstellen
                    route_Counter++; // Zähler
                    current = nodes.get(0);// Nächste Suche von Depot aus, da neue Route
                    continue;
                }
                currentRoute.addCost(next.getDistance());
                next.getNode().setCleared(true);
                currentRoute.reduceCapacity(nodeDemand);
                currentRoute.addCheckpoint(next);

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
        int initialParents = (int) (nodes.size() * initialParentSize);
        ArrayList<TSPInstance> parents = findStartInstances(nodes, capacity, initialParents);
        StringBuilder parentInfo = new StringBuilder();
        for (TSPInstance tspInstance : parents) {
            parentInfo.append("\nElternID: " + tspInstance.getId())
                    .append("\tGesamtdistanz: " + tspInstance.getTotalCost())
                    .append("\tAnzahl Routen: " + tspInstance.getRoutes().size());
        }
        int childId = parents.size() + 1;
        TSPInstance[] top5 = new TSPInstance[5];
        int fitnessBound = (int) (topPercentile * getLeastFit(parents));
        System.out.println(parentInfo.toString());
        // Laufzeitmessung starten
        long startTime = System.currentTimeMillis();

        // alle nicht verwandten Eltern ein Mal durchtesten (1,2 | 1,3 | 1,4 | 2,3 | 2,4
        // | 3,4)
        int parentBase = 0;
        int parentRun = parentBase + 1;
        ArrayList<TSPInstance> nextGeneration = new ArrayList<TSPInstance>();
        LimitedSizeList mostFit = new LimitedSizeList(5);
        while (true) {
            long elapsedTime = System.currentTimeMillis() - startTime;
            long remainingTime = maxRuntimeMillis - elapsedTime;
            long timeInterval = System.currentTimeMillis() - startTime;
            if (remainingTime <= 0) { // Zeit abgelaufen
                break;
            }
            if (parentRun == parents.size()) { // Ende der Parent-1-Kette
                parentBase++;
                parentRun = parentBase + 1;
                if (parentBase == parents.size() - 1) {
                    parents = nextGeneration;
                    nextGeneration = new ArrayList<TSPInstance>();
                    parentBase = 0;
                    parentRun = 1;
                }
            }
            TSPInstance parent1 = parents.get(parentBase);
            TSPInstance parent2 = parents.get(parentRun);
            if (parentsAreRelated(parent1, parent2)) { // Familie nicht miteinander Kreuzen
                parentRun++;
                continue;
            }
            TSPInstance child = combineGenetics(nodes, parent1, parent2, childId);
            childId++;
            if (!(child.getTotalCost() < fitnessBound)) {
                parentRun++;
                continue;
            }
            nextGeneration.add(child);
            for (int i = 0; i < top5.length; i++) {
                if (top5[i] == null) {
                    top5[i] = child;
                    break;
                } else if (child.getTotalCost() < top5[i].getTotalCost()) {
                    System.out.println("Top 5-Platzierung: Kind  " + top5[i].getId() + " ersetzt durch " + child.getId());
                    top5[i] = child;

                    break;
                }
            }

            parentRun++;
        }
        System.out.println("Erzeugte Kinder: " + (childId - 1 - parents.size()));
        for (int i = 0; i < top5.length; i++) {
            mostFit.add(top5[i]);
        }
        return mostFit;
    }

    private static boolean parentsAreRelated(TSPInstance parent1, TSPInstance parent2) {
        if (parent1.getParent1() == 0) {
            return false;
        }
        if (parent1.getParent1() == parent2.getParent1()
                || parent1.getParent2() == parent2.getParent2()
                || parent1.getParent2() == parent2.getParent1()
                || parent1.getParent1() == parent2.getParent2()) {
            return true;
        }
        return false;
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

    private static int getLeastFit(ArrayList<TSPInstance> parents) {
        int lowestCost = -1;
        for (TSPInstance tspInstance : parents) {
            if (lowestCost == -1 || tspInstance.getTotalCost() > lowestCost) {
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
        while (progress < newPerm.length) {
            // Permutation Suchobjekt Ab hier
            int foundHere = valueFoundTwice(newPerm, newPerm[progress], progress);
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
     * bis zur gegebenen letzten Position vorkommt
     * 
     * @param permutation
     * @param id          Suchobjekt
     * @param startpoint  Endpunkt
     * @return Ja oder Nein
     */
    private static int valueFoundTwice(int[] permutation, int id, int position) {
        int i = 0;
        while (i < permutation.length) {
            if (i == position) {
                i++;
                continue;
            }
            if (permutation[i] == id) {
                return i; // Methodenabbruch mit TRUE da Eintrag gefunden wurde
            }
            i++;
        }
        return -1; // in Schleife nichts gefunden, daher -1
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
