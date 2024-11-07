package uebung_1;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Cvrp_ls {

    public static void main(String[] args) {
        String filename;
        if (args.length < 3) {
            System.out.println("Bedienung: ./cvrp <instance> <algorithm> <seconds> [<option>*]");
            filename = "src/Loggi-n401-k23.vrp";
            //TODO: Anweisung zur Bedienung der Kommandozeilenangabe //Rückfall zur Ausführung mit Run
        }
        else {
            String instance = args[0];
            System.out.println(args[0]);
            String algorithm = args[1];
            int seconds = Integer.parseInt(args[2]);
            long maxRuntimeMillis = seconds * 1000L;
            if (instance.equals("loggi")) { 
                filename = "src/Loggi-n401-k23.vrp";
            } else {
                filename = "src/Testdaten_Loggi.vrp";
            }
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

            // Zuordnung von Knoten und Distanzen
            // Depot ist Knoten[0], das Erste Gewicht ist von Knoten[1] zu Depot
            /**
             * row + 1 = untere Knoten-ID (zählt ab 1 nicht ab 0)
             * col + 1 = obere Knoten-ID
             * row/col = Distanz von unterer zu oberer Knoten-ID
             */
            for (int row = 0; row < distances.length; row++) {
                Node current = nodes.get(row); // Knoten der aktuellen Zeile
                for (int col = 0; col < distances[row].length; col++) {
                    if (col == row) { // Selbstdistanzen überspringen
                        continue;
                    }
                    current.setDemand(demands[row][1]);
                    current.addNeighbor(new Neighbor(nodes.get(col), // Knoten-ID des Nachbarn
                            distances[row][col])); // Distanz zu ihm
                    // Bedarf ([0=NodeID][1=Demand-Menge])
                }
            }
            /**
             * Testausgaben erste 5 Zeilen/Knoten
             * Falls du dir eine Übersicht schaffen willst
             * der Algorithmus arbeitet nur mit "nodes", die anderen Strukturen waren fürs
             * Aufbauen
             */
            StringBuilder output_diagonal = new StringBuilder();
            StringBuilder output_nodes = new StringBuilder();
            StringBuilder output_final = new StringBuilder();
            for (int i = 0; i < 5; i++) {
                for (int j = 0; j < 5; j++) {
                    output_diagonal.append(distances[i][j]).append("\t");

                }
                output_nodes.append("ID: ").append(nodes.get(i).getId()).append("\t")
                        .append("X = ").append(nodes.get(i).getCoordX()).append("\t")
                        .append("Y = ").append(nodes.get(i).getCoordY()).append("\n");
                output_diagonal.append("\n");
            }

            for (int i = 0; i < 5; i++) {
                ArrayList<Neighbor> neighbors = nodes.get(i).getNeighbors();
                for (int j = 0; j < neighbors.size(); j++) {
                    if (j < 5) {
                        output_final.append("aktueller Knoten " + nodes.get(i).getId()).append("\t");
                        output_final.append("Nachbar ID: " + neighbors.get(j).getNode().getId() + "\t");
                        output_final.append("Distanz: " + neighbors.get(j).getDistance() + "\t");
                        output_final.append("Bedarf Knoten " + neighbors.get(j).getNode().getId() + ":  "
                                + neighbors.get(j).getNode().getDemand() + "\n");
                    }
                }

            }
            System.out.println(output_diagonal.toString() + "\n\n");
            System.out.println(output_nodes.toString());
            System.out.println(output_final.toString());
            ArrayList<Route> routes = find_Greedy_Set(nodes, capacity);
            for (Route route : routes) {
                System.out.println(route);   
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static ArrayList<Node> allDemandsFulfilled(ArrayList<Node> nodes) {
        for (int i = 1; i < nodes.size(); i++) { // i = 1, Depot ist bei 0 -> Demand = 0
            if (nodes.get(i).getDemand() == 0) {
                nodes.remove(i);
                //Knoten mit 0 rauswerfen, neue Liste mit nur aktiven Knoten
            }
        }
        return nodes;
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
        int demandCounter = 1; // 1 bis (Anzahl Knoten)
        Route currentRoute = new Route(capacity);
        Node current = getNodeById(nodes, 1); // Startpunkt bei Depot
        System.out.println("Depot\naktuelle Route Nr. 1");
        while (current.getClosestDemandingNeighbor() != null) {
            StringBuilder output_final = new StringBuilder();
            //den nächsten Nachbarn suchen
            Neighbor next = current.getClosestDemandingNeighbor();

            // Abarbeitung der Bedarfe und Kosten
            int nodeDemand = next.getNode().getDemand();
            currentRoute.addCost(next.getDistance());
            next.getNode().reduceDemand(currentRoute.getCapacity());
            currentRoute.reduceCapacity(nodeDemand);
            

            //Verfolgungsausgabe
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
                route_Counter++;    //Zähler
                current = nodes.get(0);//Nächste Suche von Depot aus, da neue Route
                output_final.append("\n" + "Rückkehr zu Depot ID: " + current.getId() + "\t");
                output_final.append("\naktuelle Route Nr. " + route_Counter);
            }
            // Route kann noch weiter gehen, da Kapazität noch nicht erschöpft
            // Belieferungszähler um 1 erhöhen
            else{
                current = next.getNode();
                demandCounter++;
            }

            
            // TODO: Textausgabe der besuchten Knoten
            System.out.println(output_final.toString());
        }
        // letzte Route mit Restkapazität darf nicht fehlen
        routes.add(currentRoute);
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