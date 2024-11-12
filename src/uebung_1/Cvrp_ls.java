package uebung_1;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Cvrp_ls {

    public static void main(String[] args) {
        String filename;
        String algorithm;
        int tabuTenure;
        long maxRuntimeMillis = 3; // Tabu | Genetische Suche

        if (args.length < 3) {
            System.out.println(
                    "Angaben zur Anwendung eines Algorithmus: 'java -cp bin/ uebung_1.Cvrp_ls <instance>' <algorithm> <seconds> [<option>*]");
            filename = "src/Loggi-n401-k23.vrp";
            algorithm = "greedy";
            // TODO-MAX: Anweisung zur Bedienung der Kommandozeilenangabe //Rückfall zur
            // Ausführung mit Run
        } else {
            String instance = args[0];   
            System.out.println(args[0]);
            algorithm = args[1];
            int seconds = Integer.parseInt(args[2]);
            maxRuntimeMillis = seconds * 1000L;
            if (instance.equals("loggi")) { // Auswahl der Instanz
                filename = "src/Loggi-n401-k23.vrp";
            } else {
                filename = "src/Testdaten_Loggi.vrp";
            }
            tabuTenure = Integer.valueOf(args[3]);
        }

        String line = "";
        int dimension = 0;
        int capacity = 0;
        try {
            FileReader file = new FileReader(filename);
            BufferedReader reader = new BufferedReader(file);
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                // Beginne mit dem Lesen der Koordinaten
                if (line.contains("DIMENSION")) {
                    dimension = Integer.parseInt(line.split(": ")[1]);
                } else if (line.contains("CAPACITY")) {
                    capacity = Integer.parseInt(line.split(": ")[1]);
                }
            }
            reader.close();
            // Koordinatenspeicher
            Integer[][] distances = read_distances_from_txt(filename, dimension);
            ArrayList<Node> nodes = read_nodes_from_txt(filename);
            Integer[][] demands = read_demands_from_txt(filename, dimension);

            for (int row = 0; row < distances.length; row++) {
                Node current = nodes.get(row); // Knoten der aktuellen Zeile
                for (int col = 0; col < distances[row].length; col++) {
                    if (col == row) { // Selbstdistanzen überspringen
                        continue;
                    }
                    current.setDemand(demands[row][1]);
                    current.addNeighbor(new Neighbor(nodes.get(col), // Knoten-ID des Nachbarn
                            distances[row][col])); // Distanz zu ihm
                }
            }
            // Tabu-Algorithmus
            if (algorithm.equals("taboo_search")) { 
                ArrayList<Route> routes = TabuSearch.find_Tabu_Set(nodes, capacity, maxRuntimeMillis, 300);
            }
            // Genetischer Algorithmus
            // TODO: Hart-gecodete Parameter rückgängig machen
            else if (algorithm.equals("genetic")) {
                LimitedSizeList grandsons = GeneticSearch.findGeneticSetWithTime(nodes, capacity, maxRuntimeMillis);
                if(grandsons.size() != 0){
                    System.out.println("Top Ergebnisse:\n"+ grandsons.toString());
                    System.out.println("\nbestes Ergebnis:" + grandsons.getUpperBest().toString());
                }else{
                    System.out.println("Die Fitness-Bound war zu hoch eingestellt. \n Kein Kind entsprach den Erwartungen.");
                }
                
                int routeNr = 1;
                for (Route route  : grandsons.getUpperBest().getRoutes()) {
                    System.out.println("Kosten der Reise  " + routeNr + ": " + route.getCost());
                    routeNr++;
                } 
            } else {
                ArrayList<Route> routes = find_Greedy_Set(nodes, capacity);
                System.out.println("\n\nErwartete Eingaben für Algorithmensuche\n\ttaboo_search 3\n\t genetic 3");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static ArrayList<Node> read_nodes_from_txt(String filename) throws IOException {
        FileReader file = new FileReader(filename);
        BufferedReader reader = new BufferedReader(file);
        ArrayList<Node> nodes = new ArrayList<Node>();
        boolean coords = false;
        int id = 0; // Für Array-Platzlesung
        int coordx = 1; // Für Array-Platzlesung
        int coordy = 2; // Für Array-Platzlesung

        String line = "";
        while ((line = reader.readLine()) != null) {
            line = line.trim();
            // Beginne mit dem Lesen der Koordinaten
            if (line.equals("NODE_COORD_SECTION")) {
                coords = true;
                break;
            }
        }
        while (coords == true) {
            line = reader.readLine();
            // Ende der Koordinatenrubrick
            if (line.equals("DEMAND_SECTION")) {
                coords = false;
                break;
            } else {
                // Werte der Koordinatensektion auslesen und Objektivieren
                int[] values = new int[3];
                values[id] = Integer.parseInt(line.split(" ")[id]);
                values[coordx] = Integer.parseInt(line.split(" ")[coordx]);
                values[coordy] = Integer.parseInt(line.split(" ")[coordy]);
                // innerhalb der Koordinatenrubrick
                nodes.add(new Node(values[id],
                        values[coordx],
                        values[coordy]));
            }
        }
        reader.close();
        return nodes;
    }

    private static Integer[][] read_distances_from_txt(String filename, int dimension) throws IOException {
        Integer[][] distances = new Integer[dimension][dimension];
        for (int i = 0; i < distances.length; i++) {
            distances[i][i] = 0;
        }
        boolean edges = false;
        FileReader file = new FileReader(filename);
        BufferedReader reader = new BufferedReader(file);
        String line;
        while ((line = reader.readLine()) != null) {
            line = line.trim();
            // Beginne mit dem Lesen der Kantendistanzen
            if (line.equals("EDGE_WEIGHT_SECTION")) {
                edges = true;
                break;
            }
        }
        int row = 1; // Reihe der Spiegelmatrix
        while (edges == true) {
            line = reader.readLine();
            // Ende der Kantenrubrick
            if (line.equals("NODE_COORD_SECTION")) {
                edges = false;
                break;
            } else {
                // Werte der Kantensektion auslesen und Objektivieren
                String[] txtvalues = line.split(" ");

                // Einordnung der Textzeile in Matrix
                for (int i = 0; i < txtvalues.length; i++) {
                    if (i == row) {
                        continue;
                    }
                    distances[row][i] = Integer.parseInt(txtvalues[i]); // Diagonale Wertkopie
                    distances[i][row] = Integer.parseInt(txtvalues[i]); // macht Zuordnung leichter später
                }
            }
            row++;
        }
        reader.close();
        return distances;
    }

    private static Integer[][] read_demands_from_txt(String filename, int dimension) throws IOException {
        FileReader file = new FileReader(filename);
        BufferedReader reader = new BufferedReader(file);
        Integer[][] demandlist = new Integer[dimension][2];
        boolean readingDemand = false;
        int nodeId = 0; // Für Array-Platzlesung
        int demandSlot = 1; // Für Array-Platzlesung

        String line = "";
        while ((line = reader.readLine()) != null) {
            line = line.trim();
            // Beginne mit dem Lesen der Koordinaten
            if (line.equals("DEMAND_SECTION")) {
                readingDemand = true;
                break;
            }
        }
        int row = 0;
        while (readingDemand == true) {
            line = reader.readLine();
            // Ende der Koordinatenrubrick
            if (line.equals("DEPOT_SECTION")) {
                readingDemand = false;
                break;
            } else {
                // Werte der Koordinatensektion auslesen und Objektivieren
                int[] values = new int[2];
                values[nodeId] = Integer.parseInt(line.split(" ")[nodeId]);
                values[demandSlot] = Integer.parseInt(line.split(" ")[demandSlot]);

                // innerhalb der Koordinatenrubrick
                demandlist[row][nodeId] = values[nodeId];
                demandlist[row][demandSlot] = values[demandSlot];
            }
            row++;
        }
        reader.close();
        return demandlist;
    }

    public static ArrayList<Route> find_Greedy_Set(ArrayList<Node> nodes, int capacity) {
        ArrayList<Route> routes = new ArrayList<Route>();
        int route_Counter = 1;
        Route currentRoute = new Route(capacity);
        Node current = getNodeById(nodes, 1); // Startpunkt bei Depot
        Neighbor next = current.getClosestDemandingNeighbor();
        int totalCost = 0;
        System.out.println("Depot\naktuelle Route Nr. 1");
        while (current.getClosestDemandingNeighbor() != null) {
            StringBuilder output_final = new StringBuilder();
            // den nächsten Nachbarn suchen

            next = current.getClosestDemandingNeighbor();

            // Abarbeitung der Bedarfe und Kosten
            int nodeDemand = next.getNode().getDemand();
            currentRoute.addCost(next.getDistance());
            next.getNode().setCleared(true);
            currentRoute.reduceCapacity(nodeDemand);

            // Verfolgungsausgabe
            output_final.append("Nachbar ID: " + next.getNode().getId() + "\t");
            output_final.append("\t" + "Distanz: " + next.getDistance() + "\t" + "Bedarf: " + nodeDemand);
            output_final.append("\t" + "verbleibende Kapazität " + currentRoute.getCapacity());

            // Route ist beendet, Wege zurück zu Depot und neue Route starten
            if (currentRoute.getCapacity() == 0) {
                // straight zurück zum Depot
                currentRoute.addCost(next.getNode().getNeighborById(1).getDistance());
                routes.add(currentRoute); // Route abspeichern
                totalCost = totalCost + currentRoute.getCost();
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
            System.out.println(output_final.toString());
        }
        // letzte Route mit Restkapazität darf nicht fehlen
        routes.add(currentRoute);
        totalCost = totalCost + currentRoute.getCost();
        System.out.println("\n\n" + "Gesamtkosten: " + totalCost);
        return routes;
    }

    public static Node getNodeById(ArrayList<Node> nodes, int id) {
        Node node = null;
        for (int i = 0; i < nodes.size(); i++) {
            if (nodes.get(i).getId() == id) {
                return node = nodes.get(i);
            }
        }
        return node;
    }
}