package uebung_1;
import java.util.ArrayList;
import java.util.LinkedList;

public class TabuSearch {

    public static ArrayList<Route> find_Tabu_Set(ArrayList<Node> nodes, int capacity, int tabuTenure, long maxRuntimeMillis) {
        ArrayList<Route> bestRoutes = new ArrayList<>();
        LinkedList<Route> tabuList = new LinkedList<>();
        int routeCounter = 1;

        Route currentRoute = new Route(capacity);
        ArrayList<Route> candidateRoutes = new ArrayList<>();
        ArrayList<Route> routes = new ArrayList<>();
        
        int demandCounter = 1;
        Node current = getNodeById(nodes, 1); // Startpunkt bei Depot
        System.out.println("Depot\naktuelle Route Nr. 1");

        Route bestCandidate = null;

        // Laufzeitmessung starten
        long startTime = System.currentTimeMillis();

        // Tabu-Suche Schleife mit Laufzeitbegrenzung und ohne maxIterations
        while (System.currentTimeMillis() - startTime < maxRuntimeMillis) {
            StringBuilder output_final = new StringBuilder();
            Neighbor next = current.getClosestDemandingNeighbor();
            // Falls keine Nachbarn mehr mit Bedarf vorhanden sind, neu starten
            if (next == null) {
                current = getNodeById(nodes, 1); // Startpunkt bei Depot 
                System.out.println(current.getId());
                
                for (int i = 1; i < nodes.size();i++){
                    nodes.get(i).setCleared(false);
                }
                next = current.getClosestDemandingNeighbor();
                break;
            }

            int nodeDemand = next.getNode().getDemand();
            currentRoute.addCost(next.getDistance());
            next.getNode().reduceDemand(currentRoute.getCapacity());
            currentRoute.reduceCapacity(nodeDemand);

            output_final.append("\t" + "Nachbar ID: " + next.getNode().getId() + "\t");
            output_final.append("\t" + "Distanz: " + next.getDistance() + "\t" + "Bedarf: " + nodeDemand);
            output_final.append("\t" + "verbleibender Bedarf: " + next.getNode().getDemand() + "\t"
                    + "verbleibende Kapazität " + currentRoute.getCapacity());

            if (currentRoute.getCapacity() == 0) {  
                currentRoute.addCost(next.getNode().getNeighborById(1).getDistance());
                candidateRoutes.add(currentRoute);
                
                if (bestCandidate == null || currentRoute.getCost() < bestCandidate.getCost()) {
                    bestCandidate = currentRoute;
                }

                if (!tabuList.contains(currentRoute) || aspirationCriteria(currentRoute, bestCandidate)) {
                    routes.add(currentRoute);
                    if (tabuList.size() >= tabuTenure) {
                        tabuList.poll();
                    }
                    tabuList.offer(currentRoute);
                    routeCounter++;
                }

                currentRoute = new Route(capacity);
                current = nodes.get(0);
                output_final.append("\n" + "Rückkehr zu Depot ID: " + current.getId() + "\t");
                output_final.append("\naktuelle Route Nr. " + routeCounter);
            } else {
                current = next.getNode();
                demandCounter++;
            }
            System.out.println(output_final.toString());
        }
        
        routes.add(currentRoute);
        return routes;
    }

    private static boolean aspirationCriteria(Route candidate, Route bestCandidate) {
        return candidate.getCost() < bestCandidate.getCost();
    }

    private static Node getNodeById(ArrayList<Node> nodes, int id) {
        for (Node node : nodes) {
            if (node.getId() == id) return node;
        }
        return null;
    }
}