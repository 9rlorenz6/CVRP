package uebung_1;
import java.util.ArrayList;
import java.util.LinkedList;

public class TabuSearch {

    public static ArrayList<Route> find_Tabu_Set(ArrayList<Node> nodes, int capacity, int tabuTenure, long maxRuntimeMillis) {
        ArrayList<Route> bestRoutes = new ArrayList<>();
        LinkedList<Route> tabuList = new LinkedList<>();
        ArrayList<Route> candidateRoutes = new ArrayList<>();
        ArrayList<Route> routes = new ArrayList<>();
        Route currentRoute = new Route(capacity);
        Node current = getNodeById(nodes, 1); // Startpunkt bei Depot
        Route bestCandidate = null;
        int routeCounter = 0;
        int try_counter = 0;
        int totalCost = 0;
        int demandCounter = 1;
        int id = 0;
        // Laufzeitmessung starten
        long startTime = System.currentTimeMillis();
        // Tabu-Suche Schleife mit Laufzeitbegrenzung und ohne maxIterations
        while (System.currentTimeMillis() - startTime < maxRuntimeMillis/10) {
            StringBuilder output_final = new StringBuilder();
            Neighbor next = current.getClosestDemandingNeighbor();
            // Falls keine Nachbarn mehr mit Bedarf vorhanden sind, neu starten
            if (Math.random() < 0.5) {  // 50% Wahrscheinlichkeit
                id = (int)(Math.random() * nodes.size());
                System.out.println(id);
                next = current.getNeighborById(id);

            if (next == null) {
                totalCost = totalCost + currentRoute.getCost();
                current = getNodeById(nodes, 1); // Startpunkt bei Depot      
                for (int i = 1; i < nodes.size();i++){
                    nodes.get(i).setCleared(false);
                }
                next = current.getClosestDemandingNeighbor();
                try_counter++;
                //neuer Durchlauf geht los
                output_final.append("\n Durchlauf " +  try_counter + "\tAnzahl Runden:" + routeCounter + "\t Gesamtkosten: " + totalCost);
                output_final.append("\t " + tabuList.getFirst() + ":" + tabuList.getLast());
                routeCounter = 0;
                totalCost = 0;
            }
            int nodeDemand = next.getNode().getDemand();
            currentRoute.addCost(next.getDistance());
            next.getNode().setCleared(true);
            currentRoute.reduceCapacity(nodeDemand);

            if (currentRoute.getCapacity() == 0) {  
                currentRoute.addCost(next.getNode().getNeighborById(1).getDistance());
                totalCost = totalCost + currentRoute.getCost();
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
                    
                }
                //System.out.println();
                currentRoute = new Route(capacity);
                current = nodes.get(0);
                routeCounter++;
                
            } else {
                current = next.getNode();
                demandCounter++;
            }
            System.out.print(output_final.toString());
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