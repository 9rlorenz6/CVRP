package uebung_1;
import java.util.ArrayList;

public class TabuSearch {

    private static final int MAX_ITERATIONS_WITHOUT_IMPROVEMENT = 100; // Maximale Wiederholungen ohne Verbesserung
    private static final int MIN_COST_THRESHOLD = 1; // Minimalkosten Route

    public static ArrayList<Route> find_Tabu_Set(ArrayList<Node> nodes, int capacity, int tabuTenure, long maxRuntimeMillis) {
        boolean[][] tabuList = new boolean[401][401];
        ArrayList<Route> candidateRoutes = new ArrayList<>();
        ArrayList<Route> routes = new ArrayList<>();
        Route currentRoute = new Route(capacity);
        Node current = getNodeById(nodes, 1); // Start am Depot
        Route bestCandidate = null;
        int routeCounter = 1;
        int move = 1;
        int tryCounter = 0;
        int totalCost = 0;
        int demandCounter = 1;
        int id = 0;
        int iterationsWithoutImprovement = 0; // Tracks how many iterations since last improvement

        long startTime = System.currentTimeMillis();
        while (System.currentTimeMillis() - startTime < maxRuntimeMillis) {
            StringBuilder outputFinal = new StringBuilder();
            Neighbor next = current.getClosestDemandingNeighbor();

            // Randomly reset route occasionally
            if (Math.random() < 0.9) {
                id = (int) (Math.random() * nodes.size());
                if (current.getNeighborById(id) != null && !current.getNeighborById(id).getNode().isCleared() && !tabuList[move][id]) {
                    next = current.getNeighborById(id);
                    if (demandCounter == 1) {
                        tabuList[move][id] = true;
                        demandCounter--;
                    }
                }
            }

            if (next == null) {
                totalCost += currentRoute.getCost();
                current = getNodeById(nodes, 1);
                for (int i = 1; i < nodes.size(); i++) {
                    nodes.get(i).setCleared(false);
                }
                move = 1;
                next = current.getClosestDemandingNeighbor();
                tryCounter++;
                demandCounter = 1;
                outputFinal.append("\n Durchlauf " +  tryCounter + "\tAnzahl Runden:" + routeCounter + "\t Gesamtkosten: " + totalCost);
                routeCounter = 0;
                totalCost = 0;
            }

            int nodeDemand = next.getNode().getDemand();
            currentRoute.addCost(next.getDistance());
            next.getNode().setCleared(true);
            currentRoute.reduceCapacity(nodeDemand);

            if (currentRoute.getCapacity() == 0) {
                currentRoute.addCost(next.getNode().getNeighborById(1).getDistance());
                totalCost += currentRoute.getCost();
                candidateRoutes.add(currentRoute);

                // "aspiration criteria" 
                if (bestCandidate == null || aspirationCriteria(currentRoute, bestCandidate, iterationsWithoutImprovement)) {
                    bestCandidate = currentRoute;
                    iterationsWithoutImprovement = 0; // Verbesserungscounter zurücksetzen
                } else {
                    iterationsWithoutImprovement++;
                }

                currentRoute = new Route(capacity);
                current = nodes.get(0);
                routeCounter++;
                
            } else {
                current = next.getNode();
                demandCounter++;
            }
            move++;
            System.out.print(outputFinal.toString());

            // Stop bei Minimalkosten
            if (bestCandidate != null && bestCandidate.getCost() <= MIN_COST_THRESHOLD) {
                break;
            }
            
        }
        System.out.println();
        for (int i = 0; i < 401; i++) {
            for (int j = 0; j < 401; j++) {
                if (tabuList[i][j] == true) {
                    System.out.println("Tabu-Bewegung Nr." + i + " auf Nachbar " + j);
                }
            }
        }

        routes.add(currentRoute);
        return routes;
    }

    private static boolean aspirationCriteria(Route candidate, Route bestCandidate, int iterationsWithoutImprovement) {
        return candidate.getCost() < bestCandidate.getCost() || iterationsWithoutImprovement < MAX_ITERATIONS_WITHOUT_IMPROVEMENT;
    }

    private static Node getNodeById(ArrayList<Node> nodes, int id) {
        for (Node node : nodes) {
            if (node.getId() == id) return node;
        }
        return null;
    }
}
